package gui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
    
    
    @FXML
    private TableView memoryTable;
    @FXML
    private TableColumn positionCol, firstByteCol, secondByteCol, thirdByteCol;
    
    /* INITIALIZE APP */
    public void initialize() {
        /* EDITOR */
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
        
        /* MEMORY */
        positionCol.prefWidthProperty().bind(memoryTable.widthProperty().divide(4));
        firstByteCol.prefWidthProperty().bind(memoryTable.widthProperty().divide(4));
        secondByteCol.prefWidthProperty().bind(memoryTable.widthProperty().divide(4));
        thirdByteCol.prefWidthProperty().bind(memoryTable.widthProperty().divide(4));
        memoryTable.setPlaceholder(new Label(""));
        
        memoryTable.setItems(populateMemory());
        
        
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
    
    /* MEMORY */
    private ObservableList<String> populateMemory() {
        return FXCollections.observableArrayList(
            "0000", "00000000", "00000000", "00000000"
        );
    }
                
}
