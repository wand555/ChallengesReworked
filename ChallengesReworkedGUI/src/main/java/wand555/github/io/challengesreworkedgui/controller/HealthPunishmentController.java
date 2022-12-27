package wand555.github.io.challengesreworkedgui.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.util.converter.IntegerStringConverter;
import wand555.github.io.challengesreworked.punishments.AffectType;
import wand555.github.io.challengesreworked.punishments.health.HealthPunishment;
import wand555.github.io.challengesreworked.punishments.health.HealthPunishmentCommon;
import wand555.github.io.challengesreworkedgui.ChallengeApplication;
import wand555.github.io.challengesreworkedgui.PunishmentRow;
import wand555.github.io.challengesreworkedgui.Row;
import wand555.github.io.challengesreworkedgui.util.DisplayComboBox;

import java.io.IOException;
import java.util.ResourceBundle;

public class HealthPunishmentController extends PunishmentController implements HealthPunishment {

    @FXML
    private TextField heartsTextField;

    @FXML
    protected void initialize() {
        common = new HealthPunishmentCommon(0, AffectType.CAUSER, 1);
        super.initialize();

        heartsTextField.setTextFormatter(new TextFormatter<>(
                new IntegerStringConverter(),
                getCommon().getHealthAmount(),
                change -> {
                    String newText = change.getControlNewText();
                    if(newText.isEmpty()) {
                        getCommon().setHealthAmount(1);
                        return change;
                    }
                    if(newText.matches("^[0-9]+$")) {
                        int writtenAmount = Integer.parseInt(newText);
                        if(writtenAmount <= 99999) {
                            getCommon().setHealthAmount(writtenAmount);
                            return change;
                        }
                    }
                    return null;
                }));
    }

    @Override
    public PunishmentRow getAsOneLine() {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("wand555/github/io/challengesreworkedgui/lang_bundle");
            FXMLLoader loader = new FXMLLoader(ChallengeApplication.class.getResource("punishments/health_punishment_row.fxml"), bundle);
            PunishmentRow root = loader.load();
            HealthPunishmentController rowController = loader.getController();
            rowController.common = getCommon();
            rowController.heartsTextField.setText(Integer.toString(getHealthAmount()));
            rowController.affectTypeComboBox.setValue(getCommon().getAffectType());
            rowController.forAllChallenges.setSelected(forAllChallenges.isSelected());
            root.setPunishmentController(rowController);
            return root;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void extractDataFromPunishmentRow(PunishmentRow row) {
        super.extractDataFromPunishmentRow(row);
        HealthPunishmentController healthPunishmentController = (HealthPunishmentController) row.getPunishmentController();
        heartsTextField.setText(healthPunishmentController.heartsTextField.getText());
        System.out.println(heartsTextField.getText() + "!!!");
    }

    @Override
    public HealthPunishmentCommon getCommon() {
        return (HealthPunishmentCommon) common;
    }
}