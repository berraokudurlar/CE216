package hacp.histofact;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.geometry.Insets;

import java.io.File;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;


public class MainController {

    private ArtifactCatalog catalog;
    private ArtifactController artifactController;
    private final JsonManager jsonManager = JsonManager.getInstance();
    private Artifact selectedArtifact;

    private static final int MAX_RECENT_ACTIONS = 5;
    private final Preferences preferences = Preferences.userNodeForPackage(MainController.class);
    private final List<RecentAction> recentActions = new ArrayList<>();

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
    @FXML
    GridPane selectedTagsGrid;
    @FXML
    ListView<String> tagListView;
    @FXML
    MenuBar menuBar;

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

    public void handleExit(ActionEvent actionEvent) {
        statusLabel.setText("Exiting...");
        System.exit(0);
    }

    // recent action class for open recent functionality
    private static class RecentAction {
        private String type; // "search" or "filter"
        private String query;
        private String searchFieldOption;
        private List<String> tags;

        public RecentAction(String type, String query, String searchFieldOption, List<String> tags) {
            this.type = type;
            this.query = query;
            this.searchFieldOption = searchFieldOption;
            this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        }

        public String getType() {
            return type;
        }

        public String getQuery() {
            return query;
        }

        public String getSearchFieldOption() {
            return searchFieldOption;
        }

        public List<String> getTags() {
            return tags;
        }

        @Override
        public String toString() {
            if ("search".equals(type)) {
                return "Search: '" + query + "' in " + searchFieldOption;
            } else if ("filter".equals(type)) {
                return "Filter by tags: " + String.join(", ", tags);
            }
            return "Unknown Action";
        }
    }

    private Stage getStage() {
        return (Stage) menuBar.getScene().getWindow();
    }

    private void saveRecentActions() {
        for (int i = 0; i < MAX_RECENT_ACTIONS; i++) {
            if (i < recentActions.size()) {
                RecentAction action = recentActions.get(i);
                if (action != null) {
                    preferences.put("recentAction_" + i + "_type", action.getType());
                    preferences.put("recentAction_" + i + "_query", action.getQuery());
                    preferences.put("recentAction_" + i + "_option", action.getSearchFieldOption());
                    preferences.put("recentAction_" + i + "_tags", String.join(",", action.getTags()));
                } else {
                    removeRecentActionPreferences(i);
                }
            } else {
                removeRecentActionPreferences(i);
            }
        }
        try {
            preferences.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeRecentActionPreferences(int index) {
        preferences.remove("recentAction_" + index + "_type");
        preferences.remove("recentAction_" + index + "_query");
        preferences.remove("recentAction_" + index + "_option");
        preferences.remove("recentAction_" + index + "_tags");
    }

    private void addToRecentActions(String type, String query, String searchFieldOption, List<String> tags) {
        RecentAction newAction = new RecentAction(type, query, searchFieldOption, tags);
        recentActions.removeIf(action ->
                action.getType().equals(newAction.getType()) &&
                        Objects.equals(action.getQuery(), newAction.getQuery()) &&
                        Objects.equals(action.getSearchFieldOption(), newAction.getSearchFieldOption()) &&
                        action.getTags().equals(newAction.getTags()));
        recentActions.add(0, newAction);
        while (recentActions.size() > MAX_RECENT_ACTIONS) {
            recentActions.remove(recentActions.size() - 1);
        }
        updateOpenRecentMenu();
        saveRecentActions();
    }

    private void updateOpenRecentMenu() {
        for (Menu menu : menuBar.getMenus()) {
            if (menu.getText().equals("File")) {
                for (MenuItem item : menu.getItems()) {
                    if (item instanceof Menu && item.getText().equals("Open Recent")) {
                        updateOpenRecentMenu((Menu) item);
                        return;
                    }
                }
            }
        }
    }

    private void updateOpenRecentMenu(Menu openRecentMenu) {
        openRecentMenu.getItems().clear();
        if (recentActions.isEmpty()) {
            MenuItem emptyItem = new MenuItem("No recent actions");
            emptyItem.setDisable(true);
            openRecentMenu.getItems().add(emptyItem);
        } else {
            for (RecentAction action : recentActions) {
                MenuItem recentActionItem = new MenuItem(action.toString());
                recentActionItem.setOnAction(event -> applyRecentAction(action));
                openRecentMenu.getItems().add(recentActionItem);
            }
        }
    }

    private void applyRecentAction(RecentAction action) {
        detailsVBox.getChildren().clear();
        if ("search".equals(action.getType())) {
            searchField.setText(action.getQuery());
            searchFieldChoice.setValue(action.getSearchFieldOption());
            handleSearch();
        } else if ("filter".equals(action.getType())) {
            System.out.println("Applying filter with tags: " + action.getTags()); // Debugging

            // clear previous selections
            tagListView.getSelectionModel().clearSelection();
            selectedTags.clear();
            updateSelectedTagsDisplay();

            // select the tags from the recent action
            for (String tag : action.getTags()) {
                int index = tagListView.getItems().indexOf(tag);
                if (index != -1) {
                    tagListView.getSelectionModel().select(index);
                    if (!selectedTags.contains(tag)) {
                        selectedTags.add(tag);
                    }
                }
            }
            updateSelectedTagsDisplay();
            handleFilterByTags();
        }
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
        detailsVBox.getChildren().clear();

        //I made an important change here. There is now an HBox for our cards, an HBox for the image.
        for (Artifact artifact : artifacts) {
            HBox card = new HBox(15);
            card.setAlignment(Pos.CENTER_LEFT);
            card.getStyleClass().add("result-card");

            //This is the part that allows us to select an artifact with our mouse.
            card.setOnMouseClicked(event -> {
                selectedArtifact = artifact;
                statusLabel.setText("Selected: " + artifact.getArtifactName());
                displayArtifactResults(artifacts);
            });

            //The part that is connected to css file to make the selected artifact green.
            if (selectedArtifact != null && selectedArtifact.getArtifactId().equals(artifact.getArtifactId())) {
                card.getStyleClass().add("selected-card");
            }

            VBox textInfoBox = new VBox(5);

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

            textInfoBox.getChildren().addAll(title, id, category, civ, location, date, comp, place, weight, tagBox);

            ImageManager imageManager = ImageManager.getInstance();
            File imageFile = imageManager.getImage(artifact.getImagePath());
            ImageView imageView = new ImageView();
            if (imageFile != null) {
                imageView.setImage(new Image(imageFile.toURI().toString()));
            }
            imageView.setFitWidth(120);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);

            //This part sets some space between the information and the image. It is a bit ugly right now...
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            card.getChildren().addAll(textInfoBox, spacer, imageView);
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

        //I sneaked in the ID here since empty ID would create trouble.
        TextField idField = new TextField();
        idField.setPromptText("ID Name");

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

        //Now we can upload images on the add screen!!
        TextField imagePathField = new TextField();
        imagePathField.setPromptText("/images/example.jpg");
        Button chooseImageButton = new Button("Choose Image");

        chooseImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Artifact Image");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                String imagePath = ImageManager.getInstance().saveImage(selectedFile, nameField.getText().replaceAll("\\s+", "").toLowerCase());
                if (imagePath != null) {
                    imagePathField.setText(imagePath);
                }
            }
        });


        // Create a Button to save the artifact, do not have every field for now
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            try {
                String artifactId = idField.getText().trim();
                String name = nameField.getText().trim();
                String categoryStr = categoryComboBox.getValue();

                if (artifactId.isEmpty() || name.isEmpty() || categoryStr == null) {
                    statusLabel.setText("ID, name and category are required.");
                    return;
                }

               artifactId = System.currentTimeMillis() + name.substring(0, Math.min(3, name.length())).toLowerCase();
                Category category = Category.valueOf(categoryStr.toUpperCase(Locale.ROOT));

                Artifact newArtifact = new Artifact(artifactId, name, category);
                newArtifact.setCivilization(civilizationField.getText().trim());
                newArtifact.setDiscoveryLocation(locationField.getText().trim());
                newArtifact.setDiscoveryDate(discoveryDatePicker.getValue());

                String imagePath = imagePathField.getText().trim();
                newArtifact.setImagePath(imagePath);

                //Dealing with json for the new artifact
                jsonManager.appendArtifactToFile(newArtifact);

                catalog.addArtifact(newArtifact);
                displayArtifactResults(catalog.getAllArtifacts());
                statusLabel.setText("Artifact saved successfully.");


            } catch (Exception ex) {
                ex.printStackTrace();
                statusLabel.setText("Error saving artifact: " + ex.getMessage());
            }
        });

        // Add form fields to the form layout
        newArtifactForm.getChildren().addAll(
                new Label("ID Name:"), idField,
                new Label("Artifact Name:"), nameField,
                new Label("Category:"), categoryComboBox,
                new Label("Discovery Date:"), discoveryDatePicker,
                new Label("Civilization:"), civilizationField,
                new Label("Discovery Location:"), locationField,
                new Label("Image Path:"), imagePathField,
                chooseImageButton,
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

        detailsVBox.getChildren().clear();

        //Here, we use Optional. command to make sure the user can actually edit the imported JSON files with null attributes.
        TextField nameField = new TextField(Optional.ofNullable(selectedArtifact.getArtifactName()).orElse(""));

        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("Sculpture", "Manuscript", "Tool", "Weapon", "Jewelry", "Pottery");
        categoryComboBox.setValue(Optional.ofNullable(selectedArtifact.getCategory()).map(Enum::toString).orElse(null));

        TextField civilizationField = new TextField(Optional.ofNullable(selectedArtifact.getCivilization()).orElse(""));
        TextField locationField = new TextField(Optional.ofNullable(selectedArtifact.getDiscoveryLocation()).orElse(""));
        TextField currentPlaceField = new TextField(Optional.ofNullable(selectedArtifact.getCurrentPlace()).orElse(""));
        TextField compositionField = new TextField(Optional.ofNullable(selectedArtifact.getComposition()).orElse(""));
        DatePicker discoveryDatePicker = new DatePicker(Optional.ofNullable(selectedArtifact.getDiscoveryDate()).orElse(null));

        Dimension dim = Optional.ofNullable(selectedArtifact.getDimensions()).orElse(new Dimension(0, 0, 0));
        TextField widthField = new TextField(String.valueOf(dim.getWidth()));
        TextField lengthField = new TextField(String.valueOf(dim.getLength()));
        TextField heightField = new TextField(String.valueOf(dim.getHeight()));

        TextField weightField = new TextField(String.valueOf(Optional.ofNullable(selectedArtifact.getWeight()).orElse(0.0)));
        TextField tagsField = new TextField(String.join(", ", Optional.ofNullable(selectedArtifact.getTags()).orElse(new ArrayList<>())));

        TextField imagePathField = new TextField(Optional.ofNullable(selectedArtifact.getImagePath()).orElse(""));
        //A new button that chooses image files from the desktop. The user can also type out the path name for the file manually.
        Button chooseImageButton = new Button("Choose Image");
        chooseImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Artifact Image");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                String newPath = ImageManager.getInstance().saveImage(selectedFile, selectedArtifact.getArtifactId());
                if (newPath != null) {
                    imagePathField.setText(newPath);
                    selectedArtifact.setImagePath(newPath);
                }
            }
        });

        Button saveButton = new Button("Save Changes");
        saveButton.setOnAction(e -> {
            try {
                selectedArtifact.setArtifactName(nameField.getText());
                selectedArtifact.setCategory(Category.valueOf(categoryComboBox.getValue().toUpperCase()));
                selectedArtifact.setCivilization(civilizationField.getText());
                selectedArtifact.setDiscoveryLocation(locationField.getText());
                selectedArtifact.setCurrentPlace(currentPlaceField.getText());
                selectedArtifact.setComposition(compositionField.getText());
                selectedArtifact.setDiscoveryDate(discoveryDatePicker.getValue());

                double width = Double.parseDouble(widthField.getText());
                double length = Double.parseDouble(lengthField.getText());
                double height = Double.parseDouble(heightField.getText());
                double weight = Double.parseDouble(weightField.getText());
                selectedArtifact.setDimensions(new Dimension(width, length, height));
                selectedArtifact.setWeight(weight);

                String[] tagArray = tagsField.getText().split(",");
                ArrayList<String> tags = new ArrayList<>();
                for (String tag : tagArray) {
                    tag = tag.trim();
                    if (!tag.isEmpty()) tags.add(tag);
                }
                selectedArtifact.setTags(tags);

                artifactController.updateArtifact(selectedArtifact);
                statusLabel.setText("Artifact updated successfully.");
                displayArtifactResults(catalog.getAllArtifacts());
            } catch (Exception ex) {
                statusLabel.setText("Error: Invalid input. Please check all fields.");
                ex.printStackTrace();
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> {
            statusLabel.setText("Edit cancelled.");
            displayArtifactResults(catalog.getAllArtifacts());
        });

        HBox buttonBox = new HBox(10, saveButton, cancelButton);

        VBox form = new VBox(10,
                new Label("Edit Artifact"),
                new Label("Name:"), nameField,
                new Label("Category:"), categoryComboBox,
                new Label("Civilization:"), civilizationField,
                new Label("Discovery Location:"), locationField,
                new Label("Current Place:"), currentPlaceField,
                new Label("Composition:"), compositionField,
                new Label("Discovery Date:"), discoveryDatePicker,
                new Label("Width:"), widthField,
                new Label("Length:"), lengthField,
                new Label("Height:"), heightField,
                new Label("Weight (kg):"), weightField,
                new Label("Tags (comma separated):"), tagsField,
                new Label("Image Path:"), imagePathField,
                chooseImageButton,
                buttonBox
        );

        form.setPadding(new Insets(10));
        detailsVBox.getChildren().add(form);
    }

    @FXML
    public void handleDeleteArtifact() {
        if (selectedArtifact == null) {
            statusLabel.setText("No artifact selected for deletion.");
            return;
        }
        //This alert makes sure that the user doesn't accidentally delete something.
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Are you sure you want to delete this artifact?");
        alert.setContentText("Artifact: " + selectedArtifact.getArtifactName());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
        artifactController.deleteArtifact(selectedArtifact.getArtifactId());
        selectedArtifact = null;
        statusLabel.setText("Artifact deleted.");
        displayArtifactResults(catalog.getAllArtifacts());

        } else {
            statusLabel.setText("Deletion cancelled.");
        }
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
            addToRecentActions("search", query, selectedField, null); // Save the search
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
            addToRecentActions("filter", "", "", new ArrayList<>(selectedTags)); // Save the filter
        }

    }

    public void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Artifacts (JSON)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                jsonManager.setFile(file);
                ArrayList<Artifact> imported = jsonManager.importArtifacts();
                for (Artifact a : imported) {
                    if(catalog.getAllArtifacts().contains(a)){
                        continue;
                    }
                    catalog.addArtifact(a);
                }
                displayArtifactResults(catalog.getAllArtifacts());
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
                //User Manual text here. We can then turn this into a file.
                "Welcome to the HistoFact!\n\n"
                        + "- Use 'New' to add a new artifact.\n"
                        + "- Use 'Search' to search for the artifacts in any category.\n"
                        + "- Select an artifact by clicking it with your mouse.\n"
                        + "- The selected artifact will be appear in green.\n"
                        + "- Use 'Edit' to update a selected artifact.\n"
                        + "- Use 'Delete' to delete a selected artifact.\n"
                        + "- On the left screen, there are various tags.\n"
                        + "- Choose the tags, and then press the ''Apply Filter'' button.\n"
                        + "- Import JSON data by File -> 'Import JSON'.\n"
                        + "- Export JSON data by File -> 'Export JSON'.\n\n"
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

    public void handleEdit(ActionEvent event) {

    }
}
