/**
 * Created by zhf on 2015/12/22.
 * ��δ�жϳ���Maxֵ�����Ĵ���
 * SymTable���ű�ÿһ�еľ��庬���TableRow
 */
public class SymbolTable {

    private int rowMax=10000;           //����
    private int valueMax=100000;        //����������ֵ
    private int levelMax=3;                 //����Ƕ�ײ��
    private int addressMax=10000;       //����ַ��



    private int myconst=1;                  //����������1��ʾ
    private int var=2;                      //����������2��ʾ
    private int proc=3;             //����������3��ʾ

    //TableRow�Ƿ��ű��е�ÿһ��
    //tablePtrָ����ű����Ѿ�����ֵ���һ�����һ��
    //length��ʾ���ű������룻�˶��������ݣ�ʵ���Ͽ�����tablePtr����ʾ
    private TableRow[] table=new TableRow[rowMax];          //rowMax��


    private int tablePtr=0;
    private int length=0;


    public void setTablePtr(int tablePtr) {
        this.tablePtr = tablePtr;
    }
    //��ʼ����ȫ��Ϊ0
    public SymbolTable(){
        for(int i=0;i<rowMax;i++){
            table[i]=new TableRow();
            table[i].setAddress(0);
            table[i].setLevel(0);
            table[i].setSize(0);
            table[i].setType(0);
            table[i].setValue(0);
            table[i].setName(null);
        }
    }

    public int getVar() {
        return var;
    }

    public int getMyconst() {
        return myconst;
    }

    public int getProc() {
        return proc;
    }

    public int getLength(){
        return length;
    }

    //��ȡ���ű��е�i��
    public TableRow getRow(int i){
        return table[i];
    }
    /*
      *��¼���������ű�
      * ������
      * name��������
      * level�����ڲ��
      * value��ֵ
      * address����������ڲ�λ���ַ�ĵ�ַ
    */
    public void enterConst(String name,int level,int value,int address){
        table[tablePtr].setName(name);
        table[tablePtr].setLevel(level);
        table[tablePtr].setValue(value);
        table[tablePtr].setAddress(address);
        table[tablePtr].setType(myconst);
        table[tablePtr].setSize(4);
        tablePtr++;
        length++;
    }



    /*
     *    ��¼���������ű�
     *  ����ͬ��
     *  ˵�������ڵ�¼���ű���������ڱ�������������������������е��ã���PL/0��֧�ֱ�������ʱ��ֵ�����Բ��������value
     *
    */
    public void enterVar(String name,int level,int address){
        table[tablePtr].setName(name);
        table[tablePtr].setLevel(level);
        table[tablePtr].setAddress(address);
        table[tablePtr].setType(var);
        table[tablePtr].setSize(0);
        tablePtr++;
        length++;
    }
    //��¼���̽����ű�����ͬ��
    public void enterProc(String name,int level,int address){
        table[tablePtr].setName(name);
        table[tablePtr].setLevel(level);
        table[tablePtr].setAddress(address);
        table[tablePtr].setType(proc);
        table[tablePtr].setSize(0);
        tablePtr++;
        length++;
    }

    //��lev��֮ǰ������lev�㣬����Ϊname�ı�����������������Ƿ񱻶��壬
    //ʹ�ñ����������������ʱ���øú���
    public boolean isPreExistSTable(String name,int lev){
        for(int i=0;i<length;i++){
            if(table[i].getName().equals(name)&&table[i].getLevel()<=lev){
                return true;
            }
        }
        return false;
    }

    //��lev�㣬����Ϊname�ı�����������������Ƿ񱻶��壬
    //��������������������ʱ���øú���
    public boolean isNowExistSTable(String name,int lev){
        for(int i=0;i<length;i++){
            if(table[i].getName().equals(name)&&table[i].getLevel()==lev){
                return true;
            }
        }
        return false;
    }


    //���ط��ű�������Ϊname���е��к�
    public int  getNameRow(String name){
        for(int i=length-1;i>=0;i--){
            if(table[i].getName().equals(name)){
                return i;
            }
        }
        return -1;          //����-1��ʾ�����ڸ�����
    }
    public int getTablePtr() {
        return tablePtr;
    }

    public TableRow[] getAllTable(){
        return table;
    }

    //���ұ���Ĺ����ڷ��ű��е�λ��
    public int getLevelPorc(int level){
        for(int i=length-1;i>=0;i--){
            if(table[i].getType()==proc){
                return i;
            }
        }
        return -1;
    }

}
