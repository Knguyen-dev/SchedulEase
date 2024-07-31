package com.knguyendev.api.config.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Value(value="${spring.custom.maxSession}")
    private int maxSession;
    private final RedisIndexedSessionRepository redisIndexedSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationEntryPoint authEntryPoint;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(
            RedisIndexedSessionRepository redisIndexedSessionRepository,
            PasswordEncoder passwordEncoder,
            @Qualifier(value="customAuthEntryPoint") AuthenticationEntryPoint authEntryPoint,
            @Qualifier(value="customUserDetailsService") UserDetailsService userDetailsService
    ) {
        this.redisIndexedSessionRepository = redisIndexedSessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.authEntryPoint = authEntryPoint;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Method for creating the AuthenticationProvider component, which handles the logic of actually
     * authenticating a user, given their credentials (via a standardized token) UsernameAnd. It'll first try to find a user via their username.
     * If it finds a user, then it does a password comparison using the passwordEncoder to see if the plain-text password
     * matches up with the passwordHash (UserDetail.getPassword()) stored in the database.
     *
     * @return A DaoAuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(this.passwordEncoder);
        provider.setUserDetailsService(this.userDetailsService);
        return provider;
    }

    /**
     * Creates the bean for an 'AuthenticationManager' component
     * @return An 'AuthenticationManager'
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    @Bean
    public SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry() {
        return new SpringSessionBackedSessionRegistry<>(this.redisIndexedSessionRepository);
    }

    /** A SecurityContextRepository implementation which stores the security context in the HttpSession between requests. */
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // I haven't included the CSRF stuff yet, so I need to research that a little bit

                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/auth/register", "/auth/login").permitAll(); // Make the register and login routes accessible without needing authentication
                    auth.anyRequest().authenticated(); // Require authentication for all other routes
                }).sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession)
                        .maximumSessions(maxSession)
                        .sessionRegistry(sessionRegistry())
                )
                .exceptionHandling((ex) -> ex.authenticationEntryPoint(authEntryPoint))
                .logout(out -> out
                        .logoutUrl("/auth/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .addLogoutHandler(new LogoutHandlerImpl(redisIndexedSessionRepository))
                        .logoutSuccessHandler(
                                (request, response, authentication) -> SecurityContextHolder.clearContext()
                        )
                )
                .build();
    }






}
