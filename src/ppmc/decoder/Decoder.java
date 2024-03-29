
package decoder;

import api.Contexto;
import api.ContextoMenosUm;
import api.ContextoZero;
import codificador.ArithDecoder;
import io.BitOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class Decoder {

    Contexto contextos[];

    public Decoder(int nBitsPorSimbolo, int maiorContexto, String input, String output) throws IOException {
        int maiorSimbolo = (int)Math.pow(2, nBitsPorSimbolo);
        Contexto.setArithDecoder(new ArithDecoder(new FileInputStream(input)));
        BitOutputStream bos = new BitOutputStream(output);
        contextos = new Contexto[maiorContexto+2];
        contextos[0] = new ContextoMenosUm(maiorSimbolo);
        contextos[1] = new ContextoZero(maiorSimbolo);
        contextos[1].setProximoContexto(contextos[0]);
        for(int i = 2; i < maiorContexto + 2; i++){
            contextos[i] = new Contexto(maiorSimbolo);
            contextos[i].setProximoContexto(contextos[i-1]);
        }
        try{
            int lido;
            String contexto = "";
            lido =  contextos[1].getSimbolo(contexto);
            contexto += (char)lido;
            bos.print(lido, nBitsPorSimbolo);
            for(int i = 2; i < maiorContexto + 1; i++) {
                lido =  contextos[i].getSimbolo(contexto);
                contexto += (char)lido;
                bos.print(lido, nBitsPorSimbolo);
            }
            while(true) {
                lido =  contextos[maiorContexto+1].getSimbolo(contexto);
                contexto = contexto.substring(1) + (char)lido;
                bos.print(lido, nBitsPorSimbolo);
            }
        }catch (IOException ex){
            bos.close();            
        }
    }

}
