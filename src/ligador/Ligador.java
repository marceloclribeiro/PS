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
            int size_incremento = 0;
            int size = 0;
            for (File bin : bins){
                Scanner reader = new Scanner(bin);
                String line;
                if (controle > 1){
                    while (reader.hasNext()){
                        line = reader.nextLine();
                        
                        if (line.length() <= 16){
                            size_incremento += line.length() / 8;
                            escrever.write(line);
                            escrever.newLine();
                        } else {
                            if ("0".equals(line.charAt(6)) && "1".equals(line.charAt(7))){
                                size_incremento += line.length() / 8;
                                escrever.write(line);
                                escrever.newLine();
                            } else if (line.startsWith("W")){
                                line = line.replace("W", "");
                                size_incremento += line.length() / 8;
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
                        if (line.startsWith("W")){
                            line = line.replace("W", "");
                        }
                        size_incremento += line.length() / 8;                        
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
