package bd.edu.seu.digitalhubpro.user.controller;

import bd.edu.seu.digitalhubpro.authentication.model.Member;
import bd.edu.seu.digitalhubpro.authentication.service.MemberService;
import bd.edu.seu.digitalhubpro.user.model.Video;
import bd.edu.seu.digitalhubpro.user.service.VideoService;
import bd.edu.seu.digitalhubpro.user.model.ShortVideo;
import bd.edu.seu.digitalhubpro.user.service.ShortVideoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Controller
public class ShortsVideosController {
    private final VideoService videoService;
    private final MemberService memberService;
    private final ShortVideoService shortVideoService;

    public ShortsVideosController(VideoService videoService, MemberService memberService, ShortVideoService shortVideoService) {
        this.videoService = videoService;
        this.memberService = memberService;
        this.shortVideoService = shortVideoService;
    }

    @GetMapping({"/shorts/{id}"})
    public String showSpecificShortsVideo(@PathVariable("id") String id, @RequestParam(value = "query", required = false) String searchQuery, Model model) {
        // Old: Used Video model and required searchQuery parameter
        // New: Use ShortVideo model and make searchQuery optional
        Optional<ShortVideo> shortVideoOptional = this.shortVideoService.getShortVideoById(id);
        if (shortVideoOptional.isPresent()) {
            ShortVideo shortVideoItem = shortVideoOptional.get();
            this.shortVideoService.incrementViewCount(id);
            model.addAttribute("videoItem", shortVideoItem);
        } else {
            model.addAttribute("videoItem", (Object)null);
        }

        Member loggedInUser = this.memberService.getLoggedInUser();
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("searchQuery", searchQuery);
        return "user/shortsVideos";
    }

    @GetMapping({"/shorts"})
    public String showShortsFeed(Model model, @RequestParam(value = "query",required = false) String searchQuery) {
        List<ShortVideo> allShorts = this.shortVideoService.getShortsVideos(200L);
        if (!allShorts.isEmpty()) {
            int randomIndex = ThreadLocalRandom.current().nextInt(0, allShorts.size());
            ShortVideo randomShort = (ShortVideo)allShorts.get(randomIndex);
            this.shortVideoService.incrementViewCount(randomShort.getId());
            model.addAttribute("videoItem", randomShort);
        } else {
            model.addAttribute("videoItem", (Object)null);
        }

        Member loggedInUser = this.memberService.getLoggedInUser();
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("searchQuery", searchQuery);
        return "user/shortsVideos";
    }
}
