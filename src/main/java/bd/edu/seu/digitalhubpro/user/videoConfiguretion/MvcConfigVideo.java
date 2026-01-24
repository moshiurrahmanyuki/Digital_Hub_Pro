package bd.edu.seu.digitalhubpro.user.videoConfiguretion;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfigVideo implements WebMvcConfigurer {
    public MvcConfigVideo() {
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String dirName = "Upload_Videos";
        Path videoDir = Paths.get(dirName);
        String videoPath = videoDir.toFile().getAbsolutePath();
        registry.addResourceHandler(new String[]{"/" + dirName + "/**"}).addResourceLocations(new String[]{"file:" + videoPath + "/"});
    }
}
