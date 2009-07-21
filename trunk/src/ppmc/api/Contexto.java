
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
        codifica(contexto, simbolo, new boolean[maxSimbolos+2]);
    }

    public void codifica(String contexto, int simbolo, boolean nosAnteriores[]) throws IOException {
//        if(debug) System.out.printf("Buscando %c no contexto '%s'\n", simbolo, contexto);
        int[] freqs = map.get(contexto);
        if(freqs == null){
            proximo.codifica(contexto.substring(1), simbolo, nosAnteriores);
            int[] novaFreqs = new int[maxSimbolos + 2]; // escape + EOF
            novaFreqs[simbolo]++;
            novaFreqs[maxSimbolos]++;
            map.put(contexto, novaFreqs);
        } else {
            int aCodificar = simbolo, low = 0, high, total;
            nosAnteriores[maxSimbolos] = false;
            if(freqs[simbolo] == 0){
                aCodificar = maxSimbolos; // simbolo de escape
                if(debug) System.out.printf("Codificando escape no contexto '%s' ", contexto);
            } else {
                if(debug) System.out.printf("Codificando %c no contexto '%s' ", simbolo, contexto);
            }
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
            //codifica com low high total
            if(debug) System.out.printf("com low = %d high = %d total = %d\n", low, high, total);
            arithEncoder.encode(low, high, total);
            freqs[simbolo]++;
            if(aCodificar == maxSimbolos) {// escape
                freqs[maxSimbolos]++;
                proximo.codifica(contexto.substring(1), simbolo, nosAnteriores);
            }
            map.put(contexto, freqs);
        }
    }

    public int getSimbolo(String contexto) throws IOException {
        return getSimbolo(contexto, new boolean[maxSimbolos+2]);
    }

    public int getSimbolo(String contexto, boolean nosAnteriores[]) throws IOException {
//        if(debug) System.out.printf("Procurando contexto '%s'\n", contexto);
        int[] freqs = map.get(contexto);
        int simbolo;
        if(freqs == null){
            simbolo = proximo.getSimbolo(contexto.substring(1), nosAnteriores);
            int[] novaFreqs = new int[maxSimbolos + 2];
            novaFreqs[simbolo]++;
            novaFreqs[maxSimbolos]++;
            map.put(contexto, novaFreqs);
        } else {
//            if(debug) System.out.printf("Contexto '%s' encotrado\n", contexto);
            int low, arithLow, high, total = 0;
            for(int i = 0; i < freqs.length; i++){
                if(freqs[i] > 0 && !nosAnteriores[i]){
                    total += freqs[i];
                }
            }

            low = 0;
            simbolo = 0;            
            arithLow = arithDecoder.getCurrentSymbolCount(total);
            while(low + freqs[simbolo] <= arithLow && simbolo < maxSimbolos){
                while(nosAnteriores[simbolo]) simbolo++;
                low += freqs[simbolo++];
                while(nosAnteriores[simbolo]) simbolo++;
            }
            
            while(freqs[simbolo] == 0 || nosAnteriores[simbolo]){
                simbolo++;
            }
            high = low + freqs[simbolo];
            arithDecoder.removeSymbolFromStream(low, high, total);
            freqs[simbolo]++;
            if(simbolo == maxSimbolos) { // escape
                if(debug) System.out.printf("escape decodificado no contexto '%s' com low = %d arith = %d total = %d\n", contexto, low, arithLow, total);
                for(int i = 0; i < maxSimbolos; i++){
                    if(freqs[i] > 0) nosAnteriores[i] = true;
                }
                nosAnteriores[maxSimbolos] = false;
                simbolo = proximo.getSimbolo(contexto.substring(1), nosAnteriores);
                freqs[simbolo]++;
            } else {
                if(debug) System.out.printf("%c decodificado no contexto '%s' com low = %d arith = %d total = %d\n", simbolo, contexto, low, arithLow, total);
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
