/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alejandro
 */
public class String24
{
    String bits;

    public String24()
    {
        bits = new String("000000000000000000000000");
    }

    public void setBits(String bits)
    {
        this.bits = bits;
    }
    int toInt()
    {
        char[] num = bits.toCharArray();
        int result = 0;

        for(int i = num.length - 1; i >= 0; i--)
        {
            if (num[i] == '1')
                result += Math.pow(2, (num.length - i - 1));
        }
        return result;
    }
}