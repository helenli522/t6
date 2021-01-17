import analyser.Analyser;
import error.CompileError;
import tokenizer.StringIter;
import tokenizer.Token;
import tokenizer.Tokenizer;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws CompileError, IOException {
//        InputStream in = new FileInputStream(args[0]);
        InputStream in = new FileInputStream("F:\\jiao_now\\JavaProjects\\c0_tests\\3-double\\ac2-prime.input.txt");
        Scanner scanner = new Scanner(in);
        StringIter it = new StringIter(scanner);
        Tokenizer tokenizer = new Tokenizer(it);
        Token t = null;
//        do{
//            t = tokenizer.nextToken();
//            System.out.println(t+" "+t.getEndPos());
//        }while(t.getTokenType() != TokenType.EOF);
        Analyser analyser = new Analyser(tokenizer);
        analyser.analyse();
    }

}
