/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package macro;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Alejandro
 */


public class Macro {
    public String nome;
    public List<String> definicao = new ArrayList<>();
    public List<String> argumentos = new ArrayList<>();
    public List<String> labels = new ArrayList<>();
    public int MACRO = 0;
    public int MEND = 0;
    
    public void print(){
        System.out.println(nome);
        System.out.println(definicao);
        System.out.println(argumentos);
        System.out.println(labels);
    }
    
}