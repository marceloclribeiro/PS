
import java.util.ArrayList;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alejandro
 */
public class CPU {
    private static String24 A = new String24();
    private static String24 X = new String24();
    

    public static void main(String[] args) {
        
        Opcode_f2.CLEAR.getOpcode();
        A.setBits("100");
        System.out.println("Oi:" + A.toInt());
        add(5);
        System.out.println(A.toInt());
        X.setBits("001");
        addr(A, X);
        System.out.println(A.toInt());
        System.out.println("Opcode" + Opcode_f3.MUL.getOpcode());

    }
    public static void add (int valor){
        A.setBits( A.toInt() + valor );
    }
    public static void addr (String24 registrador1, String24 registrador2){
        registrador2.setBits( registrador1.toInt() + registrador2.toInt());
    }
    public static void clear (String24 registrador){
        registrador.setBits(0);
    }
    



}   

