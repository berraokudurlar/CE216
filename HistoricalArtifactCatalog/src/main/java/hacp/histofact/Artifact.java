package hacp.histofact;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Artifact {
    private String artifactId;
    private String artifactName;
    private Category category;
    private String civilization;
    private String discoveryLocation;
    private String composition;
    private LocalDate discoveryDate;
    private String currentPlace;
    private Dimension dimensions;
    private double weight;
    private ArrayList<String> tags;
    private String imagePath;


    public Artifact(String artifactId, String artifactName, Category category) {
        this.artifactId = artifactId;
        this.artifactName = artifactName;
        this.category = category;
        this.tags = new ArrayList<>();
    }

    public Artifact(String artifactId, String artifactName, Category category,
                    String civilization, String discoveryLocation, String composition,
                    LocalDate discoveryDate, String currentPlace, Dimension dimensions,
                    double weight, ArrayList<String> tags, String imagePath) {
        this.artifactId = artifactId;
        this.artifactName = artifactName;
        this.category = category;
        this.civilization = civilization;
        this.discoveryLocation = discoveryLocation;
        this.composition = composition;
        this.discoveryDate = discoveryDate;
        this.currentPlace = currentPlace;
        this.dimensions = dimensions;
        this.weight = weight;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.imagePath = imagePath;
    }
    public Artifact() {
        this.tags = new ArrayList<>();
    }
    // Getters and setters
    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getArtifactName() {
        return artifactName;
    }

    public void setArtifactName(String artifactName) {
        this.artifactName = artifactName;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getCivilization() {
        return civilization;
    }

    public void setCivilization(String civilization) {
        this.civilization = civilization;
    }

    public String getDiscoveryLocation() {
        return discoveryLocation;
    }

    public void setDiscoveryLocation(String discoveryLocation) {
        this.discoveryLocation = discoveryLocation;
    }

    public String getComposition() {
        return composition;
    }

    public void setComposition(String composition) {
        this.composition = composition;
    }

    public LocalDate getDiscoveryDate() {
        return discoveryDate;
    }

    public void setDiscoveryDate(LocalDate discoveryDate) {
        this.discoveryDate = discoveryDate;
    }

    public String getCurrentPlace() {
        return currentPlace;
    }

    public void setCurrentPlace(String currentPlace) {
        this.currentPlace = currentPlace;
    }

    public Dimension getDimensions() {
        return dimensions;
    }

    public void setDimensions(Dimension dimensions) {
        this.dimensions = dimensions;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
    }

    public void addTag(String tag) {
        if (tag != null && !tag.trim().isEmpty() && !tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public boolean removeTag(String tag) {
        return tags.remove(tag);
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artifact artifact = (Artifact) o;
        return Objects.equals(artifactId, artifact.artifactId);
    }

    @Override
    public String toString() {
        return "Artifact{" +
                "id='" + artifactId + '\'' +
                ", name='" + artifactName + '\'' +
                ", category=" + category +
                ", civilization='" + civilization + '\'' +
                '}';
    }
}