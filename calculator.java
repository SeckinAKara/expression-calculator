import java.lang.Math;
import java.util.*;


class calculator {

    
    private class treeNode {

        public String value;
        public int lchild;
        public int rchild;

        public treeNode(String new_value, int new_lchild, int new_rchild) {
            value = new_value;
            lchild = new_lchild;
            rchild = new_rchild;
        }
        public treeNode(String new_value) {
            value = new_value;
            lchild = 0;
            rchild = 0;
        }
        public treeNode() {
            value = "fuck";
            lchild = 0;
            rchild = 0;
        }
    }        

    HashMap<String, Integer> functDic = new HashMap<String, Integer>();
    List<String> functList;
    ArrayList<String> functs = new ArrayList<String>(Arrays.asList("tan", "lg", "cos", "ln", "mod", "sin", "sqrt", "exp"));
    ArrayList<String> singleFuncts = new ArrayList<String>(Arrays.asList("+", "-", "*", "/", "^"));
    ArrayList<String[]> all_lines = new ArrayList<String[]>(); 
    HashMap<String, Double> varDic = new HashMap<String, Double>();
    ArrayList<treeNode> nodes = new ArrayList<treeNode>();

    public calculator() {
        functDic.put("+", 1);
        functDic.put("-", 1);
        functDic.put("*", 2);
        functDic.put("/", 2);
        functDic.put("^", 3);
        functDic.put("sqrt", 4);
        functDic.put("exp", 4);
        functDic.put("sin", 4);
        functDic.put("cos", 4);
        functDic.put("tan", 4);
        functDic.put("ln", 4);
        functDic.put("mod", 4);
        functDic.put("lg", 4);
        functList = new ArrayList(functDic.keySet()); 
        treeNode root = new treeNode("0");
        nodes.add(root);

    }

    public void getLines(String expr) {
        String[] lines = expr.split(";");
        if (lines.length == 1 && !lines[0].contains("return")) {
            lines[0] = "return " + lines[0];
        }
        String[] each = new String[2];
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].matches(".+=.+")) {
                each = lines[i].split("=");
                all_lines.add(each);
            } else if (lines[i].matches("\\s*return .+")) {
                each = lines[i].split("return");
                each[0] = "return";
                all_lines.add(each);
            }
        }
    }
                

    public double applyFunct(String funct, double operand1, double operand2) {
        switch (funct) {
            case "+": return operand1+operand2;
            case "-": return operand1-operand2;
            case "*": return operand1*operand2;
            case "/": return operand1/operand2;
            case "^": return Math.pow(operand1, operand2);
            case "sqrt": return Math.sqrt(operand1);
            case "exp": return Math.exp(operand1);
            case "sin": return Math.sin(operand1); 
            case "cos": return Math.cos(operand1);
            case "tan": return Math.tan(operand1);
            case "ln": return Math.log(operand1);
            case "lg": return Math.log(operand1)/Math.log(2);
            case "mod": return operand1 - operand2*Math.floor(operand1/operand2);
            }
        return 0;
    }


    public boolean isVariable(String expr) {
        if (expr.trim().length() == 0) {return false;}
        expr = expr.trim();
        if (expr.substring(0,1) == "") {return false;}
        if (!expr.substring(0, 1).matches("[a-zA-Z]+")) {return false;}
        else {
            for (int i = 1; i < expr.length(); i++) {
                if (!expr.substring(i, i+1).matches("[a-zA-Z]+") && !expr.substring(i, i+1).matches("[0-9]+")) {return false;}
            } return true;
        }
    }

    public boolean isNumber(String expr) {
        if (expr.trim().length() == 0) {return false;}
        expr = expr.trim();
        if (expr.substring(0, 1).equals("-")) {
            expr = expr.substring(1);
        }
        expr = expr.trim();
        try {
            double d = Double.parseDouble(expr);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }


    public double convertNum(String x) {
        double num;
        if (x.substring(0, 1).equals("-")) {
            num = -1 * Float.parseFloat(x.substring(1).trim());
        }
        else {
            num = Float.parseFloat(x);
        }
        return num;
    }


    public String mask(String s) {
        int nestLevel = 0;
        char[] masked = s.toCharArray();
        for (int i = 0; i < s.length(); i++) {
            if (masked[i] == ')') {
                nestLevel -= 1;
            } else if (masked[i] == '(') {
                nestLevel += 1;
            } if ((nestLevel>0) && !(masked[i]=='(')) {
                masked[i] = ' ';
            }
        } String b = new String(masked);
        return b;
    }


    public int getNextFunctPos(String expr, int pos) {
        if (expr.length() == 0) {return -1;}
        int oprPos=-1;
        char opr;
        while (pos < expr.length()) {
            if (functList.contains(Character.toString(expr.charAt(pos)))) {
                oprPos = pos;
                opr = expr.charAt(pos);
                if ((opr == '-') && (pos==0)) {
                    if ((expr.substring(pos+1).trim().charAt(0) == '(') || (Character.isLetter(expr.substring(pos+1).trim().charAt(0)))) {break;}
                    else {
                        pos += 1;
                        continue;
                    }
                } else {break;}
            } else if (Character.isLetter(expr.charAt(pos))) {
                int scanPos = pos;
                while (Character.isLetter(expr.charAt(scanPos))) {
                    scanPos += 1;
                    if (scanPos >= expr.length()) {break;}
                } if (functList.contains(expr.substring(pos, scanPos))) {
                    oprPos = pos;
                    break;
                }
            } pos += 1;
        } return oprPos;
    }


    public treeNode constructTree(String expr) {
        expr = expr.trim();
        String test = mask(expr);
        test = test.replaceAll("[()]", "");
        boolean good;
        treeNode node;
        if (test.replaceAll("\\s","").matches("^$")) {
            expr = expr.substring(1, expr.length()-1);
            good = true;
        } else {good = false;}
        System.out.println(expr);
        ArrayList<String> oprList = new ArrayList<String>();
        ArrayList<Integer> oprPosList = new ArrayList<Integer>();
        ArrayList<String> itemList = new ArrayList<String>();
        int pos = 0;
        if (isNumber(expr)) {
            node = new treeNode(expr);
            nodes.add(node);
            return node;
        }
        else if (isVariable(expr)) {
            ArrayList<String> varKeys = new ArrayList(varDic.keySet());
            if (varKeys.contains(expr.trim())) {
                double value = varDic.get(expr.trim());
                System.out.println(value);
                node = new treeNode(Double.toString(value));
                nodes.add(node);
                return node;
            } else {
                node = new treeNode();
                return node;
            }
        } String opr;
        while (pos >= 0) {
            int oprPos = getNextFunctPos(mask(expr), pos);
            int oldPos= pos;
            if (oprPos >= 0) {
                pos = oprPos + 1;
            } else {
                pos = oprPos;
            }
            if (oprPos < expr.length() && oprPos >= 0) {
                if (Character.isLetter(expr.charAt(oprPos))) {
                    while (Character.isLetter(expr.charAt(pos))) {
                        pos += 1;
                    } opr = expr.substring(oprPos, pos);
                } else {
                    opr = expr.substring(oprPos, pos);
                }
                if ((opr == "-") && (oprPos == 0)) {
                    expr = "0" + expr;
                    oprPos = 1;
                }
                String item = expr.substring(oldPos, oprPos);
                System.out.println(item);
                if ((isNumber(item)) || (isVariable(item))) {
                    if (functs.contains(opr)) {
                        node = new treeNode();
                        return node;
                    }
                }
                oprList.add(opr);
                oprPosList.add(oprPos);
                itemList.add(item);
            }
        }
        int minimum = 0;
        System.out.println(oprList);
        if (oprList.size() == 0) {
            node = new treeNode(expr.trim());
            nodes.add(node);
            return node;
        } else {
            for (int i=1; i<oprList.size(); i++) {
                if (functDic.get(oprList.get(i)) <= functDic.get(oprList.get(minimum))) {
                    minimum = i;
                }
            }
        }
        String minOpr = oprList.get(minimum);
        String minItem = itemList.get(minimum);
        int minOprPos = oprPosList.get(minimum); 
        node = new treeNode(minOpr);
        nodes.add(node);
        if (singleFuncts.contains(minOpr)) {
            treeNode lchild = constructTree(expr.substring(0, minOprPos));
            treeNode rchild = constructTree(expr.substring(minOprPos+1));
            node.lchild = nodes.indexOf(lchild);
            node.rchild = nodes.indexOf(rchild);
        } else {
            int starting_paren = minOprPos + minOpr.length();
            String masked = mask(expr.substring(starting_paren));
            int closing_paren = starting_paren + masked.indexOf(')') + 1;
            if (!node.value.equals("mod")) {
                treeNode lchild = constructTree(expr.substring(starting_paren, closing_paren));
                node.lchild = nodes.indexOf(lchild);
            } else {
                String space = expr.substring(starting_paren, closing_paren);
                int comma = starting_paren + space.indexOf(',');
                treeNode lchild = constructTree(expr.substring(starting_paren+1, comma));
                treeNode rchild = constructTree(expr.substring(comma + 1, closing_paren-1));
                node.lchild = nodes.indexOf(lchild);
                node.rchild = nodes.indexOf(rchild);
            }
        }
        return node;
    }


    public double evaluateTree(treeNode node) {
        double returnValue;
        try {
            returnValue = Double.parseDouble(node.value);
        } catch (NumberFormatException e) {
            returnValue = applyFunct(node.value, evaluateTree(nodes.get(node.lchild)), evaluateTree(nodes.get(node.rchild)));
        }
        return returnValue;
    }

    public double calc(String expr) {
        getLines(expr);
        String variable;
        for (String[] line : all_lines) {
            if (line[0] == "return") {
                variable = "__return__";
            }
            else {
                variable = line[0].trim();
            }
            nodes.clear();
            treeNode node = new treeNode("0");
            nodes.add(node);
            System.out.println(line[1]);
            constructTree(line[1]);
            for (treeNode blaargh : nodes) {
                System.out.print(blaargh.value + ", ");
            }
            for (treeNode instant : nodes) {
                System.out.println(instant.value + " " + Integer.toString(instant.lchild) + " " +Integer.toString(instant.rchild));
            }
            double value = evaluateTree(nodes.get(1));
            varDic.put(variable, value);
            System.out.println(varDic);
        }
        return varDic.get("__return__");
    }




    public static void main(String[] args) {
        calculator a = new calculator();
        System.out.println(a.calc(args[0]));
        }
    }
