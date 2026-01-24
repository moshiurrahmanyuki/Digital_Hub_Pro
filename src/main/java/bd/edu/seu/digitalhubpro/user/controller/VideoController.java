package bd.edu.seu.digitalhubpro.user.controller;

import bd.edu.seu.digitalhubpro.authentication.model.Member;
import bd.edu.seu.digitalhubpro.authentication.repository.MemberRepository;
import bd.edu.seu.digitalhubpro.authentication.service.MemberService;
import bd.edu.seu.digitalhubpro.configuration.MemberUserDetails;
import bd.edu.seu.digitalhubpro.user.model.Video;
import bd.edu.seu.digitalhubpro.user.service.VideoService;
import bd.edu.seu.digitalhubpro.user.videoConfiguretion.ThumbnailsFileUploadService;
import bd.edu.seu.digitalhubpro.user.videoConfiguretion.VideoFileUploadService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Controller
public class VideoController {
    private final VideoService videoService;
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    public VideoController(VideoService videoService, MemberService memberService, MemberRepository memberRepository) {
        this.videoService = videoService;
        this.memberService = memberService;
        this.memberRepository = memberRepository;
    }

    @PostMapping({"/upload"})
    public String save(@ModelAttribute("video") @Valid Video video, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes, @RequestParam("videoFile") MultipartFile multipartFile, @RequestParam(value = "thumbnailFile",required = false) MultipartFile thumbnailFile) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("videos", this.videoService.getAllVideos());
            model.addAttribute("thumbnail", this.videoService.getAllVideos());
            model.addAttribute("video", video);
            return "user/addVideos";
        } else if (!multipartFile.isEmpty() && multipartFile.getSize() != 0L) {
            if (!multipartFile.isEmpty()) {
                String fileName = StringUtils.cleanPath((String) Objects.requireNonNull(multipartFile.getOriginalFilename()));
                video.setVideoUrl(fileName);
                video.setViewCount(0L);
                // Set uploader/channel for watch page and subscriptions
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                    Object principal = auth.getPrincipal();
                    if (principal instanceof MemberUserDetails mud && mud.getMember() != null) {
                        video.setUploaderId(mud.getMember().getId());
                        video.setUserId(mud.getMember().getId());
                    } else if (principal instanceof String email) {
                        this.memberRepository.findByEmail(email).ifPresent(m -> {
                            video.setUploaderId(m.getId());
                            video.setUserId(m.getId());
                        });
                    }
                }
                Video savedVideo = this.videoService.save(video, multipartFile);
                String uploadDir = "Upload_Videos/" + savedVideo.getId();
                VideoFileUploadService.saveFile(uploadDir, fileName, multipartFile);
                if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
                    String thumbnailFileName = StringUtils.cleanPath((String)Objects.requireNonNull(thumbnailFile.getOriginalFilename()));
                    video.setThumbnailUrl(thumbnailFileName);
                    this.videoService.save(video, multipartFile);
                    String thumbnailUploadDir = "Upload_Thumbnails/" + savedVideo.getId();
                    ThumbnailsFileUploadService.saveFile(thumbnailUploadDir, thumbnailFileName, thumbnailFile);
                }

                redirectAttributes.addFlashAttribute("message", new Message("success", "Video uploaded successfully!"));
                return "redirect:/list-video";
            } else {
                bindingResult.rejectValue("videoPath", "error.video", "Please select a video file.");
                model.addAttribute("videos", this.videoService.getAllVideos());
                model.addAttribute("video", video);
                return "user/addVideos";
            }
        } else {
            model.addAttribute("videos", this.videoService.getAllVideos());
            return "user/addVideos";
        }
    }

    @GetMapping({"/add-video"})
    public String listUploadedVideos(Model model) {
        model.addAttribute("video", new Video());
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
        }

        return "user/addVideos";
    }

    @GetMapping({"/list-video"})
    public String listVideos(Model model) {
        List<Video> videos = this.videoService.getAllVideos();
        model.addAttribute("videos", videos);
        model.addAttribute("searchQuery", "");
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
        }

        return "user/listVideos";
    }

    @GetMapping({"/delete/{id}"})
    public String deleteVideo(@PathVariable String id, RedirectAttributes redirectAttributes) {
        this.videoService.deleteByVideoById(id);
        redirectAttributes.addFlashAttribute("message", new Message("success", "Video deleted successfully."));
        return "redirect:/add-video";
    }

    public static class Message {
        private String type;
        private String text;

        public Message(String type, String text) {
            this.type = type;
            this.text = text;
        }

        public String getType() {
            return this.type;
        }

        public String getText() {
            return this.text;
        }
    }
}
