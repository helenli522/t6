package util;

import analyser.Maintainer;
import instruction.Instruction;
import instruction.Operation;

import java.util.ArrayList;
import java.util.List;

import static instruction.Operation.BR;

public class BCUtils {
    public List<BC> breaks = new ArrayList<>();
    public List<BC> continues = new ArrayList<>();

    public boolean addBreak(int wlevel, Maintainer maintainer) {
        Instruction br = new Instruction(BR,0);
        maintainer.add_instrction(br);
        BC aBreak = new BC(MyType.BREAK, br, wlevel, maintainer.get_instructions_size());
        breaks.add(aBreak);
        return true;
    }

    public boolean addContinue(int wlevel, Maintainer maintainer) {
        Instruction br = new Instruction(BR,0);
        maintainer.add_instrction(br);
        continues.add(new BC(MyType.CONTINUE, br, wlevel, maintainer.get_instructions_size()));
        return true;
    }

    public boolean notWhile(int wlevel){
        return wlevel==0;
    }

    public void handleBC(int exit,int wlevel){
        for(BC aBreak : breaks){
            if(wlevel+1 == aBreak.wnum) {
                aBreak.brins.setOperandA(exit - aBreak.p);
                //System.out.println("break, off is "+(exit - aBreak.p));
            }
        }
        for(BC aContinue : continues){
            if(wlevel+1 == aContinue.wnum) {
                aContinue.brins.setOperandA(exit - aContinue.p - 1);
                //System.out.println("break, off is "+(exit - aContinue.p));
            }
        }
        if (wlevel == 0){
            breaks.clear();
            continues.clear();
        }
    }

    @Override
    public String toString() {
        return "BCUtils{" +
                "breaks=" + breaks +
                ", continues=" + continues +
                '}';
    }
}
