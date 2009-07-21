
package api;

import java.io.IOException;

/**
 *
 */
public class ContextoMenosUm extends Contexto {

    int[] freqs;
    int total;

    public ContextoMenosUm(int maiorSimbolo) {
        super(maiorSimbolo);
        freqs = new int[maiorSimbolo+1];
        total = maiorSimbolo + 1;
        for(int i = 0; i < maiorSimbolo+1; i++){
            freqs[i] = 1;
        }
    }

    @Override
    public void codifica(String contexto, int simbolo, boolean nosAnteriores[]) throws IOException {
        if(simbolo == freqs.length){
            simbolo--;
            if(debug) System.out.printf("Codificando EOF ");
        } else {
            if(debug) System.out.printf("Codificando %c no contexto -1 ", simbolo);
        }
        int low = 0, high;

        for(int i = 0; i < simbolo; i++){
            low += freqs[i];
        }

        high = low + freqs[simbolo];
        //codifica com low high total
        if(debug) System.out.printf("com low = %d high = %d total = %d\n", low, high, total);
        arithEncoder.encode(low, high, total);
        if(simbolo == freqs.length - 1)
            throw new IOException("EOF");
        freqs[simbolo] = 0;
        total--;
    }

    @Override
    public int getSimbolo(String contexto) throws IOException {
        return getSimbolo(contexto, null);
    }

    @Override
    public int getSimbolo(String contexto, boolean nosAnteriores[]) throws IOException {
        int low, arithLow, high, simbolo;
        low = 0;
        simbolo = 0;
        arithLow = arithDecoder.getCurrentSymbolCount(total);
        while(low < arithLow && simbolo < maxSimbolos){
            low += freqs[simbolo++];
        }

//        if(simbolo == maxSimbolos + 1)
//            simbolo = maxSimbolos;
        
        while (simbolo < maxSimbolos && freqs[simbolo] == 0) {
            simbolo++;
        }
        if(simbolo == maxSimbolos)
            throw new IOException("EOF");
        high = low + freqs[simbolo];
        arithDecoder.removeSymbolFromStream(low, high, total);
        if(debug) System.out.printf("%c decodificado no contexto -1 com low = %d arith = %d total = %d\n", simbolo, low, arithLow, total);
        freqs[simbolo] = 0;
        total--;
        return simbolo;
    }



}
