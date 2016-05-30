import com.sun.org.glassfish.external.arc.Stability;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by zhf on 2015/10/29.
 * �ؼ���const��Ӧ�ı�����con
 * ��������Ӧ�ı�����const
 * �﷨����û�д���İ汾���汾1.3�Ͱ汾1.4���ٹ�ע
 * ����������ű�����ַ��1
 * ������¼���ű�����ַ��1
 * ���̵�¼���ű����Ƚ�level��1�����������address��0��������ɺ��ٽ�level��һ���ٽ�address�ָ���
 * ؽ����ɵĹ��������汾û��ʵ�ֽ�address�ָ��Ĳ�����Ӧ����ʵ�ָù���
 *
 * 2015/12/26
 * ���ű�Ŀ���������ɼ���޴���
 * ���Գ���test2��test3����Ӧ����bh2��bhtest
 * 2015/12/27
 * ����˽������еĹ�����ֻʣ�´�������
 * ��������ɣ�ֻʣ�¹��̴��ݲ�����������
 * ����˲�ͬ��εı�����������������
 * ����˲�������
 */
import java.io.*;
public class MpgAnalysis {
    private static  int PROG=1;//program
    private static  int BEG=2;//begin
    private static  int END=3;//end
    private static  int IF=4;//if
    private static  int THEN=5;//then
    private static  int ELS=6;//else
    private static  int CON=7;//const
    private static  int PROC=8;//procdure
    private static  int VAR=9;//var
    private static  int DO=10;//do
    private static  int WHI=11;//while
    private static  int CAL=12;//call
    private static  int REA=13;//read
    private static  int WRI=14;//write
    private static  int REP=15;//repeate
    private static  int ODD=16;//  oddl      ��keyWord��ÿ���ֵ��������ȵ�

    private static  int EQU=17;//"="
    private static  int LES=18;//"<"
    private static  int LESE=19;//"<="
    private static  int LARE=20;//">="
    private static  int LAR=21;//">"
    private static  int NEQE=22;//"<>"


    private static  int ADD=23;//"+"
    private static  int SUB=24;//"-"
    private static  int MUL=25;//"*"
    private static  int DIV=26;//"/"

    private static  int SYM=27;//��ʶ��
    private static  int CONST=28;//����

    private static  int CEQU=29;//":="

    private static  int COMMA=30;//","
    private static  int SEMIC=31;//";"
    private static  int POI=32;//"."
    private static  int LBR=33;//"("
    private static  int RBR=34;//")"

    LexAnalysis lex;
    private boolean errorHapphen=false;
    private int rvLength=1000;
    private RValue[] rv=new RValue[rvLength];
    private int terPtr=0;       //RValue�ĵ�����

    private SymbolTable STable=new SymbolTable();       //���ű�
    private AllPcode  Pcode=new AllPcode();                 //���Ŀ�����


    private int level=0;                //������Ϊ��0��
    private int address=0;             //������������������Ϊ0
    private int addrIncrement=1;//TabelRow�е�address�����������������̵Ķ����¼�����ű������ʹ�����ӣ�����Ϊʲô

    public MpgAnalysis(String filename){
        for(int i=0;i<rvLength;i++){
            rv[i]=new RValue();
            rv[i].setId(-2);
            rv[i].setValue("-2");
        }
        lex=new LexAnalysis(filename);
    }

    public void readLex(){
        String filename="lex.txt";
        File file=new File(filename);
        BufferedReader ints=null;
        String tempLex,temp[];
        try{
            ints=new BufferedReader(new FileReader(file));
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        try{
            int i=0;
            while((tempLex=ints.readLine())!=null) {
                temp = tempLex.split(" ");
                rv[i].setId(Integer.parseInt(temp[0], 10));
                rv[i].setValue(temp[1]);
                rv[i].setLine(Integer.parseInt(temp[2]));
                i++;
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void prog(){
        if(rv[terPtr].getId()==PROG){
            terPtr++;
            if(rv[terPtr].getId()!=SYM){
                errorHapphen=true;
                showError(1,"");
            }else{
                terPtr++;
                if(rv[terPtr].getId()!=SEMIC){
                    errorHapphen=true;
                    showError(0,"");
                    return;
                }else{
                    terPtr++;
                    block();
                }
            }
        }else {
            errorHapphen = true;
            showError(2,"");
            return;
        }
    }

    public void block(){
        int addr0=address;      //��¼����֮ǰ�����������Ա�ָ�ʱ����
        int tx0=STable.getTablePtr();       //��¼�������ֵĳ�ʼλ��
        int cx0;
        int  propos=0;
        if(tx0>0){
            propos=STable.getLevelPorc(level);
            tx0=tx0- STable.getRow(propos).getSize();   //��¼����������Ŀ�ʼλ��
        }
        if(tx0==0){
            address=3;      //ÿһ���ʼλ�õ������ռ�������ž�̬��SL����̬��DL���ͷ��ص�ַRA
        }else{
            //ÿһ���ʼλ�õ������ռ�������ž�̬��SL����̬��DL���ͷ��ص�ַRA
            //�����ŷ��βεĸ���
            address=3+STable.getAllTable()[propos].getSize();


        }


        //�ݴ浱ǰPcode.codePtr��ֵ����jmp,0,0��codePtr�е�λ�ã�����һ�����
        int tempCodePtr= Pcode.getCodePtr();
        Pcode.gen(Pcode.getJMP(),0,0);


        if(rv[terPtr].getId()==CON){//�˴�û��terPtr++
            condecl();
        } if(rv[terPtr].getId()==VAR){
            vardecl();
        } if(rv[terPtr].getId()==PROC){
            proc();
            level--;
        }
        /*
        * ����������ɣ�������䴦���֣�֮ǰ���ɵ�jmp��0��0Ӧ����ת�����λ��
        *
        * */
        //����jmp��0��0����ת��ַ
        if(tx0>0){
            for(int i=0;i<STable.getAllTable()[propos].getSize();i++){
                Pcode.gen(Pcode.getSTO(),0,STable.getAllTable()[propos].getSize()+3-1-i);
            }
        }
        Pcode.getPcodeArray()[tempCodePtr].setA(Pcode.getCodePtr());
        Pcode.gen(Pcode.getINT(),0,address);        //���ɷ����ڴ�Ĵ���
        if(tx0==0){
           // STable.getRow(tx0).setValue(Pcode.getCodePtr());     //���������ڷ��ű��е�ֵ��Ϊ������ִ����俪ʼ��λ��
        }else {
            STable.getRow(propos).setValue(Pcode.getCodePtr()-1-STable.getAllTable()[propos].getSize());     //���������ڷ��ű��е�ֵ��Ϊ������ִ����俪ʼ��λ��

        }

        body();
        Pcode.gen(Pcode.getOPR(),0,0);      //�����˳����̵Ĵ��룬������������ֱ���˳�����

        address=addr0;      //�ֳ���������ָ����ֵ
        STable.setTablePtr(tx0);

    }

    public void condecl(){          //const��level�����������
        if(rv[terPtr].getId()==CON){
            terPtr++;
            myconst();
            while(rv[terPtr].getId()==COMMA){
                terPtr++;
                myconst();
            }
            if(rv[terPtr].getId()!=SEMIC){
                errorHapphen=true;
                showError(0,"");
                return;
            }else{
                terPtr++;
            }
        }else{
            errorHapphen=true;
            showError(-1,"");
            return;
        }
    }

    public void myconst(){
        String name;
        int value;
        if(rv[terPtr].getId()==SYM){
            name=rv[terPtr].getValue();
            terPtr++;
            if(rv[terPtr].getId()==CEQU){
                terPtr++;
                if(rv[terPtr].getId()==CONST){
                    value=Integer.parseInt(rv[terPtr].getValue());
                    if(STable.isNowExistSTable(name, level)){
                        errorHapphen=true;
                        showError(15,name);
                    }
                    STable.enterConst(name,level,value,address);
//                   address+=addrIncrement;             //��¼���ű���ַ��1ָ����һ��
                    terPtr++;
                }
            }else{
                errorHapphen=true;
                showError(3,"");
                return;
            }
        }else {
            errorHapphen=true;
            showError(1,"");
            return;
        }
    }

    public void vardecl(){
        String name;
        int value;
        if(rv[terPtr].getId()==VAR){
            terPtr++;
            if(rv[terPtr].getId()==SYM){
                name=rv[terPtr].getValue();
                if(STable.isNowExistSTable(name, level)){
                    errorHapphen=true;
                    showError(15,name);
                }
                STable.enterVar(name,level,address);
                address+=addrIncrement;
                terPtr++;
                while(rv[terPtr].getId()==COMMA){
                    terPtr++;
                    if(rv[terPtr].getId()==SYM){
                        name=rv[terPtr].getValue();
                        if(STable.isNowExistSTable(name, level)){
                            errorHapphen=true;
                            showError(15,name);
                        }
                        STable.enterVar(name,level,address);
                        address+=addrIncrement;     //��ַ��1��¼���ű�
                        terPtr++;
                    }else{
                        errorHapphen=true;
                        showError(1,"");
                        return;
                    }
                }
                if(rv[terPtr].getId()!=SEMIC){
                    errorHapphen=true;
                    showError(0,"");
                    return;
                }else{
                    terPtr++;
                }
            }else {
                errorHapphen=true;
                showError(1,"");
                return;
            }

        }else{
            errorHapphen=true;
            showError(-1,"");
            return;
        }
    }

    public void proc(){
        if(rv[terPtr].getId()==PROC){
            terPtr++;
            //id();
            int count=0;//������¼proc���βεĸ���
            int propos;// ��¼��proc�ڷ��ű��е�λ��
            if(rv[terPtr].getId()==SYM){
                String name=rv[terPtr].getValue();
                if(STable.isNowExistSTable(name, level)){
                    errorHapphen=true;
                    showError(15,name);
                }
                propos=STable.getTablePtr();
                STable.enterProc(rv[terPtr].getValue(),level,address);
                level++;                //levelֵ��һ����Ϊ�������ж�����ڸ��µ�proc�����
                terPtr++;
                if(rv[terPtr].getId()==LBR){
                    terPtr++;
                    //id();
                    if(rv[terPtr].getId()==SYM){
                        STable.enterVar(rv[terPtr].getValue(),level,3+count) ;      //3+count+1Ϊ�β��ڴ洢�ռ��е�λ��
                        count++;
                        STable.getAllTable()[propos].setSize(count);        //�ñ������ڷ��ű��е�size���¼�βεĸ���
                        terPtr++;
                        while(rv[terPtr].getId()==COMMA){
                            terPtr++;
                            if(rv[terPtr].getId()==SYM){
                                STable.enterVar(rv[terPtr].getValue(),level,3+count) ;      //3+count+1Ϊ�β��ڴ洢�ռ��е�λ��
                                count++;
                                STable.getAllTable()[propos].setSize(count);        //�ñ������ڷ��ű��е�size���¼�βεĸ���
                                terPtr++;
                            }else{
                                errorHapphen=true;
                                showError(1,"");
                                return;
                            }
                        }
                    }
                    if(rv[terPtr].getId()==RBR){
                        terPtr++;
                        if(rv[terPtr].getId()!=SEMIC){
                            errorHapphen=true;
                            showError(0,"");
                            return;
                        }else{
                            terPtr++;
                            block();
                            while(rv[terPtr].getId()==SEMIC){
                                terPtr++;
                                proc();
                            }
                        }
                    }else{
                        errorHapphen=true;
                        showError(5,"");
                        return;
                    }

                }else{
                    errorHapphen=true;
                    showError(4,"");
                    return;
                }
            }else{
                errorHapphen=true;
                showError(1,"");
                return;
            }

        }else{
            errorHapphen=true;
            showError(-1,"");
            return;
        }
    }

    public void body(){
        if(rv[terPtr].getId()==BEG){
            terPtr++;
            statement();
            while(rv[terPtr].getId()==SEMIC){
                terPtr++;
                statement();
            }
            if(rv[terPtr].getId()==END){
                terPtr++;
            }else{
                errorHapphen=true;
                showError(7,"");
                return;
            }
        }else{
            errorHapphen=true;
            showError(6,"");
            return;
        }
    }

    public void statement(){
        if(rv[terPtr].getId()==IF){
            int cx1;
            terPtr++;
            lexp();
            if(rv[terPtr].getId()==THEN){
                cx1=Pcode.getCodePtr();             //��cx1��¼jpc ��0��0������������һ����������Ŀ����룩��Pcode�еĵ�ַ������һ�����
                Pcode.gen(Pcode.getJPC(),0,0);  //��������ת��ָ�������boolֵΪ0ʱ��ת����ת��Ŀ�ĵ�ַ��ʱ��Ϊ0
                terPtr++;
                statement();
                int cx2=Pcode.getCodePtr();
                Pcode.gen(Pcode.getJMP(),0,0);
                Pcode.getPcodeArray()[cx1].setA(Pcode.getCodePtr());        //��ַ�����jpc��0��0�е�A����
                Pcode.getPcodeArray()[cx2].setA(Pcode.getCodePtr());
                if(rv[terPtr].getId()==ELS){
                    terPtr++;
                    statement();
                    Pcode.getPcodeArray()[cx2].setA(Pcode.getCodePtr());
                }//û�ˣ�
            }else{
                errorHapphen=true;
                showError(8,"");
                return;
            }
        }else if(rv[terPtr].getId()==WHI){
            int cx1=Pcode.getCodePtr();     //�����������ʽ��Pcode�еĵ�ַ
            terPtr++;
            lexp();
            if(rv[terPtr].getId()==DO){
                int cx2=Pcode.getCodePtr();     //����������תָ��ĵ�ַ���ڻ���ʱʹ�ã�������������������ת
                Pcode.gen(Pcode.getJPC(),0,0);
                terPtr++;
                statement();
                Pcode.gen(Pcode.getJMP(),0,cx1);    //���DO������������Ҫ��ת���������ʽ��������Ƿ�������������Ƿ����ѭ��
                Pcode.getPcodeArray()[cx2].setA(Pcode.getCodePtr());        //��������ת��ָ��
            }else{
                errorHapphen=true;
                showError(9,"");
                return;
            }
        }else if(rv[terPtr].getId()==CAL){
            terPtr++;
            //id();
            int count=0;//�������鴫��Ĳ������趨�Ĳ����Ƿ����
            TableRow tempRow;
            if(rv[terPtr].getId()==SYM){
                if(STable.isPreExistSTable(rv[terPtr].getValue(),level)){        //���ű��д��ڸñ�ʶ��
                     tempRow=STable.getRow(STable.getNameRow(rv[terPtr].getValue()));  //��ȡ�ñ�ʶ�������е�������Ϣ��������tempRow��
                    if(tempRow.getType()==STable.getProc()) { //�жϸñ�ʶ�������Ƿ�Ϊprocedure��SymTable��procdure������proc��������ʾ��
                        ;
                    }       //if����Ϊproc
                    else{       //cal���Ͳ�һ�µĴ���
                        errorHapphen=true;
                        showError(11,"");
                        return;
                    }
                }       //if���ű��д��ڱ�ʶ��
                else{           //cal δ��������Ĵ���
                    errorHapphen=true;
                    showError(10,"");
                    return;
                }
                terPtr++;
                if(rv[terPtr].getId()==LBR){
                    terPtr++;
                    if(rv[terPtr].getId()==RBR){
                        terPtr++;
                        Pcode.gen(Pcode.getCAL(),level-tempRow.getLevel(),tempRow.getValue());        //���ù����еı����ֳ��ɽ��ͳ�����ɣ�����ֻ����Ŀ�����,+3����ϸ˵��
                    }else{
                        exp();
                        count++;
                        while(rv[terPtr].getId()==COMMA){
                            terPtr++;
                            exp();
                            count++;
                        }
                        if(count!=tempRow.getSize()){
                            errorHapphen=true;
                            showError(16,tempRow.getName());
                            return;
                        }
                        Pcode.gen(Pcode.getCAL(),level-tempRow.getLevel(),tempRow.getValue());        //���ù����еı����ֳ��ɽ��ͳ�����ɣ�����ֻ����Ŀ�����,+3����ϸ˵��
                        if(rv[terPtr].getId()==RBR){
                            terPtr++;
                        }else{
                            errorHapphen=true;
                            showError(5,"");
                            return;
                        }
                    }
                }else{
                    errorHapphen=true;
                    showError(4,"");
                    return;
                }
            }else{
                errorHapphen=true;
                showError(1,"");
                return;
            }

        }else if(rv[terPtr].getId()==REA){
            terPtr++;
            if(rv[terPtr].getId()==LBR){
                terPtr++;
                //      id();
                if(rv[terPtr].getId()==SYM){
                    if(!STable.isPreExistSTable((rv[terPtr].getValue()),level)){      //�����ж��ڷ��ű����ڱ���򱾲�֮ǰ�Ƿ��д˱���
                        errorHapphen=true;
                        showError(10,"");
                        return;

                    }//if�ж��ڷ��ű����Ƿ��д˱���
                    else{           //stoδ��������Ĵ���
                        TableRow tempTable=STable.getRow(STable.getNameRow(rv[terPtr].getValue()));
                        if(tempTable.getType()==STable.getVar()){       //�ñ�ʶ���Ƿ�Ϊ��������
                            Pcode.gen(Pcode.getOPR(),0,16);         //OPR 0 16	�������ж���һ����������ջ��   //���ĺ������ڣ���ֱ����Ƕ�׵Ĳ������Ϊ������������
                            Pcode.gen(Pcode.getSTO(),level-tempTable.getLevel(),tempTable.getAddress());  //STO L ��a ������ջջ�������ݴ����������Ե�ַΪa����β�ΪL��
                        }//if��ʶ���Ƿ�Ϊ��������
                        else{       //sto���Ͳ�һ�µĴ���
                            errorHapphen=true;
                            showError(12,"");
                            return;
                        }
                    }
                    terPtr++;
                    while(rv[terPtr].getId()==COMMA){
                        terPtr++;
                        if(rv[terPtr].getId()==SYM){
                            if(!STable.isPreExistSTable((rv[terPtr].getValue()),level)){      //�����ж��ڷ��ű����Ƿ��д˱���
                                errorHapphen=true;
                                showError(10,"");
                                return;

                            }//if�ж��ڷ��ű����Ƿ��д˱���
                            else{           //stoδ��������Ĵ���
                                TableRow tempTable=STable.getRow(STable.getNameRow(rv[terPtr].getValue()));
                                if(tempTable.getType()==STable.getVar()){       //�ñ�ʶ���Ƿ�Ϊ��������
                                    Pcode.gen(Pcode.getOPR(),0,16);         //OPR 0 16	�������ж���һ����������ջ��   //���ĺ������ڣ���ֱ����Ƕ�׵Ĳ������Ϊ������������
                                    Pcode.gen(Pcode.getSTO(),level-tempTable.getLevel(),tempTable.getAddress());  //STO L ��a ������ջջ�������ݴ����������Ե�ַΪa����β�ΪL��
                                }//if��ʶ���Ƿ�Ϊ��������
                                else{       //sto���Ͳ�һ�µĴ���
                                    errorHapphen=true;
                                    showError(12,"");
                                    return;
                                }
                            }
                            terPtr++;
                        }else{
                            errorHapphen=true;
                            showError(1,"");
                            return;
                        }
                    }
                    if(rv[terPtr].getId()==RBR){
                        terPtr++;
                    }else{
                        errorHapphen=true;
                        showError(25,"");
                    }
                }else{
                    errorHapphen=true;
                    showError(26,"");
                }
            }else{
                errorHapphen=true;
                showError(4,"");
                return;
            }
        }else if(rv[terPtr].getId()==WRI){
            terPtr++;
            if(rv[terPtr].getId()==LBR){
                terPtr++;
                exp();
                Pcode.gen(Pcode.getOPR(),0,14);         //���ջ����ֵ����Ļ
                while(rv[terPtr].getId()==COMMA){
                    terPtr++;
                    exp();
                    Pcode.gen(Pcode.getOPR(),0,14);         //���ջ����ֵ����Ļ
                }

                Pcode.gen(Pcode.getOPR(),0,15);         //�������
                if(rv[terPtr].getId()==RBR){
                    terPtr++;
                }else{
                    errorHapphen=true;
                    showError(5,"");
                    return;
                }
            }else{
                errorHapphen=true;
                showError(4,"");
                return;
            }
        }else if(rv[terPtr].getId()==BEG){//����Ҳû��terPtr++;          //body������Ŀ�����
            body();
        }else if(rv[terPtr].getId()==SYM){      //��ֵ���
            String name=rv[terPtr].getValue();
            terPtr++;
            if(rv[terPtr].getId()==CEQU){
                terPtr++;
                exp();
                if(!STable.isPreExistSTable(name,level)){        //����ʶ���Ƿ��ڷ��ű��д���
                    errorHapphen=true;
                    showError(14,name);
                    return;
                }//if�ж��ڷ��ű����Ƿ��д˱���
                else{           //stoδ��������Ĵ���
                    TableRow tempTable=STable.getRow(STable.getNameRow(name));
                    if(tempTable.getType()==STable.getVar()){           //����ʶ���Ƿ�Ϊ��������
                        Pcode.gen(Pcode.getSTO(),level-tempTable.getLevel(),tempTable.getAddress());  //STO L ��a ������ջջ�������ݴ������
                    }////����ʶ���Ƿ�Ϊ��������
                    else{       //���Ͳ�һ�µĴ���
                        errorHapphen=true;
                        showError(13,name);
                        return;
                    }
                }
            }else{
                errorHapphen=true;
                showError(3,"");
                return;
            }
        }else{
            errorHapphen=true;
            showError(1,"");
            return;
        }
    }

    public void lexp(){
        if(rv[terPtr].getId()==ODD){
            terPtr++;
            exp();
            Pcode.gen(Pcode.getOPR(),0,6);  //OPR 0 6	ջ��Ԫ�ص���ż�жϣ����ֵ��ջ��
        }else{
            exp();
            int loperator=lop();        //����ֵ��������Ŀ����룬����
            exp();
            if(loperator==EQU){
                Pcode.gen(Pcode.getOPR(),0,8);      //OPR 0 8	��ջ����ջ���Ƿ���ȣ�������ջԪ�أ����ֵ��ջ
            }else if(loperator==NEQE){
                Pcode.gen(Pcode.getOPR(),0,9);      //OPR 0 9	��ջ����ջ���Ƿ񲻵ȣ�������ջԪ�أ����ֵ��ջ
            }else if(loperator==LES){
                Pcode.gen(Pcode.getOPR(),0,10);     //OPR 0 10	��ջ���Ƿ�С��ջ����������ջԪ�أ����ֵ��ջ
            }else if(loperator==LESE){
                Pcode.gen(Pcode.getOPR(),0,13);     // OPR 0 13	��ջ���Ƿ�С�ڵ���ջ����������ջԪ�أ����ֵ��ջ
            }else if(loperator==LAR){
                Pcode.gen(Pcode.getOPR(),0,12);     //OPR 0 12	��ջ���Ƿ����ջ����������ջԪ�أ����ֵ��ջ
            }else if(loperator==LARE){
                Pcode.gen(Pcode.getOPR(),0,11);     //OPR 0 11	��ջ���Ƿ���ڵ���ջ����������ջԪ�أ����ֵ��ջ
            }
        }
    }

    public void exp(){
        int tempId=rv[terPtr].getId();
        if(rv[terPtr].getId()==ADD){
            terPtr++;
        }else if(rv[terPtr].getId()==SUB){
            terPtr++;
        }
        term();
        if(tempId==SUB){
            Pcode.gen(Pcode.getOPR(),0,1);      //  OPR 0 1	ջ��Ԫ��ȡ��
        }
        while(rv[terPtr].getId()==ADD||rv[terPtr].getId()==SUB){
            tempId=rv[terPtr].getId();
            terPtr++;
            term();
            if(tempId==ADD){
                Pcode.gen(Pcode.getOPR(),0,2);       //OPR 0 2	��ջ����ջ����ӣ�������ջԪ�أ����ֵ��ջ
            }else if(tempId==SUB){
                Pcode.gen(Pcode.getOPR(),0,3);      //OPR 0 3	��ջ����ȥջ����������ջԪ�أ����ֵ��ջ
            }
        }
    }

    public void term(){
        factor();
        while(rv[terPtr].getId()==MUL||rv[terPtr].getId()==DIV){
            int tempId=rv[terPtr].getId();
            terPtr++;
            factor();
            if(tempId==MUL){
                Pcode.gen(Pcode.getOPR(),0,4);       //OPR 0 4	��ջ������ջ����������ջԪ�أ����ֵ��ջ
            }else if(tempId==DIV){
                Pcode.gen(Pcode.getOPR(),0,5);      // OPR 0 5	��ջ������ջ����������ջԪ�أ����ֵ��ջ
            }
        }
    }

    public void factor(){
        if(rv[terPtr].getId()==CONST){
            Pcode.gen(Pcode.getLIT(),0,Integer.parseInt(rv[terPtr].getValue()));    //�Ǹ�����,  LIT 0 a ȡ����a��������ջջ��
            terPtr++;
        }else if(rv[terPtr].getId()==LBR){
            terPtr++;
            exp();
            if(rv[terPtr].getId()==RBR){
                terPtr++;
            }else{
                errorHapphen=true;
                showError(5,"");
            }
        }else if(rv[terPtr].getId()==SYM){
            String name=rv[terPtr].getValue();
            if(!STable.isPreExistSTable(name,level)){     //�жϱ�ʶ���ڷ��ű����Ƿ����
                errorHapphen=true;
                showError(10,"");
                return;
            }//if�ж��ڷ��ű����Ƿ��д˱���
            else{           //δ��������Ĵ���
                TableRow tempRow= STable.getRow(STable.getNameRow(name));
                if(tempRow.getType()==STable.getVar()){ //��ʶ���Ǳ�������
                    Pcode.gen(Pcode.getLOD(),level-tempRow.getLevel(),tempRow.getAddress());    //������LOD L  ȡ��������Ե�ַΪa�����ΪL���ŵ�����ջ��ջ��
                }else if (tempRow.getType()==STable.getMyconst()){
                    Pcode.gen(Pcode.getLIT(),0,tempRow.getValue());         //������LIT 0 a ȡ����a��������ջջ��
                }
                else{       //���Ͳ�һ�µĴ���
                    errorHapphen=true;
                    showError(12,"");
                    return;
                }
            }
            terPtr++;
        }else {
            errorHapphen=true;
            showError(1,"");
        }
    }

    public int lop(){
        String loperator;
        if(rv[terPtr].getId()==EQU){
            terPtr++;
            return EQU;
        }else if(rv[terPtr].getId()==NEQE){
            terPtr++;
            return NEQE;
        }else if(rv[terPtr].getId()==LES){
            terPtr++;
            return LES;
        }else if(rv[terPtr].getId()==LESE){
            terPtr++;
            return LESE;
        }else if(rv[terPtr].getId()==LAR){
            terPtr++;
            return LAR;
        }else if(rv[terPtr].getId()==LARE){
            terPtr++;
            return LARE;
        }
        return -1;
    }

    // public void id(){

    //    }
    public boolean mgpAnalysis(){
        lex.bAnalysis();
        readLex();
        prog();
        return errorHapphen;
    }

    public void showtable(){
        System.out.println("type,name,level,address,value,size");
        for(int i=0;i<STable.getLength();i++){
            System.out.println(STable.getRow(i).getType()+"  "+ STable.getRow(i).getName()+"  "+STable.getRow(i).getLevel()+"  "+STable.getRow(i).getAddress()+"  "+STable.getRow(i).getValue()+
            "  "+STable.getRow(i).getSize());
        }
    }

    public void showPcode(){
        for(int i=0;i<Pcode.getCodePtr();i++){
           switch (Pcode.getPcodeArray()[i].getF()){
               case 0:
                   System.out.print("LIT  ");
                   break;
               case 1:
                   System.out.print("OPR  ");
                   break;
               case 2:
                   System.out.print("LOD  ");
                   break;
               case 3:
                   System.out.print("STO  ");
                   break;
               case 4:
                   System.out.print("CAL  ");
                   break;
               case 5:
                   System.out.print("INT  ");
                   break;
               case 6:
                   System.out.print("JMP  ");
                   break;
               case 7:
                   System.out.print("JPC  ");
                   break;
               case 8:
                   System.out.print("RED  ");
                   break;
               case 9:
                   System.out.print("WRI  ");
                   break;
           }
            System.out.println(Pcode.getPcodeArray()[i].getL()+"  "+Pcode.getPcodeArray()[i].getA());
        }
    }

    public void showPcodeInStack(){
        Interpreter inter=new Interpreter();
        inter.setPcode(Pcode);
        for(int i=0;i<inter.getCode().getCodePtr();i++){
            switch (inter.getCode().getPcodeArray()[i].getF()){
                case 0:
                    System.out.print("LIT  ");
                    break;
                case 1:
                    System.out.print("OPR  ");
                    break;
                case 2:
                    System.out.print("LOD  ");
                    break;
                case 3:
                    System.out.print("STO  ");
                    break;
                case 4:
                    System.out.print("CAL  ");
                    break;
                case 5:
                    System.out.print("INT  ");
                    break;
                case 6:
                    System.out.print("JMP  ");
                    break;
                case 7:
                    System.out.print("JPC  ");
                    break;
                case 8:
                    System.out.print("RED  ");
                    break;
                case 9:
                    System.out.print("WRI  ");
                    break;

            }
            System.out.println(inter.getCode().getPcodeArray()[i].getL()+"  "+inter.getCode().getPcodeArray()[i].getA());
        }


    }

    public void showError(int i,String name){
        switch (i){
            case -1:
                System.out.print("ERROR "+i+" "+"in line " + rv[terPtr].getLine()+":");
                System.out.println("wrong token");        //�������岻��const��ͷ,�������岻��var ��ͷ
                break;
            case 0:
                System.out.print("ERROR "+i+" "+"in line " + (rv[terPtr].getLine()-1)+":");
                System.out.println("Missing semicolon");        //ȱ�ٷֺ�
                break;
            case 1:
                System.out.print("ERROR "+i+" "+"in line " + rv[terPtr].getLine()+":");
                System.out.println("Identifier illegal");       //��ʶ�����Ϸ�
                break;
            case 2:
                System.out.print("ERROR "+i+" "+"in line " + rv[terPtr].getLine()+":");
                System.out.println("The beginning of program must be 'program'");       //����ʼ��һ���ַ�������program
                break;
            case 3:
                System.out.print("ERROR "+i+" "+"in line " + rv[terPtr].getLine()+":");
                System.out.println("Assign must be ':='");       //��ֵû�ã�=
                break;
            case 4:
                System.out.print("ERROR "+i+" "+"in line " + rv[terPtr].getLine()+":");
                System.out.println("Missing '('");       //ȱ��������
                break;
            case 5:
                System.out.print("ERROR "+i+" "+"in line " + rv[terPtr].getLine()+":");
                System.out.println("Missing ')'");       //ȱ��������
                break;
            case 6:
                System.out.print("ERROR "+i+" "+"in line " + rv[terPtr].getLine()+":");
                System.out.println("Missing 'begin'");       //ȱ��begin
                break;
            case 7:
                System.out.print("ERROR "+i+" "+"in line " + rv[terPtr].getLine()+":");
                System.out.println("Missing 'end'");       //ȱ��end
                break;
            case 8:
                System.out.print("ERROR "+i+" "+"in line " + rv[terPtr].getLine()+":");
                System.out.println("Missing 'then'");       //ȱ��then
                break;
            case 9:
                System.out.print("ERROR "+i+" "+"in line " + rv[terPtr].getLine()+":");
                System.out.println("Missing 'do'");       //ȱ��do
                break;
            case 10:
                System.out.print("ERROR "+i+" "+"in line " + rv[terPtr].getLine()+":");
                System.out.println("Not exist "+"'"+rv[terPtr].getValue()+"'");       //call��write��read����У������ڱ�ʶ��
                break;
            case 11:
                System.out.print("ERROR "+i+" "+"in line " + rv[terPtr].getLine()+":");
                System.out.println("'"+rv[terPtr].getValue()+"'"+"is not a procedure");       //�ñ�ʶ������proc����
                break;
            case 12:
                System.out.print("ERROR "+i+" "+"in line " + rv[terPtr].getLine()+":");
                System.out.println("'"+rv[terPtr].getValue()+"'"+"is not a variable");       //read��write����У��ñ�ʶ������var����
                break;
            case 13:
                System.out.print("ERROR "+i+" "+"in line " + rv[terPtr].getLine()+":");
                System.out.println("'"+name+"'"+"is not a variable");       //��ֵ����У��ñ�ʶ������var����
                break;
            case 14:
                System.out.print("ERROR "+i+" "+"in line " + rv[terPtr].getLine()+":");
                System.out.println("Not exist"+"'"+name+"'");       //��ֵ����У��ñ�ʶ��������
                break;
            case 15:
                System.out.print("ERROR "+i+" "+"in line " + rv[terPtr].getLine()+":");
                System.out.println("Already exist"+"'"+name+"'");       //�ñ�ʶ���Ѿ�����
                break;
            case 16:
                System.out.print("ERROR "+i+" "+"in line " + rv[terPtr].getLine()+":");
                System.out.println("Number of parameters of procedure "+"'"+name+"'"+"is incorrect");       //�ñ�ʶ���Ѿ�����
                break;
        }

    }
    public void interpreter(){
        if(errorHapphen){
            return;
        }
        Interpreter inter=new Interpreter();
        inter.setPcode(Pcode);
        inter.interpreter();
    }

}

