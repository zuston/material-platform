package Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

/**
 * Created by zuston on 17/3/27.
 */
public class AnalysisExpressionTest {
    public static void main(String[] args) {
        String testStr1 = "1A&2A&S";
        System.out.println(simpleAnaly(testStr1));
    }

    public static ArrayList<String> simpleAnaly(String str){
        Stack<Character> stack = new Stack<Character>();
        char [] strChar = str.toCharArray();
        int flag = 0;
        StringBuilder sb = new StringBuilder();
        ArrayList<String> arr = new ArrayList<String>();
        for (char c:strChar){


            if (c==')'){
                sb = new StringBuilder();
                while (stack.peek()!='('){
                    sb.append(stack.pop());
                }
                stack.pop();
                if (sb.length()>0){
                    arr.add(String.valueOf(sb.reverse()));
                }
            }else{
                stack.push(c);
            }
        }
        sb = new StringBuilder();
        while (!stack.isEmpty()){
            sb.append(stack.pop());
        }
        sb = sb.reverse();
        System.out.println(arr);
        System.out.println(sb);
        if (arr.isEmpty()){
            return new ArrayList<String>(Arrays.asList(str.split("\\|")));
        }
        if (sb.length()>=2){
            int len = arr.size();
            ArrayList<String> front = new ArrayList<String>();
            ArrayList<String> end = new ArrayList<String>();
            for (int i=0;i<len;i++){
                if (isTag(sb.charAt(i))){
                    front.add(String.valueOf(sb.charAt(i)));
                }
                if (isTag(sb.charAt(sb.length()-1-i))){
                    end.add(String.valueOf(sb.charAt(sb.length()-1-i)));
                }

            }
            System.out.println("前缀"+front);
            Collections.reverse(end);
            System.out.println("后缀"+end);

            ArrayList<String> allStr = new ArrayList<String>();
            for (String splitStr:arr){
                if (splitStr.indexOf("\\|")>0){
                    allStr.add(splitStr);
                }else if (splitStr.indexOf("&")>0){
                    sb.append(splitStr);
                }
            }
            // 针对于左单括号
            if (front.size()==1&&end.size()==0){
                if (front.get(0).equals("&")){
                    ArrayList<String> resS = new ArrayList<String>();
                    for (String splitStr:arr.get(0).split("\\|")){
                        System.out.println(splitStr);
                        StringBuilder res = new StringBuilder();
                        res.append(splitStr).append(sb);
                        resS.add(String.valueOf(res));
                    }
                    return resS;
                }
            }

            // 针对于右单括号
            if (front.size()==0&&end.size()==1){
                if (end.get(0).equals("&")){
                    ArrayList<String> resS = new ArrayList<String>();
                    for (String splitStr:arr.get(0).split("\\|")){
                        StringBuilder res = new StringBuilder();
                        res.append(sb).append(splitStr);
                        resS.add(String.valueOf(res));
                    }
                    return resS;
                }
            }
        }

        return null;
    }

    public static boolean isTag(char c){
        if (c=='&'||c=='~'||c=='|'){
            return true;
        }
        return false;
    }

    public static ArrayList<String> analyExpression(String str){
        Stack<Character> stack = new Stack<Character>();
        char [] strChar = str.toCharArray();
        for (char c:strChar){
            if (c==')'){
                int flag = 0;
                StringBuilder sb = new StringBuilder();
                while (!stack.isEmpty()){
                    char value = stack.peek();
                    if (value!='('){
                        sb.append(value);
                    }else{
                        char tag = stack.pop();
                        if(tag=='&'||tag=='|'||tag=='~'){
                            if (tag=='&'){
                                StringBuilder s = sb.reverse();
                                System.out.println(s.toString());
                            }
                        }
                        flag = 1;
                    }
                    if (flag==1){
                        break;
                    }
                }
            }else {
                stack.push(c);
            }
        }
        return null;
    }

}
