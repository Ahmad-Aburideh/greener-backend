package com.greener.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String token) {

        String link = "https://greener-backend-production-edcc.up.railway.app/api/auth/verify?token=" + token;

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("ggahmadgg2003@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Verify your Greener account 🌱");

            String htmlContent = """
    <div style="font-family: Arial, sans-serif; text-align: center; padding: 20px;">
        
        <img src="https://i.imgur.com/TiqHF9K.png" 
             width="120" 
             style="margin-bottom: 20px;" />

        <h2 style="color: #2ecc71;">Welcome to Greener 🌱</h2>

        <p style="font-size: 16px;">
            Click the button below to verify your account:
        </p>

        <a href="%s" 
           style="display: inline-block; padding: 14px 30px; font-size: 16px; 
                  color: white; background-color: #2ecc71; text-decoration: none; 
                  border-radius: 10px; margin-top: 20px;">
            Verify Account
        </a>

        <p style="margin-top: 25px; font-size: 12px; color: gray;">
            This link will expire in 24 hours.
        </p>

    </div>
""".formatted(link);

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email");
        }
    }
}