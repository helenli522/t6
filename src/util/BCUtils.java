package util;

import analyser.Maintainer;
import error.AnalyzeError;
import error.CompileError;
import error.ErrorCode;
import instruction.Inser;
import instruction.Instruction;

import java.util.ArrayList;
import java.util.List;

class BC {
    MyType type;
    Instruction instruction;
    int whileCount;
    int p;

    public MyType getType() {
        return type;
    }

    public void setType(MyType type) {
        this.type = type;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public void setInstruction(Instruction instruction) {
        this.instruction = instruction;
    }

    public int getWhileCount() {
        return whileCount;
    }

    public void setWhileCount(int whileCount) {
        this.whileCount = whileCount;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public BC(MyType type, Instruction instruction, int whileCount, int p) {
        this.type = type;
        this.instruction = instruction;
        this.whileCount = whileCount;
        this.p = p;
    }
}

public class BCUtils {
    public int whileLevel = 0;
    public List<BC> breaks = new ArrayList<>();
    public List<BC> continues = new ArrayList<>();

    public boolean checkBreak(Maintainer maintainer) {
        if(whileLevel > 0){
            maintainer.add_instrction(Inser.brZero);
            BC aBreak = new BC(MyType.BREAK, Inser.brZero, maintainer.get_instructions_size(), whileLevel);
            breaks.add(aBreak);
            return true;
        }
        return false;
    }

    public boolean checkContinue(Maintainer maintainer) {
        if(whileLevel > 0){
            maintainer.add_instrction(Inser.brZero);
            BC aContinue = new BC(MyType.CONTINUE, Inser.brZero, maintainer.get_instructions_size(), whileLevel);
            continues.add(aContinue);
            return true;
        }
        return false;
    }

    public void enter(){
        whileLevel += 1;
    }

    public void leave(){
        whileLevel -= 1;
    }

    public void handleBC(int exit){
        for(BC aBreak : breaks){
            if(whileLevel == aBreak.whileCount - 1)
                aBreak.instruction.setOperandA(exit - aBreak.p);
        }
        for(BC aContinue : breaks){
            if(whileLevel == aContinue.whileCount - 1)
                aContinue.instruction.setOperandA(exit - aContinue.p);
        }
        if (whileLevel == 0){
            breaks = new ArrayList<>();
            continues = new ArrayList<>();
        }
    }
}
