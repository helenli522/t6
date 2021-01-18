package util;

import tokenizer.Token;

public enum MyType {
    INT,
    DOUBLE,
    VOID,
    STRING,
    FUNCTION,
    CHAR,
    BREAK,
    CONTINUE,
    UNKNOWN; //用于报错

    //转换成字符串，用于传参
    public static String toStr(MyType type){
        switch (type){
            case INT:
                return "int";
            case DOUBLE:
                return "double";
            case VOID:
                return "void";
            default:
                return null;
        }
    }

    public static MyType toType(Token token){
        String str = (String) token.getValue();
        if(str.equals("int"))
            return INT;
        else if(str.equals("double"))
            return DOUBLE;
        else if(str.equals("void"))
            return VOID;
        else if(str.equals("string"))
            return STRING;
        else if(str.equals("function"))
            return FUNCTION;
        else if(str.equals("char"))
            return CHAR;
        else
            return UNKNOWN;
    }
}
