package hacp.histofact;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.ArrayList;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainController {

    private ArtifactCatalog catalog;
    private ArtifactController artifactController;

    //fxml items for handleSearch()
    @FXML
    TextField searchField;
    @FXML
    ChoiceBox<String> searchFieldChoice;
    @FXML
    ListView<Artifact> artifactListView;
    @FXML
    Label statusLabel;
    @FXML
    VBox detailsVBox;



    public void initialize() {
        this.catalog = new ArtifactCatalog();
        //this is used for testing
//        Artifact a1 = new Artifact(
//                "A001", "Golden Vase", Category.SCULPTURE,
//                "Ancient Egypt", "Giza", "Gold",
//                LocalDate.of(-1500, 6, 1), "Cairo Museum",
//                new Dimension(30, 15, 15), 2.5,
//                new ArrayList<>(java.util.List.of("royalty", "ceremonial")), "/images/golden_vase.jpg"
//        );
//
//        Artifact a2 = new Artifact(
//                "A002", "Clay Tablet", Category.MANUSCRIPT,
//                "Sumerians", "Uruk", "Clay",
//                LocalDate.of(-3200, 1, 15), "British Museum",
//                new Dimension(10, 8, 1), 0.7,
//                new ArrayList<>(java.util.List.of("writing", "cuneiform")), "/images/clay_tablet.jpg"
//        );
//
//        Artifact a3 = new Artifact(
//                "A003", "Bronze Helmet", Category.TOOL,
//                "Ancient Greece", "Athens", "Bronze",
//                LocalDate.of(-500, 4, 20), "Athens War Museum",
//                new Dimension(25, 20, 30), 3.2,
//                new ArrayList<>(java.util.List.of("military", "helmet")), "/images/bronze_helmet.jpg"
//        );
//
//        Artifact a4 = new Artifact(
//                "A004", "Terracotta Figurine", Category.SCULPTURE,
//                "Ancient China", "Xi'an", "Terracotta",
//                LocalDate.of(-210, 9, 10), "Shaanxi Museum",
//                new Dimension(50, 20, 20), 7.5,
//                new ArrayList<>(java.util.List.of("terracotta", "figurine")), "/images/terracotta_figurine.jpg"
//        );
//
//        Artifact a5 = new Artifact(
//                "A005", "Stone Axe", Category.TOOL,
//                "Neolithic", "Çatalhöyük", "Stone",
//                LocalDate.of(-6000, 3, 5), "Anatolian Civilizations Museum",
//                new Dimension(18, 5, 3), 1.1,
//                new ArrayList<>(java.util.List.of("tool", "prehistoric")), "/images/stone_axe.jpg"
//        );
//        catalog.addArtifact(a1);
//        catalog.addArtifact(a2);
//        catalog.addArtifact(a3);
//        catalog.addArtifact(a4);
//        catalog.addArtifact(a5);
        this.artifactController = new ArtifactController(catalog);
    }

    // Methods for handling user interactions
    public void handleAddArtifact() {

    }

    public void handleEditArtifact() {

    }

    public void handleDeleteArtifact() {

    }

    // when search button is clicked
    @FXML
    public void handleSearch() {
        //clean the vbox area
        detailsVBox.getChildren().clear();
        //get fields in UI
        String query = searchField.getText();
        String selectedField = searchFieldChoice.getValue();

        //check fields and update statuslabel
        if (query == null || query.isEmpty()) {
            statusLabel.setText("Search field is empty.");
            return;
        }

        //print statements for testing
        //System.out.println("Searching in " + selectedField);
        //System.out.println("Searching for " + query);

        ArrayList<Artifact> results = artifactController.searchArtifacts(query, selectedField);

        if (results.isEmpty()) {
            Label noResults = new Label("No artifacts found.");
            noResults.getStyleClass().add("no-results-label");
            detailsVBox.getChildren().add(noResults);
            statusLabel.setText("No artifacts found.");
        } else {
            statusLabel.setText("Found " + results.size() + " artifacts.");
            //list all results separately in vbox card format
            for (Artifact artifact : results) {
                VBox card = new VBox(5);
                card.getStyleClass().add("result-card");

                Label title = new Label(artifact.getArtifactName());
                title.getStyleClass().add("result-title");

                Label id = new Label("ID: " + artifact.getArtifactId());
                id.getStyleClass().add("result-subtitle");

                Label category = new Label("Category: " + artifact.getCategory());
                Label civ = new Label("Civilization: " + artifact.getCivilization());
                Label location = new Label("Discovery Location: " + artifact.getDiscoveryLocation());
                Label date = new Label("Discovery Date: " + artifact.getDiscoveryDate());
                Label comp = new Label("Composition: " + artifact.getComposition());
                Label place = new Label("Current Place: " + artifact.getCurrentPlace());
                Label weight = new Label("Weight: " + artifact.getWeight());

                //each tag's css must slay
                HBox tagBox = new HBox(5);
                tagBox.getStyleClass().add("tag-container");
                for (String tag : artifact.getTags()) {
                    Label tagLabel = new Label(tag);
                    tagLabel.getStyleClass().add("tag-bubble");
                    tagBox.getChildren().add(tagLabel);
                }

                //add all attributes to card and add card to vbox
                card.getChildren().addAll(title, id, category, civ, location, date, comp, place, weight, tagBox);
                detailsVBox.getChildren().add(card);
            }
        }

    }

    public void handleFilterByTags() {

    }

    public void handleImport() {

    }

    public void handleExport() {

    }

    public void handleShowHelp() {

    }

}
