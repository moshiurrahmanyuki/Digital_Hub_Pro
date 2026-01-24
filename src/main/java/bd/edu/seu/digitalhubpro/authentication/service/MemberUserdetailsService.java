package bd.edu.seu.digitalhubpro.authentication.service;

import bd.edu.seu.digitalhubpro.authentication.model.Member;
import bd.edu.seu.digitalhubpro.authentication.repository.MemberRepository;
import bd.edu.seu.digitalhubpro.configuration.MemberUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MemberUserdetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    public MemberUserdetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = (Member)this.memberRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new MemberUserDetails(member);
    }
}
