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
    private String bits;

    public String24()
    {
        bits = new String("000000000000000000000000");
    }

    public void setBits(String bits)
    {
        this.bits = bits.format("%24s", bits).replace(' ', '0');
    }

    public void setBits(int bits)
    {
        toString24(bits);
    }

    public String getBits()
    {
        return bits;
    }

    public int toInt()
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

    private void toString24(int bit)
    {
        String s = "";

        while (bit > 0)
        {
            s = ( (bit % 2 ) == 0 ? "0" : "1") + s;
            bit /= 2;
        }

        s = String.format("%24s", s).replace(' ', '0');
        setBits(s);
    }
}