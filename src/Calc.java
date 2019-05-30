import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Stack;

public class Calc extends JFrame{
	private Stack<Double> operandStack= new Stack<>();
    private Stack<String> operatorStack = new Stack<>();
    //����
    private Calc(){
        setTitle("������");	//���ڱ���
        setSize(400,480);	//���ڴ�С
        setLocation(500,100);
        Container c=getContentPane();
        c.setLayout(null);	//���ָ�ʽΪ��
        setResizable(false);//���ý�ֹ�ı䴰�ڴ�С
        JTextArea jt=new JTextArea(100,100);	//�����ı���
        jt.setFont(new Font("Aria",Font.BOLD,30));//�����ı�������
        jt.setLineWrap(true);					//�����Զ�����
        JScrollPane sp=new JScrollPane(jt);			
        jt.setCaretPosition(jt.getDocument().getLength());
        sp.setBounds(0,0,405,100);		//�����ı����С
        c.add(sp);
        JPanel p=new JPanel();
        p.setLayout(new GridLayout(5,4,0,0));//����5��4�в���
        p.setBounds(0,100,395,345);	//��������С				
        String[] num={"(",")","AC","+","7","8","9","-","4",
        		"5","6","x","1","2","3","��","0",".","DEL","="};
        JButton[] jb=new JButton[20];		//��Ӱ�ť
        Font f=new Font("����",Font.BOLD,25);	//���ð�ť����	
        for(int i=0;i<20;i++){
            jb[i]=new JButton(num[i]);
            jb[i].setFont(f);
            p.add(jb[i]);
        }
        c.add(p);
        for(int i=0;i<18;i++){
            if(i!=2){
                final int j=i;
                jb[i].addActionListener(e-> jt.append(num[j]));
            }
        }
        jb[2].addActionListener(e->{
            jt.setText("");
            operandStack.clear();
            operatorStack.clear();
        });
        jb[18].addActionListener(e->{
            try{
                jt.setText(jt.getText().substring(0,jt.getText().length()-1));
            }catch(Exception ignored) { }
        });
        jb[19].addActionListener(e->{
            try{
                double x= calculate(jt.getText()+"#");
                jt.setText("");
	//ɾ��С�����������0
                DecimalFormat decimalFormat = new DecimalFormat();
                jt.append(String.valueOf(decimalFormat.format(x)));
            }catch(Exception ex){
                if(ex.getMessage()==null)
                    jt.setText("ERROR!");
                else
                    jt.setText(ex.getMessage());
            }
        });
        //��ֹ�ı����enter����
        KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
        jt.getInputMap().put(enter, "none");
        this.getRootPane().setDefaultButton(jb[19]);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    //���㣨�������ż��������ջ��
    //Ψһ���㣺�����������������������ΪΪ����ʱ����֪������ô����
    private void calculate(){
        String b = operatorStack.pop();
        Double c = operandStack.pop();
        Double d = operandStack.pop();
        BigDecimal b1 = new BigDecimal(Double.toString(d));  
        BigDecimal b2 = new BigDecimal(Double.toString(c)); 
        Double e;			//��BigDecimal����мӼ��˳���
        if (b.equals("+")) {//ֱ����double���ͼ���ᾫ�ȶ�ʧ
            e=b1.add(b2).doubleValue();		//��
            operandStack.push(e);
        }
        if (b.equals("-")) {
            e = b1.subtract(b2).doubleValue();	//��
            operandStack.push(e);
        }
        if (b.equals("x")) {
            e = b1.multiply(b2).doubleValue();	//��
            operandStack.push(e);
        }
        if (b.equals("��")) {
            if(c==0)
                throw new ArithmeticException("DivideByZero!");
            e=b1.divide(b2, 15, 
            RoundingMode.HALF_UP).doubleValue();//��
            operandStack.push(e);
        }
    }
    private Double calculate(String text){			//��������
        HashMap<String,Integer> precede=new HashMap<>();
        precede.put("(",0);
        precede.put(")",0);
        precede.put("��",2);
        precede.put("x",2);
        precede.put("-",1);
        precede.put("+",1);
        precede.put("#",0);
        operatorStack.push("#");
        int flag=0;
        for(int i=0;i<text.length();i++){
            String a=String.valueOf(text.charAt(i));
            if(!a.matches("[0-9.]")){
                if(flag!=i)
                    operandStack.push(Double.parseDouble(text.substring(flag,i)));
                flag=i+1;
                while(!(a.equals("#")&&operatorStack.peek().equals("#"))){
                    if(precede.get(a)>precede.get(operatorStack.peek())||a.equals("(")){
                        operatorStack.push(a);
                        break;
                    }else {
                        if(a.equals(")")) {
                            while(!operatorStack.peek().equals("("))
                                calculate();
                            operatorStack.pop();
                            break;
                        }
                        calculate();
                    }
                }

            }
        }
        return(operandStack.pop());
    }
    public static void main(String[] args){
        new Calc();
    }
}