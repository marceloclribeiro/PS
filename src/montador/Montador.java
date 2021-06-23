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
    private static int wordline = 0;
    private static ArrayList<Integer> start = new ArrayList();
    private static ArrayList<String> instructions = new ArrayList<>(Arrays.asList("ADDR", "CLEAR", "COMPR", "DIVR", "MULR", "RMO", "SHIFTL", "SHIFTR", "SUBR", "TIXR", "ADD", "AND", "COMP", "DIV", "J", "JEQ", "JGT", "JLT", "JSUB", "LDA", "LDB", "LDCH", "LDL", "LDS", "LDT", "LDX", "MUL", "OR", "RSUB", "STA", "STB", "STCH", "STL", "STS", "STT", "STX", "SUB", "TIX", "WORD", "END"));
    // <= 9 -> 2 bytes
    private static ArrayList<Integer> instructions_opcode = new ArrayList<>(Arrays.asList(144, 4, 160, 156, 152, 172, 164, 168, 148, 184, 6, 16, 10, 9, 15, 12, 13, 14, 18, 0, 26, 20, 2, 27, 29, 1, 8, 17, 19, 3, 30, 21, 5, 31, 33, 4, 7, 11, 0, 18));
    private static HashMap<String, Integer> tabelaDeLabels = new HashMap<>();
    private static HashMap<String, String> num_regs = new HashMap<>();
    private static HashMap<String, Integer> tabelaDeSimbolosGlobais = new HashMap<>();
    private static ArrayList<String> tabelaDeSimbolosGlobaisRepetidos = new ArrayList<>();
    private static String saida = new String();
    private static ArrayList<String> erros = new ArrayList<>();

    public static File assembler(File sic) {
        String[] nome_saida = sic.getName().split("[.]");
        nome_saida = nome_saida[0].split("-");
        File arquivo_saida = new File("test/" + nome_saida[0] + "-obj.txt");
        File lst = new File("test/" + nome_saida[0] + "-lst.txt");
        
        try {
            Scanner reader = new Scanner(sic);

            FileWriter fileWriter = new FileWriter(arquivo_saida, false);
            BufferedWriter escrever = new BufferedWriter(fileWriter);

            FileWriter lstWriter = new FileWriter(lst, false);
            BufferedWriter escreverLst = new BufferedWriter(lstWriter);

            lst.createNewFile();
            arquivo_saida.createNewFile();

            String line;
            ArrayList<String> conteudo = new ArrayList<>();
            int word_count = 0;
            boolean hasStart = false;
            boolean hasEnd = false;
            int count = 1;
            while (reader.hasNext()) {
                line = reader.nextLine();
                escreverLst.write(line);
                escreverLst.newLine();
                int i = line.indexOf('*');
                if (line.length() > 80) {
                    erros.add("ERRO! Linha muito longa. Não deve haver mais de 80 caracteres numa linha. Linha: " + count + "\n");
                }
                if (line.contains("START")) {
                    String[] aux = line.split(" ");
                    start.add(Integer.parseInt(aux[aux.length - 1]));
                    hasStart = true;
                    continue;
                } else if (i != -1) {
                    line = line.substring(0, i);
                }
                if (line.contains("WORD")) {
                    word_count += 1;
                }
                if (line.equals("END")) {
                    hasEnd = true;
                }
                if (line.contains("EXTREF") || line.contains("EXTDEF")) {
                    String[] s;
                    s = line.replace(",", " ").split(" ");

                    for (int k = 1; k < s.length - 1; k++) {
                        tabelaDeSimbolosGlobais.put(s[k], -1);
                    }
                    continue;
                }

                line = line.replace("\t", "").replace(",", "");
                conteudo.add(line);
                count++;
            }
            if (hasStart == false) {
                start.add(0);
            }
            if (hasEnd == false) {
                erros.add("ERRO! O código não possui a diretiva END.");
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
                    if (tabelaDeLabels.containsKey(word[0])) {
                        erros.add("ERRO! Referência simbólica com definições múltiplas. Linha: " + i + "\n");
                    }
                    if (instructions.indexOf(word[1].replace("+", "")) <= 9) {
                        tabelaDeLabels.put(word[0], i);
                        i += 2;
                    } else if (tabelaDeSimbolosGlobais.containsKey(word[0])) {
                        boolean r;
                        r = tabelaDeSimbolosGlobais.replace(word[0], -1, i);
                        if(r == false){
                            tabelaDeSimbolosGlobaisRepetidos.add(word[0]);
                        }
                        i += 3;
                    } else if (word[1].charAt(0) == '+') {
                        tabelaDeLabels.put(word[0], i);
                        i += 4;
                    } else if (instructions.indexOf(word[1]) == instructions.size() - 1) {
                        tabelaDeLabels.put(word[0], i);
                        if (numberOfFiles > 1 && numberOfFiles != controle) {
                            i += 3;
                        } else {
                            i += 1;
                        }
                    } else {
                        tabelaDeLabels.put(word[0], i);
                        i += 3;
                    }
                } else {
                    if (instructions.indexOf(word[0].replace("+", "")) <= 9) {
                        i += 2;
                    } else if (word[0].charAt(0) == '+') {
                        i += 4;
                    } else if (instructions.indexOf(word[0]) == instructions.size() - 1) {
                        if (numberOfFiles > 1 && numberOfFiles != controle) {
                            i += 3;
                        } else {
                            i += 1;
                        }
                    } else {
                        i += 3;
                    }
                }
            }

            i = 0;
            int num_linha = 1;
            boolean symbolnotdef;
            //segunda passada
            for (String c : conteudo) {
                symbolnotdef = false;
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
                    } else if (!java.lang.Character.isDigit(linha[j].replace("#", "").replace("@", "").charAt(0))) {
                        if (!num_regs.containsKey(linha[j])) {
                            symbolnotdef = true;
                            erros.add("ERRO! Referência simbólica não definida. Linha: " + i + "\n");
                        }
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
                        if (numberOfFiles > 1 && numberOfFiles != controle) {
                            instsize = 3;
                        } else {
                            instsize = 1;
                        }
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
                            if ("X".equals(linha[2])) {
                                nixbpe[2] = '1';
                            } else {
                                erros.add("ERRO! A instrucao " + linha[0] + " recebe (m..m+2) ou (m..m+2),X. \n Argumento passado -> " + linha[2] + " Linha: " + i + "\n");
                            }
                        }
                        saida = saida + String.valueOf(opBinary.getBits());
                        saida = saida + String.valueOf(nixbpe);
                        adress.setBits(Integer.parseInt(linha[1]));
                        if (symbolnotdef == false) {
                            saida = saida + String.valueOf(adress.getBits());
                        }
                        else{ 
                             saida = saida + " " + linha[1];
                        }
                    } else if ("WORD".equals(linha[0])) {
                        wordline = i;
                        saida = "";
                        adress = new String24(24);
                        adress.setBits(Integer.parseInt(linha[1]));
                        saida = saida + String.valueOf(adress.getBits());
                        saida = "W" + saida;
                    } else {
                        if (controle != numberOfFiles) {
                            String24 words = new String24(12);
                            words.setBits((word_count * 3) + i + 3);
                            saida = "001111" + "110000" + String.valueOf(words.getBits());
                        } else {
                            saida = saida + String.valueOf(opBinary.getBits());
                        }
                    }
                } else {
                    erros.add("ERRO! A instrucao " + linha[0] + " nao existe. Linha: " + i + "\n");
                }
                escreverLst.write(saida.replace("W", ""));
                escreverLst.newLine();
                escrever.write(saida);
                escrever.newLine();
                num_linha += 1;
                i += instsize;
            }

            if (!erros.isEmpty()) {
                for (String e : erros) {
                    escreverLst.write(e);
                    escreverLst.newLine();
                }
            } else {
                escreverLst.write("Nenhum erro detectado.");
            }

            controle++;
            escreverLst.close();
            lstWriter.close();
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

    public static ArrayList<Integer> getStart() {
        return Montador.start;
    }

    public static ArrayList<String> getErros() {
        return erros;
    }
    public static void setErros(String e) {
        Montador.erros.add(e);
    }
    public static HashMap<String, Integer> getTabelaDeSimbolosGlobais() {
        return Montador.tabelaDeSimbolosGlobais;
    }
    public static ArrayList<String> getTabelaDeSimbolosGlobaisRepetidos() {
        return Montador.tabelaDeSimbolosGlobaisRepetidos;
    }
    
    public static void clearMontador() {
        Montador.numberOfFiles = 0;
        Montador.controle = 1;
        Montador.wordline = 0;
        Montador.start.clear();
        Montador.tabelaDeLabels.clear();
        Montador.num_regs.clear();
        Montador.tabelaDeSimbolosGlobais.clear();
        Montador.tabelaDeSimbolosGlobaisRepetidos.clear();
        Montador.saida = new String();
        Montador.erros.clear();
    }
}
