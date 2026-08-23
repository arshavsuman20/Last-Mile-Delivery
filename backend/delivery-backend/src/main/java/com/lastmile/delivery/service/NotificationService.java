package com.lastmile.delivery.service;

import com.lastmile.delivery.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger =
            LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendStatusNotification(Order order) {

        String email = order.getCustomer().getEmail();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Last Mile Delivery - Order Status Update");
        message.setText(
                "Hello " + order.getCustomer().getName() + ",\n\n" +
                "Your order #" + order.getId() +
                " status has been updated to: " +
                order.getStatus() + ".\n\n" +
                "Thank you."
        );

        try {
            mailSender.send(message);
            logger.info("Status notification sent for order {}", order.getId());
        } catch (Exception e) {
            logger.warn(
                    "Email notification failed for order {}: {}",
                    order.getId(),
                    e.getMessage()
            );
        }
    }
}