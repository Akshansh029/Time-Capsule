package com.akshansh.timecapsulebackend.service;

import com.akshansh.timecapsulebackend.model.entity.Capsule;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendUnlockNotification(Capsule capsule) {
        List<String> recipients = new ArrayList<>();

        // Add owner
        recipients.add(capsule.getOwner().getEmail());

        // Add all members
        capsule.getMembers().forEach(m -> recipients.add(m.getUser().getEmail()));

        for (String email : recipients) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setTo(email);
                helper.setSubject("🔓 Your Time Capsule \"" + capsule.getTitle() + "\" is Now Open!");
                helper.setText(buildEmailBody(capsule), true); // true = isHtml

                mailSender.send(message);
                log.info("SENT unlock notification for capsule: {} to email: {}", capsule.getSlug(), email);
            } catch (MessagingException e) {
                log.error("Failed to send unlock email to {}: {}", email, e.getMessage());
            }
        }
    }

    private String buildEmailBody(Capsule capsule) {
        String capsuleUrl = frontendUrl + "/capsule/" + capsule.getSlug();
        String unlockedDate = capsule.getUnlockDate()
                .format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

        return """
                <!DOCTYPE html>
                <html>
                <body style="margin:0; padding:0; background-color:#f4f1eb; font-family: Georgia, serif;">

                  <table width="100%%" cellpadding="0" cellspacing="0">
                    <tr>
                      <td align="center" style="padding: 40px 20px;">

                        <table width="600" cellpadding="0" cellspacing="0"
                               style="background-color:#fffef9; border-radius:12px;
                                      border: 1px solid #e8e2d4; overflow:hidden;">

                          <!-- Header -->
                          <tr>
                            <td align="center"
                                style="background-color:#2c2c2a; padding: 36px 40px;">
                              <p style="margin:0; font-size:28px;">🕰️</p>
                              <h1 style="margin:12px 0 0; color:#f5f0e8;
                                         font-size:22px; font-weight:normal;
                                         letter-spacing:2px; text-transform:uppercase;">
                                Time Capsule
                              </h1>
                            </td>
                          </tr>

                          <!-- Body -->
                          <tr>
                            <td style="padding: 40px 48px;">

                              <h2 style="margin:0 0 16px; color:#2c2c2a;
                                         font-size:20px; font-weight:normal;">
                                Your capsule has been unlocked.
                              </h2>

                              <p style="margin:0 0 24px; color:#5c5a52;
                                         font-size:15px; line-height:1.7;">
                                The time capsule <strong>"%s"</strong> was sealed with
                                memories, thoughts, and moments — and today, on
                                <strong>%s</strong>, by <strong>%s</strong> it is finally open.
                              </p>

                              <p style="margin:0 0 32px; color:#5c5a52;
                                         font-size:15px; line-height:1.7;">
                                Everything that was locked inside is now waiting for you.
                                Take a moment — some of these memories might surprise you.
                              </p>

                              <!-- CTA Button -->
                              <table cellpadding="0" cellspacing="0">
                                <tr>
                                  <td align="center"
                                      style="background-color:#2c2c2a; border-radius:8px;">
                                    <a href="%s"
                                       style="display:inline-block; padding:14px 32px;
                                              color:#f5f0e8; font-size:15px;
                                              text-decoration:none; letter-spacing:1px;">
                                      Open Your Capsule →
                                    </a>
                                  </td>
                                </tr>
                              </table>

                            </td>
                          </tr>

                          <!-- Divider -->
                          <tr>
                            <td style="padding: 0 48px;">
                              <hr style="border:none; border-top:1px solid #e8e2d4; margin:0;">
                            </td>
                          </tr>

                          <!-- Footer -->
                          <tr>
                            <td style="padding: 24px 48px 36px;">
                              <p style="margin:0; color:#9c9a8e; font-size:12px;
                                         line-height:1.6;">
                                You received this email because you are a member of this
                                time capsule. If you believe this was a mistake, please
                                ignore this email.
                              </p>
                            </td>
                          </tr>

                        </table>

                      </td>
                    </tr>
                  </table>

                </body>
                </html>
                """.formatted(capsule.getTitle(), unlockedDate, capsule.getOwner().getName(), capsuleUrl);
    }
}
