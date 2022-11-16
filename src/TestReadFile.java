import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TestReadFile {
    public static void main(String[] args) {
        try {
            File f = new File("CW1/src/test.txt");
            FileReader fileReader = new FileReader(f);
            int length = 0;
            char ch;
            while ((length = fileReader.read()) != -1) {
                ch = (char) length;
                System.out.println(ch);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        public static boolean isValidIdentifier(String token){
        // Can not start with number
        for(int i=0; i<token.length(); i++){
            if((i==0)){
                // Can not start with number
                char charAt0 = token.charAt(0);
                if((charAt0-'0')>=0 && (charAt0-'0')<=9){
                    return false;
                }else{
                    //如果 ！(大小写字母 or 数字 or 下划线) 不是有效的
                    if(!(((charAt0>='a' && charAt0<='z') || (charAt0>='A' && charAt0<='Z')) || ((charAt0-'0')>=0 && (charAt0-'0')<=9) || charAt0=='_'))
                        return false;
                }
            }
            else{
                //如果 ！(大小写字母 or 数字 or 下划线) 不是有效的
                char eachOne = token.charAt(i);
                if(!(((eachOne>='a' && eachOne<='z') || (eachOne>='A' && eachOne<='Z')) || ((eachOne-'0')>=0 && (eachOne-'0')<=9) || eachOne=='_'))
                    return false;
            }
        }
        return true;
    }
}
