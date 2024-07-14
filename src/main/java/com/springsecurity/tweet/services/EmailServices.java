package com.springsecurity.tweet.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServices {
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String sender;

    public EmailServices(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendTxtMail(String recipient,String subject, String message){
        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(recipient);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            mailSender.send(mailMessage);
        }catch (Exception e){
            System.out.println("erro "+e.getMessage());
            System.out.println("por causa +"+e.getCause());
            System.out.println("por causa +"+e.getStackTrace());
            System.out.println("por causa +"+e.getLocalizedMessage());
        }
    }
}
