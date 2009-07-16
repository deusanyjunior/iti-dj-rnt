package compressor.encoder;

import compressor.api.Main;
import compressor.io.BitInputStream;
import compressor.io.BitOutputStream;
import compressor.huffman.HuffmanTree;
import compressor.huffman.Node;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 */
public class Encoder {

    private BitInputStream bis;
    private BitOutputStream bos;
    private HuffmanTree tree;
    private int simbolSize,  counterSize;

    /**
     *
     * @param input Arquivo comprimido
     * @param simbolSize Tamanho em bits de cada simbolo
     * @param simbolSize Tamanho em bits de cada contador no cabecalho
     * @throws java.io.IOException caso ocorra um erro com a leitura
     */
    public Encoder(String input, int simbolSize, int counterSize) throws IOException {
        tree = new HuffmanTree(input, simbolSize, counterSize);
        bis = new BitInputStream(input, false);
        this.simbolSize = simbolSize;
        this.counterSize = (counterSize > 32)? 32 : counterSize - counterSize%8;
    }

    /**
     * 
     * @param output Caminho de descompressao
     */
    public void compressTo(String output) {
        bos = new BitOutputStream(output);
        printHeader();
        int simbol;
        try {
            if(Main.isInDebugMode())
                System.out.println("Excrevendo simbolos codificados...");
            while (true) {
                simbol = bis.nextBits(simbolSize);
                Node aux = tree.get(simbol);
                bos.print(aux.getCode(), aux.getCodeSize());
            }
        } catch (IOException ex) {
            bos.close();
            System.out.println("O arquivo foi compactado com sucesso");
        }
    }

    private void printHeader() {

        if(Main.isInDebugMode())
            System.out.println("Escrevendo o cabecalho...");

        if (simbolSize == 8) {
            bos.print(0, 1);
        } else {
            bos.print(1, 1);
        }
        if (counterSize == 8) {
            bos.print(0, 2);
        } else if(counterSize == 16){
            bos.print(1, 2);
        } else if(counterSize == 24){
            bos.print(2, 2);
        } else {
            bos.print(3, 2);
        }
        LinkedList<Node> nodes = tree.getLeafs();
        bos.print(nodes.size()-1, simbolSize);
        for (Node n : nodes) {
            bos.print(n.getSimbol(), simbolSize);
            bos.print(n.getFreq()-1, counterSize);
        }
    }

    
}
