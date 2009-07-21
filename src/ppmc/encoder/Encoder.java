
package encoder;

import api.Contexto;
import api.ContextoMenosUm;
import api.ContextoZero;
import codificador.ArithEncoder;
import io.BitInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 */
public class Encoder {

    Contexto contextos[];

    public Encoder(int nBitsPorSimbolo, int maiorContexto, String input, String output) throws IOException {
        int maiorSimbolo = (int)Math.pow(2, nBitsPorSimbolo);
        Contexto.setArithEncoder(new ArithEncoder(new FileOutputStream(output)));
        contextos = new Contexto[maiorContexto+2];
        contextos[0] = new ContextoMenosUm(maiorSimbolo);
        contextos[1] = new ContextoZero(maiorSimbolo);
        contextos[1].setProximoContexto(contextos[0]);
        for(int i = 2; i < maiorContexto + 2; i++){
            contextos[i] = new Contexto(maiorSimbolo);
            contextos[i].setProximoContexto(contextos[i-1]);
        }
        BitInputStream bis = new BitInputStream(input);
        int lido;
        String contexto = "";
        try{
            for(int i = 1; i < maiorContexto+1; i++){
                lido = bis.nextBits(nBitsPorSimbolo);                
                contextos[i].codifica(contexto, lido);
                contexto += (char)lido;
            }
            while(true){
                lido = bis.nextBits(nBitsPorSimbolo);
                contextos[maiorContexto+1].codifica(contexto, lido);
                contexto = contexto.substring(1) + (char)lido;
            }
        } catch(IOException ex){
            Contexto.getArithEncoder().close();
        }
    }
}
