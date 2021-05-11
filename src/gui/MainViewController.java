package gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

/**
 *
 * @author edlcorrea
 */

public class MainViewController {
    @FXML
    private TextArea codeArea;
    @FXML
    private TextArea lineCounter;
    @FXML
    private Tab codeTab;
    
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
    
    public File chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle ("Open input file");
        
        // create extension filter (.txt only)
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("TEXT files (*.txt)", "*.txt");
        
        // apply filter
        fileChooser.getExtensionFilters().add(filter);
        
        File file = fileChooser.showOpenDialog(null);
        
        if(file != null) {
            return file;
        } else {
            return null;
        }
    }
    
    public void loadFile() throws FileNotFoundException {
        File inputFile = chooseFile();
        Scanner scanner = new Scanner(inputFile);
        
        StringBuilder code = new StringBuilder();
        
        while(scanner.hasNextLine()) {
            code.append(scanner.nextLine()).append("\n");
        }
        
        scanner.close();
        
        // update code area
        this.codeArea.setText(code.toString());
        this.codeArea.requestFocus();
        this.codeArea.end();
        //update title
        this.codeTab.setText(inputFile.getName().toString());
        
    }
}
