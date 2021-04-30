/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alejandro
 */
public class Memory
{
    private String24[] memory;

    public Memory(int size)
    {
        memory = new String24[size];
    }

    public String24 mem_read(int endereco, int format)
    {
        if (format == 4)
        {
            String24 n = new String24(32);
            
            for(int i = 12; i < 24; i++)
                n.setBit(i - 12, memory[endereco].charAt(i));
            
            for(int i = 0; i < 20; i++)
                n.setBit(i + 12, memory[endereco + 1].charAt(i));
            return n;
        }
        else if (format == 2){
            String24 n = new String24(16);
            
            for(int i = 0;  i< 16; i++)
                n.setBit(i, memory[endereco].charAt(i));
            return n;
        }
        else
            return memory[endereco];
    }

    public void mem_write(int endereco, String24 valor, int format)
    {
        if (format == 4)
        {
            for (int i = 0; i < 12; i++){
                memory[endereco].setBit(i, 'X');
            }
            for (int i = 12; i < 24; i++){
                memory[endereco].setBit(i, valor.charAt(i - 12));
            }
            
            for (int i = 0; i < 20; i++){
                memory[endereco+1].setBit(i, valor.charAt(i + 12));
            }
            
        }
        else if(format == 3){
            memory[endereco] = valor;
        }
        else{
            for (int i = 0; i < 16; i++)
                memory[endereco].setBit(i, valor.charAt(i));
            
            for (int i = 16; i < 24; i++){
                memory[endereco].setBit(i, 'X');
            }
        }
    }
}