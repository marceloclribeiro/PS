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
 * @author Thomazio
 */
public class Montador {
    private static int numberOfFiles = 0;
    private static int controle = 1;
    private static ArrayList<String> instructions = new ArrayList<>(Arrays.asList("ADDR", "CLEAR", "COMPR", "DIVR", "MULR", "RMO", "SHIFTL", "SHIFTR", "SUBR", "TIXR", "ADD", "AND", "COMP", "DIV", "J", "JEQ", "JGT", "JLT", "JSUB", "LDA", "LDB", "LDCH", "LDL", "LDS", "LDT", "LDX", "MUL", "OR", "RSUB", "STA", "STB", "STCH", "STL", "STS", "STT", "STX", "SUB", "TIX", "WORD", "END"));
    // <= 9 -> 2 bytes
    private static ArrayList<Integer> instructions_opcode = new ArrayList<>(Arrays.asList(144, 4, 160, 156, 152, 172, 164, 168, 148, 184, 6, 16, 10, 9, 15, 12, 13, 14, 18, 0, 26, 20, 2, 27, 29, 1, 8, 17, 19, 3, 30, 21, 5, 31, 33, 4, 7, 11, 0, 18));
    private static HashMap<String, Integer> tabelaDeLabels = new HashMap<>();
    private static HashMap<String, String> num_regs = new HashMap<>();
    private static String saida = new String();

    public static File assembler(File sic) {
            String[] nome_saida = sic.getName().split("[.]");
            nome_saida[0] = nome_saida[0].concat("asm.txt");
            File arquivo_saida = new File("test/"+nome_saida[0]);
        try {
            Scanner reader = new Scanner(sic);
            FileWriter fileWriter = new FileWriter(arquivo_saida, false);
            BufferedWriter escrever = new BufferedWriter(fileWriter);
            arquivo_saida.createNewFile();
            String line;
            ArrayList<String> conteudo = new ArrayList<>();
            int word_count = 0;
            while (reader.hasNext()) {
                line = reader.nextLine();
                int i = line.indexOf('*');
                if (i != -1) {
                    line = line.substring(0, i);
                }
                if (line.contains("WORD")){
                    word_count += 1;
                }
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
            for (String c : conteudo) {
                boolean isLabel;
                String[] word = c.split(" ");
                isLabel = !instructions.contains(word[0].replace("+", ""));

                if (isLabel == true) {
                    if (instructions.indexOf(word[1].replace("+", "")) <= 9) {
                        tabelaDeLabels.put(word[0], i);
                        i += 2;
                    } else if (word[1].charAt(0) == '+') {
                        //word[1] = word[1].replace("+", "");   //tira o + da instrucao definitivamente, nao se sabe se vai precisar aqui ou nao
                        tabelaDeLabels.put(word[0], i);
                        i += 4;
                    } else if (instructions.indexOf(word[1]) == instructions.size() - 1) {
                        tabelaDeLabels.put(word[0], i);
                        if (numberOfFiles > 1)
                            i += 3;
                        else
                            i += 1;
                    } else {
                        tabelaDeLabels.put(word[0], i);
                        i += 3;
                    }
                } else {
                    if (instructions.indexOf(word[0].replace("+", "")) <= 9) {
                        i += 2;
                    } else if (word[0].charAt(0) == '+') {
                        //word[1] = word[1].replace("+", "");    //tira o + da instrucao definitivamente, nao se sabe se vai precisar aqui ou nao
                        i += 4;
                    } else if (instructions.indexOf(word[0]) == instructions.size() - 1) {
                        if (numberOfFiles > 1)
                            i += 3;
                        else
                            i += 1;
                    } else {
                        i += 3;
                    }
                }
            }

            i = 0;
            int num_linha = 1;
            //segunda passada
            for (String c : conteudo) {
                String[] word = c.split(" ");
                String[] linha = null;
                char[] nixbpe = {'0', '0', '0', '0', '0', '0'};
                saida = new String();
                if (tabelaDeLabels.containsKey(word[0])) {
                    linha = Arrays.copyOfRange(word, 1, word.length);
                } else {
                    linha = word;
                }
                int instsize = 0;

                boolean isExtended;
                if (linha[0].charAt(0) == '+') {
                    isExtended = true;
                } else {
                    isExtended = false;
                }
                int op;
                String24 opBinary;
                String24 adress = new String24(1);
                op = instructions.indexOf(linha[0].replace("+", ""));

                for (int j = 1; j < linha.length; j++) {
                    if (tabelaDeLabels.containsKey(linha[j])) {
                        linha[j] = Integer.toString(tabelaDeLabels.get(linha[j]));
                    }
                }

                if (op != -1) {
                    if (isExtended == true) {
                        instsize = 4;
                        nixbpe[5] = '1';
                    } else if (op <= 9) {
                        instsize = 2;
                    } else if (op != instructions.size() - 1) {
                        instsize = 3;
                    } else {
                        if (numberOfFiles > 1)
                            instsize = 3;
                        else
                            instsize = 1;
                    }
                    op = instructions_opcode.get(op);
                    if (instsize == 3) {
                        opBinary = new String24(6);
                        adress = new String24(12);
                    } else if (instsize == 4) {
                        opBinary = new String24(6);
                        adress = new String24(20);
                    } else {
                        opBinary = new String24(8);
                    }
                    opBinary.setBits(op);
                    if (instsize == 2) {
                        saida = saida + String.valueOf(opBinary.getBits());
                        if (linha.length == 2) {
                            saida = saida + num_regs.get(linha[1]);
                            saida = saida + "0000";
                        } else {
                            saida = saida + num_regs.get(linha[1]);
                            saida = saida + num_regs.get(linha[2]);
                        }
                    } else if (((instsize == 3) || (instsize == 4)) && !("WORD".equals(linha[0])) && !("END".equals(linha[0]))) {                                                  
                            if (linha[1].startsWith("@")) { //indireto
                                nixbpe[0] = '1';
                                linha[1] = linha[1].replace("@", "");
                            } else if (linha[1].startsWith("#")) {//imediato
                                nixbpe[1] = '1';
                                linha[1] = linha[1].replace("#", "");                              
                            } else {//normal
                                nixbpe[0] = '1';
                                nixbpe[1] = '1';
                            }
                        if (linha.length == 3) {
                            if ("X".equals(linha[2]))
                                nixbpe[2] = '1';
                            else {
                                System.out.println("ERRO! A instrucao " + linha[0] + " recebe (m..m+2) ou (m..m+2),X. \n Argumento passado -> " + linha[2]);
                                System.out.println("Linha " + i);
                            }
                        }
                        saida = saida + String.valueOf(opBinary.getBits());
                        saida = saida + String.valueOf(nixbpe);                                                 
                        adress.setBits(Integer.parseInt(linha[1]));
                        saida = saida + String.valueOf(adress.getBits());
                    } else if ("WORD".equals(linha[0])){
                        saida = "";
                        adress = new String24(24);
                        adress.setBits(Integer.parseInt(linha[1]));
                        saida = saida + String.valueOf(adress.getBits());
                    } else {
                        if (controle != numberOfFiles){
                            String24 words = new String24(12);
                            words.setBits((word_count * 3) + i);
                            saida = "001111" + "110000" + String.valueOf(words.getBits());
                        } else {
                            saida = saida + String.valueOf(opBinary.getBits());
                        }
                    }
                } else {
                    System.out.println("ERRO! A instrucao " + linha[0] + " nao existe");
                    System.out.println("Linha: " + i);
                }              
                escrever.write(saida);
                escrever.newLine();
                num_linha += 1;
                i += instsize;
                controle++;
            }
            escrever.close();
            fileWriter.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Montador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Montador.class.getName()).log(Level.SEVERE, null, ex);
        }
        return arquivo_saida;
    }
    
    public static void setNumberOfFiles(int value) {
        Montador.numberOfFiles = value;
    }
    public static int getNumberOfFiles() {
        return Montador.numberOfFiles;
    }
}