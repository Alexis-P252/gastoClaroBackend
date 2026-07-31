package com.api1.demo.config;

import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Configuration
public class SpringDocSecurityConfig {

    // Le dice a springdoc: "cualquier parámetro anotado con @AuthenticationPrincipal
    // no es parte del contrato HTTP del endpoint, no lo documentes ni intentes
    // generarle un schema". Tiene que ejecutarse ANTES de que springdoc escanee
    // los controllers, por eso va en un bloque static.
    static {
        SpringDocUtils.getConfig().addAnnotationsToIgnore(AuthenticationPrincipal.class);
    }
}
