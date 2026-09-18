package com.amar.resource_booking.config;

import com.amar.resource_booking.entity.Role;
import com.amar.resource_booking.entity.User;
import com.amar.resource_booking.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Value("${seed.admin.username:admin}")
    private String adminUsername;

    @Value("${seed.admin.password:admin}")
    private String adminPassword;

    @Value("${seed.user.username:user}")
    private String userUsername;

    @Value("${seed.user.password:password}")
    private String userPassword;

    @Bean
    CommandLineRunner createUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (userRepository.findByUsername(adminUsername).isEmpty()) {

                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setPassword(
                        passwordEncoder.encode(adminPassword)
                );
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);
            }

            if (userRepository.findByUsername(userUsername).isEmpty()) {

                User user = new User();
                user.setUsername(userUsername);
                user.setPassword(
                        passwordEncoder.encode(userPassword)
                );
                user.setRole(Role.USER);

                userRepository.save(user);
            }
        };
    }
}