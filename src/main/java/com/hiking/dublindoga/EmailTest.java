package com.hiking.dublindoga;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.SimpleMailMessage;
import java.util.Properties;

public class EmailTest {
    public static void main(String[] args) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.ionos.co.uk");
        mailSender.setPort(587);
        mailSender.setUsername("info@originmaster.ie");
        mailSender.setPassword("Telefon191**");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.ionos.co.uk");

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("info@originmaster.ie");
            message.setTo("ygnhmt@gmail.com");
            message.setSubject("Test Mail");
            message.setText("Hello! This is a test email.");

            mailSender.send(message);
            System.out.println("Email sent successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
