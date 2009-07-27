
package api;

import java.util.LinkedList;

/**
 *
 */
public class Node {

    private LinkedList<Node> filhos;
    private int simbolo;
    private int contador;
//    private static int escape;

    public Node(int simbolo){
        this.simbolo = simbolo;
        this.contador = 1;
        filhos = new LinkedList<Node>();
    }


    /**
     * Busca um contexto na arvore
     * @param contexto lista com os simbolos anteriores
     * @param off Indice do primeiro simbolo valido no contexto
     * @return Node do contexto ou null se ele nao existir
     */
    public Node getContexto(LinkedList<Integer> contexto, int off){
        int i = off;
        Node pai = this;

        while(pai != null && i < contexto.size()) {
            pai = pai.getFilho(contexto.get(i++));
        }

        return pai;
    }


    public void inserir(int simbolo, int escape, LinkedList<Integer> contexto, int off) {
        int i = off;
        Node avo = this, pai = this;

        while(pai != null && i < contexto.size()) {
            avo = pai;
            pai = pai.getFilho(contexto.get(i++));
        }

        while(i < contexto.size()){
            pai = new Node(contexto.get(i++));
            avo.filhos.add(pai);
            avo = pai;
        }

        pai.filhos.add(new Node(simbolo));
        pai.filhos.add(new Node(escape));

    }

    /**
     * Busca um simbolo sob um contexto
     * @param simbolo
     * @param contexto
     * @param off Indice do primeiro simbolo valido no contexto
     * @return true se o simbolo esta na arvore, false caso contrario
     */
    public boolean existeNoContexto(int simbolo, LinkedList<Integer> contexto, int off) {

        int i = off;
        Node pai = this;

        while(pai != null && i < contexto.size()) {
            pai = pai.getFilho(contexto.get(i++));
        }

        if(i == contexto.size())
            return true;
        else
            return false;
    }

    /**
     * Incrementa o contador de um simbolo sob um contexto
     * @param simbolo
     * @param contexto
     * @param off Indice do primeiro simbolo valido no contexto
     * 
     */
    public void inc(int simbolo, LinkedList<Integer> contexto, int off) {

        

    }

    /**
     * 
     * @param simbolo
     * @return Filho que contem aquele simbolo ou null se o simbolo nao existe nesse contexto
     */
    public Node getFilho(int simbolo){
        for (Node no : filhos)
            if(no.simbolo == simbolo)
                return no;
        return null;
    }

}
