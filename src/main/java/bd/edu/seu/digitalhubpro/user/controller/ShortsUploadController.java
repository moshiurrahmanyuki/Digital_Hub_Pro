package bd.edu.seu.digitalhubpro.user.controller;

import bd.edu.seu.digitalhubpro.user.model.ShortVideo;
import bd.edu.seu.digitalhubpro.user.service.ShortVideoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Controller
public class ShortsUploadController {
    private final ShortVideoService shortVideoService;

    public ShortsUploadController(ShortVideoService shortVideoService) {
        this.shortVideoService = shortVideoService;
    }

    @GetMapping("/shorts/upload")
    public String uploadForm(Model model) {
        model.addAttribute("shortVideo", new ShortVideo());
        return "user/uploadShort";
    }

    @PostMapping("/shorts/upload")
    public String handleUpload(@RequestParam("file") MultipartFile file,
                               @RequestParam(value = "title", required = false) String title) throws IOException {
        if (file == null || file.isEmpty()) {
            return "redirect:/shorts/upload?error=nofile";
        }
        ShortVideo sv = new ShortVideo();
        sv.setTitle(title != null ? title : file.getOriginalFilename());
        sv.setUploadDate(LocalDateTime.now());
        ShortVideo saved = shortVideoService.save(sv);
        String fileName = file.getOriginalFilename();
        String dir = "Upload_Videos/shorts/" + saved.getId();
        Path p = Paths.get(dir);
        Files.createDirectories(p);
        Files.copy(file.getInputStream(), p.resolve(fileName));
        saved.setVideoUrl("Upload_Videos/shorts/" + saved.getId() + "/" + fileName);
        shortVideoService.save(saved);
        // Old: redirected to /shorts?id={id} which controller does not handle → videoItem stayed null
        // New: use path variable route handled by ShortsVideosController: GET /shorts/{id}
        return "redirect:/shorts/" + saved.getId();
    }
}


