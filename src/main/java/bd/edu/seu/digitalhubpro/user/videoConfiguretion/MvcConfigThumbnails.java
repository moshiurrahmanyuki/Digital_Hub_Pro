package bd.edu.seu.digitalhubpro.user.videoConfiguretion;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfigThumbnails implements WebMvcConfigurer {
    public MvcConfigThumbnails() {
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Old: included trailing slash which could cause pattern "/Upload_Thumbnails//**" not to match
        // String dirName = "Upload_Thumbnails/";
        String dirName = "Upload_Thumbnails";
        Path videoDir = Paths.get(dirName);
        String videoPath = videoDir.toFile().getAbsolutePath();
        registry.addResourceHandler(new String[]{"/" + dirName + "/**"}).addResourceLocations(new String[]{"file:" + videoPath + "/"});
    }
}
