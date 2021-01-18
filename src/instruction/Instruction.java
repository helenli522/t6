package instruction;

import analyser.Maintainer;
import util.MyType;
import tokenizer.TokenType;

import java.util.List;
import java.util.Objects;

public class Instruction {
    Operation operation;
    long operandA;
    double operandD;

    public Instruction(Operation operation) {
        this.operation = operation;
        this.operandA = 0;
    }

    public Instruction(Operation operation, long operandA) {
        this.operation = operation;
        this.operandA = operandA;
    }

    public Instruction(Operation operation, double operandD) {
        this.operation = operation;
        this.operandD = operandD;
    }

    @Override
    public int hashCode() {
        return Objects.hash(operation, operandA);
    }

    @Override
    public String toString() {
        return operation+" "+ operandA +'\n';
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public long getOperandA() {
        return operandA;
    }

    public void setOperandA(long operandA) {
        this.operandA = operandA;
    }

    // 操作指令入栈
    public static void operate(TokenType sign, MyType operand, Maintainer maintainer){
        boolean isInt = true;
        if(operand != MyType.INT) isInt = false;
        if(sign == TokenType.PLUS){
            if(isInt) maintainer.add_instrction(Inser.addI);
            else maintainer.add_instrction(Inser.addF);
        }
        else if(sign == TokenType.MINUS){
            if(isInt) maintainer.add_instrction(Inser.subI);
            else maintainer.add_instrction(Inser.subF);
        }
        else if(sign == TokenType.MUL){
            if(isInt) maintainer.add_instrction(Inser.mulI);
            else maintainer.add_instrction(Inser.mulF);
        }
        else if(sign == TokenType.DIV){
            if(isInt) maintainer.add_instrction(Inser.divI);
            else maintainer.add_instrction(Inser.divF);
        }
        else if(sign == TokenType.EQ){
            if(isInt) maintainer.add_instrction(Inser.cmpI);
            else maintainer.add_instrction(Inser.cmpF);
            // 这里逻辑再想一下
            maintainer.add_instrction(Inser.not);
        }
        else if(sign == TokenType.NEQ){
            if(isInt) maintainer.add_instrction(Inser.cmpI);
            else maintainer.add_instrction(Inser.cmpF);
        }
        else if(sign == TokenType.LT){  //<
            if(isInt) maintainer.add_instrction(Inser.cmpI);
            else maintainer.add_instrction(Inser.cmpF);
            maintainer.add_instrction(Inser.setLt);
        }
        else if(sign == TokenType.LE){  //<=
            if(isInt) maintainer.add_instrction(Inser.cmpI);
            else maintainer.add_instrction(Inser.cmpF);
            // not>表示<=
            maintainer.add_instrction(Inser.setGt);
            maintainer.add_instrction(Inser.not);
        }
        else if(sign == TokenType.GT){  //>
            if(isInt) maintainer.add_instrction(Inser.cmpI);
            else maintainer.add_instrction(Inser.cmpF);
            maintainer.add_instrction(Inser.setGt);
        }
        else if(sign == TokenType.GE){  //>=
            if(isInt) maintainer.add_instrction(Inser.cmpI);
            else maintainer.add_instrction(Inser.cmpF);
            // not<表示>=
            maintainer.add_instrction(Inser.setLt);
            maintainer.add_instrction(Inser.not);
        }
        else if(sign == TokenType.NEGATE){
            if(isInt) maintainer.add_instrction(Inser.negI);
            else maintainer.add_instrction(Inser.negF);
        }
        return;
    }

}
