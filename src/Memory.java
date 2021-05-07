/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alejandro
 */
import java.io.*;

public class Memory
{
    private String24[] memory;

    public Memory(int size){
        memory = new String24[size];
        init(size);
    }

    public String24 mem_read(int endereco){
        int format;
        
        if(memory[endereco].charAt(6) == '0' && memory[endereco].charAt(7) == '0')
            format = 2;
        else if(memory[endereco + 1].charAt(3) == '1')
            format = 4;
        else
            format = 3;
        
        String24 inst = new String24(format * 8);
        
        for (int i = 0, j = -1; i < format * 8; i++){
            if ((i % 8) == 0){
                j++;
            }
            inst.setBit(i, memory[endereco + j].charAt(i % 8));
        }
        return inst;
    }
    public String24 mem_read(int endereco, int size){
        String24 inst = new String24(size * 8);

        for (int i = 0, j = -1; i < size * 8; i++){
            if ((i % 8) == 0){
                j++;
            }
            inst.setBit(i, memory[endereco + j].charAt(i % 8));
        }
        return inst;
    }

    public void mem_write(int endereco, String24 valor){
        int k = 0;
        for (int i = 0; i < 3; i++){
           for(int j = 0; j < 8; j++){
            memory[endereco + i].setBit(j, valor.charAt(k));
                    k++;
        }
    }
    }
    
    public void readInput(){
        try{  
            File file = new File(System.getProperty("user.dir") + "\\test\\Input.txt");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            int i = 0;
            while((line = br.readLine()) != null){
                mem_write(i, new String24(line));
                i += (line.length() == 32 ? 4 : line.length() == 24 ? 3 : 2);
            }  
            fr.close();
        }catch(IOException e){  
            e.printStackTrace();  
        }
    }
    
    private void init(int size){
        for (int i = 0; i < size; i++)
            memory[i] = new String24(8);
    }
}