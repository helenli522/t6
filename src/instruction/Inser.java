package instruction;

public class Inser {
    public static Instruction store64MinusOne = new Instruction(Operation.STORE64,-1);
    public static Instruction ret = new Instruction(Operation.RET,-1);
    public static Instruction itof = new Instruction(Operation.ITOF, -1);
    public static Instruction ftoi = new Instruction(Operation.FTOI,-1);
    public static Instruction stackallocZero = new Instruction(Operation.STACKALLOC,0);
    public static Instruction stackallocOne = new Instruction(Operation.STACKALLOC,1);
    public static Instruction load64MinusOne = new Instruction(Operation.LOAD64,-1);
    public static Instruction brtrue = new Instruction(Operation.BRTRUE,1);
    public static Instruction brZero = new Instruction(Operation.BR,0);
    public static Instruction brMiusOne = new Instruction(Operation.BR,-1);
    public static Instruction popn = new Instruction(Operation.POPN,1);
    public static Instruction addI = new Instruction(Operation.ADDI,-1);// +
    public static Instruction addF = new Instruction(Operation.ADDF,-1);
    public static Instruction subI = new Instruction(Operation.SUBI,-1);// -
    public static Instruction subF = new Instruction(Operation.SUBF,-1);
    public static Instruction mulI = new Instruction(Operation.MULI,-1);// *
    public static Instruction mulF = new Instruction(Operation.MULF,-1);
    public static Instruction divI = new Instruction(Operation.DIVI,-1);// /
    public static Instruction divF = new Instruction(Operation.DIVF,-1);
    public static Instruction cmpI = new Instruction(Operation.CMPI,-1);// cmp
    public static Instruction cmpF = new Instruction(Operation.CMPF,-1);
    public static Instruction not = new Instruction(Operation.NOT,-1);// not
    public static Instruction negI = new Instruction(Operation.NEGI,-1);// negate
    public static Instruction negF = new Instruction(Operation.NEGF,-1);
    public static Instruction setGt = new Instruction(Operation.SETGT,-1);// setGreaterThan
    public static Instruction setLt = new Instruction(Operation.SETLT,-1);// setLessThan

    public static Instruction arga(long operand){
        return new Instruction(Operation.ARGA,operand);
    }

    public static Instruction globa(int global_count){
        return new Instruction(Operation.GLOBA,global_count);
    }

    public static Instruction loca(int local_count){
        return new Instruction(Operation.LOCA,local_count);
    }

    public static Instruction push(Long operand){
        return new Instruction(Operation.PUSH,operand);
    }

    public static Instruction call(int operand){
        return new Instruction(Operation.CALL,operand);
    }

    public static Instruction callname(int operand){
        return new Instruction(Operation.CALLNAME,operand);
    }
}
