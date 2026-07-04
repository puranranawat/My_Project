package com.devsecops.ems.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration to enable JPA auditing.
 * <p>
 * Supports automatic population of {@code @CreatedDate} and
 * {@code @LastModifiedDate} annotated fields in entity classes.
 */
@Configuration
@EnableJpaAuditing
public class AuditConfig {

    public AuditConfig() {
        // Default constructor for Spring bean creation
    }

}
