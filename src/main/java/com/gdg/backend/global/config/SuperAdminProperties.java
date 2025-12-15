package com.gdg.backend.global.config;

import com.gdg.backend.user.domain.Provider;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "super-admin")
public class SuperAdminProperties {
    Provider provider;
    String providerId;
}
