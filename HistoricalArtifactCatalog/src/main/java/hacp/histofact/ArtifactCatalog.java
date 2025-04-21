package hacp.histofact;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class ArtifactCatalog {
    private ArrayList<Artifact> artifacts;
    public int size;

    ArtifactCatalog() {
        artifacts = new ArrayList<>();
        size = 0;
    }

    // Methods for adding, removing, updating artifacts
    public void addArtifact(Artifact artifact) {

        if (artifact != null) {
            artifacts.add(artifact);
            size++;
        }
    }

    public boolean removeArtifact(String artifactId) {
        if (artifactId == null) {
            return false;
        }
        Iterator<Artifact> iterator = artifacts.iterator();
        while (iterator.hasNext()) {
            Artifact artifact = iterator.next();
            if (Objects.equals(artifact.getArtifactId(), artifactId)) {
                iterator.remove();
                size--;
                return true;
            }
        }
        return false;
    }

    //this is temporarily so will think about the update functionality
    public boolean updateArtifact(Artifact artifact) {
        if (artifact == null || artifact.getArtifactId() == null) {
            return false;
        }
        for (int i = 0; i < artifacts.size(); i++) {
            if (Objects.equals(artifacts.get(i).getArtifactId(), artifact.getArtifactId())) {
                artifacts.set(i, artifact);
                return true;
            }
        }
        return false;
    }

    public Artifact getArtifact(String artifactId) {
        return null;
    }

    public ArrayList<Artifact> getAllArtifacts() {
        return artifacts;
    }
}
