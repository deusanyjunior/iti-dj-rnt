
package api;

import decoder.Decoder;
import encoder.Encoder;
import java.io.IOException;

/**
 *
 */
public class Main {

    public static void main(String[] args) throws IOException {
//        Contexto.debug = true;
        long time = System.currentTimeMillis();
//        Encoder encoder = new Encoder(8, 1, "scriptTabelasReduzidoPostgres.sql", "comp.ppm");
//        Decoder decoder = new Decoder(8, 1, "comp.ppm","strp.sql");
        Encoder encoder = new Encoder(16, 1, "100-1.dat", "comp.ppm");
        Decoder decoder = new Decoder(16, 1, "comp.ppm","100-1(2).dat");
//        Encoder encoder = new Encoder(8, 1, "Selecao_-_Jailton_Maciel.rar", "comp.ppm");
//        Decoder decoder = new Decoder(8, 1, "comp.ppm","sjm.rar");
        System.out.println(System.currentTimeMillis() - time);
        
    }

}
