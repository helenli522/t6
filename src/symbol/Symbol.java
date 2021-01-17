package symbol;

import util.MyType;

public class Symbol {
        String name;
        MyType type;
        int level;
        boolean isVar;
        boolean isFunc;

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public MyType getType() {
                return type;
        }

        public void setType(MyType type) {
                this.type = type;
        }

        public int getLevel() {
                return level;
        }

        public void setLevel(int level) {
                this.level = level;
        }

        public boolean isVar() {
                return isVar;
        }

        public void setVar(boolean var) {
                isVar = var;
        }

        public boolean isFunc() {
                return isFunc;
        }

        public void setFunc(boolean func) {
                isFunc = func;
        }

        public Symbol() {
        }

        public Symbol(String name, MyType type, int level, boolean isVar, boolean isFunc) {
                this.name = name;
                this.type = type;
                this.level = level;
                this.isVar = isVar;
                this.isFunc = isFunc;
        }
}
