/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author Alejandro
 */
public class Macro_Processor {

    public static File run(String diretorio) {

        List<Macro> macros = new ArrayList<>();

        File arquivo_entrada = new File(diretorio);
        String[] nome_saida = arquivo_entrada.getName().split("[.]");
        nome_saida[0] = nome_saida[0].concat("-macro.txt");
        File arquivo_saida = new File("test/"+nome_saida[0]);

        List<String> conteudo = new ArrayList<>();

        try {
            arquivo_saida.createNewFile();
            Scanner reader = new Scanner(arquivo_entrada);
            FileWriter fileWriter = new FileWriter(arquivo_saida, false);
            BufferedWriter escrever = new BufferedWriter(fileWriter);

            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                line = line.toUpperCase();
                if (!line.isEmpty()) {
                    conteudo.add(line);
                }
            }
            String[] line_args;
            ArrayList<String> line_definicao = new ArrayList();
            String line_label;
            for (int i = 0; i < conteudo.size(); i++) {
                if (conteudo.get(i).contains("MACRO") == true) {
                    Macro macro = new Macro();
                    macro.MACRO = i;
                    
                    line_args = conteudo.get(i).split(",");
                    line_args[0] = line_args[0].split(" ")[2];
                    line_label = conteudo.get(i).split(" ")[0];
                    
                        for (int l = 0; l < line_args.length; ++l) {
                            line_args[l] = line_args[l].replace(" ", "");
                            macro.argumentos.add(line_args[l]);
                        }
                    macro.nome = line_label;
                    
                    for (int j = i + 1; conteudo.get(j).compareTo("MEND") != 0; j++) {
                        macro.definicao.add(conteudo.get(j).replace("&", ""));
                        macro.MEND = j + 1;
                    }
                    macros.add(macro);
                }
            }
            for (int i = macros.size() - 1; i >= 0; --i) {
                conteudo.removeAll(conteudo.subList(macros.get(i).MACRO, macros.get(i).MEND));
            }
            
            for (int i = 0; i < conteudo.size(); ++i) {
                Macro macro_chamada = new Macro();
                String[] aux_labels = conteudo.get(i).split("&");
                String[] aux_linha = conteudo.get(i).split(" ", 2);

                if (aux_labels.length > 1) {
                    for (int j = 1; j < aux_labels.length - 1; ++j) {
                        macro_chamada.labels.add(aux_labels[j]);
                    }

                    aux_linha = aux_labels[aux_labels.length - 1].split(" ", 2);
                    aux_linha = aux_linha[aux_labels.length - 1].split(" ", 2);

                    aux_labels = aux_labels[aux_labels.length - 1].split(" ", 2);
                    macro_chamada.labels.add(aux_labels[0]);
                }

                for (Macro iterator : macros) {

                    if (aux_linha[0].compareTo(iterator.nome) == 0) {

                        macro_chamada.nome = aux_linha[0];

                        String[] aux_argumentos = aux_linha[1].split(",");
                        for (int l = 0; l < aux_argumentos.length; ++l) {
                            aux_argumentos[l] = aux_argumentos[l].replace(" ", "");
                            macro_chamada.argumentos.add(aux_argumentos[l]);
                        }

                        for (int l = 0; l < iterator.definicao.size(); ++l) {
                            macro_chamada.definicao.add(iterator.definicao.get(l));
                        }

                        String replace = null;
                        String []aux;
                        String []aux2;
                        for (int l = 0; l < macro_chamada.definicao.size(); ++l) {
                            aux = iterator.definicao.get(l).split(" ", 2);
                            aux2 = aux[1].split("\\*",2);
                            for (int k = 0; k < macro_chamada.argumentos.size(); ++k) {
                                aux2[0] = aux2[0].replace(iterator.argumentos.get(k).replace("&", ""), macro_chamada.argumentos.get(k));
                                if(aux2.length > 1){
                                replace = aux[0] + " " + aux2[0] + " *" + aux2[1];
                                }else{
                                replace = aux[0] + " " + aux2[0];
                                }
                                if (replace.compareTo(macro_chamada.definicao.get(l)) != 0){
                                    macro_chamada.definicao.set(l, replace);
                                }
                            }
                            macro_chamada.definicao.set(l, replace);
                        }

                        for (int l = 0; l < macro_chamada.labels.size(); ++l) {
                            for (int k = 0; k < macro_chamada.labels.size(); ++k) {
                                replace = macro_chamada.definicao.get(l).replace(iterator.labels.get(k), macro_chamada.labels.get(k));
                                if (replace.compareTo(macro_chamada.definicao.get(l)) != 0) {
                                    macro_chamada.definicao.set(l, replace);
                                }
                            }
                        }

                        conteudo.remove(i);
                        
                        for (int l = 0; l < macro_chamada.definicao.size(); ++l) {
                            conteudo.add(i + l, macro_chamada.definicao.get(l));
                        }
                        i = 0;
                    }
                }
            }

            if ("MEND".equals(conteudo.get(0)))
                conteudo.remove(0);
            for (String iterator : conteudo) {
                escrever.write(iterator);
                escrever.newLine();
            }
            escrever.close();
            fileWriter.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }

        return arquivo_saida;
    }
}
