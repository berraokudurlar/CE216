package hacp.histofact;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.geometry.Insets;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;

import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
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

        /* Okay, so. Whenever we run the app, it would always start with this hardcoded version, so it was like,
        impossible to see if a change was permanent, actually working and so on. So I added the JSON exports to the
        ArtifactController, and edited the initialize method like the following. Here it looks at our imported Artifact
        list first. If this change is a hindrance, please feel free to delete it. But I felt that we needed to test things
        as soon as possible.
         */

        File jsonFile = new File("artifacts.json");
        JsonManager.getInstance().setFile(jsonFile);

        ArrayList<Artifact> importedArtifacts = JsonManager.getInstance().importArtifacts();

        if (!importedArtifacts.isEmpty()) {
            for (Artifact artifact : importedArtifacts) {
                catalog.addArtifact(artifact);
            }
        } else {

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

            JsonManager.getInstance().exportArtifacts(catalog.getAllArtifacts());
        }

        this.artifactController = new ArtifactController(catalog);
        displayArtifactResults(catalog.getAllArtifacts());
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
            HBox card = new HBox(10);
            card.setAlignment(Pos.CENTER_LEFT);
            card.getStyleClass().add("result-card");

            //This is the part that allows us to select an artifact with our mouse.
            card.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1) {
                    // Select the artifact on single click
                    if (selectedArtifact == null || !selectedArtifact.getArtifactId().equals(artifact.getArtifactId())) {
                        selectedArtifact = artifact;
                        statusLabel.setText("Selected: " + artifact.getArtifactName());
                        displayArtifactResults(artifacts);
                    }
                } else if (event.getClickCount() == 2) {
                    // Deselect on double click if this artifact is already selected
                    if (selectedArtifact != null && selectedArtifact.getArtifactId().equals(artifact.getArtifactId())) {
                        selectedArtifact = null;
                        statusLabel.setText("Deselected artifact.");
                        displayArtifactResults(artifacts);
                        event.consume(); // prevent default behavior
                    }
                }
            });

            // Add selected style if artifact is selected
            if (selectedArtifact != null && selectedArtifact.getArtifactId().equals(artifact.getArtifactId())) {
                card.getStyleClass().add("selected-card");
            }

            VBox textInfoBox = new VBox(5);

            Label title = new Label(artifact.getArtifactName());
            title.getStyleClass().add("result-title");
            Label id = new Label("ID: " + artifact.getArtifactId());
            id.getStyleClass().add("result-subtitle");
            Label category = new Label("Category: " + artifact.getCategory());
            Label date = new Label("Discovery Date: " + artifact.getDiscoveryDate());
            Label civ = new Label("Civilization: " + artifact.getCivilization());
            Label location = new Label("Discovery Location: " + artifact.getDiscoveryLocation());
            Label place = new Label("Current Place: " + artifact.getCurrentPlace());
            Label comp = new Label("Composition: " + artifact.getComposition());
            Dimension dim = artifact.getDimensions();
            Label dimensions = new Label("Dimensions: " +
                    (dim != null ? dim.getWidth() + " x " + dim.getLength() + " x " + dim.getHeight() : "N/A"));
            Label weight = new Label("Weight: " + artifact.getWeight() + " kg");

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

            textInfoBox.getChildren().addAll(title, id, category, date, civ, location, place, comp, dimensions, weight, tagBox);

            ImageManager imageManager = ImageManager.getInstance();
            File imageFile = imageManager.getImage(artifact.getImagePath());

            Region spacer = new Region();
            spacer.setMinWidth(200);

// If image exists, create and add it with frame
            if (imageFile != null && imageFile.exists()) {
                ImageView imageView = new ImageView(new Image(imageFile.toURI().toString()));
                imageView.setFitWidth(200);
                imageView.setFitHeight(200);
                imageView.setPreserveRatio(false);

                StackPane imageFrame = new StackPane(imageView);
                imageFrame.getStyleClass().add("image-frame");
                imageFrame.setMinSize(208, 208);
                imageFrame.setMaxSize(208, 208);

                card.getChildren().addAll(textInfoBox, spacer, imageFrame);
            } else {
                card.getChildren().addAll(textInfoBox);
            }

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

    //Add button is updated to get every field from user now
    //Also added a preview dialog for the user to see the new artifact before save
    //We now also check the dimension input validity

    //Improved the GUI of New and Edit button forms
    @FXML
    public void handleAddArtifact() {

        detailsVBox.getChildren().clear();

        //I sneaked in the ID here since empty ID would create trouble.
        TextField idField = new TextField();
        idField.setPromptText("Artifact ID");

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
        TextField currentPlaceField = new TextField();
        currentPlaceField.setPromptText("Current Place");

        TextField compositionField = new TextField();
        compositionField.setPromptText("Composition");

        TextField widthField = new TextField();
        widthField.setPromptText("Width");

        TextField lengthField = new TextField();
        lengthField.setPromptText("Length");

        TextField heightField = new TextField();
        heightField.setPromptText("Height");

        TextField weightField = new TextField();
        weightField.setPromptText("Weight (kg)");

        TextField tagsField = new TextField();
        tagsField.setPromptText("Tags (comma separated)");

        TextField imagePathField = new TextField();
        imagePathField.setPromptText("/images/example.jpg");
        imagePathField.setEditable(false);

        Button chooseImageButton = new Button("Choose Image");
        chooseImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Artifact Image");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                String imagePath = ImageManager.getInstance().saveImage(
                        selectedFile, nameField.getText().replaceAll("\\s+", "").toLowerCase()
                );
                if (imagePath != null) {
                    imagePathField.setText(imagePath);
                }
            }
        });

        // Red borders when an info field is left empty
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            nameField.setStyle(newVal.trim().isEmpty() ? "-fx-border-color: red;" : null);
        });

        categoryComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            categoryComboBox.setStyle(newVal == null ? "-fx-border-color: red;" : null);
        });

        weightField.textProperty().addListener((obs, oldVal, newVal) -> {
            try {
                if (!newVal.trim().isEmpty()) Double.parseDouble(newVal);
                weightField.setStyle(null);
            } catch (NumberFormatException e) {
                weightField.setStyle("-fx-border-color: red;");
            }
        });

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            try {
                String artifactId = idField.getText().trim();
                String name = nameField.getText().trim();
                String categoryStr = categoryComboBox.getValue();

                if (artifactId.isEmpty()) {
                    artifactId = System.currentTimeMillis() + name.substring(0, Math.min(3, name.length())).toLowerCase();
                }

                if (name.isEmpty() || categoryStr == null) {
                    statusLabel.setText("ID, name, and category are required.");
                    return;
                }

                Category category = Category.valueOf(categoryStr.toUpperCase(Locale.ROOT));
                Artifact newArtifact = new Artifact(artifactId, name, category);
                newArtifact.setCivilization(civilizationField.getText().trim());
                newArtifact.setDiscoveryLocation(locationField.getText().trim());
                newArtifact.setDiscoveryDate(discoveryDatePicker.getValue());
                newArtifact.setComposition(compositionField.getText().trim());
                newArtifact.setCurrentPlace(currentPlaceField.getText().trim());
                newArtifact.setImagePath(imagePathField.getText().trim());

                try {
                    double w = Double.parseDouble(widthField.getText());
                    double l = Double.parseDouble(lengthField.getText());
                    double h = Double.parseDouble(heightField.getText());
                    newArtifact.setDimensions(new Dimension(w, l, h));
                } catch (NumberFormatException ignored) {
                    statusLabel.setText("Invalid dimensions entered. Skipped dimensions.");
                }

                try {
                    double weight = Double.parseDouble(weightField.getText());
                    newArtifact.setWeight(weight);
                } catch (NumberFormatException ignored) {
                    statusLabel.setText("Invalid weight entered. Skipped weight.");
                }

                String[] tagArray = tagsField.getText().split(",");
                for (String tag : tagArray) {
                    newArtifact.addTag(tag.trim());
                }

                // A preview dialog for the user to see what they are adding
                Dialog<ButtonType> confirmDialog = new Dialog<>();
                confirmDialog.setTitle("Confirm Artifact");
                confirmDialog.setHeaderText("Please confirm the artifact details:");

                String previewText = String.format("""
ID: %s
Name: %s
Category: %s
Civilization: %s
Location: %s
Date: %s
Composition: %s
Dimensions: %s
Weight: %.2f kg
Tags: %s
Current Place: %s
""",
                        newArtifact.getArtifactId(),
                        newArtifact.getArtifactName(),
                        newArtifact.getCategory(),
                        newArtifact.getCivilization(),
                        newArtifact.getDiscoveryLocation(),
                        newArtifact.getDiscoveryDate() != null ? newArtifact.getDiscoveryDate().toString() : "N/A",
                        newArtifact.getComposition(),
                        newArtifact.getDimensions() != null ? newArtifact.getDimensions().toString() : "N/A",
                        newArtifact.getWeight(),
                        String.join(", ", newArtifact.getTags()),
                        newArtifact.getCurrentPlace()
                );

// TextArea setup
                TextArea previewArea = new TextArea(previewText);
                previewArea.setEditable(false);
                previewArea.setWrapText(true);
                previewArea.setMaxWidth(Double.MAX_VALUE);
                previewArea.setMaxHeight(Double.MAX_VALUE);

// Image setup
                ImageManager imageManager = ImageManager.getInstance();
                File imageFile = imageManager.getImage(newArtifact.getImagePath());

                StackPane imageFrame = new StackPane();
                imageFrame.getStyleClass().add("image-frame");
                imageFrame.setMinSize(208, 208);
                imageFrame.setMaxSize(208, 208);

                //If there is no image, we don't get frames to keep the UI busy.

                if (imageFile != null && imageFile.exists()) {
                    ImageView imageView = new ImageView(new Image(imageFile.toURI().toString()));
                    imageView.setFitWidth(200);
                    imageView.setFitHeight(200);
                    imageView.setPreserveRatio(false);
                    imageFrame.getChildren().add(imageView);
                } else {
                    // Optional: placeholder if no image found
                    imageFrame.getChildren().add(new Label("No Image"));
                }


// Layout: TextArea on left, spacer and imageFrame on right
                Region spacer = new Region();
                spacer.setMinWidth(20);

                HBox contentBox = new HBox(previewArea, spacer, imageFrame);
                contentBox.setSpacing(10);
                contentBox.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(previewArea, Priority.ALWAYS);

                DialogPane dialogPane = confirmDialog.getDialogPane();
                dialogPane.setContent(contentBox);
                dialogPane.getStylesheets().add(getClass().getResource("/hacp/resources/styles/histofact-style.css").toExternalForm());
                dialogPane.getStyleClass().add("dialog-pane");

                dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                Optional<ButtonType> result = confirmDialog.showAndWait();
                if (result.isEmpty() || result.get() != ButtonType.OK) {
                    statusLabel.setText("Artifact save canceled.");
                    return;
                }

                JsonManager.getInstance().appendArtifactToFile(newArtifact);
                catalog.addArtifact(newArtifact);
                displayArtifactResults(catalog.getAllArtifacts());
                statusLabel.setText("Artifact saved successfully.");


            } catch (Exception ex) {
                ex.printStackTrace();
                statusLabel.setText("Error saving artifact: " + ex.getMessage());
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> {
            detailsVBox.getChildren().clear();
            statusLabel.setText("Artifact creation canceled.");
            //The code returns the main page after the cancel button is clicked.
            displayArtifactResults(catalog.getAllArtifacts());
        });

        // Changed to a grid layout, looks better
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(15, 15, 15, 15));

        int row = 0;

        formGrid.add(new Label("Artifact ID:"), 0, row);
        formGrid.add(idField, 1, row++);

        formGrid.add(new Label("Artifact Name:"), 0, row);
        formGrid.add(nameField, 1, row++);

        formGrid.add(new Label("Category:"), 0, row);
        formGrid.add(categoryComboBox, 1, row++);

        formGrid.add(new Label("Discovery Date:"), 0, row);
        formGrid.add(discoveryDatePicker, 1, row++);

        formGrid.add(new Label("Civilization:"), 0, row);
        formGrid.add(civilizationField, 1, row++);

        formGrid.add(new Label("Discovery Location:"), 0, row);
        formGrid.add(locationField, 1, row++);

        formGrid.add(new Label("Current Place:"), 0, row);
        formGrid.add(currentPlaceField, 1, row++);

        formGrid.add(new Label("Composition:"), 0, row);
        formGrid.add(compositionField, 1, row++);

        formGrid.add(new Label("Dimensions (W × L × H):"), 0, row);
        formGrid.add(new HBox(5, widthField, lengthField, heightField), 1, row++);

        formGrid.add(new Label("Weight (kg):"), 0, row);
        formGrid.add(weightField, 1, row++);

        formGrid.add(new Label("Tags:"), 0, row);
        formGrid.add(tagsField, 1, row++);

        // Add the new form to the detailsVBox
        formGrid.add(new Label("Image Path:"), 0, row);
        formGrid.add(new HBox(5, imagePathField, chooseImageButton), 1, row++);

        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        formGrid.add(buttonBox, 1, row);
        GridPane.setHalignment(buttonBox, HPos.RIGHT);

        detailsVBox.getChildren().add(formGrid);

    }

    public void handleEditArtifact() {
        if (selectedArtifact == null) {
            statusLabel.setText("No artifact selected for editing.");
            return;
        }

        detailsVBox.getChildren().clear();

        // Fields
        TextField nameField = new TextField(Optional.ofNullable(selectedArtifact.getArtifactName()).orElse(""));
        ComboBox<Category> categoryComboBox = new ComboBox<>(FXCollections.observableArrayList(Category.values()));
        categoryComboBox.setValue(Optional.ofNullable(selectedArtifact.getCategory()).orElse(null));

        TextField civilizationField = new TextField(Optional.ofNullable(selectedArtifact.getCivilization()).orElse(""));
        TextField locationField = new TextField(Optional.ofNullable(selectedArtifact.getDiscoveryLocation()).orElse(""));
        TextField compositionField = new TextField(Optional.ofNullable(selectedArtifact.getComposition()).orElse(""));
        DatePicker discoveryDatePicker = new DatePicker(Optional.ofNullable(selectedArtifact.getDiscoveryDate()).orElse(null));
        TextField currentPlaceField = new TextField(Optional.ofNullable(selectedArtifact.getCurrentPlace()).orElse(""));
        TextField weightField = new TextField(String.valueOf(Optional.ofNullable(selectedArtifact.getWeight()).orElse(0.0)));

        Dimension dim = Optional.ofNullable(selectedArtifact.getDimensions()).orElse(new Dimension(0, 0, 0));
        TextField widthField = new TextField(String.valueOf(dim.getWidth()));
        TextField lengthField = new TextField(String.valueOf(dim.getLength()));
        TextField heightField = new TextField(String.valueOf(dim.getHeight()));

        TextField tagsField = new TextField(String.join(", ", Optional.ofNullable(selectedArtifact.getTags()).orElse(new ArrayList<>())));
        TextField imagePathField = new TextField(Optional.ofNullable(selectedArtifact.getImagePath()).orElse(""));
        imagePathField.setEditable(false);

        Button chooseImageButton = new Button("Browse");
        chooseImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Artifact Image");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            File selectedFile = fileChooser.showOpenDialog(null);  // <-- Changed this line only
            if (selectedFile != null) {
                String imagePath = ImageManager.getInstance().saveImage(selectedFile, nameField.getText().replaceAll("\\s+", "").toLowerCase());
                if (imagePath != null) {
                    imagePathField.setText(imagePath);
                }
            }
        });

        Button removeImageButton = new Button("Remove Image");
        removeImageButton.setOnAction(e -> {
            // Clear the image path
            imagePathField.setText("No image. Add one!");
            // Set the text color to white
            imagePathField.setStyle("-fx-text-fill: white;");
        });

        // Save / Cancel buttons
        Button saveButton = new Button("Save Changes");
        Button cancelButton = new Button("Cancel");

        saveButton.setOnAction(e -> {
            try {
                selectedArtifact.setArtifactName(nameField.getText().trim());
                selectedArtifact.setCategory(categoryComboBox.getValue());
                selectedArtifact.setCivilization(civilizationField.getText().trim());
                selectedArtifact.setDiscoveryLocation(locationField.getText().trim());
                selectedArtifact.setCurrentPlace(currentPlaceField.getText().trim());
                selectedArtifact.setComposition(compositionField.getText().trim());
                selectedArtifact.setDiscoveryDate(discoveryDatePicker.getValue());

                try {
                    double width = Double.parseDouble(widthField.getText());
                    double length = Double.parseDouble(lengthField.getText());
                    double height = Double.parseDouble(heightField.getText());
                    selectedArtifact.setDimensions(new Dimension(width, length, height));
                } catch (NumberFormatException ignored) {
                    statusLabel.setText("Invalid dimensions. Skipped dimensions.");
                }

                try {
                    double weight = Double.parseDouble(weightField.getText());
                    selectedArtifact.setWeight(weight);
                } catch (NumberFormatException ignored) {
                    statusLabel.setText("Invalid weight. Skipped weight.");
                }

                String[] tagArray = tagsField.getText().split(",");
                ArrayList<String> tags = new ArrayList<>();
                for (String tag : tagArray) {
                    tag = tag.trim();
                    if (!tag.isEmpty()) tags.add(tag);
                }
                selectedArtifact.setTags(tags);
                String imagePathText = imagePathField.getText().trim();
                selectedArtifact.setImagePath(imagePathText.isEmpty() ? null : imagePathText);

                artifactController.updateArtifact(selectedArtifact);
                statusLabel.setText("Artifact updated successfully.");
                displayArtifactResults(catalog.getAllArtifacts());
            } catch (Exception ex) {
                statusLabel.setText("Error: Invalid input. Please check all fields.");
                ex.printStackTrace();
            }
        });

        cancelButton.setOnAction(e -> {
            statusLabel.setText("Edit cancelled.");
            displayArtifactResults(catalog.getAllArtifacts());
        });

        // Layout
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(15));

        int row = 0;
        formGrid.add(new Label("Artifact Name:"), 0, row);
        formGrid.add(nameField, 1, row++);

        formGrid.add(new Label("Category:"), 0, row);
        formGrid.add(categoryComboBox, 1, row++);

        formGrid.add(new Label("Discovery Date:"), 0, row);
        formGrid.add(discoveryDatePicker, 1, row++);

        formGrid.add(new Label("Civilization:"), 0, row);
        formGrid.add(civilizationField, 1, row++);

        formGrid.add(new Label("Discovery Location:"), 0, row);
        formGrid.add(locationField, 1, row++);

        formGrid.add(new Label("Current Place:"), 0, row);
        formGrid.add(currentPlaceField, 1, row++);

        formGrid.add(new Label("Composition:"), 0, row);
        formGrid.add(compositionField, 1, row++);

        formGrid.add(new Label("Dimensions (W × L × H):"), 0, row);
        formGrid.add(new HBox(5, widthField, lengthField, heightField), 1, row++);

        formGrid.add(new Label("Weight (kg):"), 0, row);
        formGrid.add(weightField, 1, row++);

        formGrid.add(new Label("Tags:"), 0, row);
        formGrid.add(tagsField, 1, row++);

        formGrid.add(new Label("Image Path:"), 0, row);
        formGrid.add(new HBox(5, imagePathField, chooseImageButton, removeImageButton), 1, row++);

        Region spacer = new Region();
        spacer.setMinHeight(20);
        formGrid.add(spacer, 0, row++, 2, 1);

        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        formGrid.add(buttonBox, 1, row);
        GridPane.setHalignment(buttonBox, HPos.RIGHT);

        detailsVBox.getChildren().add(formGrid);
    }

    @FXML
    public void handleDeleteArtifact() {
        if (selectedArtifact == null) {
            statusLabel.setText("No artifact selected for deletion.");
            return;
        }

        Dialog<ButtonType> confirmDialog = new Dialog<>();
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setHeaderText("Are you sure you want to delete this artifact?");

        TextArea content = new TextArea("Artifact: " + selectedArtifact.getArtifactName());
        content.setWrapText(true);
        content.setEditable(false);
        content.setPrefWidth(400);
        content.setPrefHeight(100);
        content.getStyleClass().add("text-area");

        confirmDialog.getDialogPane().setContent(content);
        confirmDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        confirmDialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/hacp/resources/styles/histofact-style.css").toExternalForm()
        );
        confirmDialog.getDialogPane().getStyleClass().add("dialog-pane");

        Optional<ButtonType> result = confirmDialog.showAndWait();
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

    //The user can now refresh to default display anytime
    @FXML
    public void handleRefresh() {
        displayArtifactResults(catalog.getAllArtifacts());
        statusLabel.setText("Artifact list refreshed.");
        searchField.clear();
        searchFieldChoice.setValue("All Fields");
        selectedTags.clear();
        selectedTagsGrid.getChildren().clear();
        tagListView.getSelectionModel().clearSelection();
    }

    //Methods for sorting the artifacts
    //Can sort alphabetically, by discovery date, category, and civilization
    @FXML
    public void handleSortAZ() {
        List<Artifact> sorted = new ArrayList<>(catalog.getAllArtifacts());
        sorted.sort(Comparator.comparing(Artifact::getArtifactName, String.CASE_INSENSITIVE_ORDER));
        displayArtifactResults(sorted);
        statusLabel.setText("Sorted A → Z");
    }

    @FXML
    public void handleSortZA() {
        List<Artifact> sorted = new ArrayList<>(catalog.getAllArtifacts());
        sorted.sort(Comparator.comparing(Artifact::getArtifactName, String.CASE_INSENSITIVE_ORDER).reversed());
        displayArtifactResults(sorted);
        statusLabel.setText("Sorted Z → A");
    }

    @FXML
    public void handleSortByDiscoveryDateAsc() {
        List<Artifact> sorted = new ArrayList<>(catalog.getAllArtifacts());
        sorted.sort(Comparator.comparing(Artifact::getDiscoveryDate));
        displayArtifactResults(sorted);
        statusLabel.setText("Sorted by earliest discovery date");
    }

    @FXML
    public void handleSortByDiscoveryDateDesc() {
        List<Artifact> sorted = new ArrayList<>(catalog.getAllArtifacts());
        sorted.sort(Comparator.comparing(Artifact::getDiscoveryDate).reversed());
        displayArtifactResults(sorted);
        statusLabel.setText("Sorted by latest discovery date");
    }
    @FXML
    public void handleSortByCategory() {
        List<Artifact> sorted = new ArrayList<>(catalog.getAllArtifacts());
        sorted.sort(Comparator.comparing(a -> a.getCategory().toString(), String.CASE_INSENSITIVE_ORDER));
        displayArtifactResults(sorted);
        statusLabel.setText("Sorted by category.");
    }

    @FXML
    public void handleSortByCivilization() {
        List<Artifact> sorted = new ArrayList<>(catalog.getAllArtifacts());
        sorted.sort(Comparator.comparing(Artifact::getCivilization, String.CASE_INSENSITIVE_ORDER));
        displayArtifactResults(sorted);
        statusLabel.setText("Sorted by civilization.");
    }

    public void handleImport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Artifacts (JSON)");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            // Alerts are changed to be dialogs so that we get more freedom in their UI design.
            Dialog<ButtonType> confirmDialog = new Dialog<>();
            confirmDialog.setTitle("Confirm Import");
            confirmDialog.setHeaderText("Are you sure you want to import this file?");
            TextArea content = new TextArea("File: " + file.getName());
            content.setWrapText(true);
            content.setEditable(false);
            content.setPrefWidth(400);
            content.setPrefHeight(100);
            content.getStyleClass().add("text-area");
            confirmDialog.getDialogPane().setContent(content);

            confirmDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // We use the .css style sheet for dialog design.
            confirmDialog.getDialogPane().getStylesheets().add(
                    getClass().getResource("/hacp/resources/styles/histofact-style.css").toExternalForm()
            );
            confirmDialog.getDialogPane().getStyleClass().add("dialog-pane");

            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    jsonManager.setFile(file);
                    ArrayList<Artifact> imported = jsonManager.importArtifacts();

                    for (Artifact a : imported) {
                        // If artifact has no ID, assign a new unique one
                        if (a.getArtifactId() == null || a.getArtifactId().isBlank()) {
                            String newId;
                            boolean isDuplicate;
                            do {
                                newId = UUID.randomUUID().toString();
                                isDuplicate = false;
                                for (Artifact existing : catalog.getAllArtifacts()) {
                                    if (newId.equals(existing.getArtifactId())) {
                                        isDuplicate = true;
                                        break;
                                    }
                                }
                            } while (isDuplicate);
                            a.setArtifactId(newId);
                        }

                        if (catalog.getAllArtifacts().contains(a)) {
                            continue;
                        }
                        catalog.addArtifact(a);
                        JsonManager.getInstance().exportArtifacts(catalog.getAllArtifacts());
                    }

                    displayArtifactResults(catalog.getAllArtifacts());
                    statusLabel.setText("Imported " + imported.size() + " artifacts.");
                } catch (Exception e) {
                    // Show error dialog with your style
                    Dialog<ButtonType> errorDialog = new Dialog<>();
                    errorDialog.setTitle("Import Error");
                    errorDialog.setHeaderText("Error importing artifacts:");
                    TextArea errorContent = new TextArea(e.getMessage());
                    errorContent.setWrapText(true);
                    errorContent.setEditable(false);
                    errorContent.setPrefWidth(400);
                    errorContent.setPrefHeight(150);
                    errorContent.getStyleClass().add("text-area");
                    errorDialog.getDialogPane().setContent(errorContent);
                    errorDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

                    errorDialog.getDialogPane().getStylesheets().add(
                            getClass().getResource("/hacp/resources/styles/histofact-style.css").toExternalForm()
                    );
                    errorDialog.getDialogPane().getStyleClass().add("dialog-pane");

                    errorDialog.showAndWait();

                    statusLabel.setText("Error importing artifacts.");
                }
            } else {
                statusLabel.setText("Import cancelled.");
            }
        }
    }



    public void handleExport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Artifacts (JSON)");
        fileChooser.setInitialFileName("artifacts.json");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File file = fileChooser.showSaveDialog(null);

        //Are you sure you want to use export to this file? alert added.
        if (file != null) {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Confirm Export");
            dialog.setHeaderText(null);
            dialog.getDialogPane().setPrefSize(650, 200);

            // Content text area
            TextArea confirmText = new TextArea(
                    "Are you sure you want to export to this file?\n\n" +
                            "File: " + file.getAbsolutePath()
            );
            confirmText.setWrapText(true);
            confirmText.setEditable(false);
            confirmText.setPrefWidth(600);
            confirmText.setPrefHeight(100);
            confirmText.getStyleClass().add("text-area");

            dialog.getDialogPane().setContent(confirmText);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            dialog.getDialogPane().getStylesheets().add(
                    getClass().getResource("/hacp/resources/styles/histofact-style.css").toExternalForm()
            );
            dialog.getDialogPane().getStyleClass().add("dialog-pane");

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                jsonManager.setFile(file);
                jsonManager.exportArtifacts(catalog.getAllArtifacts());
                statusLabel.setText("Exported " + catalog.getAllArtifacts().size() + " artifacts.");
            } else {
                statusLabel.setText("Export cancelled.");
            }
        }
    }

    @FXML
    public void handleUserManual(ActionEvent event) {
        //User manual now uses HTML file to function. Same with About.
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("User Manual");
        dialog.setHeaderText(null);

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Correct path relative to classpath
        URL manualUrl = getClass().getResource("/UserManual.html");
        if (manualUrl != null) {
            webEngine.load(manualUrl.toExternalForm());
        } else {
            webEngine.loadContent("<html><body><h2>User manual not found.</h2></body></html>");
        }

        webView.setPrefSize(600, 400);
        dialog.getDialogPane().setContent(webView);
        dialog.getDialogPane().setPrefSize(650, 450);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/hacp/resources/styles/histofact-style.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        dialog.showAndWait();
    }


    @FXML
    public void handleAbout(ActionEvent event) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("About");
        dialog.setHeaderText(null);

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        // Load About.html from resources
        URL aboutUrl = getClass().getResource("/About.html");
        if (aboutUrl != null) {
            webEngine.load(aboutUrl.toExternalForm());
        } else {
            webEngine.loadContent("<html><body><h2>About content not found.</h2></body></html>");
        }

        webView.setPrefSize(600, 400);

        dialog.getDialogPane().setContent(webView);
        dialog.getDialogPane().setPrefSize(650, 450);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Add your custom stylesheet to style the dialog pane itself (buttons, borders, etc.)
        dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/hacp/resources/styles/histofact-style.css").toExternalForm()
        );
        dialog.getDialogPane().getStyleClass().add("dialog-pane");

        dialog.showAndWait();
    }


    public void handleEdit(ActionEvent event) {

    }
}
