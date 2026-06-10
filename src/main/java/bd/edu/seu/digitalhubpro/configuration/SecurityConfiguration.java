package bd.edu.seu.digitalhubpro.configuration;

import bd.edu.seu.digitalhubpro.authentication.service.MemberUserdetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfiguration {

    @Autowired
    private MemberUserdetailsService memberUserdetailsService;

    @Autowired
    private MemberLoginSuccessHandler memberLoginSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(daoAuthenticationProvider());

        http.authorizeHttpRequests(authorize -> authorize
                        // Publicly accessible paths
                        .requestMatchers("/authentication/**", "/Images/**", "/user/**", "/video_details/**").permitAll()
                        .requestMatchers("/register", "/login", "/home", "/member-list", "/member/**", "/add-video", "/list-videos", "/shorts", "/shorts/").permitAll()

                        // Admin specific paths
                        .requestMatchers("/admin").hasAnyRole("ADMIN", "FACULTY")

                        // Any other requests need authentication
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .failureUrl("/login?error=true")
                        .successHandler(memberLoginSuccessHandler) // এই সাকসেস হ্যান্ডলারটি ভালো করে চেক করবেন
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(memberUserdetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}