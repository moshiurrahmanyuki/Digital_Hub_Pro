package bd.edu.seu.digitalhubpro.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MemberLoginSuccessHandler implements AuthenticationSuccessHandler {

    public MemberLoginSuccessHandler() {
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("Inside onAuthenticationSuccess ");
        MemberUserDetails userDetails = (MemberUserDetails) authentication.getPrincipal();
        HttpSession session = request.getSession();
        session.setAttribute("name", userDetails.getName());
        session.setAttribute("email", userDetails.getUsername());
        session.setAttribute("loggedInUser", userDetails);

        response.sendRedirect(request.getContextPath() + "/home");
    }
}