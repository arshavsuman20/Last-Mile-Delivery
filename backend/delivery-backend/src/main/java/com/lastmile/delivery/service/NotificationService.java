package com.lastmile.delivery.service;

import com.lastmile.delivery.entity.Notification;
import com.lastmile.delivery.entity.Order;
import com.lastmile.delivery.repository.NotificationRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;

@Service
public class NotificationService {

    private static final Logger logger =
            LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.auth-token:}")
    private String authToken;

    @Value("${twilio.phone-number:}")
    private String twilioPhoneNumber;

    public NotificationService(
            JavaMailSender mailSender,
            NotificationRepository notificationRepository) {

        this.mailSender = mailSender;
        this.notificationRepository = notificationRepository;
    }

    @PostConstruct
    public void initTwilio() {
        if (!accountSid.isBlank() && !authToken.isBlank()) {
            Twilio.init(accountSid, authToken);
            logger.info("Twilio initialized successfully");
        } else {
            logger.warn("Twilio credentials are not configured");
        }
    }

    public void sendStatusNotification(Order order) {

        String messageText =
                "Hello " + order.getCustomer().getName() + ",\n\n" +
                "Your order #" + order.getId() +
                " status has been updated to: " +
                order.getStatus() + ".\n\n" +
                "Thank you.";

        sendEmail(order, messageText);
        sendSms(order, messageText);
    }

    private void sendEmail(Order order, String messageText) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(order.getCustomer().getEmail());
            message.setSubject(
                    "Last Mile Delivery - Order Status Update"
            );
            message.setText(messageText);

            mailSender.send(message);

            saveNotification(
                    order,
                    Notification.Channel.EMAIL,
                    messageText,
                    Notification.NotificationStatus.SENT
            );

            logger.info(
                    "Email notification sent for order {}",
                    order.getId()
            );

        } catch (Exception e) {

            saveNotification(
                    order,
                    Notification.Channel.EMAIL,
                    messageText,
                    Notification.NotificationStatus.FAILED
            );

            logger.warn(
                    "Email notification failed for order {}: {}",
                    order.getId(),
                    e.getMessage()
            );
        }
    }

    private void sendSms(Order order, String messageText) {

        String phone = order.getCustomer().getPhone();

        if (phone == null || phone.isBlank()) {
            logger.warn(
                    "SMS skipped for order {}: customer phone missing",
                    order.getId()
            );
            return;
        }

        try {

            Message.creator(
                    new PhoneNumber(phone),
                    new PhoneNumber(twilioPhoneNumber),
                    messageText
            ).create();

            saveNotification(
                    order,
                    Notification.Channel.SMS,
                    messageText,
                    Notification.NotificationStatus.SENT
            );

            logger.info(
                    "SMS notification sent for order {}",
                    order.getId()
            );

        } catch (Exception e) {

            saveNotification(
                    order,
                    Notification.Channel.SMS,
                    messageText,
                    Notification.NotificationStatus.FAILED
            );

            logger.warn(
                    "SMS notification failed for order {}: {}",
                    order.getId(),
                    e.getMessage()
            );
        }
    }

    private void saveNotification(
            Order order,
            Notification.Channel channel,
            String messageText,
            Notification.NotificationStatus status) {

        Notification notification = Notification.builder()
                .order(order)
                .customer(order.getCustomer())
                .type(Notification.NotificationType.ORDER_STATUS)
                .channel(channel)
                .message(messageText)
                .status(status)
                .sentAt(
                        status == Notification.NotificationStatus.SENT
                                ? LocalDateTime.now()
                                : null
                )
                .build();

        notificationRepository.save(notification);
    }
}
