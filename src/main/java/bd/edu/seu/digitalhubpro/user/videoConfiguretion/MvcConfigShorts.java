package bd.edu.seu.digitalhubpro.user.videoConfiguretion;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfigShorts implements WebMvcConfigurer {
    public MvcConfigShorts() {
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String dirName = "Shorts";
        Path videoDir = Paths.get(dirName);
        String videoPath = videoDir.toFile().getAbsolutePath();
        System.out.println(">>> Short Video Resource Handler Path: " + videoPath);

        registry.addResourceHandler(new String[]{"/" + dirName + "/**"}).addResourceLocations(new String[]{"file:" + videoPath + "/"});
    }
}
