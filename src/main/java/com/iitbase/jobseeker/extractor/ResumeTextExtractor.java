package com.iitbase.jobseeker.extractor;

import com.iitbase.common.MemoryLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Extracts plain text from a resume file stream.
 * Supports PDF and DOCX — the two formats IIT resumes realistically come in.
 * Text quality here directly affects parse quality. PDFBox handles
 * LaTeX-generated PDFs (common with IIT resumes) better than most alternatives.
 */
@Slf4j
@Component
public class ResumeTextExtractor {

    private static final int MAX_CHARS = 12_000;
    private static final PDFTextStripper STRIPPER;

    static {
        STRIPPER = new PDFTextStripper();
        STRIPPER.setSortByPosition(true);
    }

    public String extract(InputStream inputStream, String contentType) throws IOException {
        MemoryLogger.log("BEFORE_TEXT_EXTRACTION");

        String raw = switch (contentType) {
            case "application/pdf" -> extractPdf(inputStream);
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                 "application/msword" -> extractDocx(inputStream);
            default -> throw new IllegalArgumentException(
                    "Unsupported resume format: " + contentType
            );
        };

        String cleaned = clean(raw);
        log.debug("Extracted {} chars (type={})", cleaned.length(), contentType);
        MemoryLogger.log("AFTER_TEXT_EXTRACTION");
        System.gc();
        return cleaned;
    }

    private String extractPdf(InputStream inputStream) throws IOException {
        File tempFile = File.createTempFile("resume", ".pdf");
        Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        try (PDDocument document = Loader.loadPDF(tempFile)) {
            return STRIPPER.getText(document);
        } finally {
            Files.deleteIfExists(tempFile.toPath()); // safer than tempFile.delete()
            System.gc();
        }
    }

    private String extractDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    private String clean(String raw) {
        if (raw == null) return "";
        String cleaned = raw
                .replaceAll("\r\n", "\n")
                .replaceAll("\r", "\n")
                .replaceAll("[ \\t]+", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .replaceAll("[^\\x20-\\x7E\\n]", " ")
                .trim();
        return cleaned.length() > MAX_CHARS ? cleaned.substring(0, MAX_CHARS) : cleaned;
    }
}