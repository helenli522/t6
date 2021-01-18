import analyser.*;
import error.CompileError;
import instruction.Generator;
import tokenizer.StringIter;
import tokenizer.Token;
import tokenizer.Tokenizer;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws CompileError, IOException {
        File file = new File(args[0]);
//        File file = new File("F:\\jiao_now\\JavaProjects\\c0_tests\\6-break-continue\\ac2-guess-v3.input.txt");
        Scanner scanner = new Scanner(file);
        StringIter it = new StringIter(scanner);
        Tokenizer tokenizer = new Tokenizer(it);
        Token t = null;
//        do{
//            t = tokenizer.nextToken();
//            System.out.println(t+" "+t.getEndPos());
//        }while(t.getTokenType() != TokenType.EOF);
        Analyser analyser = new Analyser(tokenizer);
        Maintainer maintainer = analyser.maintainer;
        analyser.analyse();

        Generator generator = new Generator();
        List<Byte> bytes = generator.generate(maintainer.global_table, maintainer.function_table);
        int len = bytes.size();
        byte[] out = new byte[len];
        for(int i = 0; i < len; i++){
            out[i] = bytes.get(i);
            //debug code
            int bi = out[i];
            System.out.print(bi+" ");
            if((i+1)%4 == 0) System.out.println();
        }
        DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(new File(args[1])));
        outputStream.write(out);
    }

}
