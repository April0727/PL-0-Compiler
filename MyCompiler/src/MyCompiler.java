/**
 * Created by zhf on 2015/12/22
 * ����˳���������������˵�����ĵ�¼���ű�Ĳ���
 * ������
 * SymbolTable+TableRow�����ɷ��ű�ĵ�¼����������TableRow��SymbolTable�е�һ��
 */
import java.io.*;
import java.util.Scanner;

public class MyCompiler {
    public static void main(String [] args) {
        String filename;
        System.out.println(">Please input filename:");
        System.out.print(">>");
        Scanner s=new Scanner(System.in);
        filename=s.next();
        MpgAnalysis mp=new MpgAnalysis(filename);
        if(!mp.mgpAnalysis()){
            System.out.println(">compile succeed!");
        }
        String choice;
        System.out.println(">Please input your choice:");
        System.out.println(">1.run now  " + "2.show PCode  " + "3.show Symbol Table");
        System.out.print(">>");
        choice=s.next();
        if(choice.equals("1")) {
            System.out.println("running...");
            mp.interpreter();
        }else if(choice.equals("2")){
            System.out.println("The following is PCode:");
            mp.showPcodeInStack();
        }else if(choice.equals("3")){
            System.out.println("The following is Symbol Table:");
            mp.showtable();
        }

    }


}
