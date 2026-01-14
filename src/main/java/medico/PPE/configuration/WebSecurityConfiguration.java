package medico.PPE.configuration;

import medico.PPE.Services.DoctorateServiceImpl;
import medico.PPE.Services.UserDetailsServiceImpl;
import medico.PPE.filters.CustomerJwtRequestFilter;
import medico.PPE.filters.DocteurJwtRequestFilter;
import medico.PPE.jwt.CustomerServiceImpl;

import org.mapstruct.Qualifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration {

    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final CustomerJwtRequestFilter customerFilter;
    private final DocteurJwtRequestFilter docteurFilter;
    private final DoctorateServiceImpl docteurService;
    private final CustomerServiceImpl customerservice;

    @Autowired
    public WebSecurityConfiguration(DoctorateServiceImpl docteurService,
            CustomerServiceImpl customerservice, UserDetailsServiceImpl userDetailsServiceImpl,
            CustomerJwtRequestFilter customerFilter, DocteurJwtRequestFilter docteurFilter) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.customerFilter = customerFilter;
        this.docteurFilter = docteurFilter;
        this.docteurService = docteurService;
        this.customerservice = customerservice;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // ============================================
                        // 🌐 ROUTES PUBLIQUES
                        // ============================================
                        .requestMatchers(
                                "/signup/**",
                                "/login/**",
                                "/docteur/login/**",
                                "/error")
                        .permitAll()

                        // ============================================
                        // APPOINTMENT ROUTES - Protégées
                        // ============================================
                        // .requestMatchers(HttpMethod.GET, "/appointment").hasAnyRole("USER", "DOCTOR")
                        // .requestMatchers(HttpMethod.GET, "/appointment/get/**").hasAnyRole("USER", "DOCTOR")
                        // .requestMatchers(HttpMethod.POST, "/appointment/add").hasRole("USER")
                        // .requestMatchers(HttpMethod.DELETE, "/appointment/delete/**").hasAnyRole("USER", "DOCTOR")
                        // .requestMatchers(HttpMethod.PUT, "/appointment/*/validate").hasRole("DOCTOR")
                        // .requestMatchers(HttpMethod.PUT, "/appointment/*/reject").hasRole("DOCTOR")
                        // .requestMatchers(HttpMethod.PUT, "/appointment/*/start").hasRole("DOCTOR")
                        .requestMatchers("api/conseils/**").permitAll()

                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(customerFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(docteurFilter, CustomerJwtRequestFilter.class)

                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * ✅ Configuration CORS explicite et sécurisée
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origines autorisées
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:4200"));

        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers autorisés
        configuration.setAllowedHeaders(List.of("*"));

        // Credentials autorisés
        configuration.setAllowCredentials(true);

        // Max age
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsServiceImpl)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }
   @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsServiceImpl);
        provider.setPasswordEncoder(passwordEncoder());
        // ✅ IMPORTANT : Désactiver la gestion des exceptions pour éviter les retentatives
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

}
