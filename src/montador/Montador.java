/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package montador;
import java.io.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.HashMap;

/**
 *
 * @author codtg
 */
public class Montador {
    
    private static ArrayList<String> instructions = new ArrayList<>(Arrays.asList("ADDR","CLEAR","COMPR","DIVR","MULR","RMO","SHIFTL","SHIFTR","SUBR","TIXR","ADD","AND","COMP","DIV","J","JEQ","JGT","JLT","JSUB","LDA","LDB","LDCH","LDL","LDS","LDT","LDX","MUL","OR","RSUB","STA","STB","STCH","STL","STS","STT","STX","SUB","TIX","WORD","END"));
    // <= 9 -> 2 bytes
    private static HashMap<String, Integer> tabelaDeLabels = new HashMap<>(); 
    
    public static void assembler(File sic){
        try {
            Scanner reader = new Scanner(sic);
            String line;
            ArrayList<String> conteudo = new ArrayList<>();
            
            while(reader.hasNext())
            {
                line = reader.nextLine();
                int i = line.indexOf('*');
                if (i != -1)
                    line = line.substring(0, i);
                line = line.replace("\t", "").replace(",", "");
                conteudo.add(line);
            }       
            
            int i = 0;
            
            for (String c : conteudo){
                boolean isLabel;
                String[] word = c.split(" ");
                isLabel = !instructions.contains(word[0].replace("+", ""));
                
                if (isLabel == true){
                    if (instructions.indexOf(word[1].replace("+", "")) <= 9){
                        tabelaDeLabels.put(word[0], i);
                        i += 2;
                    }
                    else if (word[1].charAt(0) == '+'){
                        //word[1] = word[1].replace("+", "");   //tira o + da instrucao definitivamente, nao se sabe se vai precisar aqui ou nao
                        tabelaDeLabels.put(word[0], i);
                        i += 4;
                    } else if (instructions.indexOf(word[1]) == word.length - 1){
                        tabelaDeLabels.put(word[0], i);
                        i += 1;
                    } else {
                        tabelaDeLabels.put(word[0], i);
                        i += 3;
                    }
                } else {
                    if (instructions.indexOf(word[0].replace("+", "")) <= 9){                 
                        i += 2;
                    }
                    else if (word[0].charAt(0) == '+'){
                        //word[1] = word[1].replace("+", "");    //tira o + da instrucao definitivamente, nao se sabe se vai precisar aqui ou nao
                        i += 4;
                    } else if (instructions.indexOf(word[0]) == word.length - 1){
                        i += 1;
                    } else {
                        i += 3;
                    }
                }                
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Montador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
