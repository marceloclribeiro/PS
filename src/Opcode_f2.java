/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marce
 */
public enum Opcode_f2 {
    ADDR(0x90),
    CLEAR(0x4),
    COMPR(0xA0),
    DIVR(0x9C),
    MULR(0x98),
    RMO(0xAC),
    SHIFTL(0xA4),
    SHIFTR(0xA8),
    SUBR(0x94),
    TIXR(0xB8);
    private final int op_f2;

    Opcode_f2(int op_f2) {
        this.op_f2 = op_f2;
    }
    public int getOpcode(){
        return op_f2;
    }
}