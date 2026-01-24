package bd.edu.seu.digitalhubpro.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    public MvcConfig() {
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String dirName = "member-photos";
        Path memberPhotosDir = Paths.get(dirName);
        String memberPhotosPath = memberPhotosDir.toFile().getAbsolutePath();
        registry.addResourceHandler(new String[]{"/" + dirName + "/**"}).addResourceLocations(new String[]{"file:" + memberPhotosPath + "/"});
    }
}
