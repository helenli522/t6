package analyser;

import instruction.Inser;
import instruction.Instruction;
import instruction.Operation;
import tokenizer.TokenType;
import util.MyType;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Maintainer {
    int level = 1;
    int global_count = 0;
    int func_count = 0;
    int local_count = 0;

    Function cur_func = new Function();
    Function ret_func;

    public List<Symbol> symbol_table = new ArrayList<>();
    public List<GVar> global_table = new ArrayList<>();
    public List<FVar> function_table = new ArrayList<>();
    public List<Instruction> instructions = new ArrayList<>();
    // 栈，用于计算
    Stack<TokenType> stack = new Stack<>();
    //0:equal
    //1:left>right
    //2:left<right
    //3:cannot compare
    int[][] priority = {
            {1,1,2,2,1,1,1,1,1,1,2,1,2},
            {1,1,2,2,1,1,1,1,1,1,2,1,2},
            {1,1,1,1,1,1,1,1,1,1,2,1,2},
            {1,1,1,1,1,1,1,1,1,1,2,1,2},
            {2,2,2,2,1,1,1,1,1,1,2,1,2},
            {2,2,2,2,1,1,1,1,1,1,2,1,2},
            {2,2,2,2,1,1,1,1,1,1,2,1,2},
            {2,2,2,2,1,1,1,1,1,1,2,1,2},
            {2,2,2,2,1,1,1,1,1,1,2,1,2},
            {2,2,2,2,1,1,1,1,1,1,2,1,2},
            {2,2,2,2,2,2,2,2,2,2,2,0,2},
            {2,2,2,2,2,2,2,2,2,2,3,2,2},
            {1,1,1,1,1,1,1,1,1,1,1,1,1},
    };

    // 在该层符号表查找
    public int find_symbol_scope(String str){
        for(int i = symbol_table.size() - 1; i >= 0; i--){
            Symbol symbol = symbol_table.get(i);
            if(str.equals(symbol.getName()) && level == symbol.getLevel())
                return i;
        }
        return -1;
    }

    // 在整个符号表查找
    public int find_symbol_all(String str){
        for(int i = symbol_table.size() - 1; i >= 0; i--){
            Symbol symbol = symbol_table.get(i);
            if(str.equals(symbol.getName()))
                return i;
        }
        return -1;
    }

    // 是否是标准库函数
    public Function is_lib_func(String name){
        List<Var> param_list = new ArrayList<>();
        MyType ret_type = MyType.VOID;
        if(name.equals("getdouble"))
            ret_type = MyType.DOUBLE;
        else if(name.equals("getint") || name.equals("getchar"))
            ret_type = MyType.INT;
        else if(name.equals("putln"))
            ret_type = MyType.VOID;
        else if(name.equals("putint")){
            param_list.add(new Var("para1", MyType.INT, level+1, true, false, -1, -1, -1, 0));
        }
        else if(name.equals("putdouble")){
            param_list.add(new Var("para1", MyType.DOUBLE, level+1, true, false, -1, -1, -1, 0));
        }
        else if(name.equals("putchar")){
            param_list.add(new Var("para1", MyType.INT, level+1, true, false, -1, -1, -1, 0));
        }
        else if(name.equals("putstr")){
            param_list.add(new Var("para1", MyType.STRING, level+1, true, false, -1, -1, -1, 0));
        }
        else return null;

        Function lib_func = new Function(name, MyType.FUNCTION, level, false, param_list, ret_type);
        return lib_func;
    }

    // 退出一层，删除这层的符号
    public void pop_symbols(){
        for(int i = symbol_table.size()-1; i >= 0; i--){
            Symbol cur = symbol_table.get(i);
            if(cur.getLevel() == level)
                symbol_table.remove(i);
        }
    }

    // 将符号弹出符号栈
    public void pop_operator(MyType operand){
        while(!stack.empty()){
            Instruction.operate(stack.pop(), operand, this);
        }
    }

    // 查询函数编号
    public int get_func_num_by_name(String name){
        for(int i = function_table.size() - 1; i >= 0; i--){
            FVar FVar = function_table.get(i);
            if(name.equals(FVar.fName))
                return FVar.fNum;
        }
        return -1;
    }

    //每次分析一个新的函数前初始化
    public void init_function_analyse(){
        instructions = new ArrayList<>();
        reset_local_count();
    }

    // 指令列表中添加指令
    public void add_instrction(Instruction instruction){
        instructions.add(instruction);
    }

    // 全局或局部变量指令
    public void add_var_instruction(){
        if(level == 1) instructions.add(Inser.globa(global_count));
        else instructions.add(Inser.loca(local_count));
    }

    // 返回当前指令在指令集中的位置
    public int get_instructions_size(){
        return instructions.size();
    }

    // 获取某个位置的指令
    public Instruction get_ins_at(int index){
        return instructions.get(index);
    }

    // 获取最后一条指令
    public int get_last_pos(){
        return instructions.size()-1;
    }

    // 设置指令的操作数
    public void set_operand(int index,int a){
        instructions.get(index).setOperandA(a);
    }

    //添加符号（参数 / 局部变量 / 全局变量）
    public void add_symbol(Var var){
        if(var.paramNum != -1 && var.fNum != -1){
            FVar FVar = function_table.get(var.fNum-1);
            instructions.add(new Instruction(Operation.ARGA, FVar.returnSlots+var.paramNum));
        }
        else if(var.level != 1){
            instructions.add(new Instruction(Operation.LOCA,var.localNum));
        }
        else{
            instructions.add(new Instruction(Operation.GLOBA,var.globalNum));
        }
    }

    //添加字符串全局变量
    public void add_global_str(String str){
        GVar gVar = new GVar(global_count,true,str.length(),str);
        global_table.add(gVar);
        Instruction instruction = new Instruction(Operation.PUSH,global_count);
        instructions.add(instruction);
        global_count += 1;
    }

    //添加全局或局部的常量
    public void check_global_const(){
        if(level == 1){
            global_table.add(new GVar(global_count,true));
            Instruction ins = new Instruction(Operation.GLOBA,global_count);
            instructions.add(ins);
        }
        else{
            Instruction ins = new Instruction(Operation.LOCA,local_count);
            instructions.add(ins);
        }
    }

    public void check_global_let(){
        if(level == 1) global_table.add(new GVar(global_count,false));
    }

    //添加变量或常量到symbol_table
    public void add_var(String name,MyType type,boolean isConst){
        symbol_table.add(new Var(name,type,level,true,isConst,local_count,global_count,-1,-1));
    }

    //加进符号表 & 函数表
    public void add_function_two_tables(String name,List<Var> paramList,MyType returnType,int returnSlot){
        Function function = new Function(name,MyType.FUNCTION,level,false,paramList,returnType);
        cur_func = function;
        symbol_table.add(function);
        FVar FVar = new FVar(func_count,name,global_count,returnSlot,paramList.size(), 0,null);
        function_table.add(FVar);
    }

    //加进全局表
    public void add_function_global(String name){
        GVar gVar = new GVar(global_count,true,name.length(),name);
        global_table.add(gVar);
    }

    public Function get_func_symbol_at(int index){
        if(symbol_table.get(index).isVar == false)
            return (Function) symbol_table.get(index);
        return null;
    }

    //在符号表中找
    public Symbol get_symbol_at(int index) {
        return symbol_table.get(index);
    }

    //全局符号表大小
    public int get_global_size(){
        return global_table.size();
    }

    //添加函数参数
    public Var add_param(String name,MyType type,boolean is_const,int param_num){
        int param_level = level + 1;
        Var param = new Var(name,type,param_level,true,is_const,-1,-1,func_count,param_num);
        symbol_table.add(param);
        return param;
    }

    //函数属性
    public void set_func_attr(){
        int i = function_table.size()-1;
        function_table.get(i).locSlots = local_count;
        function_table.get(i).body = instructions;
        function_table.get(i).globalPos = global_count;
    }

    //清零局部变量个数
    public void reset_local_count(){
        local_count = 0;
    }

    //stack的push
    public void push(TokenType type){
        stack.push(type);
    }

    //stack的peek
    public TokenType peek(){
        return stack.peek();
    }

    //弹栈没有运算的符号
    public void peek_and_operate(MyType operand){
        while( (!stack.empty()) && (stack.peek() != TokenType.L_PAREN) ){
            TokenType tt = stack.pop();
            Instruction.operate(tt,operand,this);
        }
    }

    //stack的pop
    public TokenType pop(){
        return stack.pop();
    }

    //stack计算
    public void stack_calculate(MyType operand,TokenType lt){
        int l,r;
        TokenType rt;
        l = TokenType.toInt(lt);
        while(!stack.empty()){
            rt = stack.peek();
            r = TokenType.toInt(rt);
            if(priority[r][l] == 1){
                rt = stack.pop();
                Instruction.operate(rt,operand,this);
            }
            else break;
        }
        stack.push(lt);
    }

    //main函数返回值分配和调用操作
    public List<Instruction> alloc_and_call_main(List<Instruction> init,Function main,int main_num){
        if(main.returnType == MyType.VOID){
            init.add(Inser.stackallocZero);
            init.add(Inser.call(main_num));
        }
        else{
            init.add(Inser.stackallocOne);
            init.add(Inser.call(main_num));
            init.add(Inser.popn);
        }
        return init;
    }

    //设置程序入口
    public void set_start(List<Instruction> init,int main_pos){
        Function main = get_func_symbol_at(main_pos);
        int main_num = get_func_num_by_name("main");
        init = alloc_and_call_main(init, main, main_num);
        add_start(init);
    }

    //添加start函数
    public void add_start(List<Instruction> init){
        global_table.add(new GVar(global_count,true,6,"_start"));
        function_table.add(0, new FVar(0,"_start",global_count,0,0,0,init));
        global_count += 1;
    }

    public Instruction call_function(Function function,boolean isLib){
        Instruction ins;
        if(isLib){
            add_function_global(function.name);
            ins = Inser.callname(global_count);
            global_count += 1;
        }
        else{
            int fNum = get_func_num_by_name(function.name);
            if (fNum == -1) fNum = func_count;
            ins = Inser.call(fNum);
        }
        if (function.returnType == MyType.VOID)
            add_instrction(Inser.stackallocZero);
        else
            add_instrction(Inser.stackallocOne);
        return ins;
    }

    public boolean cur_is_ret(){
        return ret_func.getName().equals(cur_func.getName());
    }

    //判断当前函数有无返回值
    public boolean cur_return_void(){
        if(cur_func.getReturnType() == MyType.VOID) return true;
        return false;
    }

    //当前函数返回类型
    public MyType cur_return_type(){
        return cur_func.getReturnType();
    }

    //函数返回
    public void func_return(){
        instructions.add(new Instruction(Operation.RET,-1));
        ret_func = cur_func;
    }

    //添加一条store指令
    public void ins_store(){
        instructions.add(Inser.store64MinusOne);
    }

    //添加一条ret指令
    public void ins_ret(){
        instructions.add(Inser.ret);
    }

    public void ins_itof(){
        instructions.add(Inser.itof);
    }

    public void ins_ftoi(){
        instructions.add(Inser.ftoi);
    }

    public void ins_push(Long operand){
        instructions.add(Inser.push(operand));
    }

    public void ins_load(){
        instructions.add(Inser.load64MinusOne);
    }

    public void ins_arga(long operand){
        instructions.add(Inser.arga(operand));
    }

    public void ins_brZero(){
        instructions.add(Inser.brZero);
    }

    public void ins_brMinusOne(){
        instructions.add(Inser.brMiusOne);
    }

    public void ins_brtrueOne(){
        instructions.add(Inser.brtrue);
    }
}
