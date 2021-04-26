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
        String24[] memory = new String24[size];
    }

    public String24 mem_read(int endereco)
    {
        return memory[endereco];
    }

    public void mem_write(int endereco, String24 valor)
    {
        memory[endereco] = valor;
    }
}