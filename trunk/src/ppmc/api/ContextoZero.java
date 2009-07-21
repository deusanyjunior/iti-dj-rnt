
package api;

import java.io.IOException;

/**
 *
 */
public class ContextoZero extends Contexto {

    public ContextoZero(int maiorSimbolo) {
        super(maiorSimbolo);
    }

    @Override
    public void codifica(String contexto, int simbolo) throws IOException {
        contexto = " "; // para nao da erro com o contexto.substring(1);
        super.codifica(contexto, simbolo);
    }

    @Override
    public int getSimbolo(String contexto) throws IOException {
        contexto = " "; // para nao da erro com o contexto.substring(1);
        return super.getSimbolo(contexto);
    }

}
