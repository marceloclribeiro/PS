package logic;
import java.lang.Object.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Thomazio
 */
public class String24
{    
    private char[] bits;
    private int size;
    
    public String24(int i){
        bits = new char[i];
        size = i;
    }
    public String24(char[] b){
        bits = b;
        size = b.length;
    }
    public String24(String b){
        bits = b.toCharArray();
        size = b.length();
    }
    
    public void setBit(int index, char c){
        this.bits[index] = c;
    }
    public void setBits(int b){
        if (Integer.toBinaryString(b).length() < size){
            String i = String.format("%" + size + "s", Integer.toBinaryString(b)).replace(" ", "0");
            bits = i.toCharArray();
        } else {
            bits = Integer.toBinaryString(b).toCharArray();
        }
    }
    public void setBits(char[] bits){
        this.bits = bits;
        size = bits.length;
    }

    public char[] getBits(){
        return bits;
    }
    
    public char charAt(int index){
        return bits[index];
    }

    public int toInt(){
        int result = 0;

        for(int i = bits.length - 1; i >= 0; i--)
        {
            if (bits[i] == '1')
                result += Math.pow(2, (bits.length - i - 1));
        }
        return result;
    }

}