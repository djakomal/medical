package medico.PPE.configuration;

import medico.PPE.Services.DoctorateServiceImpl;
import medico.PPE.Services.UserDetailsServiceImpl;
import medico.PPE.Services.ZoomMeetingService;
import medico.PPE.filters.CustomerJwtRequestFilter;
import medico.PPE.filters.DocteurJwtRequestFilter;
import medico.PPE.jwt.CustomerServiceImpl;
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
    private final ZoomMeetingService zoomservice;

    @Autowired
    public WebSecurityConfiguration(DoctorateServiceImpl docteurService,
            CustomerServiceImpl customerservice, UserDetailsServiceImpl userDetailsServiceImpl,
            CustomerJwtRequestFilter customerFilter, DocteurJwtRequestFilter docteurFilter,
            ZoomMeetingService zoomservice) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
        this.customerFilter = customerFilter;
        this.docteurFilter = docteurFilter;
        this.docteurService = docteurService;
        this.customerservice = customerservice;
        this.zoomservice = zoomservice;
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
                                "/ws/**",
                                "/error")
                        .permitAll()
                        .requestMatchers("/code-activation").permitAll()
                        .requestMatchers("/api/meetings/**").permitAll()
                        .requestMatchers("/api/meetings/authorize").permitAll()
                        .requestMatchers("/api/meetings/callback").permitAll()
                        .requestMatchers("/api/meetings/me").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("api/conseils/**").permitAll()
                        .requestMatchers("api/publication/**").permitAll()
                        .requestMatchers("/ws/**", "/ws/info/**").permitAll()
                        .requestMatchers("api/appointment/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/signup/docteur/all").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/creneaux/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/creneaux/**").authenticated()

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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:4200",
                "http://localhost:5173"));

        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"));

        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin"));

        //  CRUCIAL — doit être true
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //  Couvre aussi /ws/info, /ws/iframe etc.
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
        // ✅ IMPORTANT : Désactiver la gestion des exceptions pour éviter les
        // retentatives
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

}
