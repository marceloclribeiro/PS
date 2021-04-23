
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
    private String A, X, L, B, F, T, S, PC, SW; 
    
public CPU(){
    
Init();    //Função que inicializa todos registradores da CPU

}  

public String getA(){
    return A;
}

public String getX(){
    return X;
}

public String getL(){
    return L;
}

public String getB(){
    return B;
}

public String getF(){
    return F;
}

public String getT(){
    return T;
}

public String getS(){
    return S;
}

public String getPC(){
    return PC;
}

public String getSW(){
    return SW;
}
      
public void Init(){

A = "000000000000000000000000"; //Registrador acumulador
X = "000000000000000000000000"; //Registrador indice
L = "000000000000000000000000"; //Registrador de ligação
B = "000000000000000000000000"; //Registrador base
T = "000000000000000000000000"; //Registrador uso geral
S = "000000000000000000000000"; //Registrador uso geral
F = "000000000000000000000000000000000000000000000000"; //Registrador acumulador de ponto flutuante
PC = "000000000000000000000000"; //Contador de instruções
SW = "000000000000000000000000"; //Palavra de status

} 

}   

