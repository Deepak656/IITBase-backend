package com.iitbase.jobseeker.storage;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class R2StorageService {

    private final S3Client s3Client;
    private final String bucket;
    private final String publicBaseUrl;

    // Limits
    private static final long MAX_RESUME_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long MAX_PHOTO_SIZE  = 2 * 1024 * 1024; // 2MB

    // Allowed types
    private static final Set<String> ALLOWED_RESUME_TYPES =
            Set.of("application/pdf");

    private static final Set<String> ALLOWED_PHOTO_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    private static final Set<String> ALLOWED_PHOTO_EXT =
            Set.of("jpg", "jpeg", "png", "webp");

    public R2StorageService(
            @Value("${r2.account-id}") String accountId,
            @Value("${r2.access-key}") String accessKey,
            @Value("${r2.secret-key}") String secretKey,
            @Value("${r2.bucket}") String bucket,
            @Value("${r2.public-url}") String publicUrl
    ) {

        this.bucket = bucket;
        this.publicBaseUrl = normalizeUrl(publicUrl);

        this.s3Client = S3Client.builder()
                .endpointOverride(
                        URI.create("https://" + accountId + ".r2.cloudflarestorage.com")
                )
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .region(Region.of("auto"))
                .build();
    }

    // -------------------------
    // Upload Resume
    // -------------------------

    public String uploadResume(MultipartFile file, Long userId) {

        validateFile(file, MAX_RESUME_SIZE, ALLOWED_RESUME_TYPES, "Resume");

        String key = String.format(
                "resumes/%d/%s.pdf",
                userId,
                UUID.randomUUID()
        );

        return upload(file, key);
    }

    // -------------------------
    // Upload Profile Photo
    // -------------------------

    public String uploadProfilePhoto(MultipartFile file, Long userId) {

        validateFile(file, MAX_PHOTO_SIZE, ALLOWED_PHOTO_TYPES, "Profile photo");

        String ext = getExtension(file.getOriginalFilename());

        if (!ALLOWED_PHOTO_EXT.contains(ext)) {
            throw new IllegalArgumentException("Invalid image extension: " + ext);
        }

        String key = String.format(
                "photos/%d/%s.%s",
                userId,
                UUID.randomUUID(),
                ext
        );

        return upload(file, key);
    }

    // -------------------------
    // Delete Object
    // -------------------------

    public void delete(String fileUrl) {

        if (fileUrl == null || !fileUrl.startsWith(publicBaseUrl)) {
            return;
        }

        String key = fileUrl.substring(publicBaseUrl.length());

        try {

            s3Client.deleteObject(
                    DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build()
            );

            log.debug("Deleted R2 object: {}", key);

        } catch (Exception ex) {

            log.warn(
                    "Failed deleting R2 object {} : {}",
                    key,
                    ex.getMessage()
            );
        }
    }

    // -------------------------
    // Core Upload Method
    // -------------------------

    private String upload(MultipartFile file, String key) {

        try {

            PutObjectRequest request =
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .cacheControl("public, max-age=31536000")
                            .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromInputStream(
                            file.getInputStream(),
                            file.getSize()
                    )
            );

            log.debug("Uploaded to R2: {}", key);

            return publicBaseUrl + key;

        } catch (IOException ex) {

            throw new RuntimeException(
                    "Failed reading uploaded file",
                    ex
            );

        } catch (Exception ex) {

            throw new RuntimeException(
                    "R2 upload failed: " + ex.getMessage(),
                    ex
            );
        }
    }

    // -------------------------
    // Validation
    // -------------------------

    private void validateFile(
            MultipartFile file,
            long maxSize,
            Set<String> allowedTypes,
            String label
    ) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(label + " file is empty");
        }

        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException(
                    label + " exceeds maximum allowed size of "
                            + (maxSize / 1024 / 1024) + "MB"
            );
        }

        if (!allowedTypes.contains(file.getContentType())) {
            throw new IllegalArgumentException(
                    label + " type not allowed. Allowed: " + allowedTypes
            );
        }
    }

    // -------------------------
    // Helpers
    // -------------------------

    private String getExtension(String filename) {

        if (filename == null || !filename.contains(".")) {
            return "jpg";
        }

        return filename
                .substring(filename.lastIndexOf('.') + 1)
                .toLowerCase();
    }

    private String normalizeUrl(String url) {

        return url.endsWith("/") ? url : url + "/";
    }

    @PreDestroy
    public void shutdown() {

        try {
            s3Client.close();
        } catch (Exception ignored) {
        }
    }
}