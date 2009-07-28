
package api;

import decoder.Decoder;
import encoder.Encoder;
import java.io.IOException;

/**
 *
 */
public class Main {

    public static void main(String[] args) throws IOException {

        int nBitsPorSimbolo = 8; // testar com 1,2,4,8
        int tamanhoContexto = 4; // testar com 0,1,...
        String file = "sjm.rar";
        
//        Contexto.debug = true;
        long time = System.currentTimeMillis();

        Divisor div = new Divisor();

        String[] inputs = new String[8/nBitsPorSimbolo];


        for(int i = 0; i < inputs.length; i++){
            inputs[i] = div.divide(file, nBitsPorSimbolo, tamanhoContexto, i*nBitsPorSimbolo);
        }

        for(int i = 0; i < inputs.length; i++){
            Encoder encoder = new Encoder(nBitsPorSimbolo, tamanhoContexto, inputs[i], inputs[i]+".ppm");
            Decoder decoder = new Decoder(nBitsPorSimbolo, tamanhoContexto, inputs[i]+".ppm", "(2)"+inputs[i]);
            inputs[i] = "(2)" + inputs[i];
        }

        div.junta(inputs, "(2)"+file, nBitsPorSimbolo);
        
//        Encoder encoder = new Encoder(8, 1, "scriptTabelasReduzidoPostgres.sql", "comp.ppm");
//        Decoder decoder = new Decoder(8, 1, "comp.ppm","strp.sql");
//        Encoder encoder = new Encoder(1, 1, "100-1.dat", "comp.ppm");
//        Decoder decoder = new Decoder(1, 1, "comp.ppm","100-1(2).dat");
//        Encoder encoder = new Encoder(8, 4, "Selecao_-_Jailton_Maciel.rar", "comp.ppm");
//        Decoder decoder = new Decoder(8, 4, "comp.ppm","sjm.rar");
        
        System.out.println(System.currentTimeMillis() - time+"ns");
        
    }

}
