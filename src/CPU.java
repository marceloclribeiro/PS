import gui.*;
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
    private static Memory mem = new Memory(240);                 //memoria de tamanho (80 * 24)Bytes 
    private static String24 op = new String24(8);               //salva operacao atual
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
        mem.mem_write(6,new String24("000000000000000000000110".toCharArray()));
        mem.readInput();
        mem.mem_write(0, new String24("110000110000000100101011".toCharArray()));
        next_instruction();
    }
    
    public static void next_instruction(){
        String24 inst = mem.mem_read(PC.toInt());
        
        int inst_size;
        
        if (inst.charAt(6) == '0' && inst.charAt(7) == '0'){
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
        
        set_nixbpe(inst_size, inst);
    }
    
    public static void set_nixbpe(int inst_size, String24 inst){
        if(inst_size == 2)
            return;
        else
            for (int i = 0; i < 6; i++)
                nixbpe[i] = inst.charAt(i + 6);
    }
    
    public static int get_address(int inst_size, String24 inst){
        String24 ad = new String24(20);
        if (inst_size == 3){
         for (int i = 11, j = 0; i < 24; i++, j++){
                ad.setBit(j, inst.charAt(i));
            }
        }else {
            for (int i = 11, j = 0; i < 32; i++, j++){
                ad.setBit(j, inst.charAt(i));
            }
        }
        return address_mode(ad.toInt());
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
            //nao faco ideia do que as flags fazem aqui
        } else if (nixbpe[0] == '0' && nixbpe[1] == '1'){       //imediato
            //aqui tambem nao
        }
    }
    
    public static void get_regs(){
        
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
        //depende de C
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
        PC.setBits(endereco);
    }
    
    public static void jgt (int endereco){
        PC.setBits(endereco);
    }
    
    public static void jlt (int endereco){
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
        mem.mem_write(endereco, A);
    }
    
    public static void stb (int endereco, int inst_size){
        mem.mem_write(endereco, B);
    }
    
    public static void stch (int endereco, int inst_size){
        mem.mem_write(endereco, A);
    }
    
    public static void stl (int endereco, int inst_size){
        mem.mem_write(endereco, L);
    }
    
    public static void sts (int endereco, int inst_size){
        mem.mem_write(endereco, S);
    }
    
    public static void stt (int endereco, int inst_size){
        mem.mem_write(endereco, T);
    }
    
    public static void stx (int endereco, int inst_size){
        mem.mem_write(endereco, X);
    }
    
    public static void sub (int endereco){
        String24 dado = new String24(24);
        dado =  mem.mem_read(endereco, 3);
        A.setBits( A.toInt() - dado.toInt());
    }
    
    public static void tix (int endereco){
        String24 dado = new String24(24);
        dado =  mem.mem_read(endereco, 3);
        X.setBits(dado.toInt()-1);
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
        //depende da flag C
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
        //????? de novo essa desgraca de tix, agora com registrador ?????
        //depende de C
    }
    //FIM OPERACOES DE 2 BYTES
    
//    public static void run_op(int formact)
//    {
//        switch(formact){
//            case 2:
//            switch(op.toInt())
//            {
//                case 0x90:                        //case ADDR
//                    addr();
//                break;
//                case 0x4:                         //case CLEAR
//                    clear();
//                break;
//                case 0xA0:                        //case COMPR
//                    compr();
//                break;
//                case 0x9C:                        //case DIVR
//                    divr();
//                break;
//                case 0x98:                        //case MULR
//                    mulr();
//                break;
//                case 0xAC:                        //case RMO
//                    rmo();
//                break;
//                case 0xA4:                        //case SHIFTL
//                    shiftl();
//                break;
//                case 0xA8:                        //case SHIFTR
//                    shiftr();
//                break;
//                case 0x94:                        //case SUBR
//                    subr();
//                break;   
//                case 0xB8:                        //case TIXR
//                    tixr();
//                break;                   
//            }
//            break;
//            case 3:
//            switch(op.toInt())
//            {
//                case 0x18:                        //case ADD
//                    add();
//                break;
//                case 0x40:                         //case AND
//                    and();
//                break;
//                case 0x28:                        //case COMP
//                    comp();
//                break;
//                case 0x24:                        //case DIV
//                    div();
//                break;
//                case 0x3C:                        //case J
//                    j();
//                break;
//                case 0x30:                        //case JEQ
//                    jeq();
//                break;
//                case 0x34:                        //case JGT
//                    jgt();
//                break;
//                case 0x38:                        //case JLT
//                    jlt();
//                break;   
//                case 0x48:                        //case JSUB
//                    jsub();
//                break;   
//                case 0x0:                        //case LDA
//                    lda();
//                break;
//                case 0x68:                         //case LDB
//                    ldb();
//                break;
//                case 0x50:                        //case LDCH
//                    ldch();
//                break;
//                case 0x8:                        //case LDL
//                    ldl();
//                break;
//                case 0x6C:                        //case LDS
//                    lds();
//                break;
//                case 0x74:                        //case LDT
//                    ldt();
//                break;
//                case 0x4:                        //case LDX
//                    ldx();
//                break;
//                case 0x20:                        //case MUL
//                    mul();
//                break;
//                case 0x44:                        //case OR
//                    or();
//                break;   
//                case 0x4C:                        //case RSUB
//                    rsub();
//                break;  
//                case 0x0C:                        //case STA
//                    sta();
//                break;
//                case 0x78:                        //case STB
//                    stb();
//                break;
//                case 0x54:                        //case STCH
//                    stch();
//                break;   
//                case 0x14:                        //case STL
//                    stl();
//                break;   
//                case 0x7C:                        //case STS
//                    sts();
//                break;
//                case 0x84:                        //case STT
//                    stt();
//                break;   
//                case 0x10:                        //case STX
//                    stx();
//                break;  
//                case 0x1C:                        //case SUB
//                    sub();
//                break;
//                case 0x2C:                        //case TIX
//                    tix();
//                break;
//        }
//    }
//}
}   