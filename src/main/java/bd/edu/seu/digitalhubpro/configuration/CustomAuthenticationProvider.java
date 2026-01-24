package bd.edu.seu.digitalhubpro.configuration;


import bd.edu.seu.digitalhubpro.authentication.model.Member;
import bd.edu.seu.digitalhubpro.authentication.repository.MemberRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();
        Member member = (Member)this.memberRepository.findByEmail(email).get();
        if (member != null && this.passwordEncoder.matches(password, member.getPassword())) {
            List<GrantedAuthority> authorities = (List)member.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            System.out.println("User roles: " + String.valueOf(member.getRoles()));
            return new UsernamePasswordAuthenticationToken(email, (Object)null, authorities);
        } else {
            throw new UsernameNotFoundException("Invalid email or password");
        }
    }

    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

