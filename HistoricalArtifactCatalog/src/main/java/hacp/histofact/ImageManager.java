package hacp.histofact;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ImageManager {
    private static final String MAIN_DIRECTORY_NAME = "HistoricalArtifactZBE";
    private static final String IMAGES_DIRECTORY_NAME = "images";
    private static final File IMAGE_DIRECTORY;

    static {
        File baseDir = getDocumentsDirectory();
        File mainDir = new File(baseDir, MAIN_DIRECTORY_NAME);
        if (!mainDir.exists()) {
            mainDir.mkdirs();
        }

        IMAGE_DIRECTORY = new File(mainDir, IMAGES_DIRECTORY_NAME);
        if (!IMAGE_DIRECTORY.exists()) {
            IMAGE_DIRECTORY.mkdirs();
        }
    }


    // Singleton instance
    private static final ImageManager instance = new ImageManager();

    public static ImageManager getInstance() {
        return instance;
    }

    private ImageManager() {
        // Private constructor for singleton
    }

    public String saveImage(File sourceImage, String artifactId) {
        if (sourceImage == null || !sourceImage.exists()) {
            return null;
        }

        String extension = getFileExtension(sourceImage.getName());
        if (extension == null) extension = "jpg"; // fallback default

        String baseFileName = artifactId + "." + extension;
        File targetFile = new File(IMAGE_DIRECTORY, baseFileName);

        int counter = 1;
        while (targetFile.exists()) {
            targetFile = new File(IMAGE_DIRECTORY, artifactId + "_" + counter + "." + extension);
            counter++;
        }

        try {
            Files.copy(sourceImage.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return "/" + MAIN_DIRECTORY_NAME + "/" + IMAGES_DIRECTORY_NAME + "/" + targetFile.getName();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public File getImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        File file = new File(getDocumentsDirectory(), imagePath.startsWith("/") ? imagePath.substring(1) : imagePath);
        return file.exists() ? file : null;
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex > 0 && dotIndex < filename.length() - 1) ?
                filename.substring(dotIndex + 1) : null;
    }

    private static File getDocumentsDirectory() {
        String userHome = System.getProperty("user.home");
        File documentsDirectory = new File(userHome, "Documents");
        return documentsDirectory.exists() ? documentsDirectory : new File(userHome);
    }
}
