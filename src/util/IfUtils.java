package util;

import analyser.Maintainer;
import instruction.Instruction;
import instruction.Operation;

public class IfUtils {
    public static int un_cond_to_else(Maintainer maintainer){
        Instruction jmp_else = new Instruction(Operation.BR, 0);
        maintainer.add_instrction(jmp_else);
        return maintainer.get_last_pos();
    }

    public static void cond_to_else(Maintainer maintainer, int offset){
        Instruction jmp_else = new Instruction(Operation.BR, offset);
        maintainer.add_instrction(jmp_else);
    }

    public static int to_end(Maintainer maintainer){
        //操作数为-1
        Instruction jmp_end = new Instruction(Operation.BR, -1);
        maintainer.add_instrction(jmp_end);
        return maintainer.get_last_pos();
    }
}
