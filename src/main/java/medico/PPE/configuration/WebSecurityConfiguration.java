package medico.PPE.configuration;


import medico.PPE.Services.DoctorateServiceImpl;
import medico.PPE.filters.EmailFixingFilter;
import medico.PPE.filters.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration {

    private  final DoctorateServiceImpl docteurService;
    @Autowired
    private EmailFixingFilter emailFixingFilter;
    private final JwtRequestFilter jwtRequestFilter;

    @Autowired
    public WebSecurityConfiguration(DoctorateServiceImpl docteurService, JwtRequestFilter jwtRequestFilter) {
        this.docteurService = docteurService;
        this.jwtRequestFilter = jwtRequestFilter;
    }




    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security.cors().and().csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/signup", "/login","/docteur/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/medico/type").authenticated() // Authentification requise
                .anyRequest().permitAll() // Toutes les autres routes sont accessibles
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(emailFixingFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(docteurService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }


}
