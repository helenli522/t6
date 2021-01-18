package analyser;

import instruction.Instruction;

import java.util.List;

public class Func {
    public int fNum; //函数编号
    public String fName; //函数名
    public int globalPos; //函数名称在全局变量中的位置
    public int returnSlots; //返回值占据的 slot 数
    public int paramSlots; //参数占据的 slot 数
    public int locSlots; //局部变量占据的 slot 数
    public List<Instruction> body;

    public Func(int fNum, String fName, int globalPos, int returnSlots, int paramSlots, int locSlots, List<Instruction> body) {
        this.fNum = fNum;
        this.fName = fName;
        this.globalPos = globalPos;
        this.returnSlots = returnSlots;
        this.paramSlots = paramSlots;
        this.locSlots = locSlots;
        this.body = body;
    }

    public int getfNum() {
        return fNum;
    }

    public void setfNum(int fNum) {
        this.fNum = fNum;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public int getGlobalPos() {
        return globalPos;
    }

    public void setGlobalPos(int globalPos) {
        this.globalPos = globalPos;
    }

    public int getReturnSlots() {
        return returnSlots;
    }

    public void setReturnSlots(int returnSlots) {
        this.returnSlots = returnSlots;
    }

    public int getParamSlots() {
        return paramSlots;
    }

    public void setParamSlots(int paramSlots) {
        this.paramSlots = paramSlots;
    }

    public int getLocSlots() {
        return locSlots;
    }

    public void setLocSlots(int locSlots) {
        this.locSlots = locSlots;
    }

    public List<Instruction> getBody() {
        return body;
    }

    public void setBody(List<Instruction> body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Func{" +
                "fNum=" + fNum +
                ", fName='" + fName + '\'' +
                ", globalPos=" + globalPos +
                ", returnSlots=" + returnSlots +
                ", paramSlots=" + paramSlots +
                ", locSlots=" + locSlots +
                ", body=" + body +
                '}';
    }
}
