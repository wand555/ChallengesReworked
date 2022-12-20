package wand555.github.io.challengesreworkedgui.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.bukkit.Material;
import org.controlsfx.control.ListSelectionView;
import wand555.github.io.challengesreworked.logging.PlaceHolderHandler;
import wand555.github.io.challengesreworkedgui.HelloApplication;
import wand555.github.io.challengesreworkedgui.MaterialRow;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NoCraftingChallengeController extends ChallengeController {
    @FXML
    private ToggleButton activateButton;
    @FXML
    private ListView<MaterialRow> craftableItemsList;
    @FXML
    private TitledPane noCraftingTitledPane;
    @FXML
    private Button addCraftableItemButton;
    @FXML
    private Button addForbiddenUIButton;
    @FXML
    private ListView forbiddenUIList;

    private final BooleanProperty active = new SimpleBooleanProperty(false);

    @FXML
    @Override
    protected void initialize() {

        /*System.out.println(VersionInfo.getRuntimeVersion());*/
        super.initialize();
        craftableItemsList.setItems(FXCollections.observableArrayList(new MaterialRow(Material.BLAZE_POWDER), new MaterialRow(Material.ENDER_EYE)));
        addCraftableItemButton.setOnAction(event -> {
            initCraftableItemsUI();
        });
    }

    private void initCraftableItemsUI() {
        Stage stage = new Stage();
        StackPane root = new StackPane();
        ListSelectionView<MaterialRow> listSelectionView = new ListSelectionView<>();
        List<Material> obtainableItems = Stream.of(Material.values())
                .filter(Material::isItem)
                .filter(Predicate.not(Material::isAir))
                .filter(Predicate.not(Material::isLegacy))
                .sorted(Comparator.comparing(Enum::toString))
                .toList();
        List<MaterialRow> rows = obtainableItems.stream().map(MaterialRow::new).toList();
        listSelectionView.setSourceItems(FXCollections.observableArrayList(rows));
        listSelectionView.setTargetItems(craftableItemsList.getItems());
        root.getChildren().add(listSelectionView);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        //stage.initOwner();
        stage.show();
        stage.setOnCloseRequest(event1 -> craftableItemsList.refresh());
        listSelectionView.setCellFactory(param -> {
            ListCell<MaterialRow> cell = new ListCell<>();
            ContextMenu contextMenu = new ContextMenu(new MenuItem("TEST"));

            cell.emptyProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue) {
                    cell.setContextMenu(null);
                }
                else {
                    cell.setContextMenu(contextMenu);
                }
            });
            cell.graphicProperty().bind(cell.itemProperty());
            return cell;
        });
    }
}