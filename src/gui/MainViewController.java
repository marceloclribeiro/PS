package gui;

import logic.*;
import macro.*;
import montador.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private TableView<MemoryTuple> memoryTable;
    @FXML
    private TableColumn<MemoryTuple, String> positionCol, byteCol;
    private ObservableList<MemoryTuple> data = FXCollections.observableArrayList();
    
    @FXML
    private Label aLabel, xLabel, lLabel, bLabel, sLabel, tLabel, fLabel, swLabel, pcLabel;
    @FXML
    private Button runButton, stepButton;
    @FXML
    private MenuItem menuRun, menuStep, menuOpenFile;
    @FXML
    private Label statusLabel, resultLabel;
    
    private String loadedFilePath;
    private boolean hasLoaded = false;
    
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
        // set columns width
        positionCol.prefWidthProperty().bind(memoryTable.widthProperty().multiply(0.25));
        byteCol.prefWidthProperty().bind(memoryTable.widthProperty().multiply(0.75));
        memoryTable.setPlaceholder(new Label(""));
        
        populateMemoryTable();
        positionCol.setCellValueFactory(new PropertyValueFactory<>("tupleIndex"));
        byteCol.setCellValueFactory(new PropertyValueFactory<>("tupleByte"));
        
        memoryTable.setItems(data);
        
        statusLabel.setText(statusWaiting());
        
    };
    
    /* STATUS */
    public String statusWaiting() {
        menuOpenFile.setDisable(false);
        runButton.setDisable(true);
        stepButton.setDisable(true);
        menuRun.setDisable(true);
        menuStep.setDisable(true);
        resultLabel.setText("-");
        return "Waiting for input file";
    };

    public String statusReady() {
        runButton.setDisable(false);
        stepButton.setDisable(false);
        menuRun.setDisable(false);
        menuStep.setDisable(false);
        return "Ready to run";
    };
    
    public String statusRunning() {
        menuOpenFile.setDisable(true);
        return "Running";
    };
    
    public String statusCompleted() {
        menuOpenFile.setDisable(true);
        runButton.setDisable(true);
        stepButton.setDisable(true);
        menuRun.setDisable(true);
        menuStep.setDisable(true);
        resultLabel.setText(String.valueOf(CPU.getA().toInt()));
        return "Completed (reset to run again)";
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
    };
    
    /* FILE LOADER */
    public File chooseFile() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle ("Open input file");

            // create extension filter (.txt only)
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Input files (*.txt, *.asm, *.sic)", "*.txt", "*.asm", "*.sic");

            // apply filter
            fileChooser.getExtensionFilters().add(filter);

            File file = fileChooser.showOpenDialog(null);
            
            if(file != null) {
                return file;    
            } else {
                return null;
            }
            
        }catch(NullPointerException e){  
            return null;
        }
    };
    
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
            this.loadedFilePath = inputFile.getAbsolutePath();
        
        } catch(NullPointerException e){
            System.out.println("Invalid input path.");
        }
        
    };
    
    /* MEMORY */
    public void populateMemoryTable() {
        String24[] memFx = CPU.getMem().getMemory();
        
        for (int i=0; i<CPU.getMemSize(); i++) {
            char[] bitsFx = memFx[i].getBits();
            if(bitsFx[0] == '0' || bitsFx[0] == '1'){
                data.add(new MemoryTuple(String.valueOf(i), String.valueOf(bitsFx)));
            } else{
                data.add(new MemoryTuple(String.valueOf(i)));
            }
        }
        
    };      
    
    /* REGISTERS */
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
    
    /* GENERAL */
    public void expandMacros() {
       File macroProcOutput = Macro_Processor.run(this.loadedFilePath);
       
    }
    
    public void runAll() {
        CPU.loadMem(this.loadedFilePath);
        this.hasLoaded = true;
        CPU.run();

        updateRegisters();
        data.clear();
        populateMemoryTable();
        statusLabel.setText(statusCompleted());
    
    }
    
    public void runStep() {
        boolean hasNextStep = CPU.step();
        
        // load memory only once
        if(!hasLoaded) {
            CPU.loadMem(this.loadedFilePath);
            this.hasLoaded = true;
        }
        
        updateRegisters();
        data.clear();
        populateMemoryTable();
        
        if(hasNextStep) {
            statusLabel.setText(statusRunning());
        } else {
            statusLabel.setText(statusCompleted());
        }
        
    };
    
    public void resetAll() {
        CPU.reset();
        codeArea.setText("");
        this.codeTab.setText("Untitled");
        
        data.clear();
//        populateMemoryTable();
        updateRegisters();
        this.hasLoaded = false;
        statusLabel.setText(statusWaiting());
    };

}
