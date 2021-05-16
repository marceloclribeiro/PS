package logic;

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
    
    public String24(int i){
        bits = new char[i];
    }
    public String24(char[] b){
        bits = b;
    }
    public String24(String b){
        bits = b.toCharArray();
    }
    
    public void setBit(int index, char c){
        this.bits[index] = c;
    }
    public void setBits(int b){
        this.bits = Integer.toBinaryString(b).toCharArray();
    }
    public void setBits(char[] bits){
        this.bits = bits;
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