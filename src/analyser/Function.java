package analyser;

import instruction.Instruction;
import util.MyType;

import java.util.ArrayList;
import java.util.List;

public class Function extends Symbol {

    List<Var> paramList = new ArrayList<>(); //参数列表
    MyType returnType; //函数返回值类型

    public Function() {
    }

    public Function(String name, MyType type, int level, boolean isVar, List<Var> paramList, MyType returnType) {
        super(name, type, level, isVar);
        this.paramList = paramList;
        this.returnType = returnType;
    }

    public List<Var> getParamList() {
        return paramList;
    }

    public void setParamList(List<Var> paramList) {
        this.paramList = paramList;
    }

    public MyType getReturnType() {
        return returnType;
    }

    public void setReturnType(MyType returnType) {
        this.returnType = returnType;
    }

    @Override
    public String toString() {
        return "Function{" +
                "name=" + name +
                ", type=" + type +
                ", level=" + level +
                ", paramList=" + paramList +
                ", returnType=" + returnType +
                '}';
    }
}