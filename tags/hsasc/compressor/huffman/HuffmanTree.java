
package compressor.huffman;

import compressor.api.Main;
import compressor.io.BitInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;


/**
 *
 */
public class HuffmanTree {

    private LinkedList<Node> leafs;
    private HashMap<Integer, Node> simbols;
    public Node root;
    private long maxCounter;

    /**
     * Gera uma arvore de Huffman a partir das estatisticas de cada simbolo no arquivo a comprimir
     * @param pathName Caminho do arquivo a comprimir
     * @param simbolSize Tamanho em bits de cada simbolo
     */
    public HuffmanTree(String pathName, int simbolSize, int counterSize) {
        init(getOcorrencias(pathName, simbolSize), counterSize);
    }

    /**
     * Gera uma arvore de Huffman a partir das estatisticas de cada simbolo no arquivo a comprimir
     * @param ocorrencias Mapeamento dos simbolos com suas ocorrencias
     */
    public HuffmanTree(HashMap<Integer, Integer> ocorrencias, int counterSize) {
        init(ocorrencias, counterSize);
    }

    private void init(HashMap<Integer, Integer> ocorrencias, int counterSize){
        leafs = new LinkedList<Node>();
        simbols = new HashMap<Integer, Node>();
        for(int key : ocorrencias.keySet()){
            Node node = new Node(key, ocorrencias.get(key));
            leafs.add(node);
            simbols.put(key, node);
        }
        this.maxCounter = (long)Math.pow(2, counterSize);
        mountTree();
    }

    /**
     * Le bit a bit e percorre a arvore de Huffman com eles ate chegar em uma folha
     * @param in Stream de onde os bits serao lidos
     * @return Folha com o simbolo encotrado
     * @throws java.io.IOException
     */
    public Node nextSimbol(BitInputStream in) throws IOException{
        Node node = root;
        while(!node.isLeaf())
            node = node.getSon(in.nextBits(1));        
        return node;
    }

    /**
     *
     * @param simbol
     * @return O no' da arvore de huffman que contem o simbolo
     */
    public Node get(int simbol){
        return simbols.get(simbol);
    }

    /**
     *
     * @return lista de no's folhas da arvore de Huffman
     */
    public LinkedList<Node> getLeafs(){
        return leafs;
    }
    
    private void mountTree(){
        normalizeFreqs();
        if(Main.isInDebugMode())
            System.out.println("Formando arvore com "+leafs.size()+" folhas...");
        Collections.sort(leafs);
        PriorityQueue<Node> nodes = new PriorityQueue<Node>(leafs);        
        while(nodes.size() > 1)
            nodes.add(new Node(nodes.poll(), nodes.poll()));        
        root = nodes.poll();        
        root.generateCodes();
    }

    private HashMap<Integer, Integer> getOcorrencias(String fileName, int simbolSize) {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        try {
            BitInputStream in = new BitInputStream(fileName, false);
            if(Main.isInDebugMode())
                System.out.println("Contando ocorrencias dos simbolos no arquivo...");
            while(true){
                int simbol = in.nextBits(simbolSize);
                if(map.containsKey(simbol)){
                    map.put(simbol, map.get(simbol)+1);
                } else {
                    map.put(simbol, 1);
                }
            }
        } catch (IOException ex){ //EOF
            return map;
        }
    }

    private void normalizeFreqs() {
        int maior = 0;
        for (Node no : leafs) {
            if (no.getFreq() > maior) {
                maior = no.getFreq();
            }
        }
        if (maior > maxCounter) {
            for (Node no : leafs) {
                no.setFreq((int)((no.getFreq() * maxCounter) / maior));
                if (no.getFreq() == 0) {
                    no.setFreq(1);
                }
            }
        }
    }
}
