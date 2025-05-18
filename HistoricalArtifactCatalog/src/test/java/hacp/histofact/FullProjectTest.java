package hacp.histofact;

import org.junit.jupiter.api.*;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FullProjectTest {

    private static Artifact artifact;
    private static ArtifactCatalog catalog;
    private static ArtifactController controller;
    private static JsonManager jsonManager;

    @BeforeAll
    public static void setupOnce() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // JavaFX already initialized
        }
        catalog = new ArtifactCatalog();
        controller = new ArtifactController(catalog);
        artifact = new Artifact("T001", "Test Vase", Category.POTTERY);
        artifact.setCivilization("Test Civilization");
        artifact.setDiscoveryLocation("Test Location");
        artifact.setComposition("Test Material");
        artifact.setDiscoveryDate(LocalDate.of(2020, 1, 1));
        artifact.setCurrentPlace("Test Museum");
        artifact.setDimensions(new Dimension(10, 20, 5));
        artifact.setWeight(2.5);
        artifact.addTag("test");
        artifact.setImagePath("/images/test.jpg");

        jsonManager = JsonManager.getInstance();
    }

    @Test
    @Order(1)
    public void testArtifactGetters() {
        assertEquals("T001", artifact.getArtifactId());
        assertEquals("Test Vase", artifact.getArtifactName());
        assertEquals("Test Civilization", artifact.getCivilization());
        assertEquals(2.5, artifact.getWeight());
    }

    @Test
    @Order(2)
    public void testCatalogAddRemoveUpdate() {
        catalog.addArtifact(artifact);
        assertEquals(1, catalog.getAllArtifacts().size());
        Artifact updated = new Artifact("T001", "Updated Vase", Category.POTTERY);
        assertTrue(catalog.updateArtifact(updated));
        assertTrue(catalog.removeArtifact("T001"));
        assertEquals(0, catalog.getAllArtifacts().size());
    }

    @Test
    @Order(3)
    public void testArtifactControllerAddDelete() {
        controller.addArtifact(artifact);
        assertEquals(1, catalog.getAllArtifacts().size());
        controller.deleteArtifact("T001");
        assertEquals(0, catalog.getAllArtifacts().size());
    }

    @Test
    @Order(4)
    public void testJsonManagerExportImport() throws Exception {
        File testFile = File.createTempFile("artifacts_test", ".json");
        jsonManager.setFile(testFile);
        ArrayList<Artifact> list = new ArrayList<>();
        list.add(artifact);
        jsonManager.exportArtifacts(list);

        ArrayList<Artifact> imported = jsonManager.importArtifacts();
        assertEquals(1, imported.size());
        assertEquals("Test Vase", imported.get(0).getArtifactName());
        testFile.deleteOnExit();
    }

    @Test
    @Order(5)
    public void testImageManagerSaveImageFail() {
        File dummy = new File("non_existent.jpg");
        String result = ImageManager.getInstance().saveImage(dummy, "fake");
        assertNull(result); // Expected null since file doesn't exist
    }

    @Test
    @Order(6)
    public void testFilterByTags() {
        controller.addArtifact(artifact);
        ArrayList<String> tags = new ArrayList<>();
        tags.add("test");
        ArrayList<Artifact> filtered = controller.filterByTags(tags);
        assertEquals(1, filtered.size());
        assertEquals("Test Vase", filtered.get(0).getArtifactName());
    }

    @Test
    @Order(7)
    public void testSearchArtifacts() {
        catalog.getAllArtifacts().clear();
        controller.addArtifact(artifact);
        ArrayList<Artifact> results = controller.searchArtifacts("test", "All Fields");
        assertEquals(1, results.size());
    }

    @Test
    @Order(8)
    public void testMainControllerUtilityMethods() {
        Platform.runLater(() -> {
            MainController mc = new MainController();
            assertDoesNotThrow(() -> mc.handleAbout(null));
            assertDoesNotThrow(() -> mc.handleUserManual(null));
        });
    }
}
