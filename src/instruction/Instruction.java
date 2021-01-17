package instruction;

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
    public static void operate(TokenType type, List<Instruction> instructions, MyType op){
        Instruction newIns;
        boolean isInt = true;
        if(op != MyType.INT) isInt = false;
        if(type == TokenType.PLUS){
            if(isInt) newIns = new Instruction(Operation.ADDI,-1);
            else newIns = new Instruction(Operation.ADDF,-1);
            instructions.add(newIns);
        }
        else if(type == TokenType.MINUS){
            if(isInt) newIns = new Instruction(Operation.SUBI,-1);
            else newIns = new Instruction(Operation.SUBF,-1);
            instructions.add(newIns);
        }
        else if(type == TokenType.MUL){
            if(isInt) newIns = new Instruction(Operation.MULI,-1);
            else newIns = new Instruction(Operation.MULF,-1);
            instructions.add(newIns);
        }
        else if(type == TokenType.DIV){
            if(isInt) newIns = new Instruction(Operation.DIVI,-1);
            else newIns = new Instruction(Operation.DIVF,-1);
            instructions.add(newIns);
        }
        else if(type == TokenType.EQ){
            if(isInt) newIns = new Instruction(Operation.CMPI,-1);
            else newIns = new Instruction(Operation.CMPF,-1);
            instructions.add(newIns);
            // 这里逻辑再想一下
            newIns = new Instruction(Operation.NOT,-1);
            instructions.add(newIns);
        }
        else if(type == TokenType.NEQ){
            if(isInt) newIns = new Instruction(Operation.CMPI,-1);
            else newIns = new Instruction(Operation.CMPF,-1);
            instructions.add(newIns);
        }
        else if(type == TokenType.LT){  //<
            if(isInt) newIns = new Instruction(Operation.CMPI,-1);
            else newIns = new Instruction(Operation.CMPF,-1);
            instructions.add(newIns);
            newIns = new Instruction(Operation.SETLT,-1);
            instructions.add(newIns);
        }
        else if(type == TokenType.LE){  //<=
            if(isInt) newIns = new Instruction(Operation.CMPI,-1);
            else newIns = new Instruction(Operation.CMPF,-1);
            instructions.add(newIns);
            // not>表示<=
            newIns = new Instruction(Operation.SETGT,-1);
            instructions.add(newIns);
            newIns = new Instruction(Operation.NOT,-1);
            instructions.add(newIns);
        }
        else if(type == TokenType.GT){  //>
            if(isInt) newIns = new Instruction(Operation.CMPI,-1);
            else newIns = new Instruction(Operation.CMPF,-1);
            instructions.add(newIns);
            newIns = new Instruction(Operation.SETGT,-1);
            instructions.add(newIns);
        }
        else if(type == TokenType.GE){  //>=
            if(isInt) newIns = new Instruction(Operation.CMPI,-1);
            else newIns = new Instruction(Operation.CMPF,-1);
            instructions.add(newIns);
            // not<表示>=
            newIns = new Instruction(Operation.SETLT,-1);
            instructions.add(newIns);
            newIns = new Instruction(Operation.NOT,-1);
            instructions.add(newIns);
        }
        else if(type == TokenType.NEGATE){
            if(isInt) newIns = new Instruction(Operation.NEGI,-1);
            else newIns = new Instruction(Operation.NEGF,-1);
            instructions.add(newIns);
        }
        return;
    }

}
