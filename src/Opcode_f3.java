/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author marce
 */
public enum Opcode_f3 {
    ADD(0x18),
    AND(0x40),
    CLEAR(0x4),
    COMP(0x28),
    DIV(0x24),
    J(0x3C),
    JEQ(0x30),
    JGT(0x34),
    JLT(0x38),
    JSUB(0x48),
    LDA(0x0),
    LDB(0x68),
    LDCH(0x50),
    LDL(0x8),
    LDS(0x6C),
    LDT(0x74),
    LDX(0x4),
    MUL(0x20),
    OR(0x44),
    RSUB(0x4C),
    STA(0x0C),
    STB(0x78),
    STCH(0x54),
    STL(0x14),
    STS(0x7C),
    STT(0x84),
    STX(0x10),
    SUB(0x1C),
    TIX(0x2C);

    private final int op_f3;

    Opcode_f3(int op_f3) {
        this.op_f3 = op_f3;
    }
    public int getOpcode(){
        return op_f3;
    }
}
