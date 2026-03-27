package com.iitbase.email.template;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EmailTemplateShell {

    @Value("${app.base-url:https://iitbase.com}")
    String baseUrl;   // package-private so subclasses in same package can read it

    public String shell(String content) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin:0;padding:0;background:#f1f5f9;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI','DM Sans',sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f1f5f9;padding:40px 20px;">
                <tr><td align="center">
                  <table width="600" cellpadding="0" cellspacing="0" style="max-width:600px;width:100%%;">
 
                    <!-- Logo -->
                    <tr>
                      <td style="padding-bottom:24px;text-align:center;">
                        <table cellpadding="0" cellspacing="0" style="display:inline-table;">
                          <tr>
                            <td style="background:linear-gradient(135deg,#6366f1,#818cf8);width:36px;height:36px;border-radius:9px;text-align:center;vertical-align:middle;">
                              <span style="color:white;font-size:20px;font-weight:700;font-family:Georgia,serif;line-height:36px;">I</span>
                            </td>
                            <td style="padding-left:10px;vertical-align:middle;">
                              <span style="font-family:Georgia,serif;font-size:20px;font-weight:500;color:#0f172a;letter-spacing:-0.3px;">IITBase</span>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
 
                    <!-- Card -->
                    <tr>
                      <td style="background:#ffffff;border-radius:16px;padding:40px;border:1px solid #e2e8f0;">
                        %s
                      </td>
                    </tr>
 
                    <!-- Footer -->
                    <tr>
                      <td style="padding-top:24px;text-align:center;">
                        <p style="color:#94a3b8;font-size:12px;margin:0;line-height:1.6;">
                          © 2026 IITBase. Built for IIT graduates.<br>
                          <a href="%s/unsubscribe" style="color:#94a3b8;">Unsubscribe</a>
                        </p>
                      </td>
                    </tr>
 
                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(content, baseUrl);
    }

    public String ctaButton(String href, String label) {
        return """
            <table cellpadding="0" cellspacing="0" style="margin:28px 0;">
              <tr>
                <td style="background:linear-gradient(135deg,#6366f1,#818cf8);border-radius:10px;">
                  <a href="%s"
                     style="display:inline-block;padding:13px 28px;color:#ffffff;font-size:15px;
                            font-weight:500;text-decoration:none;border-radius:10px;letter-spacing:-0.1px;">
                    %s
                  </a>
                </td>
              </tr>
            </table>
            """.formatted(href, label);
    }

    public String divider() {
        return "<div style='height:1px;background:#f1f5f9;margin:24px 0;'></div>";
    }

    public String linkFallback(String href) {
        return """
            <p style="color:#94a3b8;font-size:12px;margin:16px 0 0;line-height:1.6;">
              If the button doesn't work, copy this link into your browser:<br>
              <a href="%s" style="color:#6366f1;word-break:break-all;">%s</a>
            </p>
            """.formatted(href, href);
    }

    public String featureRow(String icon, String title, String description) {
        return """
            <tr>
              <td style="padding:10px 0;vertical-align:top;width:32px;">
                <span style="font-size:18px;line-height:1;">%s</span>
              </td>
              <td style="padding:10px 0 10px 12px;vertical-align:top;">
                <p style="margin:0 0 2px;font-size:14px;font-weight:600;color:#0f172a;">%s</p>
                <p style="margin:0;font-size:13px;color:#64748b;font-weight:300;line-height:1.5;">%s</p>
              </td>
            </tr>
            """.formatted(icon, title, description);
    }
}