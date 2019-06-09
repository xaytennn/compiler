package ru.eltex.Main;
import java.io.*;
import java.util.*;


public class Lexer {
    /**
     *       Лексер
     *____________________________________________________________________________________________________________
     */

    static String buf = "";
    static Type str1;
    static int row = 1;
    static int col = 1, j = 0;
    static int i = 0;
    static boolean flag = false;
    public static String  buf2="";

    public enum Type {
        FOR,
        INT,
        LPAREN,
        RPAREN,
        IF,
        WHILE,
        INPUT,
        INCREMENT,
        DECREMENT,
        DESCRIPTION,
        LBRACET,
        RBRACET,
        CHAR,
        UNKNOWN,
        MAIN,
        LBRACE,
        RBRACE,
        SEMI,
        EOF,
        RETURN,
        LITERAL,
        NUMERIC,
        ID,
        SYMBOL,
        IDENTIFIER;
    }
    public static class Token {
        public final Type t;
        public String c = "";
        public int i;
        public int row;
        public int col;


        public Token(Type t, String c, int i, int row, int col) {
            this.t = t;
            this.c = c;
            this.i = i;
            this.row = row;
            this.col = col;
        }

        public String toString() {
            //return  t.toString() + " '" + c + "' " + "Loc=<hh.c:" + row + ":" + col + ">" ;
            return  t.toString() + " '" + c + "' ";
        }
        public Token(Type t, String c){
            this.t = t;
            this.c = c;
        }
        public Token(Type t){
            this.t = t;
        }
        public Type getToken() {
            return t;
        }
        public String getValue() {
            return c;
        }
        public int getRow(){return row;}
        public int getCol(){return col;}
        public int getI() {
            return i;
        }
    }

    public static List<Token> lex(String input) {
        List<Token> result = new ArrayList<Token>();
        //читаю до конца строки
        for (int i = 0; i < input.length();){

            //проверка на комменты
             if(input.charAt(i) == '/' && input.charAt(i+1) == '/'){
                while(i < input.length()){
                    i++;
                    continue;
                }
            }
            //проверка литерал
            else if(input.charAt(i) == '"') {
                flag = false;
                buf="";
                buf+=input.charAt(i);
                col = i+1;
                i++;
                while(input.charAt(i) != '"'){
                    buf+=input.charAt(i);
                    i++;
                    flag = true;
                }
                if(flag) {
                    buf+='"';
                    str1 = Eq(buf);
                    result.add(new Token(str1, buf, i, row, col));
                }
            }
            else if(Character.isDigit(input.charAt(i))){
                flag = false;
                col = i+1;
                buf="";
                for (int j = i; j < input.length(); j++) {
                    if (input.charAt(j) == ' ') break;
                    if (Character.isDigit(input.charAt(j))) i++;
                    else if(input.charAt(j)=='.'){
                        i++;//??
                    }
                    else if (Character.isLetter(input.charAt(j))) {
                        buf += input.charAt(j);
                        result.add(new Token(Type.UNKNOWN, buf, i, row, col));
                        flag = true;
                        break;
                    }
                    else if(!Character.isLetter(input.charAt(j)) && !Character.isDigit(input.charAt(j))){
                        i--;
                        break;
                    }
                    buf += input.charAt(j);
                }
                if(!flag) {
                    str1 = Eq(buf);
                    result.add(new Token(str1, buf, i, row, col));
                }
            }
            else if(input.charAt(i) == ';'){
                 col = i+1;
                 result.add(new Token(Type.SEMI, ";", i, row, col));
             }
            else if(input.charAt(i) == '('){
                col = i+1;
                result.add(new Token(Type.LPAREN, "(", i, row, col));
            }
            else if(input.charAt(i) == ')'){
                col = i+1;
                result.add(new Token(Type.RPAREN, ")", i, row, col));
            }
            else if(input.charAt(i) == '{'){
                 col = i+1;
                 result.add(new Token(Type.LBRACE, "{", i, row, col));
             }
             else if(input.charAt(i) == '['){
                 col = i+1;
                 result.add(new Token(Type.LBRACET, "[", i, row, col));
             }
             else if(input.charAt(i) == ']'){
                 col = i+1;
                 result.add(new Token(Type.RBRACET, "]", i, row, col));
             }
            else if(input.charAt(i) == '}'){
                 col = i+1;
                 result.add(new Token(Type.RBRACE, "}", i, row, col));
             }
             else if(input.charAt(i) == '-' && input.charAt(i+1) == '-'){
                 buf2="";
                 col = i+1;
                 buf2+=input.charAt(i);
                 buf2+=input.charAt(i+1);
                 result.add(new Token(Type.DECREMENT, buf2, i, row, col));
                 i++;//
             }
            else if(input.charAt(i) == '+' && input.charAt(i+1) == '+'){
                 buf2="";
                 col = i+1;
                 buf2+=input.charAt(i);
                 buf2+=input.charAt(i+1);
                 result.add(new Token(Type.INCREMENT, buf2, i, row, col));
                 i++;//
             }
             else if(input.charAt(i) == '<' || input.charAt(i) == '>' || input.charAt(i) == '=' || input.charAt(i) == '-' ||
                     input.charAt(i) == '*' || input.charAt(i) == '/' || input.charAt(i) == '+' || input.charAt(i)==','
                    || input.charAt(i) == '!' || input.charAt(i)=='%'){
                 buf2="";
                 col = i+1;
                 buf2+=input.charAt(i);
                 result.add(new Token(Type.SYMBOL, buf2, i, row, col));
             }
            else if(Character.isLetter(input.charAt(i))){
                 col = i+1;
                 buf = "";
                 while(i < input.length() && input.charAt(i) != ' ' && (Character.isLetter(input.charAt(i)) || Character.isDigit(input.charAt(i)))){
                     buf += input.charAt(i);
                     i++;
                 }
                 i--;//
                 str1 = Eq(buf);
                 result.add(new Token(str1, buf, i, row, col));
             }
        col = 1;
        i++;

        }

        return result;
    }

    /**
     *       Parser
     *____________________________________________________________________________________________________________
     */

    public static int getRule(String terminalName, String noterminal, String nextTerminalName){

        if(terminalName.equals("INT")&& nextTerminalName.equals("MAIN"))
            return 1;
        else if(terminalName.equals("IDENTIFIER")){
            if(noterminal.equals("printf")&& nextTerminalName.equals("LPAREN"))
                return 2;
        }
        else if(terminalName.equals("SEMI") && (nextTerminalName.equals("NUMERIC") || nextTerminalName.equals("LITERAL")
                ||  nextTerminalName.equals("LPAREN")
                || nextTerminalName.equals("RPAREN") || nextTerminalName.equals("LBRACET")
                || nextTerminalName.equals("RBRACET") || nextTerminalName.equals("LBRACE")))
            return 0;
        else if(terminalName.equals("MAIN") && nextTerminalName.equals("LPAREN"))
            return 17;
        else if(terminalName.equals("LPAREN")&&(nextTerminalName.equals("LITERAL")))
            return 3;
        else if(terminalName.equals("LPAREN")&&(nextTerminalName.equals("INT")))
            return 3;
        else if(terminalName.equals("LPAREN"))
            return 3;
        else if(terminalName.equals("NUMERIC")&& (nextTerminalName.equals("SEMI") || nextTerminalName.equals("RBRACET")
                || nextTerminalName.equals("RPAREN") || nextTerminalName.equals("SYMBOL")))
            return 10;
        else if(terminalName.equals("RPAREN") && (nextTerminalName.equals("LBRACE") || nextTerminalName.equals("SEMI")
                || nextTerminalName.equals("RPAREN") ||nextTerminalName.equals("LPAREN")
                || nextTerminalName.equals("SYMBOL") || nextTerminalName.equals("ID")))
            return 4;
        else if(terminalName.equals("LBRACE")&& nextTerminalName.equals("INT"))
            return 5;
        else if(terminalName.equals("LBRACE")&& nextTerminalName.equals("CHAR"))
            return 5;
        else if(terminalName.equals("LBRACE")&& nextTerminalName.equals("IDENTIFIER"))
            return 5;
        else if(terminalName.equals("LBRACE")&& nextTerminalName.equals("RETURN"))
            return 5;
        else if(terminalName.equals("LBRACE"))
            return 5;
        else if(terminalName.equals("RBRACE"))
            return 6;
        else if(terminalName.equals("LITERAL") && (nextTerminalName.equals("SEMI") || nextTerminalName.equals("SYMBOL")
                || nextTerminalName.equals("RPAREN")))
            return 7;
        else if(terminalName.equals("SEMI") && nextTerminalName.equals("SEMI"))
            return 0;
        else if(terminalName.equals("SEMI"))
            return 8;
        else if(terminalName.equals("LBRACET") && (nextTerminalName.equals("NUMERIC") || nextTerminalName.equals("RBRACET")||nextTerminalName.equals("ID")))
            return 15;
        else if(terminalName.equals("RBRACET") && (nextTerminalName.equals("SYMBOL") || nextTerminalName.equals("RPAREN")))
            return 16;
        else if(terminalName.equals("RETURN") && nextTerminalName.equals("NUMERIC"))
            return 9;
/*        else if(terminalName.equals("NUMERIC"))
            return 10;*/
        else if(terminalName.equals("INT") && (nextTerminalName.equals("ID") || nextTerminalName.equals("INPUT")))
            return 1;
        else if(terminalName.equals("CHAR") && (nextTerminalName.equals("ID") || nextTerminalName.equals("SYMBOL")))
            return 18;
        else if(terminalName.equals("FOR")&& nextTerminalName.equals("LPAREN"))
            return 12;
        else if(terminalName.equals("ID") && (nextTerminalName.equals("SEMI") || nextTerminalName.equals("LBRACET")
                ||  nextTerminalName.equals("RPAREN") || nextTerminalName.equals("LPAREN")
                || nextTerminalName.equals("SYMBOL") || nextTerminalName.equals("INCREMENT")
                || nextTerminalName.equals("DECREMENT") || nextTerminalName.equals("ID") || nextTerminalName.equals("RBRACET")))
            return 11;
        else if(terminalName.equals("SYMBOL") && (nextTerminalName.equals("NUMERIC") || nextTerminalName.equals("LITERAL") ||
                nextTerminalName.equals("ID")|| nextTerminalName.equals("LPAREN") || nextTerminalName.equals("INT") ||
                nextTerminalName.equals("CHAR") || nextTerminalName.equals("SYMBOL") || nextTerminalName.equals("INPUT") ))
            return 13;
        else if(terminalName.equals("INCREMENT") && (nextTerminalName.equals("RPAREN") || nextTerminalName.equals("SEMI")))
            return 14;
        else if(terminalName.equals("IF")&& nextTerminalName.equals("LPAREN"))
            return 15;
        else if(terminalName.equals("INPUT")&& (nextTerminalName.equals("SYMBOL") || nextTerminalName.equals("LBRACET")))
            return 16;
        else if(terminalName.equals("WHILE")&& (nextTerminalName.equals("LPAREN")))
            return 17;
        else
            return 0;

        return 0;//!!!!!!!!!!!!!!Переделай(просто так стоит)
    }

    /**
     *      Tree
     * ____________________________________________________________________________________________________________
     */

    public static class BinaryTree {

        static class Node {
            String value;
            String terminalName;
            Node left;
            Node right;

            Node(String terminalName, String value) {
                this.value = value;
                this.terminalName = terminalName;
                right = null;
                left = null;
            }

        }
        Node root;
        public Node addRecursive(Node current, String terminalName, String value, String side) {
            if (current == null) {
                return new Node(terminalName, value);
            }
            if(side.equals("left"))
                current.left = addRecursive(current.left, terminalName, value, "left");
            if(side.equals("right"))
                current.right = addRecursive(current.right, terminalName, value, "right");

            return current;
        }
        public void add(String terminalName, String value, String side) {
            root = addRecursive(root, terminalName, value, side);
        }
        public void traversePreOrder(Node node) {
            if (node != null) {
                System.out.println(" " +node.terminalName + " '" + node.value + "' ");
                traversePreOrder(node.left);
                traversePreOrder(node.right);
            }
        }
        public void traverseInOrder(Node node) {
            if (node != null) {
                traverseInOrder(node.left);
                System.out.println(" " +node.terminalName + " '" + node.value + "' ");
                traverseInOrder(node.right);
            }
        }
        public void traversePostOrder(Node node) {
            if (node != null) {
                traversePostOrder(node.left);
                traversePostOrder(node.right);
                System.out.println(" " +node.terminalName + " '" + node.value + "' ");
            }
        }


        public void traverseLevelOrder() {
            if (root == null) {
                return;
            }

            Queue<Node> nodes = new LinkedList<>();
            nodes.add(root);

            while (!nodes.isEmpty()) {

                Node node = nodes.remove();

                System.out.println(" " +node.terminalName + " '" + node.value + "' ");

                if (node.left != null) {
                    nodes.add(node.left);
                }

                if (node.right!= null) {
                    nodes.add(node.right);

                }
            }
        }


    }

    /**
     *      Assembler
     * _____________________________________________________________________________________________________________
     */
    public static String eval(List<Token> tokens, HashMap idTable){//eval(String terminalName, String noterminal, String nextTerminalName){
        StringBuilder str = new StringBuilder();
        Map<Integer, String> hashMap = new HashMap<Integer, String>(idTable);
        Map<String,Integer> tmpHashMap = new HashMap<>();
        int tmp = 1;
        int w = 0;






        for (int q = 0; q < tokens.size()-1; q++) {
            if(String.valueOf(tokens.get(q).getToken()).equals("INT") && String.valueOf(tokens.get(q+1).getToken()).equals("ID") &&tokens.get(q+2).getValue().equals("[")) {
                int j = Integer.parseInt(tokens.get(q+3).getValue());
                tmpHashMap.put(String.valueOf(tokens.get(q+1).getValue()), w);
                w++;
                String hashCode1 = "";
                for(Map.Entry m:tmpHashMap.entrySet()){
                    if(m.getKey()==String.valueOf(tokens.get(q+1).getValue())) {
                        hashCode1 = m.getValue()+String.valueOf(m.hashCode()) ;
                        str.append("\na" +hashCode1 + ":");
                        for (int k = 0; k < j; k++) {
                            str.append("\n\t.long " + tokens.get(q+6+k*2).getValue());
                        }
                    }
                }
            }
            else if(String.valueOf(tokens.get(q).getToken()).equals("INT") && String.valueOf(tokens.get(q+1).getToken()).equals("ID") && tokens.get(q+2).getValue().equals("=")) {
                tmpHashMap.put(String.valueOf(tokens.get(q+1).getValue()), w);
                w++;
                String hashCode1 = "";
                for(Map.Entry m:tmpHashMap.entrySet()){
                    if(String.valueOf(m.getKey()).equals(String.valueOf(tokens.get(q + 1).getValue()))) {//если сломается, то не приводи тип у ключа
                        hashCode1 = m.getValue()+String.valueOf(m.hashCode()) ;
                        str.append("\na" +hashCode1 + ":");
                        str.append("\n\t.long " + tokens.get(q+3).getValue());
                    }
                }
            }
            else if(String.valueOf(tokens.get(q).getToken()).equals("CHAR") && String.valueOf(tokens.get(q+1).getToken()).equals("ID") && tokens.get(q+2).getValue().equals("=")) {
                tmpHashMap.put(String.valueOf(tokens.get(q+1).getValue()), w);
                w++;
                String hashCode1 = "";
                for(Map.Entry m:tmpHashMap.entrySet()){
                    if(m.getKey()==String.valueOf(tokens.get(q+1).getValue())) {
                        hashCode1 = m.getValue()+String.valueOf(m.hashCode()) ;
                        str.append("\na" +hashCode1 + ":");
                        str.append("\n\t.long " + tokens.get(q+3).getValue());
                    }
                }
            }
            else if(String.valueOf(tokens.get(q).getToken()).equals("IDENTIFIER")){
                if(!tmpHashMap.containsKey(String.valueOf(tokens.get(q+2).getValue()))){
                    tmpHashMap.put(String.valueOf(tokens.get(q+2).getValue()), w);
                }
                w++;
                String hashCode1 = "";
                for(Map.Entry m:tmpHashMap.entrySet()){
                    if(m.getKey()==String.valueOf(tokens.get(q+2).getValue())) {
                        hashCode1 = m.getValue()+String.valueOf(m.hashCode());
                        str.append("\na" +hashCode1 + ":");
                        str.append("\n\t.string " + tokens.get(q+2).getValue());
                    }
                }
            }



        }


        for (int q = 0; q < tokens.size()-1; q++){
            if(String.valueOf(tokens.get(q).getToken()).equals("MAIN")) str.append("\n.global main\n\t.LCO: \n\t.text\n\t.type\tmain, @function\n" +
                    "main:\n.LFB0:\n\tpushq\t%rbp\n\tmovq\t%rsp, %rbp\n\tsubq\t$64, %rsp");
            else if(String.valueOf(tokens.get(q).getToken()).equals("IDENTIFIER")){
                String hashCode2 = "";
                for (Map.Entry m : tmpHashMap.entrySet()) {
                    if (String.valueOf(m.getKey()).equals(String.valueOf(tokens.get(q+2).getValue()))) hashCode2 = m.getValue() + String.valueOf(m.hashCode());
                }
                for (int qq = q+4; !(tokens.get(qq).getValue().equals(";")); qq++) {
                    if (String.valueOf(tokens.get(qq).getToken()).equals("ID") && (String.valueOf(tokens.get(qq+1).getValue()).equals(",")|| String.valueOf(tokens.get(qq+1).getValue()).equals(")"))){
                        String hashCode1 = "";
                        for (Map.Entry m : tmpHashMap.entrySet()) {
                            if (String.valueOf(m.getKey()).equals(String.valueOf(tokens.get(qq).getValue()))) {
                                hashCode1 = m.getValue() + String.valueOf(m.hashCode());
                                str.append("\n\tmovl\ta" + hashCode1 + "(%ebx), %eax");
                                str.append("\n\tmovl\t%eax, %esi\n\tmovl\t$a"+ hashCode2 + ", %edi\n\tmovl\t$0, %eax\n\tcall\tprintf");
                            }
                        }
                    }
                    else if(String.valueOf(tokens.get(qq).getToken()).equals("ID") && String.valueOf(tokens.get(qq+1).getValue()).equals("[")){
                        String hashCode1 = "";
                        for (Map.Entry m : tmpHashMap.entrySet()) {
                            if (String.valueOf(m.getKey()).equals(String.valueOf(tokens.get(qq).getValue()))) {
                                hashCode1 = m.getValue() + String.valueOf(m.hashCode());
                                str.append("\n\tmovl\t$" + Integer.parseInt(String.valueOf(tokens.get(qq+2).getValue()))*4 + ", %ebx");
                                str.append("\n\tmovl\ta" + hashCode1 + "(%ebx), %eax");

                                str.append("\n\tmovl\t%eax, %esi\n\tmovl\t$a"+ hashCode2 + ", %edi\n\tmovl\t$0, %eax\n\tcall\tprintf");
                            }
                        }
                    }
                }
            }
            else if(String.valueOf(tokens.get(q).getToken()).equals("ID") && (String.valueOf(tokens.get(q - 1).getValue()).equals("{")
                    || String.valueOf(tokens.get(q-1).getValue()).equals("}") || String.valueOf(tokens.get(q-1).getValue()).equals(";")) && String.valueOf(tokens.get(q+1).getValue()).equals("=")){
                //Написать изменения значений переменных прим. j = 5;
                String hashCode1;
                for(Map.Entry m:tmpHashMap.entrySet()){
                    if(String.valueOf(m.getKey()).equals(String.valueOf(tokens.get(q).getValue()))) {
                        hashCode1 = m.getValue()+String.valueOf(m.hashCode());
                        str.append("\n\tmovl\t$" +tokens.get(q+2).getValue() + ", %eax");
                        //str.append("\n\t.long " + tokens.get(q+3).getValue());
                    }
                }

            }
        }
        str.append("\n\tmovl\t$0, %eax\n\tleave\n\tret");

        for(Map.Entry m:tmpHashMap.entrySet()){
            System.out.println(m.getKey()+" "+m.getValue()+" "+ m.hashCode() );
        }

        for(Map.Entry m:hashMap.entrySet()){
            //System.out.println(m.getKey()+" "+m.getValue()+" "+ m.hashCode() );
        }
        System.out.println("_____________________________");
        return str.toString(); // замени
    }


    public static void printError(String terminalNamem, String nextTerminalName, int row, int col, int type){
        System.out.println();
        System.out.println(" " + (char)27 + "[31mХеллоу, ты думаешь, что все хорошо, но вот она я, ошибочка");
        if(type == 1)
            System.out.println(" " + terminalNamem + " идет перед " + nextTerminalName + " а так нельзя! " + (char)27 + "[34m<Loc=<hh.c:" + row + ":" + col + ">");
        if(type == 2)
            System.out.println(" Элемент " + terminalNamem  + " не задан!" + (char)27 + "[34m<Loc=<hh.c:" + row + ":" + col + ">");
        if(type == 3)
            System.out.println(" Где-то лишняя '(', ')', '{', '}', '[' или ']' ");
        if(type == 4)
            System.out.println(" При объявлении переменных нужно задавать значение " + (char)27 + "[34m<Loc=<hh.c:" + row + ":" + col + ">");
    }



    public static void main(String[] args) throws IOException {
        String file = "C:\\Users\\Влад\\Desktop\\compiler\\src\\file.txt";
                        //String out ="C:\Users\Влад\Desktop\Lexer\src\file.txt";
        Scanner in = new Scanner(new File(file));
                        //FileWriter fstream = new FileWriter(out);
                        //BufferedWriter out1 = new BufferedWriter(fstream);
        final LineNumberReader lnr = new LineNumberReader(new FileReader(file));



        String str = "", eval = "";
        List<Token> tokens = lex(str);
        //считывание строки и сбор токенов
        for (int j = 0; lnr.readLine() != null;) {
            str = in.nextLine();
            tokens.addAll(lex(str));
            row++;
        }
        tokens.add(new Token(Type.EOF, "eof", i, row, col));

        //_____________________ПАРСЕР_____________________________

        BinaryTree bt = new BinaryTree();
        int level = 0;
        int lbrace = 0, rbrace = 0, lbracet = 0, rbracet = 0, lparen = 0, rparen = 0;
        HashMap<Integer, List<String>> idTable = new HashMap<Integer, List<String>>();
        for (int q = 0; q < tokens.size()-1; q++) {//не смотрю последний символ
            int j = getRule(String.valueOf(tokens.get(q).getToken()),tokens.get(q).getValue(),
                    String.valueOf(tokens.get(q+1).getToken()));
            if(j==0) {
                printError(String.valueOf(tokens.get(q).getToken()), String.valueOf(tokens.get(q+1).getToken()),
                        tokens.get(q).row, tokens.get(q).col, 1);
                System.out.println((char)27 + "[37m ERROR");
                return ;
            }
            if(String.valueOf(tokens.get(q).getToken()).equals("ID")
                    && String.valueOf(tokens.get(q+1).getToken()).equals("SEMI") && (String.valueOf(tokens.get(q-1).getToken()).equals("INT") ||
                    String.valueOf(tokens.get(q-1).getToken()).equals("CHAR"))){
                printError(String.valueOf(tokens.get(q).getToken()), String.valueOf(tokens.get(q+1).getToken()),
                        tokens.get(q-1).row, tokens.get(q-1).col, 4);
                System.out.println((char)27 + "[37m ERROR");
                return ;
            }
            else if(q%2 == 0 && q>0)
                bt.add(String.valueOf(tokens.get(q).getToken()), tokens.get(q).getValue(), "right");
            else if(q % 2 == 1)
                bt.add(String.valueOf(tokens.get(q).getToken()), tokens.get(q).getValue(), "left");
            if(q == 0)
                bt.add(String.valueOf(tokens.get(q).getToken()), tokens.get(q).getValue(), "no");


            //_______________________Table of symbols____________________________________

            if(String.valueOf(tokens.get(q).getToken()).equals("LBRACE")) {level++; lbrace++;}
            if(String.valueOf(tokens.get(q).getToken()).equals("RBRACE")) {level--; rbrace++;}
            if(String.valueOf(tokens.get(q).getToken()).equals("LBRACET")) lbracet++;
            if(String.valueOf(tokens.get(q).getToken()).equals("RBRACET")) rbracet++;
            if(String.valueOf(tokens.get(q).getToken()).equals("LPAREN")) lparen++;
            if(String.valueOf(tokens.get(q).getToken()).equals("RPAREN")) rparen++;

            if(String.valueOf(tokens.get(q).getToken()).equals("ID") && !(String.valueOf(tokens.get(q-1).getToken()).equals("INT")
                    || String.valueOf(tokens.get(q-1).getToken()).equals("CHAR"))){
                int tmp = 0;
                if (idTable.containsKey(level)) {
                        for (int r = level; r >= 0; r--) {
                            if (idTable.containsKey(r)) {
                                if (idTable.get(r).contains(tokens.get(q).getValue())) {tmp = 1; break;}
                            }
                        }
                        if(tmp == 0) {
                            printError(String.valueOf(tokens.get(q).getValue()), String.valueOf(tokens.get(q + 1).getToken()),
                                    tokens.get(q).row, tokens.get(q).col, 2);
                            System.out.println((char)27 + "[37m ERROR");
                        }
                }
            }
            if(String.valueOf(tokens.get(q).getToken()).equals("ID") && (String.valueOf(tokens.get(q-1).getToken()).equals("INT")||String.valueOf(tokens.get(q-1).getToken()).equals("CHAR"))
                    && ((tokens.get(q+1).getValue().equals("=")
                    || tokens.get(q+1).getValue().equals("[")))) {
                if (idTable.containsKey(level)) {
                    if(idTable.get(level).contains(tokens.get(q).getValue())){}
                    else
                        idTable.get(level).add(tokens.get(q).getValue());
                } else {
                    idTable.put(level, new ArrayList<>(Arrays.asList(tokens.get(q).getValue())));
                }
            }
            if(String.valueOf(tokens.get(q).getToken()).equals("RBRACE")){
                //idTable.remove(level+1);//_________________________________Delete from table of symbols____________
            }


        }

        //eval = eval(tokens,idTable);
        //System.out.println(eval);

        if(lbrace != rbrace || lbracet != rbracet || lparen != rparen) {
            printError(String.valueOf(tokens.get(0).getToken()), String.valueOf(tokens.get(1).getToken()),
                    tokens.get(0).row, tokens.get(0).col, 3);
            System.out.println((char)27 + "[37m ERROR");
            return ;
        }


        System.out.println("_____________");
        System.out.println("\n\nТаблица символов\n\n");
        for (char c='a'; c<='z'; c++) {
            //System.out.println("code="+(int)c+"\tsumbol="+c + " " + (char)80);
        }
        for(Map.Entry m:idTable.entrySet()){
            System.out.println(m.getKey()+" "+m.getValue()); //_______________Output from table of symbols____________________
        }

//        bt.traversePreOrder(bt.root);
//        System.out.println( );
//        bt.traverseInOrder(bt.root);
//        System.out.println( );
//        bt.traversePostOrder(bt.root);
        System.out.println( "\n\n Парсер\n\n");
        bt.traverseLevelOrder(); // _________________________________________ВЫВОД ПАРСЕРА______________________________



        //вывод
        for(int q = 0; q < tokens.size(); q++) {
                    //out1.write(str+"\r\n");
            //System.out.println(tokens.get(q)); //__________________________ВЫВОД ЛЕКСЕРА______________________________
        }
                    //out1.write(str+"\r\n");
                    //out1.close();

    }


    public static Type Eq(String str){
        if(str.charAt(0)=='"' && str.charAt(str.length()-1) == '"') return Type.LITERAL;
        if(str.toUpperCase().equals("FOR")) return Type.FOR;
        if(str.toUpperCase().equals("IF")) return Type.IF;
        if(str.toUpperCase().equals("INT")) return Type.INT;
        if(str.toUpperCase().equals("ARGC") || str.toUpperCase().equals("ARGV")) return Type.INPUT;
        if(str.toUpperCase().equals("CHAR")) return Type.CHAR;
        if(str.toUpperCase().equals("MAIN")) return Type.MAIN;
        if(str.toUpperCase().equals("WHILE")) return Type.WHILE;
        if(str.toUpperCase().equals("PRINTF")) return Type.IDENTIFIER;
        if(str.toUpperCase().equals("RETURN")) return Type.RETURN;
        if(str.toUpperCase().equals("(")) return Type.LPAREN;
        if(str.toUpperCase().equals(")")) return Type.RPAREN;
        if(Character.isDigit(str.charAt(0))) return Type.NUMERIC;
        if(Character.isLetter(str.charAt(0))) return Type.ID;
        return Type.UNKNOWN;
    }
}