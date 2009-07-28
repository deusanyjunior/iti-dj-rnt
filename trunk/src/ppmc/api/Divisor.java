package api;

import io.BitOutputStream;
import io.BitInputStream;
import java.io.IOException;

/**
 *
 */
public class Divisor {

    public String divide(String input, int nBitsALer, int contexto, int off) throws IOException {
        int EOF = 2*((int)Math.pow(2, nBitsALer-1))+1;
        BitInputStream bis = new BitInputStream(input, false);
        String output = String.format("[%d-%d]%s.pt%d", contexto, nBitsALer, input, off/nBitsALer);
        BitOutputStream bos = new BitOutputStream(output);
        
        bis.nextBits(off);
        int i = 0, lido = bis.nextBits(nBitsALer), cont = 0;
        while (lido != EOF) {
            cont += nBitsALer;
            bos.print(lido, nBitsALer);
            bis.nextBits(8-nBitsALer);
            lido = bis.nextBits(nBitsALer);
        }
            bos.writeBigrama();        
//            bos.close();

        return output;
    }


    public String[] divide(String input, int nBitsALer, int contexto) throws IOException {
        int EOF = 2*((int)Math.pow(2, nBitsALer-1))+1;
        BitInputStream bis = new BitInputStream(input, false);
        String[] outputs = new String[8 / nBitsALer];
        BitOutputStream[] boss = new BitOutputStream[outputs.length];
        for (int i = 0; i < outputs.length; i++) {
            outputs[i] = String.format("[%d-%d]%s.pt%d", contexto, nBitsALer, input, i+1);
            boss[i] = new BitOutputStream(outputs[i]);
        }

        int i = 0, lido = bis.nextBits(nBitsALer), cont = 0;
        while (lido != EOF) {
            cont += nBitsALer;
            boss[i].print(lido, nBitsALer);
            lido = bis.nextBits(nBitsALer);
            i = (i + 1) % boss.length;
        }
        for (int j = 0; j < boss.length; j++) {
            boss[i].writeBigrama();
        }
        for (int j = 0; j < boss.length; j++) {
            boss[i].close();
        }
        return outputs;
    }

    public void junta(String[] inputs, String output, int nBitsPorVez) throws IOException {

        int EOF = 2*((int)Math.pow(2, nBitsPorVez-1))+1;
        BitInputStream[] biss = new BitInputStream[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            biss[i] = new BitInputStream(inputs[i], true);
        }

        BitOutputStream bos = new BitOutputStream(output);

        int i = 0, lido = biss[0].nextBits(nBitsPorVez), cont = 0;

        while (lido != EOF) {
            cont += nBitsPorVez;
            bos.print(lido, nBitsPorVez);
            i = (i + 1) % biss.length;
            lido = biss[i].nextBits(nBitsPorVez);
        }
        bos.close();

    }

}
