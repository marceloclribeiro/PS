package gui;

import java.util.ArrayList;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 *
 * @author edlcorrea
 */

public class MainViewController {
    @FXML
    private TextArea codeArea;
    @FXML
    public TextArea lineCounter;
    private int lineNum;
    
    /* INITIALIZE APP */
    public void initialize() {
        // bind code editor to line counter
        lineCounter.scrollTopProperty().bindBidirectional(codeArea.scrollTopProperty());
        // set initial line counter value
        lineCounter.setText("1\n");
        this.lineNum = 1;
        
        // listen to lines being added or removed on code editor
        codeArea.getParagraphs().addListener((ListChangeListener<CharSequence>) (var change) -> {
            while(change.next()) {
                if (lineNum != codeArea.getParagraphs().size()) {
                    // update line counter
                    updateLineCounter((int) codeArea.getParagraphs().size());
                }           
            }
        });
    };
    
    /* CODE EDITOR */
    public void updateLineCounter(Integer num) {
        this.lineNum = num;
        Integer element;
        ArrayList lineArray = new ArrayList();
        String lines;
        
        lineArray.clear();
        
        for(int i=0; i < num; i++) {
            element = i+1;
            lineArray.add(element.toString());
        }
        
        lines = String.join("\n", lineArray);
        this.lineCounter.setText(lines);
    };
    
    public String getCode() {
        return codeArea.getText();    
    }
    
    public void setCode(String code) {
        codeArea.setText(code);
    }
    
}
