
package io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class BitInputStream {

    private FileInputStream in;
    private int[] buffer;
    private int[] bigBuffer;
    private int head, tail, next1, next2, bigIndex;
    private static final int BUFFER_SIZE = 1024, BIG_BUFFER_SIZE = 1024;
    private boolean jumpBigrama;

    /**
     *
     * @param pathName Caminho do arquivo de onde os bits serao lidos
     * @throws java.io.IOException
     */
    public BitInputStream(String pathName) throws IOException{
        in = new FileInputStream(pathName);
        buffer = new int[BUFFER_SIZE];
        bigBuffer = new int[BIG_BUFFER_SIZE];
        head = tail = 0;
        bigIndex = BIG_BUFFER_SIZE;
        next1 = readByte();
        next2 = readByte();
        this.jumpBigrama = false;
        fillBuffer();
    }

    /**
     *
     * @param file Arquivo onde os bits serao escrito
     * @param jumpBigrama true Para remover o delimitador inserido pelo Encoder
     */
    public BitInputStream(File file, boolean jumpBigrama) throws IOException{
        in = new FileInputStream(file);
        buffer = new int[BUFFER_SIZE];
        bigBuffer = new int[BIG_BUFFER_SIZE];
        bigIndex = BIG_BUFFER_SIZE;
        head = tail = 0;
        next1 = readByte();
        next2 = readByte();
        this.jumpBigrama = jumpBigrama;
        fillBuffer();
    }

    public int nextBits(int nBits) throws IOException {
        int soma = 0, mul = (int)Math.pow(2, nBits-1), EOF = 2*mul+1;
        while(nBits-- > 0){
            if(head == tail)
                fillBuffer();
            if(buffer[head] == -1){
                return EOF;
            }
            soma += mul*buffer[head++];
            head = head%BUFFER_SIZE;
            mul /= 2;
        }
        return soma;
    }

    private int readByte() throws IOException{
        if(bigIndex == bigBuffer.length){
            byte b[] = new byte[BIG_BUFFER_SIZE];
            int aux = in.read(b);
            if(aux < 0){
                bigBuffer[0] = -1;
            }
            for(int i = 0; i < aux; i++){
                bigBuffer[i] = (b[i] < 0)? b[i]+256 : b[i];
            }
            if(aux >= 0 && aux < BIG_BUFFER_SIZE){
                bigBuffer[aux] = -1;
            }
            bigIndex = 0;
        }
        return bigBuffer[bigIndex++];
    }

    private void fillBuffer() throws IOException{
        int aux = next1;
        next1 = next2;
        next2 = readByte();
        int size = 8;
        // Se nao houver mais bits a ler lanca o EOF
        if(aux == -1) {
            buffer[tail++] = -1;
            return;
        } 
        // Senao, remove o delimitador inserido pelo Encoder (ultimo bigrama '01' e todos os '1's que o segue)
        if(jumpBigrama && next2 == -1){
            //Se o ultimo byte foi 11111111 remove o '0' do penultimo byte e o ultimo byte
            if(next1 == 255){
                next1 = -1;
                aux/=2;
                size--;
            }
            //Se o ultimo byte foi 01111111 remove o ultimo byte
            else if(next1 == 127){
                next1 = -1;
            }
            //Senao vai removendo os ultimos bits da mensagem, ate que remova um '0'
            else if(next1 == -1) {
                for( ; aux%2 == 1; aux/=2, size--) {}
                if(size != 8){
                    aux/=2;
                    size--;                
                }
            }
        }
        int valor[] = new int[size];
        for(int i = size-1; i >= 0; i--){
            valor[i] = aux%2;
            aux /= 2;
        }
        for(int i = 0; i < size; i++){
            buffer[tail++] = valor[i];
            tail = tail%BUFFER_SIZE;
        }
    }

}
