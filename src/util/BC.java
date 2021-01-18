package util;

import instruction.Instruction;

public class BC {
    MyType type;
    public Instruction brins;
    public int wnum;
    public int p;

    public MyType getType() {
        return type;
    }

    public void setType(MyType type) {
        this.type = type;
    }

    public Instruction getBrins() {
        return brins;
    }

    public void setBrins(Instruction brins) {
        this.brins = brins;
    }

    public int getWnum() {
        return wnum;
    }

    public void setWnum(int wnum) {
        this.wnum = wnum;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public BC(MyType type, Instruction brins, int wnum, int p) {
        this.type = type;
        this.brins = brins;
        this.wnum = wnum;
        this.p = p;
    }
}
