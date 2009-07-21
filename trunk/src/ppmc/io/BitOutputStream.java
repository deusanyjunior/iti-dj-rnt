
package compressor.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 *
 */
public class BitOutputStream {

    FileOutputStream out;
    private int[] buffer;
    private byte[] bigBuffer;
    private int head, tail, bigIndex;
    private static final int BUFFER_SIZE = 1024, BIG_BUFFER_SIZE = 1024;

    /**
     *
     * @param pathName Caminho do arquivo onde os bits serao escrito
     */
    public BitOutputStream(String pathName){
        try {
            out = new FileOutputStream(pathName);
            buffer = new int[BUFFER_SIZE];
            bigBuffer = new byte[BIG_BUFFER_SIZE];            
            head = tail = bigIndex = 0;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * @param file Arquivo onde os bits serao escrito
     */
    public BitOutputStream(File file){
        try {
            out = new FileOutputStream(file);
            buffer = new int[BUFFER_SIZE];
            bigBuffer = new byte[BIG_BUFFER_SIZE];
            head = tail = bigIndex = 0;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 
     * @param value Inteiro que sera convertido para forma binaria e escrito em arquivo
     * @param nbits numero de bits que serao escritos ('0' excedentes ficaram a esquerda)
     */
    public void print(int value, int nbits){
        int valor[] = new int[nbits];
        for(int i = nbits-1; i >= 0; i--){
            valor[i] = value%2;
            value /= 2;
        }
        for(int i = 0; i < nbits; i++){
            buffer[tail++] = valor[i];
            tail = tail%BUFFER_SIZE;
            value /= 2;
        }
        while((tail-head+BUFFER_SIZE)%BUFFER_SIZE >= 8){
            int soma = 0, mul = 128, b = 8;
            while(b-- > 0){
                soma += mul*buffer[head++];
                head = head%BUFFER_SIZE;
                mul /= 2;
            }
            try {
                writeByte(soma);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * @param value Cadeia de binarios a ser escrita
     */
    public void print(String value){
        for(int i = 0; i < value.length(); i++){
            buffer[tail++] = value.charAt(i);
            tail = tail%BUFFER_SIZE;
        }
        // Grava em arquivo assim que o buffer contiver ao menos 8 bits
        while((tail-head+BUFFER_SIZE)%BUFFER_SIZE >= 8){
            int soma = 0, mul = 1, b = 8;
            while(b-- > 0){
                soma += mul*buffer[head++];
                head = head%BUFFER_SIZE;
                mul *= 2;
            }
            try {
                writeByte(soma);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Coloca o bigrama delimitador '01' e completa o ultimo byte com '1's,
     * depois imprime os bytes restantes e fecha o arquivo
     */
    public void close(){             
        try {
            out.write(bigBuffer, 0, bigIndex);
            out.close();
        } catch (IOException ex) {}
    }

    private void writeByte(int b) throws IOException{
        if(bigIndex == BIG_BUFFER_SIZE){
            out.write(bigBuffer);
            bigIndex = 0;
        }
        bigBuffer[bigIndex++] = (byte)b;
    }

}
