package bd.edu.seu.digitalhubpro.user.videoConfiguretion;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

public class VideoFileUploadService {
    public VideoFileUploadService() {
    }

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath, new LinkOption[0])) {
            Files.createDirectories(uploadPath);
        }

        try {
            try (InputStream inputStream = multipartFile.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
            }

        } catch (IOException exception) {
            throw new IOException("Could not save file " + fileName, exception);
        }
    }
}

