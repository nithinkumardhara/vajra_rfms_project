package com.vajraiot.VJ_RLY_RFMS_REST_APIs.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "VJ_RLY_RFMS",
                description = "REST_API's for Document"
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8889"
                ),
                @Server(
                        description = "API's ENV",
                        url = "http://192.168.1.26:8889"
                ),
                @Server(
                        description = "Web server ENV",
                        url = "http://192.168.1.14:8889"
                )
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }

)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
@Configuration
public class SwaggerConfig {
}
