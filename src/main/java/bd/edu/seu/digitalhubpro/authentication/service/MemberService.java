package bd.edu.seu.digitalhubpro.authentication.service;


import bd.edu.seu.digitalhubpro.authentication.model.Member;
import bd.edu.seu.digitalhubpro.authentication.repository.MemberRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member save(Member member) {
        return (Member)this.memberRepository.save(member);
    }

    public Optional<Member> findById(String id) {
        return this.memberRepository.findById(id);
    }

    public List<Member> getAllMembers() {
        return this.memberRepository.findAll();
    }

    public void deleteById(String id) {
        this.memberRepository.deleteById(id);
    }

    public Member findByEmail(String email) {
        return (Member)this.memberRepository.findByEmail(email).orElse(null);
    }

    public Member updateMember(Member member) {
        return (Member)this.memberRepository.save(member);
    }

    public Member getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                String email = ((User)principal).getUsername();
                return (Member)this.memberRepository.findByEmail(email).orElse(null);
            } else if (principal instanceof String) {
                String email = (String)principal;
                return (Member)this.memberRepository.findByEmail(email).orElse(null);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}

