package tokenizer;

import error.ErrorCode;
import error.TokenizeError;
import util.MyType;

public class Tokenizer {

    private StringIter it;

    public Tokenizer(StringIter it) {
        this.it = it;
    }

    private void skipSpaceCharacters() {
        while (!it.isEOF() && Character.isWhitespace(it.peekChar())) {
            it.nextChar();
        }
    }

    public Token lexUIntOrDouble(){
        MyType type = MyType.INT;
        String str = "";
        int size = 5535;
        while(Character.isDigit(it.peekChar()) || it.peekChar()=='.'){
            if (it.peekChar()=='.') type = MyType.DOUBLE;
            str += it.nextChar();
            size--;
            if(size <= 0) break;
        }
        if(type == MyType.DOUBLE){
            char peeked = it.peekChar();
            if(Character.toLowerCase(peeked) == 'e'){
                str += it.nextChar();
                peeked = it.peekChar();
                if(peeked=='+' || peeked=='-') str += it.nextChar();
                while(Character.isDigit(it.peekChar())){
                    str += it.nextChar();
                }
            }
        }
        if(type == MyType.INT)
            return new Token(TokenType.UINT_LITERAL, Long.parseLong(str), it.previousPos(), it.currentPos());
        else
            return new Token(TokenType.DOUBLE_LITERAL,Double.valueOf(str),it.previousPos(),it.currentPos());
    }

    public Token lexIdentOrKeyword() throws TokenizeError{
        String str = "";
        while(it.peekChar()=='_' || Character.isLetterOrDigit(it.peekChar())){
            str += it.nextChar();
        }
        if(str.equals("fn")) return new Token(TokenType.FN_KW , "fn", it.previousPos(), it.currentPos());
        else if(str.equals("let")) return new Token(TokenType.LET_KW , "let", it.previousPos(), it.currentPos());
        else if(str.equals("const")) return new Token(TokenType.CONST_KW, "const", it.previousPos(), it.currentPos());
        else if(str.equals("as")) return new Token(TokenType.AS_KW , "as", it.previousPos(), it.currentPos());
        else if(str.equals("while")) return new Token(TokenType.WHILE_KW , "while", it.previousPos(), it.currentPos());
        else if(str.equals("if")) return new Token(TokenType.IF_KW , "if", it.previousPos(), it.currentPos());
        else if(str.equals("else")) return new Token(TokenType.ELSE_KW , "else", it.previousPos(), it.currentPos());
        else if(str.equals("return"))  return new Token(TokenType.RETURN_KW,"return", it.previousPos(), it.currentPos());
        else if(str.equals("break"))  return new Token(TokenType.BREAK_KW,"break", it.previousPos(), it.currentPos());
        else if(str.equals("continue"))  return new Token(TokenType.CONTINUE_KW,"continue", it.previousPos(), it.currentPos());
        else return new Token(TokenType.IDENT, str, it.previousPos(), it.currentPos());
    }

    private Token lexIdent() throws TokenizeError {
        String str = "";
        while(it.peekChar() == '_' || Character.isLetterOrDigit(it.peekChar())){
            str += it.nextChar();
        }
        return new Token(TokenType.IDENT, str, it.previousPos(), it.currentPos());
    }

    private Token lexString() throws TokenizeError{
        String str = "";
        it.nextChar();
        int size = 6553;
        while(size > 0){
            char nxt = it.nextChar();
            size--;
            if(nxt == '\\'){
                if(it.peekChar() == 'n'){
                    it.nextChar();
                    str += '\n';
                }
                else if(it.peekChar() == '\''){
                    it.nextChar();
                    str += '\'';
                }
                else if(it.peekChar() == '\\'){
                    str += '\\';
                    it.nextChar();
                }
                else if(it.peekChar() == '"'){
                    it.nextChar();
                    str += '"';
                }
                else if(it.peekChar() == 't'){
                    it.nextChar();
                    str += '\t';
                }
                else if(it.peekChar() == 'r'){
                    it.nextChar();
                    str += '\r';
                }
            }
            else if(nxt == '"') break;
            else str += nxt;
        }
        return new Token(TokenType.STRING_LITERAL, str, it.previousPos(), it.currentPos());
    }

    //char_regular_char -> [^'\\]
    //CHAR_LITERAL -> '\'' (char_regular_char | escape_sequence) '\''
    private Token lexChar() throws TokenizeError{
        it.nextChar();
        char c = it.nextChar();
        if(c == '\\'){ //是转义字符
            if(it.peekChar() == 'n')
                c = '\n';
            else if(it.peekChar() == '\'')
                c = '\'';
            else if(it.peekChar() == '\\')
                c = '\\';
            else if(it.peekChar() == '"')
                c = '\"';
            else if(it.peekChar() == 't')
                c = '\t';
            else if(it.peekChar() == 'r')
                c = '\r';
            it.nextChar();
        }
        it.nextChar();
        return new Token(TokenType.CHAR_LITERAL, c, it.previousPos(), it.currentPos());
    }

    private Token lexOperatorOrUnknown() throws TokenizeError {
        switch (it.nextChar()) {
            //无歧义的运算符
            case '+':
                return new Token(TokenType.PLUS, '+', it.previousPos(), it.currentPos());
            case '*':
                return new Token(TokenType.MUL, '*', it.previousPos(), it.currentPos());
            case '(':
                return new Token(TokenType.L_PAREN, '(', it.previousPos(), it.currentPos());
            case ')':
                return new Token(TokenType.R_PAREN, ')', it.previousPos(), it.currentPos());
            case '{':
                return new Token(TokenType.L_BRACE, '{', it.previousPos(), it.currentPos());
            case '}':
                return new Token(TokenType.R_BRACE, '}', it.previousPos(), it.currentPos());
            case ',':
                return new Token(TokenType.COMMA, ',', it.previousPos(), it.currentPos());
            case ':':
                return new Token(TokenType.COLON, ':', it.previousPos(), it.currentPos());
            case ';':
                return new Token(TokenType.SEMICOLON, ';', it.previousPos(), it.currentPos());
            case '_':
                return new Token(TokenType.UNDERLINE, '_', it.previousPos(), it.currentPos());
            case '\\':
                return new Token(TokenType.FANXIEXIAN, '\\', it.previousPos(), it.currentPos());
            //区别 MINUS 和 ARROW
            case '-':
                if(it.peekChar() == '>'){
                    it.nextChar();
                    return new Token(TokenType.ARROW, "->", it.previousPos(), it.currentPos());
                }
                else{
                    return new Token(TokenType.MINUS, '-', it.previousPos(), it.currentPos());
                }
                //区别 EQ 和 ASSIGN
            case '=':
                if(it.peekChar() == '='){
                    it.nextChar();
                    return new Token(TokenType.EQ, "==", it.previousPos(), it.currentPos());
                }
                else{
                    return new Token(TokenType.ASSIGN, '=', it.previousPos(), it.currentPos());
                }
                //判断 NEQ
            case '!':
                if(it.peekChar() == '='){
                    it.nextChar();
                    return new Token(TokenType.NEQ, "!=", it.previousPos(), it.currentPos());
                }
                else{
                    throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
                }
                //判断 LT 和 LE
            case '<':
                if(it.peekChar() == '='){
                    it.nextChar();
                    return new Token(TokenType.LE, "<=", it.previousPos(), it.currentPos());
                }
                else{
                    return new Token(TokenType.LT, '<', it.previousPos(), it.currentPos());
                }
                //判断 GT 和 GE
            case '>':
                if(it.peekChar() == '='){
                    it.nextChar();
                    return new Token(TokenType.GE, ">=", it.previousPos(), it.currentPos());
                }
                else{
                    return new Token(TokenType.GT, '>', it.previousPos(), it.currentPos());
                }
            case '/':
                if(it.peekChar() == '/'){
                    it.nextChar();
                    lexComment();
                    return nextToken();
                }
                return new Token(TokenType.DIV, '/', it.previousPos(), it.currentPos());
            default:
                throw new TokenizeError(ErrorCode.InvalidInput, it.previousPos());
        }
    }

    private Token lexComment() throws TokenizeError{
        boolean end = true;
        while(end){
            char cur = it.nextChar();
            if(cur == '\n') end = false;
        }
        return new Token(TokenType.COMMENT, '/', it.previousPos(), it.currentPos());
    }

    public Token nextToken() throws TokenizeError{
        it.readAll();
        skipSpaceCharacters();
        if(it.isEOF()){
            return new Token(TokenType.EOF, "", it.currentPos(), it.currentPos());
        }
        //数字：int,double 字母:标识符，关键字 下划线：标识符 双引号：string
        //单引号：char _和\和|：string
        char peeked = it.peekChar();
        if(Character.isDigit(peeked))
            return lexUIntOrDouble();
        else if(peeked == '_')
            return lexIdent();
        else if(Character.isAlphabetic(peeked))
            return lexIdentOrKeyword();
        else if(peeked == '"' || peeked == '\\' || peeked == '|')
            return lexString();
        else if (peeked == '\'')
            return lexChar();
        else
            return lexOperatorOrUnknown();
    }

}
