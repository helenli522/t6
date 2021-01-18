package instruction;

import analyser.Func;
import analyser.GlobalVar;

import java.util.ArrayList;
import java.util.List;

public class Generator {
    List<Byte> byteList = new ArrayList<>();

    private void addInt(int len,int x){
        for(int i = len-1; i >= 0; i--){
            byteList.add( (byte)(x >> (i * 8) & 0xFF) );
        }
    }

    private void addLong(int len,long x){
        for(int i = len-1; i >= 0; i--){
            byteList.add( (byte)(x >> (i * 8) & 0xFF) );
        }
    }

    private void addString(String str){
        int len = str.length();
        char ch;
        for(int i = 0; i < len; i++){
            ch = str.charAt(i);
            byteList.add((byte)ch);
        }
    }

    private void addBool(Boolean x){
        byte b = (byte) (x ? 1 : 0);
        byteList.add(b);
    }

    //is_const, boolean
    //2.value.count
    //3.value.items
    public void addGlobalVar(GlobalVar globalVar){
        addBool(globalVar.isConst);
        if(globalVar.items == null){
            int count = 8;
            long items = 0L;
            addInt(4,count);
            addLong(8,items);
        }
        else{
            addInt(4,globalVar.items.length());
            addString(globalVar.items);
        }
    }

    //name,int
    //ret_slots,int
    //param_slots,int
    //loc_slots,int
    //body.count,int
    //body.items
    public void addFunc(Func func){
        addInt(4,func.globalPos);
        addInt(4,func.returnSlots);
        addInt(4,func.getParamSlots());
        addInt(4,func.locSlots);
        addInt(4,func.getBody().size());

        List<Instruction> instructions = func.getBody();
        for(Instruction instruction : instructions){
            int op = instruction.getOperation().getNode(); //指令,int
            addInt(1,op);
            if(instruction.getOperandA() != -1){ //操作数,long
                if(op == 1) //PUSH
                    addLong(8,instruction.getOperandA());
                else
                    addLong(4,instruction.getOperandA());
            }
        }
    }

    //magic
    //version
    //globals.count, int
    //global_table
    //functions.count, int
    //function_table
    public List<Byte> generate(List<GlobalVar> global_table, List<Func> function_table){
        addInt(4,0x72303b3e);
        addInt(4,0x00000001);

        addInt(4,global_table.size());
        for(GlobalVar global:global_table) addGlobalVar(global);

        addInt(4,function_table.size());
        for(Func fun:function_table) addFunc(fun);
        return byteList;
    }
}
