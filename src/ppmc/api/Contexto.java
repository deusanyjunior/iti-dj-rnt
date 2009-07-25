
package api;

import codificador.ArithDecoder;
import codificador.ArithEncoder;
import java.io.IOException;
import java.util.HashMap;


public class Contexto {

    protected HashMap<String, int[]> map;
    protected Contexto proximo;
    protected int maxSimbolos;
    protected static boolean debug;
    protected static ArithEncoder arithEncoder;
    protected static ArithDecoder arithDecoder;

    public Contexto(int maiorSimbolo){
        map = new HashMap<String, int[]>();
        this.maxSimbolos = maiorSimbolo;
    }

    public void setProximoContexto(Contexto proximo){
        this.proximo = proximo;
    }
    
    public void codifica(String contexto, int simbolo) throws IOException {
        codifica(contexto, simbolo, new boolean[maxSimbolos + 2]);
    }

    public void codifica(String contexto, int simbolo, boolean nosAnteriores[]) throws IOException {
        
        int aCodificar = simbolo, low = 0, high = 0, total = 0, escape = maxSimbolos;
        int[] freqs = map.get(contexto);
        
        if(freqs == null){
            proximo.codifica(contexto.substring(1), simbolo, nosAnteriores);
            freqs = new int[maxSimbolos + 2]; // escape + EOF
            freqs[simbolo] = 1;
            freqs[escape] = 1;
            map.put(contexto, freqs);
        } else {
            if(freqs[simbolo] == 0)
                aCodificar = escape;
            
            for(int i = 0; i < aCodificar; i++){
                if(freqs[i] > 0 && !nosAnteriores[i]){
                    low += freqs[i];
                    nosAnteriores[i] = true;
                }
            }
            
            high = low + freqs[aCodificar];
            total = low;
            
            for(int i = aCodificar; i < freqs.length; i++){
                if(freqs[i] > 0 && !nosAnteriores[i]){
                    total += freqs[i];
                    nosAnteriores[i] = true;
                }
            }
            
            nosAnteriores[escape] = false;

            arithEncoder.encode(low, high, total);
            
            if(aCodificar == escape) {
                proximo.codifica(contexto.substring(1), simbolo, nosAnteriores);
                freqs[escape]++;
            }
            freqs[simbolo]++;            
            map.put(contexto, freqs);
        }
        
        if(debug) {
            System.out.printf("Buscando %c no contexto '%s'\n", simbolo, contexto);
            if(aCodificar == maxSimbolos)
                System.out.printf("Codificando escape no contexto '%s' com low = %d high = %d total = %d\n", contexto, low, high, total);
            else
                System.out.printf("Codificando %c no contexto '%s' com low = %d high = %d total = %d\n", simbolo, contexto, low, high, total);
        }
        
    }

    public int getSimbolo(String contexto) throws IOException {
        return getSimbolo(contexto, new boolean[maxSimbolos+2]);
    }

    public int getSimbolo(String contexto, boolean nosAnteriores[]) throws IOException {

        int simbolo = 0, escape = maxSimbolos;
        int low = 0, arith = 0, high = 0, total = 0;
        int[] freqs = map.get(contexto);
        
        if(freqs == null){
            simbolo = proximo.getSimbolo(contexto.substring(1), nosAnteriores);
            int[] novaFreqs = new int[maxSimbolos + 2];
            novaFreqs[simbolo] = 1;
            novaFreqs[escape] = 1;
            map.put(contexto, novaFreqs);
        } else {
            for(int i = 0; i < freqs.length; i++)
                if(freqs[i] > 0 && !nosAnteriores[i])
                    total += freqs[i];
               
            arith = arithDecoder.getCurrentSymbolCount(total);

            while(nosAnteriores[simbolo] || low + freqs[simbolo] <= arith){
                if(nosAnteriores[simbolo])
                    simbolo++;
                else
                    low += freqs[simbolo++];
            }
            
            high = low + freqs[simbolo];
            
            arithDecoder.removeSymbolFromStream(low, high, total);
            
            freqs[simbolo]++;
            
            /* Debug */
            if(debug && simbolo == escape)
                System.out.printf("escape decodificado no contexto '%s' com low = %d arith = %d total = %d\n", contexto, low, arith, total);
            if(debug && simbolo != escape)
                System.out.printf("simbolo %c decodificado no contexto '%s' com low = %d arith = %d total = %d\n", simbolo, contexto, low, arith, total);
            /* Debug */

            if(simbolo == escape) {
                for(int i = 0; i < escape; i++)
                    if(freqs[i] > 0)
                        nosAnteriores[i] = true;                
                
                simbolo = proximo.getSimbolo(contexto.substring(1), nosAnteriores);                

                freqs[simbolo]++;                
            }
            
            map.put(contexto, freqs);
        }
        
        return simbolo;
    }

    public static ArithEncoder getArithEncoder() {
        return arithEncoder;
    }

    public static void setArithEncoder(ArithEncoder arithEncoder) {
        Contexto.arithEncoder = arithEncoder;
    }

    public static ArithDecoder getArithDecoder() {
        return arithDecoder;
    }

    public static void setArithDecoder(ArithDecoder arithDecoder) {
        Contexto.arithDecoder = arithDecoder;
    }    

}
