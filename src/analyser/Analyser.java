package analyser;

import error.*;
import symbol.Function;
import symbol.Symbol;
import symbol.Var;
import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.Tokenizer;
import util.MyType;

import java.util.ArrayList;
import java.util.List;

public class Analyser {
    Tokenizer tokenizer;
    Token peekedToken = null;

    public Analyser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    /**
     * 查看下一个 Token
     */
    private Token peek() throws TokenizeError {
        if (peekedToken == null) {
            peekedToken = tokenizer.nextToken();
        }
        return peekedToken;
    }

    /**
     * 获取下一个 Token
     */
    private Token next() throws TokenizeError {
        if (peekedToken != null) {
            Token token = peekedToken;
            peekedToken = null;
            return token;
        } else {
            return tokenizer.nextToken();
        }
    }

    /**
     * 如果下一个 token 的类型是 tt，则返回 true
     */
    private boolean check(TokenType tt) throws TokenizeError {
        Token token = peek();
        return token.getTokenType() == tt;
    }

    /**
     * 如果下一个 token 的类型是 tt，则前进一个 token 并返回，否则抛出异常
     */
    private Token expect(TokenType tt) throws CompileError {
        Token token = peek();
        if (token.getTokenType() == tt) {
            return next();
        } else {
            throw new ExpectedTokenError(tt, token);
        }
    }

    public void analyse() throws CompileError{
        analyseProgram();
    }

    // program -> (let_decl_stmt | const_decl_stmt)* function*
    public void analyseProgram() throws CompileError {
        while(check(TokenType.LET_KW) || check(TokenType.CONST_KW)){
            if (check(TokenType.LET_KW)) analyseLetDeclStmt();
            else analyseConstDeclStmt();
        }
        while (check(TokenType.FN_KW)){
            analyseFunction();
        }
    }

    //let_decl_stmt -> 'let' IDENT ':' ty ('=' expr)? ';'
    public void analyseLetDeclStmt() throws CompileError {
        expect(TokenType.LET_KW);
        expect(TokenType.IDENT);
        expect(TokenType.COLON);
        parseType();
        if(check(TokenType.ASSIGN)){
            next();
            analyseExpr();
        }
        expect(TokenType.SEMICOLON);
    }

    //const_decl_stmt -> 'const' IDENT ':' ty '=' expr ';'
    public void analyseConstDeclStmt() throws CompileError {
        expect(TokenType.CONST_KW);
        expect(TokenType.IDENT);
        expect(TokenType.COLON);
        parseType();
        expect(TokenType.ASSIGN);
        analyseExpr();
        expect(TokenType.SEMICOLON);
    }

    //ty -> IDENT 只能是void和int
    public Token parseType() throws CompileError {
        Token tt = peek();
        String str = (String) tt.getValue();
        if(str.equals("void") || str.equals("int") || str.equals("double")){
            next();
            return tt;
        }
        else throw new AnalyzeError(ErrorCode.InvalidType, peekedToken.getEndPos());
    }

    //function -> 'fn' IDENT '(' function_param_list? ')' '->' ty block_stmt
    public void analyseFunction() throws CompileError {
        expect(TokenType.FN_KW);
        expect(TokenType.IDENT);
        expect(TokenType.L_PAREN);
        if(check(TokenType.R_PAREN) == false){
            analyseFunctionParamList();
        }
        expect(TokenType.R_PAREN);
        expect(TokenType.ARROW);
        MyType returnType = MyType.toType(parseType());
        analyseBlockStmt();
    }

    //function_param_list -> function_param (',' function_param)*
    public List<Var> analyseFunctionParamList() throws CompileError {
        List<Var> paramList = new ArrayList<>();
        int i = 0;
        paramList.add(analyseFunctionParam(i));
        while (check(TokenType.COMMA)){
            next();
            i++;
            paramList.add(analyseFunctionParam(i));
        }
        return paramList;
    }

    //function_param -> 'const'? IDENT ':' ty
    public Var analyseFunctionParam(int paramNum) throws CompileError {
        boolean isConst = false;
        if(check(TokenType.CONST_KW)){
            isConst = true;
            next();
        }
        Token token = expect(TokenType.IDENT);
        expect(TokenType.COLON);
        MyType type = MyType.toType(parseType());
        return new Var();
    }

    //expr ->
    //      negate_expr
    //    | assign_expr
    //    | call_expr
    //    | literal_expr
    //    | ident_expr
    //    | group_expr
    //     (binary_operator expr||'as' ty)*
    public MyType analyseExpr() throws CompileError{
        MyType type = MyType.INT;
        boolean isVar = true;
        if(check(TokenType.MINUS)){
            type = analyseNegateExpr();
        }
        else if(check(TokenType.UINT_LITERAL) || check(TokenType.DOUBLE_LITERAL) || check(TokenType.STRING_LITERAL) || check(TokenType.CHAR_LITERAL)){
            type = analyseLiteralExpr();
        }
        else if(check(TokenType.L_PAREN)){ //括号表达式
            type = analyseGroupExpr();
        }
        else if(check(TokenType.IDENT)){
            Token token = next();
            Symbol symbol = new Symbol(); //todo
            Function function = null;
            boolean isLib = false;
            if(check(TokenType.L_PAREN)){
                //if(type != MyType.FUNCTION) throw new AnalyzeError(ErrorCode.CallNotExistFunc, token.getStartPos());
                type = analyseCallExpr(function,isLib);
            }
            else if (check(TokenType.ASSIGN)){
                type = analyseAssignExpr(symbol,type);
            }
            else analyseIdentExpr(symbol);
        }
        while (checkOperator() || check(TokenType.AS_KW)){
            if (check(TokenType.AS_KW))
                type = analyseAsExpr(type);
            else
                type = analyseBinaryOperatorExpr(type);
        }
        return type;
    }

    private boolean checkOperator() throws CompileError{
        if(check(TokenType.PLUS)||check(TokenType.MINUS)||check(TokenType.MUL)||check(TokenType.DIV)||check(TokenType.EQ)||check(TokenType.NEQ)||check(TokenType.LT)||check(TokenType.GT)||check(TokenType.LE)||check(TokenType.GE)){
            return true;
        }
        return false;
    }

    //operator_expr -> expr binary_operator expr
    //binary_operator -> '+' | '-' | '*' | '/' | '==' | '!=' | '<' | '>' | '<=' | '>='
    public MyType analyseBinaryOperatorExpr(MyType left) throws CompileError{
        if(checkOperator()){
            TokenType lt = next().getTokenType();
        }
        else throw new AnalyzeError(ErrorCode.OperatorError, peekedToken.getStartPos());
        return analyseExpr();
    }

    //negate_expr -> '-' expr
    public MyType analyseNegateExpr() throws CompileError {
        expect(TokenType.MINUS);
        MyType type = analyseExpr();
        if(type == MyType.VOID) throw new AnalyzeError(ErrorCode.VoidTypeError,peekedToken.getStartPos());
        return type;
    }

    //assign_expr -> l_expr '=' expr
    public MyType analyseAssignExpr(Symbol symbol,MyType left) throws CompileError{
        expect(TokenType.ASSIGN);
        MyType right = analyseExpr();
        return MyType.VOID;
    }

    //as_expr -> expr 'as' ty
    public MyType analyseAsExpr(MyType left) throws CompileError{
        expect(TokenType.AS_KW);
        MyType right = MyType.toType(parseType());
        if(left == MyType.INT && right == MyType.DOUBLE){
            return MyType.DOUBLE;
        }
        else if(left == MyType.DOUBLE && right == MyType.INT){
            return MyType.INT;
        }
        if(right == MyType.VOID)
            throw new AnalyzeError(ErrorCode.VoidTypeError, peekedToken.getStartPos());
        return right;
    }

    //call_param_list -> expr (',' expr)*
    public void parseCallParamList(Function function) throws CompileError {
        //List<Var> paramList = function.getParamList();
        //int pos = 0;
        MyType type = analyseExpr();
        //if(paramList.get(pos).getType() != type) throw new AnalyzeError(ErrorCode.ParamTypeError,peekedToken.getStartPos());
        //pos++;
        while(check(TokenType.COMMA)){
            next();
            type = analyseExpr();
            //if(paramList.get(pos).getType() != type) throw new AnalyzeError(ErrorCode.ParamTypeError,peekedToken.getStartPos());
            //pos++;
        }
        //if(pos != paramList.size())
            //throw new AnalyzeError(ErrorCode.ParamListNotMatch,peekedToken.getStartPos());
    }

    //call_expr -> IDENT '(' call_param_list? ')'
    public MyType analyseCallExpr(Function function,boolean isLib) throws CompileError {
        expect(TokenType.L_PAREN);
        if (check(TokenType.R_PAREN) == false) parseCallParamList(function);
        expect(TokenType.R_PAREN);
        return MyType.VOID;
        //return function.getReturnType();
    }

    //literal_expr -> UINT_LITERAL | DOUBLE_LITERAL | STRING_LITERAL | CHAR_LITERAL
    public MyType analyseLiteralExpr() throws TokenizeError, AnalyzeError {
        if(check(TokenType.UINT_LITERAL)){
            Token token = next();
            return MyType.INT;
        }
        else if(check(TokenType.CHAR_LITERAL)){
            char ch = (char) next().getValue();
            return MyType.INT;
        }
        else if(check(TokenType.DOUBLE_LITERAL)){
            Double db = (Double) next().getValue();
            return MyType.DOUBLE;
        }
        else if(check(TokenType.STRING_LITERAL)){
            String str = (String) next().getValue();
            return MyType.STRING;
        }
        else{
            throw new AnalyzeError(ErrorCode.LiteralTypeError, peekedToken.getStartPos());
        }
    }

    //ident_expr -> IDENT
    public void analyseIdentExpr(Symbol symbol){
    }

    //group_expr -> '(' expr ')'
    public MyType analyseGroupExpr() throws CompileError {
        expect(TokenType.L_PAREN);
        MyType exprType = analyseExpr();
        expect(TokenType.R_PAREN);
        return exprType;
    }

    //stmt ->
    //      expr_stmt
    //    | decl_stmt *
    //    | if_stmt *
    //    | while_stmt *
    //    | return_stmt *
    //    | block_stmt *
    //    | empty_stmt *
    public void analyseStmt() throws CompileError {
        if(check(TokenType.LET_KW)){
            analyseLetDeclStmt();
        }
        else if(check(TokenType.CONST_KW)){
            analyseConstDeclStmt();
        }
        else if(check(TokenType.IF_KW)){
            analyseIfStmt();
        }
        else if(check(TokenType.WHILE_KW)){
            analyseWhileStmt();
        }
        else if(check(TokenType.RETURN_KW)){
            analyseReturnStmt();
        }
        else if(check(TokenType.L_BRACE)){
            analyseBlockStmt();
        }
        else if(check(TokenType.SEMICOLON)){
            analyseEmptyStmt();
        }
        else analyseExprStmt();
    }

    //expr_stmt -> expr ';'
    public void analyseExprStmt() throws CompileError {
        analyseExpr();
        expect(TokenType.SEMICOLON);
    }

    //if_stmt -> 'if' expr block_stmt ('else' (block_stmt | if_stmt))?
    public void analyseIfStmt() throws CompileError{
        expect(TokenType.IF_KW);
        analyseExpr();
        analyseBlockStmt();
        if(check(TokenType.ELSE_KW)){
            expect(TokenType.ELSE_KW);
            if(check(TokenType.IF_KW)){}
            else if(check(TokenType.L_BRACE)){}
            else throw new AnalyzeError(ErrorCode.IncompleteElse, peekedToken.getStartPos());
        }
    }

    //while_stmt -> 'while' expr block_stmt
    public void analyseWhileStmt() throws CompileError {
        expect(TokenType.WHILE_KW);
        analyseExpr();
        analyseBlockStmt();
    }

    //return_stmt -> 'return' expr? ';'
    public void analyseReturnStmt() throws CompileError{
        expect(TokenType.RETURN_KW);
        if(check(TokenType.SEMICOLON) == false) analyseExpr();
        expect(TokenType.SEMICOLON);
    }

    //block_stmt -> '{' stmt* '}'
    public void analyseBlockStmt() throws CompileError{
        expect(TokenType.L_BRACE);
        while(check(TokenType.R_BRACE) == false) analyseStmt();
        expect(TokenType.R_BRACE);
    }

    public void analyseEmptyStmt() throws CompileError{
        expect(TokenType.SEMICOLON);
    }
}
