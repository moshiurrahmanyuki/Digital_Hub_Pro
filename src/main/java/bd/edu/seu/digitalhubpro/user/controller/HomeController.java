package bd.edu.seu.digitalhubpro.user.controller;


import bd.edu.seu.digitalhubpro.authentication.model.Member;
import bd.edu.seu.digitalhubpro.authentication.repository.MemberRepository;
import bd.edu.seu.digitalhubpro.authentication.service.MemberService;
import bd.edu.seu.digitalhubpro.configuration.MemberUserDetails;
import bd.edu.seu.digitalhubpro.user.dto.SearchRequest;
import bd.edu.seu.digitalhubpro.user.model.TimeAgoConverter;
import bd.edu.seu.digitalhubpro.user.model.Video;
import bd.edu.seu.digitalhubpro.user.service.VideoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
public class HomeController {
    private final MemberService memberService;
    private final VideoService videoService;
    private final MemberRepository memberRepository;
    private final TimeAgoConverter timeAgoConverter;

    public HomeController(MemberService memberService, VideoService videoService, MemberRepository memberRepository, TimeAgoConverter timeAgoConverter) {
        this.memberService = memberService;
        this.videoService = videoService;
        this.memberRepository = memberRepository;
        this.timeAgoConverter = timeAgoConverter;
    }

    @GetMapping({"/home"})
    public String home(Model model, @RequestParam(value = "search",required = false) String searchParam) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            Member loggedInUser = null;
            if (principal instanceof MemberUserDetails) {
                MemberUserDetails userDetails = (MemberUserDetails)principal;
                loggedInUser = userDetails.getMember();
            } else if (principal instanceof String) {
                String email = (String)principal;
                loggedInUser = (Member)this.memberRepository.findByEmail(email).orElse(null);
            }

            model.addAttribute("loggedInUser", loggedInUser);
            List<Video> videos = this.videoService.getAllVideos();

            for(Video video : videos) {
                video.setTimeAgo(this.timeAgoConverter.convert(video.getUploadDate()));
            }

            List<Video> video;
            SearchRequest searchRequest;
            if (searchParam != null && !searchParam.trim().isEmpty()) {
                searchRequest = new SearchRequest(searchParam);
                video = this.videoService.getVideoSearch(searchRequest);
            } else {
                searchRequest = new SearchRequest("");
                video = Collections.emptyList();
            }

            model.addAttribute("searchRequest", searchRequest);
            model.addAttribute("videos", video);
            model.addAttribute("videos", videos);
            return "user/home";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping({"/search"})
    public String searchVideos(@ModelAttribute SearchRequest dto, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("search", dto.searchText());
        return "redirect:/home";
    }
}

