package com.knguyendev.api.config.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Expecting this configuration value to be in the 'application.yml'
    @Value(value="${spring.custom.maxSession}")
    private int maxSession;

    // Expecting these four components to be obtained from beans injected outside of this
    private final RedisIndexedSessionRepository redisIndexedSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationEntryPoint authEntryPoint;
    private final UserDetailsService userDetailsService;
    private final LogoutHandler logoutHandler;


    public SecurityConfig(
            RedisIndexedSessionRepository redisIndexedSessionRepository,
            PasswordEncoder passwordEncoder,
            @Qualifier(value="customAuthEntryPoint") AuthenticationEntryPoint authEntryPoint,
            @Qualifier(value="customUserDetailsService") UserDetailsService userDetailsService,
            LogoutHandler logoutHandler
    ) {
        this.redisIndexedSessionRepository = redisIndexedSessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.authEntryPoint = authEntryPoint;
        this.userDetailsService = userDetailsService;
        this.logoutHandler = logoutHandler;
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

    /**
     * Creates a SessionRegistry bean. This component keeps track of active user sessions as it maintains an active 'registry' of
     * the active sessions and their associated principals (the users in this case). In this case, we're using the
     * 'Spring-backed' version, which integrates with Spring Session and the ability to store session date in various stores
     * such as Redis, JDBC, etc.
     * NOTE: Tracks session managed by Spring Session and stored in Redis.
     */
    @Bean
    public SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry() {
        return new SpringSessionBackedSessionRegistry<>(this.redisIndexedSessionRepository);
    }

    /*
     * A SecurityContextRepository implementation which the data of authenticated user in session when they log in.
     * More specifically it stores the security context in a HttpSession.
     *
     * With this, the SecurityContext holds the authentication/user's details, and that is also the data we store in the
     * session. So this SecurityContext, which contains our Authentication object will be our session data.
     * So when users log in, their authentication details are saved in the HTTP session. Then during each
     * request, the SecurityContext is retrieved from the session, and set in the SecurityContextHolder.
     */
    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    /**
     * Method for creating a bean of the 'SecurityFilterChain', which is responsible for defining how we process requests
     * in our application.
     * @param http Class that allows us to configure security settings for how we would deal with a http request.
     * @return A configured SecurityFilterChain bean
     * @throws Exception if any error occurs during the configuration of the chain.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable) // Disable csrf for now to focus on actual app features; later you'd probably enable it
                .cors(Customizer.withDefaults()) // Apply default settings; this will use our settings defined in 'CorsConfig'
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/api/v1/auth/**").permitAll(); // All routes that start with '/auth' don't need authentication to access

                    // Allow preflight requests to be sent without needing authentication. I think this is necessary?
                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

                    auth.anyRequest().authenticated(); // Require authentication for all other routes
                }).sessionManagement(sessionManagement -> sessionManagement
                        /*
                         * Session will be created when one doesn't already exist, or when it's required. Times when
                         * it'll be required are when Spring Security needs to store a user's authentication information
                         * which can happen when a user logs in. Or if the app uses session management operations such as
                         * storing session attributes, storing CSRF tokens, etc. A session will be created when those
                         * operations are performed.
                         */
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        // New session is created after user logs in. This is the idea of 'session fixation' and it helps against session fixation attacks
                        // Creating a new session on login will invalidate any pre-existing sessions.
                        .sessionFixation(SessionManagementConfigurer.SessionFixationConfigurer::newSession)
                        // Define the maximum number of session that a user can have (tracking done in SessionRegistry). If maximum number is exceeded, then the oldest session is invalidated.
                        .maximumSessions(maxSession)
                        .sessionRegistry(sessionRegistry())
                )
                // Pass in the 'AuthenticationEntryPoint' component that'll handle the AuthenticationExceptions
                .exceptionHandling((ex) -> ex.authenticationEntryPoint(authEntryPoint))

                /*
                 * I put custom session invalidation logic and cookie deletion logic in the 'LogoutHandlerImpl'
                 * but as soon as I comment out the 'invalidateHttpSession()' and 'deleteCookies()' method it tells me I need
                 * Like it says I'm not authenticated anymore after commenting out two lines.
                 */
                .logout(out -> out
                        .logoutUrl("/api/v1/auth/logout")
                        .addLogoutHandler(this.logoutHandler)
                        .logoutSuccessHandler(new LogoutSuccessHandlerImpl())
                )
                .build();
    }
}
