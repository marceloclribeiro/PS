package logic;

import gui.*;
import java.io.File;
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
    private static Memory mem = new Memory(1024);                //memoria de tamanho (80 * 24)Bytes 
    private static String24 op = new String24(8);               //salva operacao atual
    private static String24 r1 = new String24(4);               //salva registrador1
    private static String24 r2 = new String24(4);               //salva registrador2
    private static String24 address = new String24(20);         //endereco pra instrucao atual
    private static char[] nixbpe = new char[6];                 //salva as flags pra operacao atual
    
    private static String24 A = new String24(24);                 //ACUMULADOR     
    private static String24 X = new String24(24);                 //REGISTRADO DE INDICE
    private static String24 L = new String24(24);                 //REGISTRADOR DE LIGACAO
    private static String24 B = new String24(24);                 //REGISTRADOR BASE
    private static String24 S = new String24(24);                 //REGISTRADOR DE USO GERAL
    private static String24 T = new String24(24);                 //REGISTRADOR DE USO GERAL
    private static String24 PC = new String24(24);                //PC
    private static String24 SW = new String24(24);                //PALAVRA DE STATUS
    private static String24 F = new String24(48);
    
    public static void main(String[] args) {
                
        App app = new App();
        app.launchGUI(args);
        //usar apenas para debug 
//        run();

    }
    
    public static void loadMem(String filepath) {
        int data = mem.readInput(filepath);
    }
    
    public static int next_instruction(){
        String24 inst = mem.mem_read(PC.toInt());
        CPU.op = new String24(8);
        int inst_size;

        if(inst.toInt() == 12){
            for (int i = 0; i < 8; i++)
                op.setBit(i, inst.charAt(i));
            inst_size = 1;
        }
        else if (inst.charAt(6) == '0' && inst.charAt(7) == '0'){
            for (int i = 0; i < 8; i++)
                op.setBit(i, inst.charAt(i));
            inst_size = 2;
        }
        else if (inst.charAt(11) == '1'){
            for (int i = 0; i < 6; i++)
                op.setBit(i + 2, inst.charAt(i));
            inst_size = 4;
        }
        else{
            for (int i = 0; i < 6; i++)
                op.setBit(i + 2, inst.charAt(i));
            inst_size = 3;
        }
        PC.setBits(PC.toInt() + inst_size);
        if (inst_size == 1)
            return inst_size;
        if (inst_size == 2){
            get_address(inst_size, inst);
            return inst_size;
        }
        
        set_nixbpe(inst_size, inst);
        return inst_size;
    }
    
    public static void set_nixbpe(int inst_size, String24 inst){
        for (int i = 0; i < 6; i++)
            nixbpe[i] = inst.charAt(i + 6);
        get_address(inst_size, inst);
    }
    
public static void get_address(int inst_size, String24 inst){
        String24 ad = new String24(inst_size == 3 ? 12 : 20);
        if (inst_size == 3){
         for (int i = 12, j = 0; i < 24; i++, j++){
                ad.setBit(j, inst.charAt(i));
            }
        }
        else if(inst_size == 2){
            int j = 0;
             for (int i = 8; i < 12; i++){
                r1.setBit(j, inst.charAt(i));
                j++;
            }
             j = 0;
              for (int i = 12; i < 16; i++){
                r2.setBit(j, inst.charAt(i));
                j++;
            }   
        }
        else {
            for (int i = 12, j = 0; i < 32; i++, j++){
                ad.setBit(j, inst.charAt(i));
            }
        }
        address.setBits(address_mode(ad.toInt()));
    }
    
   public static int address_mode(int address){                //nao tenho certeza se ta certo aqui, nao entendi mt bem o pdf
        if (nixbpe[0] == '1' && nixbpe[1] == '1'){              //direto
            if (nixbpe[2] == '1'){                              //usa X
                if (nixbpe[3] == '1'){                          //B = 1 -> B + X
                    return X.toInt() + B.toInt() + address;
                }
                else if (nixbpe[4] == '1'){                     //P = 1 -> X + PC
                    return X.toInt() + PC.toInt() + address;
                }
                else {                                          //X + endereco (3 e 4 bytes)
                    return address + X.toInt();
                }
            } 
            else {                                              //nao usa X
                if (nixbpe[3] == '1'){                          //B = 1
                    return address + B.toInt();
                }
                else if (nixbpe[4] == '1'){                     //P = 1
                    return address + PC.toInt();
                }
                else {
                    return address;
                }
            }
        } else if (nixbpe[0] == '1' && nixbpe[1] == '0'){       //indireto
            return mem.mem_read(address).toInt();
        } else if (nixbpe[0] == '0' && nixbpe[1] == '1'){       //imediato
            return address;
        }
        return 1;
    }
    
    public static void run(){
        int format;
        do{
            format = next_instruction();
            run_op(format);
        } while (op.toInt() != 12);
    }
    
    public static boolean step(){
        boolean hasNextStep;
        int format;
        
        format = next_instruction();
        run_op(format);
        
        if(op.toInt() != 12) {
            hasNextStep = true;
            return hasNextStep;
        } else {
            hasNextStep = false;
            return hasNextStep;
        }
    }


    //OPERACOES DE 3/4 BYTES
    public static void add (int endereco){
        String24 dado = new String24(24);
        dado =  mem.mem_read(endereco, 3);
        A.setBits( A.toInt() + dado.toInt());
    }   
    
    public static void and (int endereco){
        String24 dado = new String24(24);
        dado =  mem.mem_read(endereco, 3);
        A.setBits( A.toInt() & dado.toInt());
    }
    
    public static void comp (int endereco){
        String24 dado = new String24(24);
        dado =  mem.mem_read(endereco, 3);
        if (A.toInt() == dado.toInt())
            SW.setBits(0);
        else if (A.toInt() > dado.toInt())
            SW.setBits(1);
        else if (A.toInt() < dado.toInt())
            SW.setBits(2);
    }
    
    public static void div (int endereco){
        String24 dado = new String24(24);
        dado =  mem.mem_read(endereco, 3);
        A.setBits( A.toInt() / dado.toInt());
    }
    
    public static void j (int endereco){
        PC.setBits(endereco);
    }
    
    public static void jeq (int endereco){
        if (SW.toInt() == 0)
            PC.setBits(endereco);
    }
    
    public static void jgt (int endereco){
        if (SW.toInt() == 1)
            PC.setBits(endereco);
    }
    
    public static void jlt (int endereco){
        if (SW.toInt() == 2)
            PC.setBits(endereco);
    }
    
    public static void jsub (int endereco){
        L.setBits(PC.getBits());
        PC.setBits(endereco);
    }
    
    public static void lda (int endereco){
        String24 dado = new String24(24);
        dado =  mem.mem_read(endereco, 3);
        A.setBits(dado.toInt());
    }
    
    public static void ldb (int endereco){
        String24 dado = new String24(24);
        dado =  mem.mem_read(endereco, 3);
        B.setBits(dado.toInt());
    }
    
    public static void ldch (int endereco){
        String24 dado = new String24(24);
        dado =  mem.mem_read(endereco+2, 1);
        A.setBits(dado.toInt());
    }
    
    public static void ldl (int endereco){
        String24 dado = new String24(24);
        dado =  mem.mem_read(endereco, 3);
        L.setBits(dado.toInt());
    }
    
    public static void lds (int endereco){
        String24 dado = new String24(24);
        dado =  mem.mem_read(endereco, 3);
        S.setBits(dado.toInt());
    }
    
    public static void ldt (int endereco){
        String24 dado = new String24(24);
        dado =  mem.mem_read(endereco, 3);
        T.setBits(dado.toInt());
    }
    
    public static void ldx (int endereco){
        String24 dado = new String24(24);
        dado =  mem.mem_read(endereco, 3);
        X.setBits(dado.toInt());
    }
    
    public static void mul (int endereco){
        String24 dado = new String24(24);
        dado =  mem.mem_read(endereco, 3);
        A.setBits( A.toInt() * dado.toInt());
    }
    
    public static void or (int endereco){
        String24 dado = new String24(24);
        dado =  mem.mem_read(endereco, 3);
        A.setBits( A.toInt() | dado.toInt());
    }
    
    public static void rsub (){
        PC.setBits(L.getBits());
    }
    
    public static void sta (int endereco, int inst_size){
        mem.mem_write(endereco, inst_size, A);
    }
    
    public static void stb (int endereco, int inst_size){
        mem.mem_write(endereco, inst_size, B);
    }
    
    public static void stch (int endereco, int inst_size){
        mem.mem_write(endereco, inst_size, A);
    }
    
    public static void stl (int endereco, int inst_size){
        mem.mem_write(endereco, inst_size, L);
    }
    
    public static void sts (int endereco, int inst_size){
        mem.mem_write(endereco, inst_size, S);
    }
    
    public static void stt (int endereco, int inst_size){
        mem.mem_write(endereco, inst_size, T);
    }
    
    public static void stx (int endereco, int inst_size){
        mem.mem_write(endereco, inst_size, X);
    }
    
    public static void sub (int endereco){
        String24 dado = new String24(24);
        dado =  mem.mem_read(endereco, 3);
        A.setBits( A.toInt() - dado.toInt());
    }
    
    public static void tix (int endereco){
        String24 dado = new String24(24);
        dado =  mem.mem_read(endereco, 3);
        if (X.toInt() == dado.toInt())
        {
            SW.setBits(0);
            X.setBits(dado.toInt() + 1);
        }
    }
    //FIM DAS OPERACOES DE 3/4 BYTES

    //OPERACOES DE 2 BYTES
    public static void addr (String24 registrador1, String24 registrador2){
        registrador2.setBits( registrador1.toInt() + registrador2.toInt());
    }
    
    public static void clear (String24 registrador){
        registrador.setBits(0);
    }

    public static void compr (String24 registrador1, String24 registrador2){
        if (registrador1.toInt() == registrador2.toInt())
            SW.setBits(0);
        else if (registrador1.toInt() > registrador2.toInt())
            SW.setBits(1);
        else if (registrador1.toInt() < registrador2.toInt())
            SW.setBits(2);
    }
    
    public static void divr (String24 registrador1, String24 registrador2){
        registrador2.setBits(registrador1.toInt() / registrador2.toInt());
    }
    
    public static void mulr (String24 registrador1, String24 registrador2){
        registrador2.setBits(registrador1.toInt() * registrador2.toInt());
    }
    
    public static void rmo (String24 registrador1, String24 registrador2){
        registrador2.setBits(registrador1.getBits());
    }
    
    public static void shiftl (String24 registrador1, int n){
        registrador1.setBits(registrador1.toInt() << n);
    }
    
    public static void shiftr (String24 registrador1, int n){
        registrador1.setBits(registrador1.toInt() >> n);
    }
    
    public static void subr (String24 registrador1, String24 registrador2){
        registrador2.setBits( registrador1.toInt() - registrador2.toInt());
    }
    
    public static void tixr (String24 registrador1){
        if (registrador1.toInt() == X.toInt())
        {
            SW.setBits(0);
            X.setBits(registrador1.toInt()+1);
        }
    }
    //FIM OPERACOES DE 2 BYTES
    
    
    public static void run_op(int formact)
    {
        

        switch(formact){
            case 2:
            switch(op.toInt())
            {
                case 144:                        //case ADDR
                    addr(select_reg(r1),select_reg(r2));
                break;
                case 4:                         //case CLEAR
                    clear(select_reg(r1));
                break;
                case 160:                        //case COMPR
                    compr(select_reg(r1),select_reg(r2));
                break;
                case 156:                        //case DIVR
                    divr(select_reg(r1),select_reg(r2));
                break;
                case 152:                        //case MULR
                    mulr(select_reg(r1),select_reg(r2));
                break;
                case 172:                        //case RMO
                    rmo(select_reg(r1),select_reg(r2));
                break;
                case 164:                        //case SHIFTL
                    shiftl(select_reg(r1),r2.toInt());
                break;
                case 168:                        //case SHIFTR
                    shiftr(select_reg(r1),r2.toInt());
                break;
                case 148:                        //case SUBR
                    subr(select_reg(r1),select_reg(r2));
                break;   
                case 184:                        //case TIXR
                    tixr(select_reg(r1));
                break;                   
            }
            break;
            case 3:
            case 4:
            switch(op.toInt())
            {
                case 24:                        //case ADD
                    add(address.toInt());
                break;
                case 64:                         //case AND
                    and(address.toInt());
                break;
                case 40:                        //case COMP
                    comp(address.toInt());
                break;
                case 36:                        //case DIV
                    div(address.toInt());
                break;
                case 60:                        //case J
                    j(address.toInt());
                break;
                case 48:                        //case JEQ
                    jeq(address.toInt());
                break;
                case 52:                        //case JGT
                    jgt(address.toInt());
                break;
                case 56:                        //case JLT
                    jlt(address.toInt());
                break;   
                case 72:                        //case JSUB
                    jsub(address.toInt());
                break;   
                case 0:                        //case LDA
                    lda(address.toInt());
                break;
                case 104:                         //case LDB
                    ldb(address.toInt());
                break;
                case 80:                        //case LDCH
                    ldch(address.toInt());
                break;
                case 8:                        //case LDL
                    ldl(address.toInt());
                break;
                case 108:                        //case LDS
                    lds(address.toInt());
                break;
                case 116:                        //case LDT
                    ldt(address.toInt());
                break;
                case 4:                        //case LDX
                    ldx(address.toInt());
                break;
                case 32:                        //case MUL
                    mul(address.toInt());
                break;
                case 68:                        //case OR
                    or(address.toInt());
                break;   
                case 76:                        //case RSUB
                    rsub();
                break;  
                case 12:                        //case STA
                    sta(address.toInt(),formact);
                break;
                case 120:                        //case STB
                    stb(address.toInt(),formact);
                break;
                case 84:                        //case STCH
                    stch(address.toInt(),formact);
                break;   
                case 20:                        //case STL
                    stl(address.toInt(),formact);
                break;   
                case 124:                        //case STS
                    sts(address.toInt(),formact);
                break;
                case 132:                        //case STT
                    stt(address.toInt(),formact);
                break;   
                case 16:                        //case STX
                    stx(address.toInt(),formact);
                break;  
                case 28:                        //case SUB
                    sub(address.toInt());
                break;
                case 44:                        //case TIX
                    tix(address.toInt());
                break;
            }
        }
    }
    public static String24 select_reg(String24 r){
        switch(r.toInt()){
            case 0:
                return A;
            case 1:
                return X;
            case 2:
                return L;
            case 3:
                return B;
            case 4:
                return S;
            case 5:
                return T;
            case 6:
                return F;
            default:
                return null;
        }
    }
    
    public static String24 getA() {
        return CPU.A;
    }
    public static String24 getX() {
        return CPU.X;
    }
    public static String24 getL() {
        return CPU.L;
    }
    public static String24 getB() {
        return CPU.B;
    }
    public static String24 getS() {
        return CPU.S;
    }
    public static String24 getT() {
        return CPU.T;
    }
    public static String24 getPC() {
        return CPU.PC;
    }
    public static String24 getSW() {
        return CPU.SW;
    }
    public static String24 getF() {
        return CPU.F;
    }
    
    public static void reset() {
        CPU.op = new String24(8);
        CPU.r1 = new String24(4);
        CPU.r2 = new String24(4);
        CPU.address = new String24(20);
        CPU.nixbpe = new char[6];
        CPU.A = new String24(24); 
        CPU.X = new String24(24);
        CPU.L = new String24(24);
        CPU.B = new String24(24);
        CPU.S = new String24(24);
        CPU.T = new String24(24);
        CPU.PC = new String24(24);
        CPU.SW = new String24(24);
        CPU.F = new String24(48);
    }

}