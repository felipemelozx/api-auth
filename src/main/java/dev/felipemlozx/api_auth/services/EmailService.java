package dev.felipemlozx.api_auth.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
public class EmailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
    this.mailSender = mailSender;
    this.templateEngine = templateEngine;
  }
  @Async
  public void sendEmail(String to, String name, String link) throws MessagingException {
     MimeMessage mimeMessage = mailSender.createMimeMessage();
     MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);

     messageHelper.setTo(to);
     messageHelper.setSubject("Confirm your e-mail");

     Context context = new Context();
     context.setVariable("verificationLink", link);
     context.setVariable("userName", name);

     String htmlContent = templateEngine.process("email-template-register", context);
     messageHelper.setText(htmlContent, true);

     mailSender.send(mimeMessage);
  }
}