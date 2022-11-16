import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * @file LexicalAnalyzer.java
 * @author Fenwei Guo 19722090
 * This code is used to scan and tokenize the C language source code
 * @date 15/11/2022
 */
public class LexicalAnalyzer {
    public static List<String> reserveWords= new ArrayList<String>(Arrays.asList(
            //这里写错了 记得改
            "#define", "#include", "int", "float", "auto", "double", "do", "switch", "return"
            ));
    public static List<String> preprocessingDirective = new ArrayList<String>(Arrays.asList(
            "#include", "#define"
            ));
    public static List<String> operator = new ArrayList<String>(Arrays.asList(
            "*", "+", "-", "/", "=","<", ">","<=", ">=","-=", "*=", "+=", "/=", "++", "--", "=="
            ));
    public static List<String> delimiter = new ArrayList<String>(Arrays.asList(
            "{", "}", ",", "(", ")", ";"
            ));
    public static List<String> loop = new ArrayList<String>(Arrays.asList(
            "for", "while"
            ));
    public static int currentLineType = 0; // 0 for statement, 1 for single-line comment, 2 for multi-line comment
    public static File f;
    public static FileReader fileReader;
    public static int lineCount = 0;
    public static char ch;

    public static List<String> number = new ArrayList<String>();
    public static List<String> tokenType = new ArrayList<String>();
    public static StringBuilder currentCharacter = new StringBuilder();
    public static StringBuilder currentToken = new StringBuilder();
    public static int legalType = 0;// 1 for number type, 2 for string type, 3 for bool type


    public static void main(String[] args){
        try{
            Analyzer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void Analyzer() throws IOException {
        f = new File("CW1/src/program.txt");
        fileReader=new FileReader(f);
        int length = 0;
        while((length=fileReader.read())!=-1) {
            ch = (char)length;
            //System.out.print("当前字符：" + ch);
            if(ch=='\n'){ //Count the total line and record the wrong line number
                lineCount++;
            }
            //It means that current line is neither single nor multi comments
            if (currentLineType != 0) {
                //char nextChar = (char)fileReader.read();
                if((currentLineType==1) && ch == '\n')
                    currentLineType = 0;
                if((currentLineType==2) && ch == '*'){
                    ch = (char)fileReader.read();
                    //maybe other situations
                    if (ch == '/') {
                        currentLineType = 0;
                        addToTwoTupleTable("*/");
                    }
                }
                continue;
            }
            if(ch == '/'){
                currentCharacter = new StringBuilder(String.valueOf(ch));
                //read the next char
                ch = (char)fileReader.read();
                if(((int)(ch)) == 65535){// EOF -1 is 65535 break the loop
                    addToTwoTupleTable(currentCharacter.toString());
                    break;
                }
                if(ch == '*') {
                    currentLineType = 2;
                    currentCharacter.append(ch);
                }else if(ch == '/'){
                    currentLineType = 1;
                    currentCharacter.append(ch);
                }
                if(currentLineType !=0){
                    addToTwoTupleTable(currentCharacter.toString());
                    continue;
                }
            }
/*            if(ch == '*'){
                currentCharacter = new StringBuilder(String.valueOf(ch));
                //read the next char
                ch = (char)fileReader.read();
                if(((int)(ch)) == 65535){// EOF -1 is 65535 break the loop
                    addToTwoTupleTable(currentCharacter.toString());
                    break;
                }
                if(ch == '/') {
                    currentLineType = 2;
                    currentCharacter.append(ch);
                }
                if(currentLineType !=0){
                    addToTwoTupleTable(currentCharacter.toString());
                    continue;
                }

            }*/
            currentCharacter = new StringBuilder(String.valueOf(ch));
            //判断当前character是否为空 可以判断当前的token类型
            if(isNotLegal(currentCharacter.toString())){
                if(currentToken.length()!=0){
                    addToTwoTupleTable(currentToken.toString());
                    currentToken.delete(0,currentToken.length()); //reset的token buffer
                }
                continue;
            }
            if(isOperator((currentCharacter.toString())) && !isOperator(currentToken.toString())){
                if(currentToken.length()!=0){
                    addToTwoTupleTable(currentToken.toString());
                    currentToken.delete(0,currentToken.length()); //reset的token buffer
                }
            }
            if(!isOperator((currentCharacter.toString())) && isOperator(currentToken.toString())){
                addToTwoTupleTable(currentToken.toString());
                currentToken.delete(0,currentToken.length()); //reset的token buffer
            }
            if(isDelimiter(currentCharacter.toString())){
                if(currentToken.length()!=0){
                    addToTwoTupleTable(currentToken.toString());
                    currentToken.delete(0,currentToken.length()); //reset的token buffer
                }
                if(isDelimiter(currentCharacter.toString())){
                    addToTwoTupleTable(currentCharacter.toString());
                    continue;
                }
            }
            currentToken.append(ch);
        }
        //System.out.println(lineCount);
    }
    public static void updateCommentType(char currentChar){

        return;
    }
    public static void addToTwoTupleTable(String token){
        if(isOperator(token)){
            System.out.println("(Operator,\t" + token + ")");
        } else if(isDelimiter(token)){
            System.out.println("(Delimiter,\t" + token + ")");
        }else if(isReservedWords(token)){
            System.out.println("(keyword,\t" + token + ")");
        }else if(isLoop(token)){
            System.out.println("(LoopSign,\t" + token + ")");
        } else if(isLegal(token)){
            if(legalType == 1){
                System.out.println("(Number,\t" + token + ")");
            }else if(legalType == 2){
                System.out.println("(String,\t" + token + ")");
            }else if(legalType == 3){
                System.out.println("(Bool,\t" + token + ")");
            }
            legalType = 0;
        }else if(isValidIdentifier(token)){
            System.out.println("(Identifier,\t" + token + ")");
        }else if(isCommentsLine(token)){
            System.out.println("(Comment,\t" + token + ")");
        }
        return;
    }
    public static boolean isCommentsLine(String token){
        return token.equals("/*") || token.equals("//") || token.equals("*/");
    }
    public static boolean isNotLegal(String token){
        return token.equals(" ") || token.equals("\n");
    }
    public static boolean isReservedWords(String token){
        for(String eachOne : reserveWords){
            if (token.equals(eachOne)) return true;
        }
        return false;
    }
    public static boolean isNumberType(String token){
        for(int i=0; i<token.length();i++){
            int chr =token.charAt(i);
            if(chr<48 || chr >57){
                return false;
            }
        }
        return true;
    }
    public static boolean isStringType(String token){
        return token.startsWith("\"") && token.endsWith("\"");
    }
    public static boolean isBoolType(String token){
        return token.equals("true") || token.equals("false");
    }
    public static boolean isLegal(String token){
        // 0 for number type, 1 for string type, 2 for bool type
        boolean flag = false;
        if(isNumberType(token)){
            flag = true;
            legalType = 1;
        }else if(isStringType(token)){
            flag = true;
            legalType = 2;
        }else if(isBoolType(token)){
            legalType = 3;
            flag = true;
        }
        //啥也不是 就直接返回 false
        return flag;
    }
    public static boolean isValidIdentifier(String token){
        for(int i=0; i<token.length(); i++){
            if((i==0)){
                char charAt0 = token.charAt(0);
                if((charAt0-'0')>=0 && (charAt0-'0')<=9){
                    return false;
                }else{
                    if(!(((charAt0>='a' && charAt0<='z') || (charAt0>='A' && charAt0<='Z')) || ((charAt0-'0')>=0 && (charAt0-'0')<=9) || charAt0=='_'))
                        return false;
                }
            }
            else{
                char eachOne = token.charAt(i);
                if(!(((eachOne>='a' && eachOne<='z') || (eachOne>='A' && eachOne<='Z')) || ((eachOne-'0')>=0 && (eachOne-'0')<=9) || eachOne=='_'))
                    return false;
            }
        }
        return true;
    }
    public static boolean isOperator(String token){
        for(String eachOne : operator){
            if(token.equals(eachOne)) return true;
        }
        return false;
    }
    public static boolean isDelimiter(String token){
        for(String eachOne : delimiter){
            if(token.equals(eachOne)){
                return true;
            }
        }
        return false;

    }
    public static boolean isLoop(String token){
        for(String eachOne : loop){
            if(token.equals(eachOne)){
                return true;
            }
        }
        return false;
    }
}
