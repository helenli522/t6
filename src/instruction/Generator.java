package instruction;

import analyser.FVar;
import analyser.GVar;

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

    //magic
    //version
    //globals.count, int
    //global_table
    //functions.count, int
    //function_table
    public List<Byte> generate(List<GVar> global_table, List<FVar> function_table){
        addInt(4,0x72303b3e);
        addInt(4,0x00000001);

        addInt(4,global_table.size());
        for(GVar gVar : global_table) {
            addBool(gVar.isConst);
            if(gVar.items == null){
                int count = 8;
                long items = 0L;
                addInt(4,count);
                addLong(8,items);
            }
            else{
                addInt(4, gVar.items.length());
                addString(gVar.items);
            }
        }

        addInt(4,function_table.size());
        for(FVar fVar:function_table) {
            addInt(4, fVar.globalPos);
            addInt(4, fVar.returnSlots);
            addInt(4, fVar.getParamSlots());
            addInt(4, fVar.locSlots);
            addInt(4, fVar.getBody().size());
            List<Instruction> instructions = fVar.getBody();
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
        return byteList;
    }
}
