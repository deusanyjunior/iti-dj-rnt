package compressor.api;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



import compressor.decoder.Decoder;
import compressor.encoder.Encoder;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Rennan
 */
public class Main {

    private static boolean inDebugMode;

    public static boolean isInDebugMode() {
        return inDebugMode;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        long time = System.currentTimeMillis();
        args = readArgs(args);
        
        int ss = Integer.parseInt(args[2]);
        int cs = Integer.parseInt(args[3]);
        if(args[0].endsWith(".cmp")){
            Decoder decoder = new Decoder(args[0]);
            decoder.decompressTo(args[1]);
        } else {
            Encoder encoder = new Encoder(args[0], ss, cs);
            encoder.compressTo(args[1]);
        }
        if(isInDebugMode())
            System.out.println("Tempo gasto: "+ (System.currentTimeMillis()-time) + "ns");
    }


    public static String[] readArgs(String args[]){
        String[] Args = {"", "", "8", "16"};
        inDebugMode = false;
        for(int i = 0; i < args.length; i++){
            String arg = args[i].toLowerCase();
            if(arg.startsWith("-ss=") || arg.startsWith("-simbolsize=")){
                Args[2] = args[i].substring(arg.indexOf('=')+1);
            } else if(arg.startsWith("-cs=") || arg.startsWith("-countersize=")) {
                Args[3] = args[i].substring(arg.indexOf('=')+1);
            } else if(arg.startsWith("-o=") || arg.startsWith("-output=")){
                Args[1] = args[i].substring(arg.indexOf('=')+1);
            } else if(arg.startsWith("-h") || arg.startsWith("-help")){
                printHelp();
                System.exit(0);
            } else if(arg.startsWith("-d") || arg.startsWith("-debug")){
                inDebugMode = true;
            } else {
                Args[0] = args[i];
            }
        }
        if(Args[0].length() == 0){
            System.out.println("Faltando arquivo a comprimir. Tente -h para verificar a sintaxe");
            System.exit(0);
        } else if(Args[1].length() == 0){
            if(Args[0].contains(".cmp")){
                Args[1]= Args[0].substring(0, Args[0].lastIndexOf(".cmp"));
            } else {
                Args[1]= Args[0] + ".cmp";
            }
        } else if(!Args[0].contains(".cmp") && !Args[1].endsWith(".cmp")){
            Args[1]= Args[1] + ".cmp";
        }
        return Args;
    }

    public static void printHelp(){
        System.out.println();
        System.out.println("*******************************************");
        System.out.println("* Compressor HSASC, v1.1                  *");
        System.out.println("* Desenvolvido por:                       *");
        System.out.println("*    Antonio Deusany de Carvalho Junior   *");
        System.out.println("*    Rennan Nunes Toscano                 *");
        System.out.println("*******************************************");
        System.out.println();
        System.out.println("Sintaxe para compactar:");
        System.out.println("    java -jar comp.jar ArquivoAComprimir [opcoes]");
        System.out.println();
        System.out.println("Sintaxe de descompactar:");
        System.out.println("    java -jar comp.jar ArquivoComprimido.cmp [opcoes]");
        System.out.println();
        System.out.println("opcoes mutuas:");
        System.out.println("    -h | -help : Ajuda");
        System.out.println("    -nd | -nodebug : Não exibe o progresso da compressao");
        System.out.println("    -o=ArquivoComprimido | -output=ArquivoComprimido");
        System.out.println();
        System.out.println("opcoes apenas do compactador:");
        System.out.println("    -ss=NumeroInteiro | -simbolsize=NumeroInteiro (Default = 8)");
        System.out.println("    -cs=NumeroInteiro | -countersize=NumeroInteiro (Default = 16)");
        System.out.println();
        System.out.println("Obs.: O arquivo sera descompactado se, e somente se, tiver extensao *.cmp\n");
    }

}










