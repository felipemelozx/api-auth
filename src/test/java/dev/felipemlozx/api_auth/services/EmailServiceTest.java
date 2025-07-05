package dev.felipemlozx.api_auth.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

  private JavaMailSender mailSender;
  private TemplateEngine templateEngine;
  private EmailService emailService;

  @BeforeEach
  void setUp() {
    mailSender = mock(JavaMailSender.class);
    templateEngine = mock(TemplateEngine.class);
    emailService = new EmailService(mailSender, templateEngine);
  }

  @Test
  void testSendEmailSuccess() throws MessagingException {
    String to = "test@example.com";
    String name = "Test User";
    String link = "http://example.com/verify";
    MimeMessage mimeMessage = mock(MimeMessage.class);

    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    when(templateEngine.process(eq("email-template-register"), any(Context.class)))
        .thenReturn("<html>Email Content</html>");

    emailService.sendEmail(to, name, link);

    verify(mailSender).createMimeMessage();
    verify(templateEngine).process(eq("email-template-register"), any(Context.class));
    verify(mailSender).send(mimeMessage);
  }

  @Test
  void testSendEmailThrowsMessagingException() throws Exception {
    String to = "test@example.com";
    String name = "Test User";
    String link = "http://example.com/verify";
    when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("Mail error"));

    assertThrows(RuntimeException.class, () -> {
      emailService.sendEmail(to, name, link);
    });
  }
}

