package com.knguyendev.TravelApp.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * This class enables 'method validation' globally. This just means we can plan custom
 * created validation annotations in our method parameters
 *
 */
@Configuration
@Validated
public class MethodValidationConfig {
}
