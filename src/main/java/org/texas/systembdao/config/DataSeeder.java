package org.texas.systembdao.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.texas.systembdao.entity.DaoCitizenPartial;
import org.texas.systembdao.entity.Role;
import org.texas.systembdao.entity.User;
import org.texas.systembdao.repository.DaoCitizenRepository;
import org.texas.systembdao.repository.RoleRepository;
import org.texas.systembdao.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedDaoData(RoleRepository roleRepo,
                                  UserRepository userRepo,
                                  DaoCitizenRepository daoCitizenRepo,
                                  PasswordEncoder passwordEncoder) {
        return args -> {

            // -------- Roles (shared table; only create if missing) --------
            for (String name : List.of("WARD_OFFICER", "CITIZEN", "DAO_OFFICER", "DPO")) {
                roleRepo.findByName(name).orElseGet(() ->
                        roleRepo.save(Role.builder().name(name).build()));
            }

            // -------- DAO Officer user --------
            seedUser(userRepo, roleRepo, passwordEncoder,
                    "dao@example.com", "DAO Officer", "DAO_OFFICER");

            // -------- Sample DAO citizens (partial records) --------
            if (daoCitizenRepo.count() == 0) {
                seedDaoCitizens(daoCitizenRepo);
                log.info("Seeded 5 sample DAO citizens (partial records).");
            }

            log.info("System B DataSeeder completed.");
        };
    }

    private void seedUser(UserRepository userRepo,
                          RoleRepository roleRepo,
                          PasswordEncoder encoder,
                          String username,
                          String fullName,
                          String roleName) {

        if (userRepo.existsByUsername(username)) {
            return;
        }

        Role role = roleRepo.findByName(roleName).orElseThrow();

        User user = User.builder()
                .username(username)
                .password(encoder.encode("password123"))
                .fullName(fullName)
                .email(username)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .roles(Set.of(role))
                .build();

        userRepo.save(user);
        log.info("Created user: {} ({})", username, roleName);
    }

    /**
     * The DAO has partial data — full name, address, parents.
     * DOB is null until fetched from System A or entered manually.
     */
    private void seedDaoCitizens(DaoCitizenRepository repo) {
        repo.saveAll(List.of(
                buildDaoCitizen("1234567890", "Ram Bahadur Shrestha",
                        "Kathmandu-10", "Hari Bahadur Shrestha / Sita Shrestha"),
                buildDaoCitizen("1234567891", "Sita Kumari Thapa",
                        "Lalitpur-05", "Purna Bahadur Thapa / Gita Thapa"),
                buildDaoCitizen("1234567892", "Bikash Gurung",
                        "Pokhara-08", "Dhan Bahadur Gurung / Maya Gurung"),
                buildDaoCitizen("1234567893", "Anita Maharjan",
                        "Bhaktapur-03", "Ram Krishna Maharjan / Laxmi Maharjan"),
                buildDaoCitizen("1234567894", "Suresh Tamang",
                        "Kathmandu-15", "Kaji Man Tamang / Phul Maya Tamang")
        ));
    }

    private DaoCitizenPartial buildDaoCitizen(String nid, String name,
                                              String address, String parents) {
        return DaoCitizenPartial.builder()
                .nid(nid)
                .fullName(name)
                .address(address)
                .parentsNames(parents)
                .dob(null)           // ← intentionally null; fetched later
                .dobSource(null)
                .createdAt(LocalDateTime.now())
                .build();
    }
}