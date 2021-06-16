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
import logic.String24;

/**
 *
 * @author codtg
 */
public class Montador {
    
    private static ArrayList<String> instructions = new ArrayList<>(Arrays.asList("ADDR","CLEAR","COMPR","DIVR","MULR","RMO","SHIFTL","SHIFTR","SUBR","TIXR","ADD","AND","COMP","DIV","J","JEQ","JGT","JLT","JSUB","LDA","LDB","LDCH","LDL","LDS","LDT","LDX","MUL","OR","RSUB","STA","STB","STCH","STL","STS","STT","STX","SUB","TIX","WORD","END"));
    // <= 9 -> 2 bytes
    private static ArrayList<Integer> instructions_opcode = new ArrayList<>(Arrays.asList(144,4,160,156,152,172,164,168,148,184,6,16,10,9,15,12,13,14,18,0,26,20,2,27,29,1,8,17,19,3,30,21,5,31,33,4,7,11,18));
    private static HashMap<String, Integer> tabelaDeLabels = new HashMap<>(); 
    private static HashMap<String, String> num_regs = new HashMap<>(); 
    private static char[] nixbpe = new char[6];
    private static String saida = new String();
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
            num_regs.put("A", "0000");
            num_regs.put("X", "0001");
            num_regs.put("L", "0010");
            num_regs.put("B", "0011");
            num_regs.put("S", "0100");
            num_regs.put("T", "0101");
            num_regs.put("F", "0110");
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
            
            i = 0;
            //segunda passada
            for (String c : conteudo){
                String[] word = c.split(" ");
                String[] linha = null;
                saida = new String();
                if (tabelaDeLabels.containsKey(word[0])){                   
                    linha = Arrays.copyOfRange(word, 1, word.length);
                } else {
                    linha = word;
                }
                int instsize = 0;
                
                boolean isExtended;
                if (linha[0].charAt(0) == '+'){
                    isExtended = true;
                } else {
                    isExtended = false;
                }
                int op;
                String24 opBinary;
                String24 adress;
                op = instructions.indexOf(linha[0].replace("+", ""));
                
                for (int j = 1; j < linha.length; j++){
                    if (tabelaDeLabels.containsKey(linha[j])){
                        linha[j] = Integer.toString(tabelaDeLabels.get(linha[j]));
                    }
                }
                
                if (op != -1){
                    if (isExtended == true){
                        instsize = 4;
                    } else if (op <= 9){
                        instsize = 2;
                    } else if (op != instructions.size() - 1){
                        instsize = 3;
                    } else {
                        instsize = 1;
                    }
                    op = instructions_opcode.get(op);
                    if(instsize == 3){
                    opBinary = new String24(6);
                    adress = new String24(12);
                    }
                    else if(instsize == 4){
                    opBinary = new String24(6);
                    adress = new String24(20);
                    }
                    else{
                    opBinary = new String24(8);
                    }
                    opBinary.setBits(op);
                    if(instsize == 2){
                    saida = saida + String.valueOf(opBinary.getBits());
                    saida = saida + num_regs.get(linha[1]);
                    saida = saida + num_regs.get(linha[2]);
                    }
                    else if (instsize == 3 || instsize == 4){
                        for (int j = 1; j < linha.length - 1; j++){
                        if (linha[i].startsWith("@")){ //indireto
                            
                        } else if (linha[i].startsWith("#")){//imediato
                            
                        } else {//normal
                            
                        }
                    }
                    }
                    else{
                        saida = saida + String.valueOf(opBinary.getBits()); 
                    }
                    
                } else {
                    System.out.println("ERRO! A instrucao " + linha[0] + " nao existe");
                    System.out.println("Linha: " + i);
                    System.exit(1);
                }
                
                
                i += instsize;
//                switch(linha[0].replace("+", "")){
//                    //2 bytes
//                    case "ADDR":
//                        instsize = 2;
//                        op = instructions_opcode.get(0);
//                        break;
//                        
//                    case "CLEAR":
//                        instsize = 2;
//                        break;
//                        
//                    case "COMPR":
//                        instsize = 2;
//                        break;
//                        
//                    case "DIVR":
//                        instsize = 2;
//                        break;
//                    
//                    case "MULR":
//                        instsize = 2;
//                        break;
//                        
//                    case "RMO":
//                        instsize = 2;
//                        break;
//                        
//                    case "SHIFTL":
//                        instsize = 2;
//                        break;
//                        
//                    case "SHIFTR":
//                        instsize = 2;
//                        break;
//                    
//                    case "SUBR":
//                        instsize = 2;
//                        break;
//                        
//                    case "TIXR":
//                        instsize = 2;
//                        break;
//                        
//                    
//                    //3/4 bytes
//                    case "ADD":
//                        break;
//                        
//                    case "AND":
//                        break;
//                        
//                    case "COMP":
//                        break;
//                    
//                    case "DIV":
//                        break;
//                        
//                    case "J":
//                        break;
//                        
//                    case "JEQ":
//                        break;
//                    
//                    case "JGT":
//                        break;
//                    
//                    case "JLT":
//                        break;
//                        
//                    case "JSUB":
//                        break;
//                        
//                    case "LDA":
//                        break;
//                        
//                    case "LDB":
//                        break;
//                        
//                    case "LDCH":
//                        break;
//                        
//                    case "LDL":
//                        break;
//                        
//                    case "LDS":
//                        break;
//                        
//                    case "LDT":
//                        break;
//                        
//                    case "LDX":
//                        break;
//                        
//                    case "MUL":
//                        break;
//                        
//                    case "OR":
//                        break;
//                        
//                    case "RSUB":
//                        break;
//                        
//                    case "STA":
//                        break;
//                        
//                    case "STB":
//                        break;
//                        
//                    case "STCH":
//                        break;
//                        
//                    case "STL":
//                        break;
//                        
//                    case "STS":
//                        break;
//                        
//                    case "STT":
//                        break;
//                        
//                    case "STX":
//                        break;
//                        
//                    case "SUB":
//                        break;
//                        
//                    case "TIX":
//                        break;
//                        
//                        
//                        
//                    //outras
//                    case "WORD":
//                        break;
//                        
//                    case "END":
//                        break;
//                        
//                        
//                        
//                    default:
//                        System.out.println("ERRO! A instrucao " + linha[0] + " nao existe");
//                        System.out.println("Linha: " + i);
//                        System.exit(1);
//                        break;
//                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Montador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
