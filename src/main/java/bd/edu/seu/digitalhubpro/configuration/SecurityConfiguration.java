package bd.edu.seu.digitalhubpro.configuration;

import bd.edu.seu.digitalhubpro.authentication.service.MemberUserdetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
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

    public SecurityConfiguration() {
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(this.daoAuthenticationProvider());
        return (SecurityFilterChain)http.authorizeHttpRequests((authorizeRequests) -> ((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)authorizeRequests.requestMatchers(new String[]{"/authentication/**", "/Images/**", "/user/**", "/video_details/**"})).permitAll().requestMatchers(new String[]{"/register", "/login", "/home", "/member-list", "/member/**", "/add-video", "/list-videos", "/shorts", "/shorts/"})).permitAll().requestMatchers(new String[]{"/admin"})).hasAnyRole(new String[]{"ADMIN", "FACULTY"}).anyRequest()).authenticated()).formLogin((form) -> ((FormLoginConfigurer)form.loginPage("/login").usernameParameter("email").passwordParameter("password").failureUrl("/login?error=true")).successHandler(this.memberLoginSuccessHandler)).logout((config) -> config.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")).logoutSuccessUrl("/login?logout=true").invalidateHttpSession(true).clearAuthentication(true).deleteCookies(new String[]{"JSESSIONID"}).permitAll()).build();
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
        provider.setUserDetailsService(this.memberUserdetailsService);
        provider.setPasswordEncoder(this.passwordEncoder());
        return provider;
    }
}
