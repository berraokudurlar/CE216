package hacp.histofact;


import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.time.LocalDate;
import java.util.ArrayList;

public class ArtifactController {
    private ArtifactCatalog catalog;

    ArtifactController(ArtifactCatalog catalog) {
        this.catalog = catalog;
    }

    // Methods for artifact operations
    public void addArtifact(Artifact artifact) {
        if (artifact == null) {
            return;
        }
        catalog.addArtifact(artifact);
    }

    public void updateArtifact(Artifact artifact) {
        if (artifact == null) {
            return;
        }
        catalog.updateArtifact(artifact);
    }

    public void deleteArtifact(String artifactId) {
        if (artifactId == null) {
            return;
        }
        catalog.removeArtifact(artifactId);
    }

    public ArrayList<Artifact> searchArtifacts(String query, String field) {
        //if null return
        if (query == null || query.isEmpty())
            return null;
        // query -> lowercase
        String searchQuery = query.toLowerCase();

        ArrayList<Artifact> results = new ArrayList<>();
        ArrayList<Artifact> allArtifacts = catalog.getAllArtifacts();
        // search all artifacts based on matches on fields
        // if no field is selected look for any matching string in all fields
        // else look for that specific filed in artifact
        // add results to the results arraylist
        for (Artifact artifact : allArtifacts) {
            boolean matches = switch (field) {
                case "All Fields" -> artifact.getArtifactName().toLowerCase().contains(searchQuery) ||
                        artifact.getArtifactId().toLowerCase().contains(searchQuery) ||
                        artifact.getCategory().toString().toLowerCase().contains(searchQuery) ||
                        artifact.getCivilization().toLowerCase().contains(searchQuery) ||
                        artifact.getDiscoveryLocation().toLowerCase().contains(searchQuery) ||
                        artifact.getComposition().toLowerCase().contains(searchQuery) ||
                        artifact.getDiscoveryDate().toString().contains(searchQuery) ||
                        artifact.getCurrentPlace().toLowerCase().contains(searchQuery) ||
                        String.valueOf(artifact.getWeight()).equals(searchQuery);
                case "Artifact ID" -> artifact.getArtifactId().toLowerCase().contains(searchQuery);
                case "Name" -> artifact.getArtifactName().toLowerCase().contains(searchQuery);
                case "Category" -> artifact.getCategory().toString().toLowerCase().contains(searchQuery);
                case "Civilization" -> artifact.getCivilization().toLowerCase().contains(searchQuery);
                case "Discovery Location" -> artifact.getDiscoveryLocation().toLowerCase().contains(searchQuery);
                case "Composition" -> artifact.getComposition().toLowerCase().contains(searchQuery);
                case "Discovery Date" -> artifact.getDiscoveryDate().toString().contains(searchQuery);
                case "Current Place" -> artifact.getCurrentPlace().toLowerCase().contains(searchQuery);
                case "Weight" -> String.valueOf(artifact.getWeight()).equals(searchQuery);
                default -> false;
            };
            if (matches) {
                results.add(artifact);
            }
        }
        return results;
    }

    public ArrayList<Artifact> filterByTags(ArrayList<String> tags) {
        if (tags == null || tags.isEmpty())
            return null;
        //if artifact has tags and contains every queried tag, add to the result
        //else break and look at the next artifact
        ArrayList<Artifact> filtered = new ArrayList<>();
        for (Artifact artifact : catalog.getAllArtifacts()) {
            if (artifact.getTags() != null) {
                boolean containsAllTags = true;
                for (String tag : tags) {
                    if (!artifact.getTags().contains(tag)) {
                        containsAllTags = false;
                        break;
                    }
                }
                if (containsAllTags) {
                    filtered.add(artifact);
                }
            }
        }
        return filtered;
    }

}
