
package compressor.huffman;

import compressor.api.Main;

/**
 *
 */
public class Node implements Comparable<Node>{

    private int simbol, unitaryCode, freq, code, codeSize;
    private Node son[];

    /**
     * Cria um no' folha
     * @param simbol Simbolo guardado na folha
     * @param freq Ocorrencias do simbolo no texto
     */
    public Node(int simbol, int freq) {
        this.simbol = simbol;
        this.freq = freq;
    }

    /**
     * Cria um no' intermediario
     * @param left Filho a esquerda
     * @param right Filho a direita
     */
    public Node(Node left, Node right) {
        this.simbol = -1;
        son = new Node[2];
        son[0] = left;
        son[0].unitaryCode = 0;
        son[1] = right;
        son[1].unitaryCode = 1;
        this.freq = right.freq + left.freq;
    }

    /**
     * 
     * @param n Filho a esquerda = 0; Filho a direita = 1
     * @return No' filho
     */
    public Node getSon(int n){
        return son[n];
    }

    public int getCode(){
        return code;
    }

    public int getUnitaryCode() {
        return unitaryCode;
    }

    public int getFreq() {
        return freq;
    }

    public int getSimbol() {
        return simbol;
    }

    public int getCodeSize() {
        return codeSize;
    }
            
    public boolean isLeaf(){
        return simbol >= 0;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    /**
     * Preenche as variaveis code e codePath.<p>
     * Este metodo deve ser usado apenas pela raiz da arvore
     */
    public void generateCodes(){
        son[0].generateCodes(0, 0);
        son[1].generateCodes(0, 0);        
    }

    private void generateCodes(int path, int size){
        code = path + unitaryCode;
        codeSize = size + 1;
        if(!isLeaf()){
            son[0].generateCodes(2*code, codeSize);
            son[1].generateCodes(2*code, codeSize);
        }
    }


    public int compareTo(Node another) {
        return (this.freq != another.freq)? freq - another.freq : simbol - another.simbol ;
    }

    

}
