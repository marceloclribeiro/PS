package gui;

import logic.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    @FXML
    private Label aLabel, xLabel, lLabel, bLabel, sLabel, tLabel, fLabel, swLabel, pcLabel;
    @FXML
    private Button runButton, stepButton, resetButton;
    @FXML
    private Label statusLabel;
    
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
        statusLabel.setText(statusWaiting());
        
    };
    
    public String statusWaiting() {
        runButton.setDisable(true);
        stepButton.setDisable(true);
        return "Waiting for input file";
    }
    
    public String statusReady() {
        runButton.setDisable(false);
        stepButton.setDisable(false);
        return "Ready to run";
    }
    
    public String statusRunning() {
        return "Running";
    }
    
    public String statusCompleted() {
        runButton.setDisable(true);
        stepButton.setDisable(true);
        return "Completed";
    }
    
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
        try {
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
            
        }catch(NullPointerException e){  
            e.printStackTrace();
            return null;
        }
    }
    
    public void loadFile() throws FileNotFoundException {
        try {
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
            statusLabel.setText(statusReady());
            CPU.loadMem(inputFile.getAbsolutePath());
        
        } catch(NullPointerException e){
            System.out.println("Invalid input path.");
        }
        
    }
    
    /* MEMORY */
    private ObservableList<String> populateMemory() {
        return FXCollections.observableArrayList(
            "0000", "00000000", "00000000", "00000000"
        );
    }
    
    public void updateRegisters() {
        aLabel.setText(String.valueOf(CPU.getA().toInt()));
        xLabel.setText(String.valueOf(CPU.getX().toInt()));
        lLabel.setText(String.valueOf(CPU.getL().toInt()));
        bLabel.setText(String.valueOf(CPU.getB().toInt()));
        sLabel.setText(String.valueOf(CPU.getS().toInt()));
        tLabel.setText(String.valueOf(CPU.getT().toInt()));
        fLabel.setText(String.valueOf(CPU.getF().toInt()));
        swLabel.setText(String.valueOf(CPU.getSW().toInt()));
        pcLabel.setText(String.valueOf(CPU.getPC().toInt()));       
    }
    
    public void runAll() {
        CPU.run();
        updateRegisters();
        
        statusLabel.setText(statusCompleted());
    }
    
    public void runStep() {
        boolean hasNextStep = CPU.step();
        updateRegisters();
        
        if(hasNextStep) {
            statusLabel.setText(statusRunning());
        } else {
            statusLabel.setText(statusCompleted());
        }
        
    }
    
    public void resetAll() {
        CPU.reset();
        statusLabel.setText(statusWaiting());
        codeArea.setText("");
        updateRegisters();
    }

}
