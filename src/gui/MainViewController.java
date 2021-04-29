package gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 *
 * @author edlcorrea
 */

public class MainViewController {
    
    @FXML
    private Label testLabel;
    @FXML
    private Button testButton;
    
    public void checkSync() {
        updateLabel("Sync.");
        updateButton("Checked");
        disableButton();
    }
    public void updateLabel(String labelText) {
        testLabel.setText(labelText);
    }
    public void updateButton(String bttnText) {
        testButton.setText(bttnText);
    }
    public void disableButton() {
        testButton.setDisable(true);
    }
}
