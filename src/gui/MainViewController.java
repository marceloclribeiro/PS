package gui;

import logic.*;
import macro.*;
import montador.*;
import ligador.*;
import carregador.*;
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
import javafx.scene.control.TabPane;
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
    private TabPane tabPane;
    
    @FXML
    private TableView<MemoryTuple> memoryTable;
    @FXML
    private TableColumn<MemoryTuple, String> positionCol, byteCol;
    private ObservableList<MemoryTuple> data = FXCollections.observableArrayList();
    
    @FXML
    private Label aLabel, xLabel, lLabel, bLabel, sLabel, tLabel, fLabel, swLabel, pcLabel;
    @FXML
    private Button assembleButton, runButton, stepButton;
    @FXML
    private MenuItem menuRun, menuStep, menuOpenFile;
    @FXML
    private Label statusLabel, resultLabel;
    @FXML
    private TextArea errorConsole;
    
    private ArrayList<String> loadedFilesPath = new ArrayList();
    private File binaryFinalFile;
    private boolean hasLoaded = false;
    
    /* INITIALIZE APP */
    public void initialize() {        
        /* EDITOR */
        codeArea.getStyleClass().add("codeArea");
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
        assembleButton.setDisable(true);
        runButton.setDisable(true);
        stepButton.setDisable(true);
        menuRun.setDisable(true);
        menuStep.setDisable(true);
        resultLabel.setText("-");
        return "Waiting for input file";
    };
    
    public String statusReadyToAssemble() {
        assembleButton.setDisable(false);
        return "Ready to assemble or load more files. Total files loaded: " + Montador.getNumberOfFiles();
    };

    public String statusReady() {
        assembleButton.setDisable(true);
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
    
    public void lineCounterVisible(boolean value) {
        this.lineCounter.setVisible(false);
    }
    
    public void updateFirstTab(File inputFile) {
        StringBuilder code = this.codeGenerator(inputFile);
        this.codeArea.setText(code.toString());
        this.codeArea.requestFocus();
        this.codeArea.end();
        //update title
        this.codeTab.setText(inputFile.getName().toString());
    }
    
    public void createNewTab(String tabName, File textContent) {
       Tab newTab = new Tab(tabName);
       newTab.setClosable(false);
       TextArea textArea = new TextArea();
       textArea.setEditable(false);
       StringBuilder code = codeGenerator(textContent);
       textArea.setText(code.toString());
       textArea.getStyleClass().add("codeArea");
       
       newTab.setContent(textArea);
       tabPane.getTabs().add(newTab);  
    };
    
    public StringBuilder codeGenerator(File fileToRead) {
        try {
            Scanner scanner = new Scanner(fileToRead);
            StringBuilder code = new StringBuilder();

            while(scanner.hasNextLine()) {
                code.append(scanner.nextLine()).append("\n");
            }
            scanner.close();
            return code;
        } catch (FileNotFoundException e) {
            System.out.println(e);
            return null;
        }
    };
    
    public void resetTabs() {
        tabPane.getTabs().subList(1, tabPane.getTabs().size()).clear();
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

            File inputFile = fileChooser.showOpenDialog(null);
            
            System.out.println("Input file: " + inputFile.getName());
            
            if(inputFile != null) {
                Montador.setNumberOfFiles(Montador.getNumberOfFiles()+1);
                return inputFile;
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
            
            if(Montador.getNumberOfFiles() == 1) {
                this.updateFirstTab(inputFile);
            } else {
                this.createNewTab(inputFile.getName(), inputFile);
            }
            
            statusLabel.setText(statusReadyToAssemble());
            this.loadedFilesPath.add(inputFile.getAbsolutePath());
            System.out.println(this.loadedFilesPath);
            
        
        } catch(NullPointerException e){
            System.out.println(e);
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
    
    public void prepareMemory() {
        Carregador.carregador(binaryFinalFile);
        this.hasLoaded = true;
        data.clear();
        populateMemoryTable();
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
       
       ArrayList<File> macroProcOutputs = new ArrayList();
        
       this.loadedFilesPath.forEach((String filePath) -> {
           macroProcOutputs.add(Macro_Processor.run(filePath));
       });
       
       macroProcOutputs.forEach((File expandedFile) -> {
           String tabName = expandedFile.getName();
           this.createNewTab(tabName, expandedFile);
       });
       
       this.assemble(macroProcOutputs);
    }
    
    public void assemble(ArrayList<File> expandedFiles) {
       ArrayList<File> assemblerOutputs = new ArrayList();       
        
       expandedFiles.forEach((File expFile) -> {
           assemblerOutputs.add(Montador.assembler(expFile));
       });
       
       assemblerOutputs.forEach((File assembFile) -> {
           String tabName = assembFile.getName();
           String lstTabName = tabName.split("-")[0] + "-lst.txt";
           
           
           File lstFile = new File("./test/"+lstTabName);
           
           this.createNewTab(tabName, assembFile);
           this.createNewTab(lstTabName, lstFile);
       });
       
       this.link(assemblerOutputs);
    }
    
    public void link(ArrayList<File> assembledFiles) {
        File binaryFile = Ligador.ligador(assembledFiles);
        
        System.out.println(binaryFile);
        this.binaryFinalFile = binaryFile;        
        this.createNewTab("linkedBin", binaryFile);
        
        if(!hasLoaded) {
            this.prepareMemory();
        }        
        updateRegisters();        
        
        statusLabel.setText(statusReady());
    }
    
    public void runAll() {
        if(!hasLoaded) {
            this.prepareMemory();
        }
        
        CPU.run();
        updateRegisters();
        this.logErrors();
        statusLabel.setText(statusCompleted());
    
    }
    
    public void runStep() {
        boolean hasNextStep = CPU.step();
        
        if(!hasLoaded) {
            this.prepareMemory();
        }
        
        updateRegisters();
        
        if(hasNextStep) {
            statusLabel.setText(statusRunning());
        } else {
            this.logErrors();
            statusLabel.setText(statusCompleted());
        }
        
    };
    
    public void logErrors() {
        ArrayList<String> errorArray = Montador.getErros();
        StringBuilder errorString = new StringBuilder();
        
        if (errorArray.size() == 0) {
            errorConsole.getStyleClass().add("success");
            errorString.append("Nenhum erro detectado.");
        } else {
            errorConsole.getStyleClass().add("error");
            errorArray.forEach(e -> {
                errorString.append(e).append("\n");
            });
        }
        
        errorConsole.setText(errorString.toString());
    }
    
    public void resetAll() {
        CPU.reset();
        codeArea.setText("");
        errorConsole.setText("");
        errorConsole.getStyleClass().remove("success");
        errorConsole.getStyleClass().remove("error");
        this.codeTab.setText("Untitled");
        this.resetTabs();
        data.clear();
        this.loadedFilesPath.clear();
        Montador.clearMontador();
        updateRegisters();
        this.hasLoaded = false;
        statusLabel.setText(statusWaiting());
    };
    
//    public void highlightMemory() {
//        int index = (int) MemoryTracker.getCurrentPosition();
//        System.out.println(index);
//        memoryTable.getSelectionModel().select(index);
//    };
}
