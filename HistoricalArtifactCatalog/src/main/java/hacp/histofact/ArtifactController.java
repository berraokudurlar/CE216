package hacp.histofact;

import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.time.LocalDate;


public class ArtifactController {
    private ArtifactCatalog catalog;

    public ArtifactController(ArtifactCatalog catalog) {
    }

    // Methods for artifact operations
    public void addArtifact(Artifact artifact) {
        /*Artifact artifact = new Artifact();
        artifact.setArtifactId(id
        artifact.setArtifactName(nameField.getText());
        artifact.setCategory();
        artifact.setCivilization();
        artifact.setDiscoveryLocation();
        artifact.setComposition();
        artifact.setDiscoveryDate();
        artifact.setCurrentPlace();
        artifact.setDimensions();
        artifact.setWeight();
        artifact.setTags();
        artifact.setImagePath();
        //artifact.*/
    }

    public void updateArtifact(Artifact artifact) {

    }

    public void deleteArtifact(String artifactId) {

    }

    public List<Artifact> searchArtifacts(String s, String query) {
        return null;
    }

    public List<Artifact> filterByTags(List<String> tags) {
        return null;
    }
}
