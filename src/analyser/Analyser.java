package analyser;

import error.*;
import instruction.Instruction;
import instruction.Operation;
import tokenizer.Token;
import tokenizer.TokenType;
import tokenizer.Tokenizer;
import util.*;

import java.util.ArrayList;
import java.util.List;

public class Analyser {
    Tokenizer tokenizer;
    Token peekedToken = null;
    public Maintainer maintainer = new Maintainer();
    public BCUtils bcUtils = new BCUtils();
    int wlevel = 0;

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
        maintainer.func_count = 1;
        List<Instruction> init = maintainer.instructions;
        while (check(TokenType.FN_KW)){
            maintainer.init_function_analyse();
            analyseFunction();
            maintainer.func_count += 1;
            maintainer.global_count += 1;
        }
        int m = maintainer.find_symbol_all("main");
        if(m == -1)
            throw new AnalyzeError(ErrorCode.NoMainError, peekedToken.getEndPos());
       maintainer.set_start(init,m);
    }

    public void checkDuplicateVar(String name, MyType type, boolean isConst) throws AnalyzeError {
        if(maintainer.find_symbol_scope(name) != -1) {
            throw new AnalyzeError(ErrorCode.DuplicateDecl, peekedToken.getEndPos());
        }
        else {
            maintainer.add_var(name, type, isConst);
        }
    }

    //let_decl_stmt -> 'let' IDENT ':' ty ('=' expr)? ';'
    public void analyseLetDeclStmt() throws CompileError {
        expect(TokenType.LET_KW);
        Token token = expect(TokenType.IDENT);
        String identStr = token.getValue().toString();
        expect(TokenType.COLON);
        MyType declType = MyType.toType(parseType());
        if(declType == MyType.VOID) throw new AnalyzeError(ErrorCode.VoidTypeError,peekedToken.getEndPos());

        //填表
        maintainer.check_global_let();
        checkDuplicateVar(identStr,declType,false);

        if(check(TokenType.ASSIGN)){
            next();
            maintainer.add_var_instruction();
            MyType exprType = analyseExpr();
            if(exprType != declType) //类型不匹配
                throw new AnalyzeError(ErrorCode.DeclAndExprTypeNotMatch,peekedToken.getEndPos());
            maintainer.pop_operator(exprType);
            maintainer.ins_store();
        }
        expect(TokenType.SEMICOLON);
        addCount();
    }

    //const_decl_stmt -> 'const' IDENT ':' ty '=' expr ';'
    public void analyseConstDeclStmt() throws CompileError {
        expect(TokenType.CONST_KW);
        Token token = expect(TokenType.IDENT);
        String identStr = token.getValue().toString();
        expect(TokenType.COLON);
        MyType type = MyType.toType(parseType());
        if(type == MyType.VOID) throw new AnalyzeError(ErrorCode.VoidTypeError,peekedToken.getEndPos());

        //填表
        maintainer.check_global_const();
        checkDuplicateVar(identStr,type,true);

        expect(TokenType.ASSIGN);
        MyType exprType = analyseExpr();
        if(exprType != type) //类型不匹配
            throw new AnalyzeError(ErrorCode.DeclAndExprTypeNotMatch,peekedToken.getEndPos());
        maintainer.pop_operator(exprType);
        expect(TokenType.SEMICOLON);
        maintainer.ins_store();
        addCount();
    }

    public void addCount(){
        if(maintainer.level == 1) maintainer.global_count++;
        else maintainer.local_count++;
    }

    //ty -> IDENT 只能是void和int
    public Token parseType() throws CompileError {
        Token peek = peek();
        String str = (String) peek.getValue();
        if(str.equals("void") || str.equals("int") || str.equals("double")){
            next();
            return peek;
        }
        else throw new AnalyzeError(ErrorCode.InvalidType, peekedToken.getEndPos());
    }

    public void checkDuplicateFunc(String name) throws AnalyzeError{
        if(maintainer.find_symbol_scope(name) != -1)
            throw new AnalyzeError(ErrorCode.DuplicateDecl,peekedToken.getEndPos());
    }

    //function -> 'fn' IDENT '(' function_param_list? ')' '->' ty block_stmt
    public void analyseFunction() throws CompileError {
        expect(TokenType.FN_KW);
        maintainer.reset_local_count();
        Token token = expect(TokenType.IDENT);
        String identStr = token.getValue().toString();
        checkDuplicateFunc(identStr);

        expect(TokenType.L_PAREN);
        List<Var> paramList = new ArrayList<>();
        if(check(TokenType.R_PAREN) == false){
            paramList = analyseFunctionParamList();
        }
        expect(TokenType.R_PAREN);
        expect(TokenType.ARROW);

        MyType returnType = MyType.toType(parseType());
        int returnSlot = 0;
        if(returnType == MyType.INT || returnType == MyType.DOUBLE) returnSlot = 1;

        maintainer.add_function_two_tables(identStr,paramList,returnType,returnSlot);
        analyseBlockStmt();
        maintainer.set_func_attr();

        if(returnType == MyType.VOID)
            maintainer.ins_ret();
        else if(maintainer.cur_is_ret() == false)
            throw new AnalyzeError(ErrorCode.FuncRetError, peekedToken.getStartPos());
        maintainer.add_function_global(identStr);
    }

    //function_param_list -> function_param (',' function_param)*
    public List<Var> analyseFunctionParamList() throws CompileError {
        List<Var> paramList = new ArrayList<>();
        paramList.add(analyseFunctionParam(0));
        int paramNum = 1;
        while (check(TokenType.COMMA)){
            next();
            paramList.add(analyseFunctionParam(paramNum));
            paramNum++;
        }
        return paramList;
    }

    //function_param -> 'const'? IDENT ':' ty
    public Var analyseFunctionParam(int paramNum) throws CompileError {
        boolean cp = false; //const param?
        if(check(TokenType.CONST_KW)){
            next();
            cp = true;
        }
        Token token = expect(TokenType.IDENT);
        String identStr = token.getValue().toString();
        expect(TokenType.COLON);
        MyType type = MyType.toType(parseType());
        return maintainer.add_param(identStr,type,cp,paramNum);
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
        MyType resType = MyType.INT;
        if(check(TokenType.MINUS)){
            resType = analyseNegateExpr();
        }
        else if(check(TokenType.UINT_LITERAL) || check(TokenType.DOUBLE_LITERAL) || check(TokenType.STRING_LITERAL) || check(TokenType.CHAR_LITERAL)){
            resType = analyseLiteralExpr();
        }
        else if(check(TokenType.L_PAREN)){
            resType = analyseGroupExpr();
        }
        else if(check(TokenType.IDENT)){ //call | assign | ident
            Token token = next();
            resType = parseIdent(token);
        }
        while (checkOperator() || check(TokenType.AS_KW)){
            if (check(TokenType.AS_KW))
                resType = analyseAsExpr(resType);
            else
                resType = analyseBinaryOperatorExpr(resType);
        }
        return resType;
    }

    //todo:check
    private MyType parseIdent(Token token) throws CompileError{
        String identStr = token.getValue().toString();
        MyType symbolType, res;
        boolean isLib = false;
        Function function = null;
        Var var = new Var();
        if(maintainer.is_lib_func(identStr) != null){
            //库函数
            symbolType = MyType.FUNCTION;
            isLib = true;
            function = maintainer.is_lib_func(identStr);
        }
        else if(maintainer.find_symbol_all(identStr) != -1){
            //函数或变量
            int i = maintainer.find_symbol_all(identStr);
            Symbol tmp = maintainer.get_symbol_at(i);
            symbolType = tmp.getType();
            if(tmp.isVar) var = (Var) tmp;
            else function = (Function) tmp;
        }
        else{
            throw new AnalyzeError(ErrorCode.UndefinedSymbol,token.getStartPos());
        }

        if(check(TokenType.L_PAREN)){
            if(symbolType == MyType.FUNCTION) res = analyseCallExpr(function,isLib);
            else throw new AnalyzeError(ErrorCode.CallNotExistFunc,token.getStartPos());
        }
        else if(check(TokenType.ASSIGN)){
            if(symbolType == MyType.FUNCTION || var.isConst)
                throw new AnalyzeError(ErrorCode.AssignError,token.getStartPos());
            res = analyseAssignExpr(var,symbolType);
        }
        else{
            analyseIdentExpr(var);
            res = symbolType;
        }
        return res;
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
        if(checkOperator() == false)
            throw new AnalyzeError(ErrorCode.OperatorError, peekedToken.getStartPos());
        else{
            TokenType lt = next().getTokenType();
            maintainer.stack_calculate(left,lt);
        }
        MyType right = analyseExpr();
        if(left != right)
            throw new AnalyzeError(ErrorCode.OperandTypeError, peekedToken.getStartPos());
        else return left;
    }

    //negate_expr -> '-' expr
    public MyType analyseNegateExpr() throws CompileError {
        expect(TokenType.MINUS);
        maintainer.push(TokenType.NEGATE);
        MyType exprType = analyseExpr();
        if(exprType == MyType.VOID) throw new AnalyzeError(ErrorCode.VoidTypeError,peekedToken.getStartPos());
        return exprType;
    }

    //assign_expr -> l_expr '=' expr
    public MyType analyseAssignExpr(Var var,MyType left) throws CompileError{
        expect(TokenType.ASSIGN);
        maintainer.add_symbol(var);
        MyType right = analyseExpr(), v = MyType.VOID;
        maintainer.pop_operator(right);
        maintainer.ins_store();
        if( (left != v) && (left == right) )
            return v;
        else throw new AnalyzeError(ErrorCode.AssignError,peekedToken.getStartPos());
    }

    //as_expr -> expr 'as' ty
    public MyType analyseAsExpr(MyType left) throws CompileError{
        expect(TokenType.AS_KW);
        MyType right = MyType.toType(parseType());
        if(left == MyType.INT && right == MyType.DOUBLE){
            maintainer.ins_itof();
            return MyType.DOUBLE;
        }
        else if(left == MyType.DOUBLE && right == MyType.INT){
            maintainer.ins_ftoi();
            return MyType.INT;
        }
        if(right != MyType.VOID)
            return right;
        else
            throw new AnalyzeError(ErrorCode.VoidTypeError, peekedToken.getStartPos());
    }

    //call_param_list -> expr (',' expr)*
    public void parseCallParamList(Function function) throws CompileError {
        MyType type = analyseExpr();
        Var param = function.paramList.get(0);
        if(type != param.type) {
            System.out.println("type:"+type+",param.type:"+param.type);
            throw new AnalyzeError(ErrorCode.ParamTypeError,peekedToken.getStartPos());
        }
        int i = 1;
        maintainer.peek_and_operate(type);
        while(check(TokenType.COMMA)){
            next();
            type = analyseExpr();
            param = function.paramList.get(i);
            if(type != param.type)
                throw new AnalyzeError(ErrorCode.ParamTypeError,peekedToken.getStartPos());
            i++;
            maintainer.peek_and_operate(type);
        }
        if(i != function.paramList.size())
            throw new AnalyzeError(ErrorCode.ParamListNotMatch,peekedToken.getStartPos());
    }

    //call_expr -> IDENT '(' call_param_list? ')'
    public MyType analyseCallExpr(Function function,boolean isLib) throws CompileError {
        Instruction callIns = maintainer.call_function(function,isLib);
        expect(TokenType.L_PAREN);
        maintainer.push(TokenType.L_PAREN);
        if (check(TokenType.R_PAREN) == false) parseCallParamList(function);
        expect(TokenType.R_PAREN);
        maintainer.pop();

        maintainer.add_instrction(callIns);
        return function.getReturnType();
    }

    //literal_expr -> UINT_LITERAL | DOUBLE_LITERAL | STRING_LITERAL | CHAR_LITERAL
    public MyType analyseLiteralExpr() throws TokenizeError, AnalyzeError {
        Token token;
        if(check(TokenType.UINT_LITERAL)){
            token = next();
            maintainer.ins_push(Long.parseLong(token.getValue().toString()));
            return MyType.INT;
        }
        else if(check(TokenType.CHAR_LITERAL)){
            char ch = (char) next().getValue();
            maintainer.ins_push((long) ch);
            return MyType.INT;
        }
        else if(check(TokenType.DOUBLE_LITERAL)){
            Double db = (Double) next().getValue();
            String binary = Long.toBinaryString(Double.doubleToRawLongBits(db));
            maintainer.ins_push(TypeUtils.strToLong(binary));
            return MyType.DOUBLE;
        }
        else if(check(TokenType.STRING_LITERAL)){
            String str = next().getValue().toString();
            maintainer.add_global_str(str);
            return MyType.STRING;
        }
        else{
            throw new AnalyzeError(ErrorCode.LiteralTypeError, peekedToken.getStartPos());
        }
    }

    //ident_expr -> IDENT
    public void analyseIdentExpr(Var symbol) throws CompileError{
        if(symbol.getType() == MyType.VOID)
            throw new AnalyzeError(ErrorCode.VoidTypeError,peekedToken.getStartPos());
        maintainer.add_symbol(symbol);
        maintainer.ins_load();
    }

    //group_expr -> '(' expr ')'
    public MyType analyseGroupExpr() throws CompileError {
        expect(TokenType.L_PAREN);
        maintainer.push(TokenType.L_PAREN);
        MyType exprType = analyseExpr();
        while(TokenType.L_PAREN != maintainer.peek()){
            TokenType tt = maintainer.pop();
            Instruction.operate(tt, exprType, maintainer);
        }
        maintainer.pop();
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
    //    | continue;
    //    | break;
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
        else if(check(TokenType.BREAK_KW)){
            analyseBreakStmt();
        }
        else if(check(TokenType.CONTINUE_KW)){
            analyseContinueStmt();
        }
        else analyseExprStmt();
    }

    //expr_stmt -> expr ';'
    public void analyseExprStmt() throws CompileError {
        MyType exprTy = analyseExpr();
        maintainer.pop_operator(exprTy);
        expect(TokenType.SEMICOLON);
    }

    //if_stmt -> 'if' expr block_stmt ('else' (block_stmt | if_stmt))?
    public void analyseIfStmt() throws CompileError{
        expect(TokenType.IF_KW);
        MyType type = analyseExpr();
        if(type == MyType.VOID)
            throw new AnalyzeError(ErrorCode.VoidTypeError,peekedToken.getStartPos());
        maintainer.pop_operator(type);
        maintainer.ins_brtrueOne();
        int to_else = IfUtils.un_cond_to_else(maintainer);

        int a[] = analyseBlockStmt(), tmp1, tmp2;
        if(Operation.RET == maintainer.get_ins_at(a[1]-1).getOperation()){
            maintainer.set_operand(to_else,a[1] - a[0]);
            if(check(TokenType.ELSE_KW)){
                expect(TokenType.ELSE_KW);
                if(check(TokenType.IF_KW))
                    analyseIfStmt();
                else if(check(TokenType.L_BRACE))
                    analyseBlockStmt();
                else throw new AnalyzeError(ErrorCode.IncompleteElse,peekedToken.getStartPos());
            }
        }
        else{
            //跳过else的指令，记录编号ins_to_end
            int to_end = IfUtils.to_end(maintainer);
            tmp1 = maintainer.get_instructions_size();
            maintainer.set_operand(to_else,tmp1 - a[0]);
            if(check(TokenType.ELSE_KW)){
                expect(TokenType.ELSE_KW);
                if(check(TokenType.IF_KW))
                    analyseIfStmt();
                else if(check(TokenType.L_BRACE)){
                    analyseBlockStmt();
                    maintainer.ins_brZero();
                }
                else throw new AnalyzeError(ErrorCode.IncompleteElse,peekedToken.getStartPos());
            }
            maintainer.set_operand(to_end,maintainer.get_instructions_size() - tmp1);
        }
    }

    private void analyseBreakStmt()throws CompileError{
        expect(TokenType.BREAK_KW);
        if(wlevel==0)
            throw new AnalyzeError(ErrorCode.InvalidBC,peekedToken.getStartPos());
        bcUtils.addBreak(wlevel,maintainer);
        expect(TokenType.SEMICOLON);
    }

    private void analyseContinueStmt()throws CompileError{
        expect(TokenType.CONTINUE_KW);
        if(wlevel==0)
            throw new AnalyzeError(ErrorCode.InvalidBC,peekedToken.getStartPos());
        bcUtils.addContinue(wlevel,maintainer);
        expect(TokenType.SEMICOLON);
    }

    //while_stmt -> 'while' expr block_stmt
    public void analyseWhileStmt() throws CompileError {
        expect(TokenType.WHILE_KW);
        int entry, jmp, exit;
        maintainer.add_instrction(new Instruction(Operation.BR,0));
        entry = maintainer.get_instructions_size();
        MyType cond = analyseExpr();
        if(cond == MyType.VOID) throw new AnalyzeError(ErrorCode.VoidTypeError,peekedToken.getStartPos());
        maintainer.pop_operator(cond);
        maintainer.ins_brtrueOne();
        Instruction to_exit = new Instruction(Operation.BR,0);
        maintainer.add_instrction(to_exit);
        jmp = maintainer.get_instructions_size();

        wlevel++;
        analyseBlockStmt();
        wlevel--;
        Instruction to_entry = new Instruction(Operation.BR,0);
        maintainer.add_instrction(to_entry);
        exit = maintainer.get_instructions_size();// 出口
        to_entry.setOperandA(entry - exit);
        to_exit.setOperandA(exit - jmp);
        bcUtils.handleBC(exit,wlevel);
    }

    //return_stmt -> 'return' expr? ';'
    public void analyseReturnStmt() throws CompileError{
        expect(TokenType.RETURN_KW);
        if(maintainer.cur_return_void()){
            expect(TokenType.SEMICOLON);
            maintainer.func_return();
        }
        else{
            MyType ret = null;
            maintainer.ins_arga(0);
            if(!check(TokenType.SEMICOLON)){
                ret = analyseExpr();
                maintainer.pop_operator(ret);
            }
            maintainer.ins_store();
            if(ret != maintainer.cur_return_type())
                throw new AnalyzeError(ErrorCode.ReturnTypeError,peekedToken.getStartPos());
            expect(TokenType.SEMICOLON);
            maintainer.func_return();
        }
    }

    //block_stmt -> '{' stmt* '}'
    public int[] analyseBlockStmt() throws CompileError{
        int[] a = new int[2];
        a[0] = maintainer.get_instructions_size();
        maintainer.level += 1;
        expect(TokenType.L_BRACE);
        while(check(TokenType.R_BRACE) == false) analyseStmt();
        expect(TokenType.R_BRACE);
        maintainer.pop_symbols(); //delete symbols in this level
        maintainer.level -= 1;
        a[1] = maintainer.get_instructions_size();
        return a;
    }

    public void analyseEmptyStmt() throws CompileError{
        expect(TokenType.SEMICOLON);
    }
}
