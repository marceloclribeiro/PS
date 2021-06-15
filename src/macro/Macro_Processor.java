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

        //INICIALIZA ARQUIVA DE ENTRADA
        File arquivo_entrada = new File(diretorio);
        String[] nome_saida = arquivo_entrada.getName().split("[.]");
        nome_saida[0] = nome_saida[0].concat(".sic");
        File arquivo_saida = new File("test/"+nome_saida[0]);

        //VETOR QUE ARMAZENA O CONTEUDO DO ARQUIVO DE ENTRADA
        List<String> conteudo = new ArrayList<>();

        try {
            //INICIALIZA ARQUIVO DE SAIDA

            arquivo_saida.createNewFile();

            //LEITURA ARQUIVO DE ENTRADA
            Scanner reader = new Scanner(arquivo_entrada);

            //PARA ESCREVER NO ARQUIVO DE SAIDA
            FileWriter fileWriter = new FileWriter(arquivo_saida, false);
            BufferedWriter escrever = new BufferedWriter(fileWriter);

            //COPIANDO ARQUIVO DE ENTRADA PARA UM VETOR
            //COLOCANDO P/ UPPER CASE
            while (reader.hasNextLine()) {
                String line = reader.nextLine();
                line = line.toUpperCase();
                //SEM LINHA VAZIA
                if (!line.isEmpty()) {
                    conteudo.add(line);
                }
            }

            //============================
            //PROCESSADOR DE MACROS EM SI:
            //============================
            //1 PASSAGEM - DEFINIÇÃO
            //PEGA A DEFINIÇÃO DE MACRO
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
                        
                    //PEGA O NOME DA MACRO
                    macro.nome = line_label;
                    
                    for (int j = i + 1; conteudo.get(j).compareTo("MEND") != 0; j++) {
                        //line_definicao.add(conteudo.get(j).replace("&", ""));
                        
                        //PEGA DEFINICAO
                        macro.definicao.add(conteudo.get(j).replace("&", ""));
                        macro.MEND = j + 1;
                    }
                    macros.add(macro);
                }
            }

            //REMOVE AS DEFINIÇÕES
            for (int i = macros.size() - 1; i >= 0; --i) {
                conteudo.removeAll(conteudo.subList(macros.get(i).MACRO, macros.get(i).MEND));
            }

            //=====================
            //2 PASSAGEM - EXPANSÃO
            //=====================
            for (int i = 0; i < conteudo.size(); ++i) {
                Macro macro_chamada = new Macro();
                String[] aux_labels = conteudo.get(i).split("&");
                String[] aux_linha = conteudo.get(i).split(" ", 2);

                //SE TEM LABEL
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

                    //SE NÃO TEM LABEL
                    if (aux_linha[0].compareTo(iterator.nome) == 0) {

                        //PASSA OS ARGUMENTOS P/ A CHAMADA
                        macro_chamada.nome = aux_linha[0];

                        String[] aux_argumentos = aux_linha[1].split(",");
                        for (int l = 0; l < aux_argumentos.length; ++l) {
                            aux_argumentos[l] = aux_argumentos[l].replace(" ", "");
                            macro_chamada.argumentos.add(aux_argumentos[l]);
                        }

                        //PASSA A DEFINIÇÃO P/ A CHAMADA
                        for (int l = 0; l < iterator.definicao.size(); ++l) {
                            macro_chamada.definicao.add(iterator.definicao.get(l));
                        }

                        //SUBSTITUI OS ARGUMENTOS
                        String replace = null;
                        for (int l = 0; l < macro_chamada.definicao.size(); ++l) {
                            for (int k = 0; k < macro_chamada.argumentos.size(); ++k) {
                                if (k == 0)
                                    replace = macro_chamada.definicao.get(l).split(" ")[0];
                                else {
                                    replace = replace.concat(",");
                                }
                                replace = replace.concat(" ");
                                replace = replace.concat(macro_chamada.argumentos.get(k));
                                //String replace = macro_chamada.definicao.get(l).replace(iterator.argumentos.get(k), macro_chamada.argumentos.get(k));
                                
                                
                                
                            }
                            macro_chamada.definicao.set(l, replace);
                        }

                        //SUBSTITUI AS LABELS
                        for (int l = 0; l < macro_chamada.labels.size(); ++l) {
                            for (int k = 0; k < macro_chamada.labels.size(); ++k) {
                                replace = macro_chamada.definicao.get(l).replace(iterator.labels.get(k), macro_chamada.labels.get(k));
                                if (replace.compareTo(macro_chamada.definicao.get(l)) != 0) {
                                    macro_chamada.definicao.set(l, replace);
                                }
                            }
                        }

                        conteudo.remove(i);
                        //EXPANDE A MACRO
                        for (int l = 0; l < macro_chamada.definicao.size(); ++l) {
                            conteudo.add(i + l, macro_chamada.definicao.get(l));
                        }
                        //Volta ao inicio do arquivo - PARA EXPANDIR MACROS DENTRO DE MACROS
                        i = 0;
                    }
                }
            }

            //ESCREVENDO NO ARQUIVO FINAL
            if (conteudo.get(0) == "MEND")
                conteudo.remove(0);
            for (String iterator : conteudo) {
                escrever.write(iterator);
                escrever.newLine();
            }

            macros.forEach(iterator -> {
                iterator.print();
            });

            //=========================
            //SALVANDO ARQUIVO DE SAIDA
            //=========================
            escrever.close();
            fileWriter.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }

        return arquivo_saida;
    }
}
