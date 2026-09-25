package com.smartcityexplorer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartcityexplorer.entity.Experience;
import com.smartcityexplorer.repository.ExperienceRepository;
import java.util.Arrays;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class SeedConfig {
    @Bean
    CommandLineRunner seed(ExperienceRepository repository, ObjectMapper mapper) {
        return args -> {
            if (repository.count() == 0) {
                try (var stream = new ClassPathResource("experiences.json").getInputStream()) {
                    repository.saveAll(Arrays.asList(mapper.readValue(stream, Experience[].class)));
                }
            }
        };
    }
}
