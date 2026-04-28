package com.akshansh.timecapsulebackend.service;

import com.akshansh.timecapsulebackend.model.dto.EmailDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class EmailService {

    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    // Send simple mail
    public String sendSimpleMail(EmailDetails details) {

        try {

            SimpleMailMessage mailMessage =
                    new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            javaMailSender.send(mailMessage);

            return "Mail Sent Successfully";

        } catch (Exception e) {

            return "Error while sending mail";
        }
    }

    // Send mail with attachment
    public String sendMailWithAttachment(
            EmailDetails details) {

        MimeMessage mimeMessage =
                javaMailSender.createMimeMessage();

        MimeMessageHelper helper;

        try {

            helper =
                    new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(sender);
            helper.setTo(details.getRecipient());
            helper.setText(details.getMsgBody());
            helper.setSubject(details.getSubject());

            FileSystemResource file =
                    new FileSystemResource(
                            new File(details.getAttachment()));

            helper.addAttachment(
                    file.getFilename(), file);

            javaMailSender.send(mimeMessage);

            return "Mail Sent Successfully";

        } catch (MessagingException e) {

            return "Error while sending mail";
        }
    }
}
