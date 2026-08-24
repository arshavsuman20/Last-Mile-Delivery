package com.lastmile.delivery.config;

import com.lastmile.delivery.entity.*;
import com.lastmile.delivery.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;
    private final ZoneRepository zoneRepository;
    private final AreaRepository areaRepository;
    private final RateCardRepository rateCardRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            DeliveryAgentRepository deliveryAgentRepository,
            ZoneRepository zoneRepository,
            AreaRepository areaRepository,
            RateCardRepository rateCardRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.deliveryAgentRepository = deliveryAgentRepository;
        this.zoneRepository = zoneRepository;
        this.areaRepository = areaRepository;
        this.rateCardRepository = rateCardRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Seed Zones
        if (zoneRepository.count() == 0) {
            Zone north = zoneRepository.save(Zone.builder().name("North Zone").description("North Logistics Sector").build());
            Zone south = zoneRepository.save(Zone.builder().name("South Zone").description("South Logistics Sector").build());
            Zone central = zoneRepository.save(Zone.builder().name("Central Zone").description("Central Metro Core").build());

            // Seed Areas
            areaRepository.save(Area.builder().name("North Suburbs").zone(north).build());
            areaRepository.save(Area.builder().name("Tech Park North").zone(north).build());
            areaRepository.save(Area.builder().name("South Industrial").zone(south).build());
            areaRepository.save(Area.builder().name("Westside Heights").zone(south).build());
            areaRepository.save(Area.builder().name("Downtown Core").zone(central).build());
            areaRepository.save(Area.builder().name("Midtown Financial").zone(central).build());
        }

        // Seed Rate Cards
        if (rateCardRepository.count() == 0) {
            rateCardRepository.save(RateCard.builder()
                    .orderType(RateCard.OrderType.B2C)
                    .intraZoneRatePerKg(new BigDecimal("40.00"))
                    .interZoneRatePerKg(new BigDecimal("80.00"))
                    .codSurcharge(new BigDecimal("50.00"))
                    .build());

            rateCardRepository.save(RateCard.builder()
                    .orderType(RateCard.OrderType.B2B)
                    .intraZoneRatePerKg(new BigDecimal("30.00"))
                    .interZoneRatePerKg(new BigDecimal("60.00"))
                    .codSurcharge(new BigDecimal("35.00"))
                    .build());
        }

        // Seed Default Users & Agents
        if (userRepository.count() == 0) {
            String sharedPassword = passwordEncoder.encode("password123");

            // Admin
            userRepository.save(User.builder()
                    .name("System Admin")
                    .email("admin@lastmile.com")
                    .phone("9998887770")
                    .password(sharedPassword)
                    .role(User.Role.ADMIN)
                    .build());

            // Customer
            userRepository.save(User.builder()
                    .name("John Customer")
                    .email("customer@lastmile.com")
                    .phone("9876543210")
                    .password(sharedPassword)
                    .role(User.Role.CUSTOMER)
                    .build());

            // Delivery Agent 1
            User agent1User = userRepository.save(User.builder()
                    .name("Alex Agent (North)")
                    .email("agent1@lastmile.com")
                    .phone("9876543211")
                    .password(sharedPassword)
                    .role(User.Role.DELIVERY_AGENT)
                    .build());

            Zone northZone = zoneRepository.findAll().stream()
                    .filter(z -> z.getName().contains("North"))
                    .findFirst()
                    .orElse(zoneRepository.findAll().get(0));

            deliveryAgentRepository.save(DeliveryAgent.builder()
                    .user(agent1User)
                    .zone(northZone)
                    .available(true)
                    .latitude(12.9716)
                    .longitude(77.5946)
                    .build());

            // Delivery Agent 2
            User agent2User = userRepository.save(User.builder()
                    .name("Sam Agent (Central)")
                    .email("agent2@lastmile.com")
                    .phone("9876543212")
                    .password(sharedPassword)
                    .role(User.Role.DELIVERY_AGENT)
                    .build());

            Zone centralZone = zoneRepository.findAll().stream()
                    .filter(z -> z.getName().contains("Central"))
                    .findFirst()
                    .orElse(zoneRepository.findAll().get(0));

            deliveryAgentRepository.save(DeliveryAgent.builder()
                    .user(agent2User)
                    .zone(centralZone)
                    .available(true)
                    .latitude(12.9352)
                    .longitude(77.6245)
                    .build());
        }
    }
}
