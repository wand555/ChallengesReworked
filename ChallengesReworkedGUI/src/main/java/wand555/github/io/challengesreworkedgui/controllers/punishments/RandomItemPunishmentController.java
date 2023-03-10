package wand555.github.io.challengesreworkedgui.controllers.punishments;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;
import wand555.github.io.challengesreworked.Common;
import wand555.github.io.challengesreworked.punishments.randomitem.RandomItemPunishment;
import wand555.github.io.challengesreworked.punishments.randomitem.RandomItemPunishmentCommon;
import wand555.github.io.challengesreworkedgui.ChallengeApplication;
import wand555.github.io.challengesreworkedgui.rows.PunishmentRow;

import java.io.IOException;

public class RandomItemPunishmentController extends PunishmentController implements RandomItemPunishment {


    @FXML
    private TextField itemAmountTextField;

    @FXML
    @Override
    public void initialize() {
        common = new RandomItemPunishmentCommon();
        super.initialize();

        itemAmountTextField.setTextFormatter(new TextFormatter<>(
                new IntegerStringConverter(),
                getCommon().getHowManyRemoved(),
                change -> {
                    String newText = change.getControlNewText();
                    if(newText.isEmpty()) {
                        getCommon().setHowManyRemoved(1);
                        return change;
                    }
                    if(newText.matches("^[0-9]+$")) {
                        int writtenAmount = Integer.parseInt(newText);
                        if(writtenAmount <= 99999) {
                            getCommon().setHowManyRemoved(writtenAmount);
                            return change;
                        }
                    }
                    return null;
                }));
    }

    @Override
    public PunishmentRow getAsOneLine() {
        try {
            FXMLLoader loader = new FXMLLoader(ChallengeApplication.class.getResource("punishments/random_item_punishment_row.fxml"), bundle);
            PunishmentRow root = loader.load();
            RandomItemPunishmentController rowController = loader.getController();
            rowController.setUnmodifiable(isUnmodifiable());

            rowController.common = getCommon();
            rowController.itemAmountTextField.setText(Integer.toString(getCommon().getHowManyRemoved()));
            rowController.affectTypeComboBox.setValue(getCommon().getAffectType());

            // changes needed if only global changes is true
            if(isUnmodifiable()) {
                rowController.affectTypeComboBox.setEditable(false);
                rowController.affectTypeComboBox.setDisable(true);
                rowController.itemAmountTextField.setEditable(false);
                rowController.itemAmountTextField.setDisable(true);
            }

            root.setPunishmentController(rowController);
            return root;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void extractDataFromPunishmentRow(PunishmentRow row) {
        super.extractDataFromPunishmentRow(row);
        RandomItemPunishmentController randomItemPunishmentController = (RandomItemPunishmentController) row.getPunishmentController();
        itemAmountTextField.setText(randomItemPunishmentController.itemAmountTextField.getText());
        if(isUnmodifiable()) {
            itemAmountTextField.setEditable(false);
            itemAmountTextField.setDisable(true);
        }
    }

    @Override
    public void setDataFromCommon(Common from, boolean thisActive) {
        super.setDataFromCommon(from, thisActive);
        itemAmountTextField.setText(Integer.toString(getCommon().getHowManyRemoved()));
        if(isUnmodifiable()) {
            itemAmountTextField.setDisable(true);
        }
    }

    @Override
    public RandomItemPunishmentCommon getCommon() {
        return (RandomItemPunishmentCommon) super.getCommon();
    }

    @Override
    public void setUnmodifiable(boolean unmodifiable) {
        super.setUnmodifiable(unmodifiable);
        itemAmountTextField.setDisable(unmodifiable);
    }
}
