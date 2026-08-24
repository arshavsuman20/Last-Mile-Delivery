package com.lastmile.delivery.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class SmsService {

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.auth-token:}")
    private String authToken;

    @Value("${twilio.phone-number:}")
    private String fromNumber;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public void sendSms(String to, String message) {

        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Customer phone number is missing");
        }

        if (accountSid.isBlank() || authToken.isBlank() || fromNumber.isBlank()) {
            throw new IllegalStateException("Twilio configuration is missing");
        }

        String url = "https://api.twilio.com/2010-04-01/Accounts/"
                + accountSid + "/Messages.json";

        String body =
                "To=" + encode(to) +
                "&From=" + encode(fromNumber) +
                "&Body=" + encode(message);

        String credentials = Base64.getEncoder()
                .encodeToString(
                        (accountSid + ":" + authToken)
                                .getBytes(StandardCharsets.UTF_8)
                );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Basic " + credentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try {
            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (response.statusCode() < 200 ||
                    response.statusCode() >= 300) {
                throw new IllegalStateException(
                        "Twilio SMS failed: HTTP " + response.statusCode()
                );
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("SMS sending interrupted", e);
        } catch (Exception e) {
            throw new IllegalStateException("SMS sending failed", e);
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
