package compressor.decoder;

import compressor.api.Main;
import compressor.io.BitInputStream;
import compressor.io.BitOutputStream;
import compressor.huffman.HuffmanTree;
import compressor.huffman.Node;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 */
public class Decoder {

    private BitInputStream bis;
    private BitOutputStream bos;
    private HuffmanTree tree;
    private int simbolSize,  counterSize;

    /**
     * 
     * @param input arquivo comprimido
     * @param output caminho de descompressao
     * @throws java.io.IOException caso ocorra um erro com a leitura
     */
    public Decoder(String input) throws IOException {
        bis = new BitInputStream(input, true);
    }

    /**
     * 
     */
    public void decompressTo(String output) {
        bos = new BitOutputStream(output);
        tree = new HuffmanTree(readHeader(), counterSize);
        try{
            if(Main.isInDebugMode())
                System.out.println("Escrevendo simbolos decodificados...");
            while(true){
                Node no = tree.nextSimbol(bis);
                bos.print(no.getSimbol(), simbolSize);
            }
        }catch(IOException ex){
            System.out.println("O arquivo foi descompactado com sucesso");
        }
    }

    /**
     * 
     * @return Mapeamento de simbolo para ocorrencias no texto limpo
     */
    private HashMap<Integer, Integer> readHeader() {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        try {
            if(Main.isInDebugMode())
                System.out.println("Lendo o cabecalho...");
            simbolSize = (1 + bis.nextBits(1)) * 8;
            counterSize = (1 + bis.nextBits(2)) * 8;
            int num = bis.nextBits(simbolSize) + 1;
            for(int i = 0; i < num; i++){
                int a = bis.nextBits(simbolSize);
                int b = bis.nextBits(counterSize)+1;
                map.put(a, b);
            }
        } catch (IOException ex) {}
        return map;
    }

}
