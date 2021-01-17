package symbol;

import util.MyType;

public class Var extends Symbol {
    boolean isConst;
    int localNum;
    int globalNum;
    int fNum;
    int paramNum;

    public Var() {
    }

    public Var(String name, MyType type, int level, boolean isVar, boolean isFunc, boolean isConst, int localNum, int globalNum, int fNum, int paramNum) {
        super(name, type, level, isVar, isFunc);
        this.isConst = isConst;
        this.localNum = localNum;
        this.globalNum = globalNum;
        this.fNum = fNum;
        this.paramNum = paramNum;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public int getLocalNum() {
        return localNum;
    }

    public void setLocalNum(int localNum) {
        this.localNum = localNum;
    }

    public int getGlobalNum() {
        return globalNum;
    }

    public void setGlobalNum(int globalNum) {
        this.globalNum = globalNum;
    }

    public int getfNum() {
        return fNum;
    }

    public void setfNum(int fNum) {
        this.fNum = fNum;
    }

    public int getParamNum() {
        return paramNum;
    }

    public void setParamNum(int paramNum) {
        this.paramNum = paramNum;
    }

    @Override
    public String toString() {
        return "Var{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", level=" + level +
                ", isConst=" + isConst +
                ", localNum=" + localNum +
                ", globalNum=" + globalNum +
                ", fNum=" + fNum +
                ", paramNum=" + paramNum +
                '}';
    }
}
