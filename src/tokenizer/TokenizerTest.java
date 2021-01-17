package tokenizer;

import error.CompileError;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class TokenizerTest {
    public static void main(String[] args) throws CompileError,FileNotFoundException{
        InputStream in = new FileInputStream("F:\\jiao_now\\JavaProjects\\c0_tests\\3-double\\ac2-prime.input.txt");
        Scanner scanner = new Scanner(in);
        StringIter it = new StringIter(scanner);
        Tokenizer tokenizer = new Tokenizer(it);
        Token t = null;
        do{
            t = tokenizer.nextToken();
            System.out.println(t+" "+t.getEndPos());
        }while(t.getTokenType() != TokenType.EOF);
    }
}