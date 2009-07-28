
package api;

import codificador.ArithDecoder;
import codificador.ArithEncoder;
import java.io.IOException;
import java.util.HashMap;


public class Contexto {

    protected HashMap<String, HashMap<Integer, Integer>> map;
    protected Contexto proximo;
    protected int maxSimbolos;
    protected static boolean debug;
    protected static ArithEncoder arithEncoder;
    protected static ArithDecoder arithDecoder;

    public Contexto(int maiorSimbolo){
        map = new HashMap<String, HashMap<Integer, Integer>>();
        this.maxSimbolos = maiorSimbolo;
    }

    public void setProximoContexto(Contexto proximo){
        this.proximo = proximo;
    }

    public void inserirContexto(String contexto, int simbolo, int escape){
        HashMap<Integer, Integer> freqs = new HashMap<Integer, Integer>();
        freqs.put(simbolo, 1);
        freqs.put(escape, 1);
        map.put(contexto, freqs);
    }

    public void incContadorDoSimbolo(HashMap<Integer, Integer> freqs, int simbolo){
        int contador = (freqs.get(simbolo) != null)? freqs.get(simbolo)+1 : 1;
        freqs.put(simbolo, contador);
    }

    public void codifica(String contexto, int simbolo) throws IOException {
        codifica(contexto, simbolo, new boolean[maxSimbolos + 2]);
    }

    public void codifica(String contexto, int simbolo, boolean nosAnteriores[]) throws IOException {
        
        int aCodificar = simbolo, low = 0, high = 0, total = 0, escape = maxSimbolos;
        HashMap<Integer, Integer> freqs = map.get(contexto);
        
        if(freqs == null){
            proximo.codifica(contexto.substring(1), simbolo, nosAnteriores);
            inserirContexto(contexto, simbolo, escape);
        } else {
            if(freqs.get(simbolo) == null)                
                aCodificar = escape;
            
            for(int i = 0; i < aCodificar; i++){
                if(freqs.get(i) != null && !nosAnteriores[i]){
                    low += freqs.get(i);
                    nosAnteriores[i] = true;
                }
            }
            
            high = low + freqs.get(aCodificar);
            total = low;

            

            for(int i = aCodificar; i < escape+2; i++){
                if(freqs.get(i) != null && !nosAnteriores[i]){
                    total += freqs.get(i);
                    nosAnteriores[i] = true;
                }
            }
            
            nosAnteriores[escape] = false;

            arithEncoder.encode(low, high, total);
            
            if(aCodificar == escape) {
                proximo.codifica(contexto.substring(1), simbolo, nosAnteriores);
                incContadorDoSimbolo(freqs, escape);
            }
            incContadorDoSimbolo(freqs, simbolo);
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
        HashMap<Integer, Integer> freqs = map.get(contexto);
        
        if(freqs == null){
            simbolo = proximo.getSimbolo(contexto.substring(1), nosAnteriores);
            inserirContexto(contexto, simbolo, escape);
        } else {
            for(int i = 0; i < escape+2; i++)
                if(freqs.get(i) != null && !nosAnteriores[i])
                    total += freqs.get(i);
               
            arith = arithDecoder.getCurrentSymbolCount(total);

            while(nosAnteriores[simbolo] || freqs.get(simbolo) == null || low + freqs.get(simbolo) <= arith){
                if(nosAnteriores[simbolo])
                    simbolo++;
                else if(freqs.get(simbolo++) != null)
                    low += freqs.get(simbolo-1);
            }
            
            high = low + freqs.get(simbolo);
            
            arithDecoder.removeSymbolFromStream(low, high, total);
            
            incContadorDoSimbolo(freqs, simbolo);
            
            /* Debug */
            if(debug && simbolo == escape)
                System.out.printf("escape decodificado no contexto '%s' com low = %d arith = %d total = %d\n", contexto, low, arith, total);
            if(debug && simbolo != escape)
                System.out.printf("simbolo %c decodificado no contexto '%s' com low = %d arith = %d total = %d\n", simbolo, contexto, low, arith, total);
            /* Debug */

            if(simbolo == escape) {
                for(int i = 0; i < escape; i++)
                    if(freqs.get(i) != null)
                        nosAnteriores[i] = true;                
                
                simbolo = proximo.getSimbolo(contexto.substring(1), nosAnteriores);                

                incContadorDoSimbolo(freqs, simbolo);
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
