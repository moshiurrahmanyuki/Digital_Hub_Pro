package bd.edu.seu.digitalhubpro.authentication.controller;



import bd.edu.seu.digitalhubpro.authentication.dto.MemberDto;
import bd.edu.seu.digitalhubpro.authentication.model.Member;
 import bd.edu.seu.digitalhubpro.authentication.repository.MemberRepository;
import bd.edu.seu.digitalhubpro.authentication.service.MemberService;
import bd.edu.seu.digitalhubpro.configuration.MemberUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @Autowired
    private MemberRepository memberRepository;
    private final MemberService memberService;

    public LoginController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping({"/login"})
    public String login(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            Object principal = authentication.getPrincipal();
            Member loggedInUser = null;
            if (principal instanceof MemberUserDetails) {
                MemberUserDetails userDetails = (MemberUserDetails)principal;
                loggedInUser = userDetails.getMember();
            } else if (principal instanceof String) {
                String email = (String)principal;
                loggedInUser = (Member)this.memberRepository.findByEmail(email).orElse(null);
                if (loggedInUser == null) {
                    model.addAttribute("error", "User not found");
                    return "authentication/login";
                }
            }

            model.addAttribute("loggedInUser", loggedInUser);
            return "redirect:/home";
        } else {
            model.addAttribute("member", new MemberDto());
            return "authentication/login";
        }
    }
}
