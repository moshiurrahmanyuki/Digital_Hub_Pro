package bd.edu.seu.digitalhubpro.configuration;


import bd.edu.seu.digitalhubpro.authentication.model.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class MemberUserDetails implements UserDetails {
    private final Member member;

    public MemberUserDetails(Member member) {
        this.member = member;
    }

    public String getName() {
        return this.member.getName();
    }

    public Member getMember() {
        return this.member;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    public String getPassword() {
        return this.member.getPassword();
    }

    public String getUsername() {
        return this.member.getEmail();
    }
}
