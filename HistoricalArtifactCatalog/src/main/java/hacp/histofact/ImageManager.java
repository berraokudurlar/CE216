package hacp.histofact;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ImageManager {
    private static final String IMAGE_DIRECTORY = "images";

    // Singleton if needed (optional)
    private static ImageManager instance = new ImageManager();
    public static ImageManager getInstance() {
        return instance;
    }

    private ImageManager() {
        File dir = new File(IMAGE_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }


    public String saveImage(File sourceImage, String artifactId) {
        if (sourceImage == null || !sourceImage.exists()) {
            return null;
        }

        String extension = getFileExtension(sourceImage.getName());
        if (extension == null) extension = "jpg"; // fallback

        String targetFileName = artifactId + "." + extension;
        File targetFile = new File(IMAGE_DIRECTORY, targetFileName);

        try {
            Files.copy(sourceImage.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return "/" + IMAGE_DIRECTORY + "/" + targetFileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

   
    public File getImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        File file = new File("." + imagePath); // prepend current dir
        return file.exists() ? file : null;
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex > 0 && dotIndex < filename.length() - 1) ?
                filename.substring(dotIndex + 1) : null;
    }
}
