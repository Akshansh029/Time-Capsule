package com.akshansh.timecapsulebackend.service;

import com.akshansh.timecapsulebackend.exception.ResendEmailException;
import com.akshansh.timecapsulebackend.model.entity.Capsule;
import com.akshansh.timecapsulebackend.model.entity.MemberRole;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ResendEmailService {

    private final Resend resend;

    @Value("${resend.from.email}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public ResendEmailService(Resend resend) {
        this.resend = resend;
    }

    public void sendVerificationEmail(String toEmail, String verificationCode) {
        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(toEmail)
                    .subject("Time Capsule: Verify your email")
                    .html(buildVerificationHtml(verificationCode))
                    .build();

            resend.emails().send(params);

            log.info("Email verification code SENT to: {}", toEmail);
        } catch (ResendException e) {
            log.error("Failed to send code verification email to {}: {}", toEmail, e.getMessage());
            throw new ResendEmailException("Failed to send verification email: " + e.getMessage());
        }
    }

    public void sendInvitationEmail(String toEmail, MemberRole inviteeRole, Capsule capsule, String invitedBy){
        try{
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(toEmail)
                    .subject("✉️ Invitation for Time Capsule \"" + capsule.getTitle() + "\"")
                    .html(buildInviteEmailBody(capsule, invitedBy, inviteeRole.toString()))
                    .build();

            resend.emails().send(params);
            log.info("Invitation email SENT to: {}", toEmail);
        } catch(ResendException e){
            log.error("Failed to send invitation email to {}: {}", toEmail, e.getMessage());
            throw new ResendEmailException("Failed to send invitation email: " + e.getMessage());
        }
    }

    public void sendUnlockNotification(Capsule capsule) {
        List<String> recipients = new ArrayList<>();

        // Add owner
        recipients.add(capsule.getOwner().getEmail());

        // Add all members
        capsule.getMembers().forEach(m -> recipients.add(m.getUser().getEmail()));

        for (String email : recipients) {
            try {
                CreateEmailOptions params = CreateEmailOptions.builder()
                        .from(fromEmail)
                        .to(email)
                        .subject("🔓 Your Time Capsule \"" + capsule.getTitle() + "\" is Now Open!")
                        .html(buildEmailBody(capsule))
                        .build();

                resend.emails().send(params);
                log.info("SENT unlock notification for capsule: {} to email: {}", capsule.getSlug(), email);
            } catch (ResendException e) {
                log.error("Failed to send unlock email to {}: {}", email, e.getMessage());
                throw new ResendEmailException("Failed to send unlock notification email: " + e.getMessage());
            }
        }
    }

    private String buildVerificationHtml(String verificationCode) {
        return """
                <h2>Hi</h2>
                <p>Thanks for registering. Your verification code is: %s.</p>
                <p>If you didn't register, ignore this email.</p>
                """.formatted(verificationCode);
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

    private String buildInviteEmailBody(Capsule capsule, String invitedBy, String inviteeRole) {
        String capsuleUrl = frontendUrl + "/capsule/" + capsule.getSlug();
        String unlockDate = capsule.getUnlockDate()
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
                            You've been invited to a time capsule.
                          </h2>

                          <p style="margin:0 0 24px; color:#5c5a52;
                                     font-size:15px; line-height:1.7;">
                            <strong>%s</strong> has invited you to join the time capsule
                            <strong>"%s"</strong> — a space sealed with memories, thoughts,
                            and moments as a <strong>%s</strong> that will remain locked until
                            <strong>%s</strong>.
                          </p>

                          <p style="margin:0 0 32px; color:#5c5a52;
                                     font-size:15px; line-height:1.7;">
                            As a contributor, you can add your own memories, photos,
                            and messages before the capsule is sealed. When the day
                            comes, everything inside will be revealed.
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
                                  View Capsule →
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
                            You received this email because <strong>%s</strong> added
                            you as a member of this time capsule. If you believe this
                            was a mistake, please ignore this email.
                          </p>
                        </td>
                      </tr>

                    </table>

                  </td>
                </tr>
              </table>

            </body>
            </html>
            """.formatted(
                invitedBy,   // invited by
                capsule.getTitle(),    // capsule title
                inviteeRole,
                unlockDate,            // unlock date
                capsuleUrl,            // CTA button href
                invitedBy    // footer
        );
    }
}
