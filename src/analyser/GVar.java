package analyser;

public class GVar {
    public int globalNum; //从0开始？
    public boolean isConst; //对应输出的is_const
    public int count; //对应输出的value.count
    public String items; //对应输出的value.items

    public GVar() {
    }

    public GVar(int globalNum, boolean isConst) {
        this.globalNum = globalNum;
        this.isConst = isConst;
    }

    public GVar(int globalNum, boolean isConst, int count, String items) {
        this.globalNum = globalNum;
        this.isConst = isConst;
        this.count = count;
        this.items = items;
    }

    public int getGlobalNum() {
        return globalNum;
    }

    public void setGlobalNum(int globalNum) {
        this.globalNum = globalNum;
    }

    public boolean isConst() {
        return isConst;
    }

    public void setConst(boolean aConst) {
        isConst = aConst;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "GlobalVar{" +
                "globalNum=" + globalNum +
                ", isConst=" + isConst +
                ", count=" + count +
                ", items=" + items +
                '}';
    }
}
