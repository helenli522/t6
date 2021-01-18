package util;

public class TypeUtils {
    public static Long strToLong(String str){
        Long res = 0L , tmp = 1L;
        for(int j = str.length() - 1; j >= 0; j--){
            if(str.charAt(j) == '1')
                res += tmp;
            tmp *= 2;
        }
        return res;
    }
}
