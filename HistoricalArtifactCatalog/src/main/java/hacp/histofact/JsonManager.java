package hacp.histofact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
@JsonIgnoreProperties(ignoreUnknown = true)
public class JsonManager {
    // Methods for handling JSON operations
    private static JsonManager uniqueInstance;
    private final ObjectMapper objectMapper;
    private File file;

    private JsonManager() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public static JsonManager getInstance(){
        if(uniqueInstance == null){
            uniqueInstance = new JsonManager();

        }
        return uniqueInstance;
    }

    public void setFile(File jsonFile) {
        this.file = jsonFile;
    }

    // Import a list of artifacts from a JSON file
    public ArrayList<Artifact> importArtifacts() {
        if(file == null){
            System.err.println("File not set for import.");
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(file, new TypeReference<ArrayList<Artifact>>() {});
        } catch (IOException e) {
            System.err.println("Error importing artifacts: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Export all artifacts to a JSON file
    public void exportArtifacts(ArrayList<Artifact> artifacts) {
        if(file == null){
            System.err.println("File not set for export.");
            return;
        }
        try {
            objectMapper.writeValue(file, artifacts);
        } catch (IOException e) {
            System.err.println("Error exporting artifacts: " + e.getMessage());
        }
    }

    // Append a single artifact to an existing file
   public void appendArtifactToFile(Artifact artifact) {
        ArrayList<Artifact> currentArtifacts = importArtifacts();
        currentArtifacts.add(artifact);
        exportArtifacts(currentArtifacts);
    }

}
