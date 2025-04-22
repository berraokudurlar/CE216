package hacp.histofact;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;

import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;


public class MainController {

    private ArtifactCatalog catalog;
    private ArtifactController artifactController;
<<<<<<< Updated upstream
    private Artifact selectedArtifact;
=======
    private JsonManager jsonManager = JsonManager.getInstance();
>>>>>>> Stashed changes

    //fxml items for handleSearch()
    @FXML
    TextField searchField;
    @FXML
    ChoiceBox<String> searchFieldChoice;
    @FXML
    Label statusLabel;
    @FXML
    VBox detailsVBox;

    //fxml items for handleFilterByTags()
    @FXML
    GridPane selectedTagsGrid;
    @FXML
    ListView<String> tagListView;

    //this stores selected tags on filterByTags part
    private List<String> selectedTags = new ArrayList<>();

    public void initialize() {
        this.catalog = new ArtifactCatalog();
        //this is used for testing
        Artifact a1 = new Artifact(
                "A001", "Golden Vase", Category.SCULPTURE,
                "Ancient Egypt", "Giza", "Gold",
                LocalDate.of(-1500, 6, 1), "Cairo Museum",
                new Dimension(30, 15, 15), 2.5,
                new ArrayList<>(java.util.List.of("royalty", "ceremonial")), "/images/golden_vase.jpg"
        );

        Artifact a2 = new Artifact(
                "A002", "Clay Tablet", Category.MANUSCRIPT,
                "Sumerians", "Uruk", "Clay",
                LocalDate.of(-3200, 1, 15), "British Museum",
                new Dimension(10, 8, 1), 0.7,
                new ArrayList<>(java.util.List.of("writing", "cuneiform")), "/images/clay_tablet.jpg"
        );

        Artifact a3 = new Artifact(
                "A003", "Bronze Helmet", Category.TOOL,
                "Ancient Greece", "Athens", "Bronze",
                LocalDate.of(-500, 4, 20), "Athens War Museum",
                new Dimension(25, 20, 30), 3.2,
                new ArrayList<>(java.util.List.of("military", "helmet")), "/images/bronze_helmet.jpg"
        );

        Artifact a4 = new Artifact(
                "A004", "Terracotta Figurine", Category.SCULPTURE,
                "Ancient China", "Xi'an", "Terracotta",
                LocalDate.of(-210, 9, 10), "Shaanxi Museum",
                new Dimension(50, 20, 20), 7.5,
                new ArrayList<>(java.util.List.of("terracotta", "figurine")), "/images/terracotta_figurine.jpg"
        );

        Artifact a5 = new Artifact(
                "A005", "Stone Axe", Category.TOOL,
                "Neolithic", "Çatalhöyük", "Stone",
                LocalDate.of(-6000, 3, 5), "Anatolian Civilizations Museum",
                new Dimension(18, 5, 3), 1.1,
                new ArrayList<>(java.util.List.of("tool", "prehistoric")), "/images/stone_axe.jpg"
        );
        catalog.addArtifact(a1);
        catalog.addArtifact(a2);
        catalog.addArtifact(a3);
        catalog.addArtifact(a4);
        catalog.addArtifact(a5);
        this.artifactController = new ArtifactController(catalog);
        displayFilterTags();
    }

    // Method to display all tag options on the filter by tags menu
    public void displayFilterTags() {
        // getting all unique tags from the catalog
        Set<String> allTags = new HashSet<>();
        for (Artifact artifact : catalog.getAllArtifacts()) {
            allTags.addAll(artifact.getTags());
        }

        // creating the tag list view
        if (tagListView != null) {
            tagListView.getItems().clear();
            // add all unique tags to the list view
            tagListView.getItems().addAll(allTags);

            // setting up multiselection
            tagListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            // event listener for selection changes
            tagListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !selectedTags.contains(newVal)) {
                    selectedTags.add(newVal);
                    updateSelectedTagsDisplay();
                }
            });
        }

        // gridpane for selected tags
        if (selectedTagsGrid != null) {
            selectedTagsGrid.setAlignment(Pos.CENTER_LEFT);
        }

    }

    // used in handleSearch() and handleFilterByTags()
    // for displaying results in a card view
    private void displayArtifactResults(List<Artifact> artifacts) {
        // clear the vbox every time
        detailsVBox.getChildren().clear();

        for (Artifact artifact : artifacts) {
            VBox card = new VBox(5);
            card.getStyleClass().add("result-card");

        //This part makes user  to be able to click on the artifact "cards" we use for the display.
            card.setOnMouseClicked(event -> {
                selectedArtifact = artifact; //Used for delete and edit!
                statusLabel.setText("Selected: " + artifact.getArtifactName());
            });

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
            Label weight = new Label("Weight: " + artifact.getWeight() + "kg");

            HBox tagBox = new HBox(5);
            tagBox.getStyleClass().add("tag-container");
            for (String tag : artifact.getTags()) {
                Label tagLabel = new Label(tag);
                tagLabel.getStyleClass().add("tag-bubble");
                if (selectedTags.contains(tag)) {
                    tagLabel.getStyleClass().add("matched-tag");
                }
                tagBox.getChildren().add(tagLabel);
            }

            card.getChildren().addAll(title, id, category, civ, location, date, comp, place, weight, tagBox);
            detailsVBox.getChildren().add(card);
        }
    }

    //updating the tags when they are deleted and added
    private void updateSelectedTagsDisplay() {
        if (selectedTagsGrid != null) {
            selectedTagsGrid.getChildren().clear();

            //this part is just to make sure tags are aligned
            //in a 2x3 (maximum of) way
            int row = 0;
            int col = 0;
            int maxCols = 3;
            int maxRows = 2;

            for (String tag : selectedTags) {
                HBox tagBubble = createTagBubble(tag);
                //checking if num of tags exceeds
                //if so exits
                if (row > maxRows) {
                    System.out.println("Max tags reached.");
                    return;
                }
                //else it adds the tag on the grid
                selectedTagsGrid.add(tagBubble, col, row);
                // increases the col num and
                // if col is 3 gets to the new row
                col++;
                if (col >= maxCols) {
                    col = 0;
                    row++;
                }
            }

            // apply the filter if there are any selected tags
            if (!selectedTags.isEmpty()) {
                artifactController.filterByTags(new ArrayList<>(selectedTags));
            }
        }
    }

    //this is for the tags' css
    private HBox createTagBubble(String tag) {
        HBox bubble = new HBox(5);
        bubble.getStyleClass().add("tag-bubble");
        bubble.setAlignment(Pos.CENTER);

        Label tagLabel = new Label(tag);
        // and the little x on each tag to deselect
        Button removeBtn = new Button("×");
        removeBtn.getStyleClass().add("tag-remove-btn");
        // action to listen click on x
        removeBtn.setOnAction(e -> {
            selectedTags.remove(tag);
            updateSelectedTagsDisplay();
        });
        // add the label and remove button on the tag bubble
        bubble.getChildren().addAll(tagLabel, removeBtn);
        return bubble;
    }


    // Methods for handling user interactions
    @FXML
    public void handleAddArtifact() {

        detailsVBox.getChildren().clear();

        VBox newArtifactForm = new VBox(10);
        new Insets(15, 10, 15, 10);

        TextField nameField = new TextField();
        nameField.setPromptText("Artifact Name");

        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("Sculpture", "Manuscript", "Tool", "Weapon", "Pottery");
        categoryComboBox.setPromptText("Select Category");

        DatePicker discoveryDatePicker = new DatePicker();
        discoveryDatePicker.setPromptText("Discovery Date");

        TextField civilizationField = new TextField();
        civilizationField.setPromptText("Civilization");

        TextField locationField = new TextField();
        locationField.setPromptText("Discovery Location");

        // Create a Button to save the artifact (for now it won't do anything)
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            // Here you can implement saving logic if you have it set up
            System.out.println("New Artifact Saved: " + nameField.getText());
        });

        // Add form fields to the form layout
        newArtifactForm.getChildren().addAll(
                new Label("Artifact Name:"), nameField,
                new Label("Category:"), categoryComboBox,
                new Label("Discovery Date:"), discoveryDatePicker,
                new Label("Civilization:"), civilizationField,
                new Label("Discovery Location:"), locationField,
                saveButton
        );

        // Add the new form to the detailsVBox
        detailsVBox.getChildren().add(newArtifactForm);


    }


    public void handleEditArtifact() {
        if (selectedArtifact == null) {
            statusLabel.setText("No artifact selected for editing.");
            return;
        }

    }

    @FXML
    public void handleDeleteArtifact() {
        if (selectedArtifact == null) {
            statusLabel.setText("No artifact selected for deletion.");
            return;
        }

        artifactController.deleteArtifact(selectedArtifact.getArtifactId());
        selectedArtifact = null; 
        statusLabel.setText("Artifact deleted.");
        displayArtifactResults(catalog.getAllArtifacts());
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

        ArrayList<Artifact> results = (ArrayList<Artifact>) artifactController.searchArtifacts(query, selectedField);

        if (results.isEmpty()) {
            Label noResults = new Label("No artifacts found.");
            noResults.getStyleClass().add("no-results-label");
            detailsVBox.getChildren().add(noResults);
            statusLabel.setText("No artifacts found.");
        } else {
            statusLabel.setText("Found " + results.size() + " artifacts.");
            displayArtifactResults(results);
        }
    }

    // when apply filer button is clicked
    public void handleFilterByTags() {
        // checking if any tags are selected
        if (selectedTags.isEmpty()) {
            statusLabel.setText("Please select at least one tag to filter.");
            return;
        }

        // clear previous results
        detailsVBox.getChildren().clear();

        // find artifacts that match ALL selected tags
        List<Artifact> filteredResults = artifactController.filterByTags((ArrayList<String>) selectedTags);

        //if there are no matches show message
        if (filteredResults.isEmpty()) {
            statusLabel.setText("No artifacts match all selected tags.");
            Label noResults = new Label("No artifacts match the selected tags.");
            noResults.getStyleClass().add("no-results-label");
            detailsVBox.getChildren().add(noResults);
        } else {
            // set the status to success message and display the resulting artifacts
            statusLabel.setText("Found " + filteredResults.size() + " artifacts matching the selected tags.");
            displayArtifactResults(filteredResults);
        }

    }

    public void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Artifacts (JSON)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                JsonManager jsonManager = JsonManager.getInstance();
                jsonManager.setFile(file);
                ArrayList<Artifact> imported = jsonManager.importArtifacts();
                for (Artifact a : imported) {
                    catalog.addArtifact(a);
                    artifactListView.getItems().add(a);
                }
                statusLabel.setText("Imported " + imported.size() + " artifacts.");
            } catch (Exception e) {
                statusLabel.setText("Error importing artifacts: " + e.getMessage());
            }
        }
    }

    public void handleExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Artifacts (JSON)");
        fileChooser.setInitialFileName("artifacts.json");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            JsonManager jsonManager = JsonManager.getInstance();
            jsonManager.setFile(file);
            jsonManager.exportArtifacts(catalog.getAllArtifacts());
            statusLabel.setText("Exported " + catalog.getAllArtifacts().size() + " artifacts.");
        }
    }

    public void handleShowHelp() {

    }

    @FXML
    public void handleUserManual(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Manual");
        alert.setHeaderText(null);
        TextArea textArea = new TextArea();
        textArea.setText(
                "Welcome to the HistoFact!\n\n"
                        + "- Use 'New' to add a new artifact\n"
                        + "- Use 'Search' to filter by any field\n"
                        + "- Use 'Tags' dropdown for advanced filtering\n"
                        + "- Export and import JSON data\n\n"
                        + "Make sure to save your work before exiting!"
        );
        textArea.setWrapText(true);
        textArea.setEditable(false);

        // Set preferred size
        textArea.setPrefWidth(600);
        textArea.setPrefHeight(400);

        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setPrefSize(650, 450);
        alert.showAndWait();

    }

    @FXML
    public void handleAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        TextArea textArea = new TextArea();
        textArea.setText(
                "HistoFact is the semester project of three IUE students, Ege Çakıcı, Zeynep Erman and Berra Okudurlar for the CE 216 class.\n\n"
                        + "It is currently a prototype.\n\n"
                        + "Our purpose with HistoFact is to make historical artifact management easier.\n\n"

        );
        textArea.setWrapText(true);
        textArea.setEditable(false);

        // Set preferred size
        textArea.setPrefWidth(600);
        textArea.setPrefHeight(400);

        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setPrefSize(650, 450);
        alert.showAndWait();

    }
}
