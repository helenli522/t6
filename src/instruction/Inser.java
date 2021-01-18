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
