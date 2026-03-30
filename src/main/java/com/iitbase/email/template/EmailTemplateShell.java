package com.iitbase.email.template;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplateShell {

    @Value("${app.base-url:https://iitbase.com}")
    String baseUrl;

    // ── Outer shell ───────────────────────────────────────────────────────
    // Dark theme consistent with auth-theme.css:
    //   --auth-bg: #0a0f1e  |  card: #0f1628  |  border: rgba(255,255,255,0.08)
    //   --auth-indigo: #6366f1  |  --auth-indigo-light: #818cf8
    //   --auth-font-display: Georgia, serif  |  --auth-font-body: system-ui, sans-serif
    //
    // Logo: <img> tag with iitbase.com/logo.png (PNG, not SVG — works across all clients).
    // Text-mark fallback renders if images are blocked (Gmail default until "Show images").
    // ─────────────────────────────────────────────────────────────────────

    public String shell(String content) {
        return """
            <!DOCTYPE html>
            <html lang="en" xmlns="http://www.w3.org/1999/xhtml">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width,initial-scale=1.0">
              <meta http-equiv="X-UA-Compatible" content="IE=edge">
              <meta name="format-detection" content="telephone=no,date=no,address=no,email=no">
              <title>IITBase</title>
              <!--[if mso]>
              <noscript>
                <xml><o:OfficeDocumentSettings>
                  <o:PixelsPerInch>96</o:PixelsPerInch>
                </o:OfficeDocumentSettings></xml>
              </noscript>
              <![endif]-->
            </head>
            <body style="margin:0;padding:0;background-color:#0a0f1e;
                         font-family:-apple-system,BlinkMacSystemFont,'Segoe UI','DM Sans',Arial,sans-serif;
                         -webkit-text-size-adjust:100%%;-ms-text-size-adjust:100%%;">

              <!-- Preheader (hidden, shows in inbox preview) -->
              <div style="display:none;font-size:1px;color:#0a0f1e;line-height:1px;
                          max-height:0;max-width:0;opacity:0;overflow:hidden;">
                IITBase — the platform for IIT graduates and top recruiters.
              </div>

              <table role="presentation" width="100%%" cellpadding="0" cellspacing="0"
                     style="background-color:#0a0f1e;padding:40px 20px;">
                <tr><td align="center">
                  <table role="presentation" width="560" cellpadding="0" cellspacing="0"
                         style="max-width:560px;width:100%%;">

                    <!-- Logo row — wordmark only -->
                    <tr>
                      <td style="padding-bottom:24px;text-align:center;">
                        <span style="font-family:Georgia,serif;font-size:22px;
                                     font-weight:500;color:#f8fafc;letter-spacing:-0.4px;">
                          IITBase
                        </span>
                      </td>
                    </tr>

                    <!-- Card -->
                    <tr>
                      <td style="background-color:#0f1628;border-radius:20px;padding:40px;
                                 border:1px solid rgba(255,255,255,0.08);">
                        %s
                      </td>
                    </tr>

                    <!-- Footer -->
                    <tr>
                      <td style="padding-top:24px;text-align:center;">
                        <p style="color:#1e293b;font-size:12px;margin:0;line-height:1.7;
                                  font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Arial,sans-serif;">
                          &copy; 2026 IITBase.com &nbsp;&middot;&nbsp; Built for IIT graduates.<br>
                          <a href="%s/privacy" style="color:#334155;text-decoration:none;">Privacy Policy</a>
                          &nbsp;&middot;&nbsp;
                          <a href="%s/unsubscribe" style="color:#334155;text-decoration:none;">Unsubscribe</a>
                        </p>
                      </td>
                    </tr>

                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(content, baseUrl, baseUrl);
    }

    // ── Eyebrow label ─────────────────────────────────────────────────────
    public String eyebrow(String label) {
        return """
            <p style="font-size:10px;font-weight:500;letter-spacing:2px;
                      text-transform:uppercase;color:#818cf8;margin:0 0 12px;
                      font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Arial,sans-serif;">
              %s
            </p>
            """.formatted(label);
    }

    // ── Section heading ───────────────────────────────────────────────────
    public String heading(String text) {
        return """
            <h2 style="margin:0 0 8px;font-size:24px;font-weight:600;color:#f8fafc;
                       font-family:Georgia,serif;letter-spacing:-0.4px;line-height:1.2;">
              %s
            </h2>
            """.formatted(text);
    }

    // ── Body text ─────────────────────────────────────────────────────────
    public String subtext(String html) {
        return """
            <p style="color:#64748b;font-size:14px;margin:0 0 28px;line-height:1.65;
                      font-weight:300;
                      font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Arial,sans-serif;">
              %s
            </p>
            """.formatted(html);
    }

    // ── Primary CTA button ────────────────────────────────────────────────
    public String ctaButton(String href, String label) {
        return """
            <table role="presentation" cellpadding="0" cellspacing="0" style="margin:6px 0 24px;">
              <tr>
                <td style="background:linear-gradient(135deg,#6366f1,#818cf8);
                            border-radius:10px;mso-padding-alt:0;">
                  <!--[if mso]>
                  <v:roundrect xmlns:v="urn:schemas-microsoft-com:vml"
                               href="%s"
                               style="height:48px;v-text-anchor:middle;width:200px;"
                               arcsize="21%%" fillcolor="#6366f1" strokecolor="#6366f1">
                    <w:anchorlock/>
                    <center style="color:#ffffff;font-family:Arial,sans-serif;font-size:15px;font-weight:500;">%s</center>
                  </v:roundrect>
                  <![endif]-->
                  <!--[if !mso]><!-->
                  <a href="%s"
                     style="display:inline-block;padding:14px 32px;color:#ffffff;font-size:15px;
                            font-weight:500;text-decoration:none;border-radius:10px;
                            letter-spacing:-0.1px;mso-hide:all;
                            font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Arial,sans-serif;">
                    %s
                  </a>
                  <!--<![endif]-->
                </td>
              </tr>
            </table>
            """.formatted(href, label, href, label);
    }

    // ── Divider ───────────────────────────────────────────────────────────
    public String divider() {
        return "<div style='height:1px;background:rgba(255,255,255,0.05);margin:24px 0;'></div>";
    }

    // ── Small muted note at bottom of card ────────────────────────────────
    public String note(String html) {
        return """
            <p style="color:#475569;font-size:13px;margin:0;line-height:1.6;
                      font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Arial,sans-serif;">
              %s
            </p>
            """.formatted(html);
    }

    // ── Link fallback below CTA ───────────────────────────────────────────
    public String linkFallback(String href) {
        return """
            <p style="color:#334155;font-size:12px;margin:16px 0 0;line-height:1.6;
                      font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Arial,sans-serif;">
              If the button doesn't work, copy this link into your browser:<br>
              <a href="%s" style="color:#6366f1;word-break:break-all;">%s</a>
            </p>
            """.formatted(href, href);
    }

    // ── Green confirmation box (verified states) ──────────────────────────
    public String successBox(String innerHtml) {
        return """
            <div style="background:rgba(16,185,129,0.06);border:1px solid rgba(16,185,129,0.18);
                        border-radius:12px;padding:16px 18px;margin-bottom:24px;">
              <p style="margin:0;font-size:13px;color:#6ee7b7;line-height:1.7;font-weight:300;
                        font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Arial,sans-serif;">
                %s
              </p>
            </div>
            """.formatted(innerHtml);
    }

    // ── Indigo info box (invite/role details) ─────────────────────────────
    public String infoBox(String innerHtml) {
        return """
            <div style="background:rgba(99,102,241,0.06);border:1px solid rgba(99,102,241,0.15);
                        border-radius:10px;padding:14px 16px;margin-bottom:8px;">
              <p style="margin:0;font-size:13px;color:#94a3b8;font-weight:300;line-height:1.6;
                        font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Arial,sans-serif;">
                %s
              </p>
            </div>
            """.formatted(innerHtml);
    }

    // ── Feature row (icon + title + desc) ────────────────────────────────
    // Rendered as a table row — caller wraps in <table width="100%%">
    public String featureRow(String emoji, String title, String description) {
        return """
            <tr>
              <td style="padding:12px 0;vertical-align:top;width:48px;border-bottom:1px solid rgba(255,255,255,0.04);">
                <div style="width:34px;height:34px;border-radius:8px;
                            background:rgba(99,102,241,0.10);
                            border:1px solid rgba(99,102,241,0.15);
                            text-align:center;line-height:34px;font-size:15px;">%s</div>
              </td>
              <td style="padding:12px 0 12px 14px;vertical-align:top;border-bottom:1px solid rgba(255,255,255,0.04);">
                <p style="margin:0 0 2px;font-size:13px;font-weight:500;color:#cbd5e1;
                          font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Arial,sans-serif;">%s</p>
                <p style="margin:0;font-size:12px;color:#475569;font-weight:300;line-height:1.5;
                          font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Arial,sans-serif;">%s</p>
              </td>
            </tr>
            """.formatted(emoji, title, description);
    }
}