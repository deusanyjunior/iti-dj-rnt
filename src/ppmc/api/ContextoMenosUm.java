
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

        int low = 0, high;

        if(simbolo == freqs.length)
            simbolo = maxSimbolos;
        
        for(int i = 0; i < simbolo; i++)
            low += freqs[i];        

        high = low + freqs[simbolo];

        arithEncoder.encode(low, high, total);

        /* Debug */
        if(debug && simbolo == maxSimbolos)
            System.out.printf("Codificando EOF com low = %d high = %d total = %d\n", low, high, total);
        if(debug && simbolo != maxSimbolos)
            System.out.printf("Codificando %c no contexto -1 com low = %d high = %d total = %d\n", simbolo, low, high, total);
        /* Debug */
        
        if(simbolo == maxSimbolos)            
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
        
        int simbolo = 0, low = 0, arithLow, high, eof = maxSimbolos;
        
        arithLow = arithDecoder.getCurrentSymbolCount(total);

        while(low < arithLow || freqs[simbolo] == 0)
            low += freqs[simbolo++];       
        
        high = low + freqs[simbolo];

        /* Debug */
        if(debug && simbolo == eof)
            System.out.printf("EOF decodificado com low = %d arith = %d total = %d\n", low, arithLow, total);
        if(debug && simbolo != eof)
            System.out.printf("%c decodificado no contexto -1 com low = %d arith = %d total = %d\n", simbolo, low, arithLow, total);
        /* Debug */
        
        if(simbolo == eof)
            throw new IOException("EOF");
        
        arithDecoder.removeSymbolFromStream(low, high, total);
        
        freqs[simbolo] = 0;

        total--;
        
        return simbolo;
    }



}
