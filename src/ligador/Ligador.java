/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ligador;
import montador.Montador;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import logic.String24;
import java.util.HashMap;
/**
 *
 * @author Thomazio
 */
public class Ligador {
    public static File ligador(ArrayList<File> bins){
        int numberOfFiles = Montador.getNumberOfFiles();
        int controle = 1;
        String nome_saida = "bin.txt";
        File arquivo_saida = new File("test/"+nome_saida);  
        
        try {
            FileWriter fileWriter = new FileWriter(arquivo_saida, false);
            BufferedWriter escrever = new BufferedWriter(fileWriter);
            arquivo_saida.createNewFile();
            var simbolos = montador.Montador.getTabelaDeSimbolosGlobais();
            var simbolosRepetidos = montador.Montador.getTabelaDeSimbolosGlobaisRepetidos();
            if (!simbolosRepetidos.isEmpty()) {
                for (int c = 0; c < simbolosRepetidos.size();c++) {
                    Montador.setErros("ERRO! Simbolo global já definido: " + simbolosRepetidos.get(c));
                }
            } 
            int size_incremento = 0;
            int size = 0;
            for (File bin : bins){
                Scanner reader = new Scanner(bin);
                String line;
                if (controle > 1){
                    while (reader.hasNext()){
                        line = reader.nextLine();
                         if(!line.endsWith("0") && !line.endsWith("1")){
                            String []aux;
                            aux = line.split(" ");
                            if(simbolos.containsKey(aux[1])){
                                String24 s = new String24(12);
                                s.setBits(simbolos.get(aux[1]));
                                line = aux[0] + String.valueOf(s.getBits());
                            }
                            else{
                                Montador.setErros("ERRO! Simbolo global não definido: "+aux[1]);
                            }
                        }
                        if (line.length() <= 16){
                            size_incremento += line.length() / 8;
                            escrever.write(line);
                            escrever.newLine();
                        } else {
                            if (line.charAt(6) == '0' && line.charAt(7) == '1'){
                                size_incremento += line.length() / 8;
                                escrever.write(line);
                                escrever.newLine();
                            } else if (line.startsWith("W")){
                                size_incremento += line.replace("W", "").length() / 8;
                                escrever.write(line);
                                escrever.newLine();
                            } else {
                                String24 s = new String24(line);
                                s.setBits(s.toInt() + size);
                                size_incremento += line.length() / 8;
                                escrever.write(s.getBits());
                                escrever.newLine();
                            }
                        }                     
                    }
                } else {
                    while (reader.hasNext()){
                        line = reader.nextLine();
                        if(!line.endsWith("0") && !line.endsWith("1")){
                            String []aux;
                            aux = line.split(" ");
                            if(simbolos.containsKey(aux[1])){
                                String24 s = new String24(12);
                                s.setBits(simbolos.get(aux[1]));
                                line = aux[0] + String.valueOf(s.getBits());
                            }
                            else{
                                Montador.setErros("ERRO! Simbolo global não definido: "+aux[1]);
                            }
                        }
                        size_incremento += line.replace("W", "").length() / 8;                        
                        escrever.write(line);
                        escrever.newLine();                              
                    }
                }
                size = size_incremento;
                controle++;
            }                                               
            escrever.close();
            fileWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(Ligador.class.getName()).log(Level.SEVERE, null, ex);
        }  
        return arquivo_saida;
    }
}
