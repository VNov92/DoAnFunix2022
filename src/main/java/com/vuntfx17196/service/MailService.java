package com.vuntfx17196.service;

import com.vuntfx17196.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class MailService {
    private static final String USER = "user";
    private static final String BASE_URL = "baseUrl";
    private static final String URL = "http://localhost:8080";
    private final JavaMailSender javaMailSender;
    private final MessageSource messageSource;
    private final SpringTemplateEngine springTemplateEngine;

    public MailService(JavaMailSender javaMailSender, MessageSource messageSource, SpringTemplateEngine springTemplateEngine) {
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.springTemplateEngine = springTemplateEngine;
    }

    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom("noreply@hieusach.com");
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
        } catch (MailException | MessagingException e) {
            // throw new Error
        }
    }

    public void sendEmailFromTemplate(User user, String templateName, String titleKey) {
        if (user.getEmail() == null) {
            return;
        }
        Locale locale = Locale.forLanguageTag("en");
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, URL);
        String content = springTemplateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(user.getEmail(), subject, content, false, true);
    }

    public void sendEmailFromTemplate(User user, String templateName, String titleKey, String newPassword) {
        if (user.getEmail() == null) {
            return;
        }
        Locale locale = Locale.forLanguageTag("en");
        Context context = new Context(locale);
        context.setVariable(USER, user);
        context.setVariable("newPassword", newPassword);
        context.setVariable(BASE_URL, URL);
        String content = springTemplateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);
        sendEmail(user.getEmail(), subject, content, false, true);
    }

    public void sendActivationEmail(User user, String newPassword) {
        sendEmailFromTemplate(user, "/mail/activationEmail", "email.creation.title", newPassword);
    }

    public void sendCreationEmail(User user) {
        sendEmailFromTemplate(user, "/mail/creationEmail", "email.activation.title");
    }

    public void sendPasswordResetMail(User user) {
        sendEmailFromTemplate(user, "/mail/passwordResetEmail", "email.reset.title");
    }

    public void sendPasswordResetByAdmin(User user, String newPassword) {
        sendEmailFromTemplate(user, "/mail/passwordResetByAdmin", "email.reset.title", newPassword);
    }
}
