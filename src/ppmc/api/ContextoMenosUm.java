
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
        freqs = new int[maiorSimbolo];
        total = maiorSimbolo;
        for(int i = 0; i < maiorSimbolo; i++){
            freqs[i] = 1;
        }
    }

    @Override
    public void codifica(String contexto, int simbolo) throws IOException {
        if(debug) System.out.printf("Codificando %c no contexto -1 ", simbolo);
        int low = 0, high;

        for(int i = 0; i < simbolo; i++){
            low += freqs[i];
        }

        high = low + freqs[simbolo];
        //codifica com low high total
        if(debug) System.out.printf("com low = %d high = %d total = %d\n", low, high, total);
        arithEncoder.encode(low, high, total);
        freqs[simbolo] = 0;
        total--;
    }

    @Override
    public int getSimbolo(String contexto) {
        int low, arithLow, simbolo;
        low = 0;
        simbolo = 0;
        arithLow = arithDecoder.getCurrentSymbolCount(total);
//        System.out.println(freqs[l]);
        while(low < arithLow && simbolo < maxSimbolos){
            low += freqs[simbolo++];
        }
        while(freqs[simbolo] == 0) simbolo++;
        if(debug) System.out.printf("%c decodificado no contexto -1 com low = %d arith = %d total = %d\n", simbolo, low, arithLow, total);
        freqs[simbolo] = 0;
        total--;
        return simbolo;
    }



}
