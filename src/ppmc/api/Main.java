
package api;

import decoder.Decoder;
import encoder.Encoder;
import java.io.IOException;

/**
 *
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Contexto.debug = true;
        Encoder encoder = new Encoder(8, 3, "in.txt", "comp.txt");
        Decoder decoder = new Decoder(8, 3, "comp.txt","out.txt");
        
        
    }

}
