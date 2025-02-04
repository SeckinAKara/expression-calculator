import java.lang.Math;
import java.util.*;


public class Calculator {

    HashMap<String, Integer> functDict = new HashMap<String, Integer>();
    ArrayList<String> functs = new ArrayList<String>(Arrays.asList("tan", "lg", "cos", "ln", "mod", "sin", "sqrt", "exp"));
    ArrayList<String> singleFuncts = new ArrayList<String>(Arrays.asList("+", "-", "*", "/", "^"));
    HashMap<String, Double> varDict = new HashMap<String, Double>();
    
    Calculator() {
        functDict.put("+", 1);
        functDict.put("-", 1);
        functDict.put("*", 2);
        functDict.put("/", 2);
        functDict.put("^", 3);
        functDict.put("sqrt", 4);
        functDict.put("exp", 4);
        functDict.put("sin", 4);
        functDict.put("cos", 4);
        functDict.put("tan", 4);
        functDict.put("ln", 4);
        functDict.put("mod", 4);
        functDict.put("lg", 4);

    }


    private class treeNode {

        public String value;
        public treeNode lchild;
        public treeNode rchild;

        public treeNode(String new_value, treeNode new_lchild, treeNode new_rchild) {
            value = new_value;
            lchild = new_lchild;
            rchild = new_rchild;
        }
        public treeNode(String new_value) {
            value = new_value;
            lchild = null;
            rchild = null;
        }
        public treeNode() {
            value = "__return__";
            lchild = null;
            rchild = null;
        }
    }        


    public ArrayList<String[]> getLines(String expr) {
        ArrayList<String[]> all_lines = new ArrayList<String[]>();
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
                each = lines[i].split("return ");
                each[0] = "return";
                all_lines.add(each);
            }
        }
        return all_lines;
    }
                

    public double applyFunct(String funct, double operand1) {
        switch (funct) {
            case "sqrt": return Math.sqrt(operand1);
            case "exp": return Math.exp(operand1);
            case "sin": return Math.sin(operand1); 
            case "cos": return Math.cos(operand1);
            case "tan": return Math.tan(operand1);
            case "ln": return Math.log(operand1);
            case "lg": return Math.log(operand1)/Math.log(2);
            }
        return 0;
    }
    
        public double applyFunct(String funct, double operand1, double operand2) {
        switch (funct) {
            case "+": return operand1+operand2;
            case "-": return operand1-operand2;
            case "*": return operand1*operand2;
            case "/": return operand1/operand2;
            case "^": return Math.pow(operand1, operand2);
            case "mod": return operand1 - operand2*Math.floor(operand1/operand2);
            }
        return 0;
    }

    public boolean isVariable(String expr) {
        if (expr.trim().length() == 0) {
            return false;
        }
        expr = expr.trim();
        if (expr.substring(0, 1) == "") {
            return false;
        }
        if (!expr.substring(0, 1).matches("[a-zA-Z]+")) {
            return false;
        }
        else {
            for (int i = 1; i < expr.length(); i++) {
                if (!expr.substring(i, i+1).matches("[0-9a-zA-Z]+")) {
                    return false;
                }
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
            } if ((nestLevel>0) && !(Character.toString(masked[i]).matches("[()]"))) {
                masked[i] = ' ';
            }
        } String b = new String(masked);
        return b;
    }


    public int getNextFunctPos(String expr, int pos) {
        if (expr.length() == 0) {
            return -1;
        }
        int oprPos = -1;
        char opr;
        while (pos < expr.length()) {
            if (functDict.containsKey(Character.toString(expr.charAt(pos)))) {
                oprPos = pos;
                opr = expr.charAt(pos);
                if ((opr == '-') && (pos==0)) {
                    if ((expr.substring(pos+1).trim().charAt(0) == '(') || (Character.isLetter(expr.substring(pos+1).trim().charAt(0)))) {
                        break;
                    }
                    else {
                        pos += 1;
                        continue;
                    }
                } else {
                    break;
                }
            } else if (Character.isLetter(expr.charAt(pos))) {
                int scanPos = pos;
                while (Character.isLetter(expr.charAt(scanPos))) {
                    scanPos += 1;
                    if (scanPos >= expr.length()) {
                        break;
                    }
                } if (functDict.containsKey(expr.substring(pos, scanPos))) {
                    oprPos = pos;
                    break;
                }
            } 
            pos += 1;
        } return oprPos;
    }


    public treeNode constructTree(String expr) {
        expr = expr.trim();
        String test = mask(expr);
        test = test.replaceAll("[()]", "");
        if (test.strip() == "") {
            expr = expr.strip().substring(1, expr.strip().length() - 1);
        }
        // boolean good;
        treeNode node;
        // if (test.replaceAll("\\s","").matches("^$")) {
        //     expr = expr.substring(1, expr.length()-1);
        //     good = true;
        // } else {
        //     good = false;
        // }
        ArrayList<String> oprList = new ArrayList<String>();
        ArrayList<Integer> oprPosList = new ArrayList<Integer>();
        ArrayList<String> itemList = new ArrayList<String>();
        int pos = 0;
        if (isNumber(expr)) {
            node = new treeNode(expr);
            return node;
        }
        else if (isVariable(expr)) {
            ArrayList<String> varKeys = new ArrayList<String>(varDict.keySet());
            if (varKeys.contains(expr.trim())) {
                double value = varDict.get(expr.trim());
                node = new treeNode(Double.toString(value));
                return node;
            } else {
                node = new treeNode();
                return node;
            }
        } 
        String opr;
        while (pos >= 0) {
            int oprPos = getNextFunctPos(mask(expr), pos);
            int oldPos = pos;
            if (oprPos >= 0) {
                pos = oprPos + 1;
            } else {
                pos = oprPos;
            }
            if (oprPos < expr.length() && oprPos >= 0) {
                if (Character.isLetter(expr.charAt(oprPos))) {
                    while (Character.isLetter(expr.charAt(pos))) {
                        pos += 1;
                    } 
                    opr = expr.substring(oprPos, pos);
                } else {
                    opr = expr.substring(oprPos, pos);
                }
                if ((opr == "-") && (oprPos == 0)) {
                    expr = "0" + expr;
                    oprPos = 1;
                }
                String item = expr.substring(oldPos, oprPos);
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
        if (oprList.size() == 0) {
            node = new treeNode(expr.trim());
            return node;
        } else {
            for (int i=1; i<oprList.size(); i++) {
                if (functDict.get(oprList.get(i)) <= functDict.get(oprList.get(minimum))) {
                    minimum = i;
                }
            }
        }
        String minOpr = oprList.get(minimum);
        String minItem = itemList.get(minimum);
        int minOprPos = oprPosList.get(minimum); 
        node = new treeNode(minOpr);
        if (singleFuncts.contains(minOpr)) {
            treeNode lchild = constructTree(expr.substring(0, minOprPos));
            treeNode rchild = constructTree(expr.substring(minOprPos+1));
            node.lchild = lchild;
            node.rchild = rchild;
        } else {
            int starting_paren = minOprPos + minOpr.length();
            String masked = mask(expr.substring(starting_paren));
            int closing_paren = starting_paren + masked.indexOf(')') + 1;
            if (!node.value.equals("mod")) {
                treeNode lchild = constructTree(expr.substring(starting_paren, closing_paren));
                node.lchild = lchild;
            } else {
                String space = expr.substring(starting_paren, closing_paren);
                int comma = starting_paren + space.indexOf(',');
                treeNode lchild = constructTree(expr.substring(starting_paren+1, comma));
                treeNode rchild = constructTree(expr.substring(comma + 1, closing_paren-1));
                node.lchild = lchild;
                node.rchild = rchild;
            }
        }
        return node;
    }


    public double evaluateTree(treeNode node) {
        double returnValue;
        if (node == null) {
            return 0;
        }
        try {
            returnValue = Double.parseDouble(node.value);
        } catch (NumberFormatException e) {
            if (node.rchild == null) {
                returnValue = applyFunct(node.value, evaluateTree(node.lchild));
            } else {
            returnValue = applyFunct(node.value, evaluateTree(node.lchild), evaluateTree(node.rchild));
            }
        }
        return returnValue;
    }

    public double calc(String expr) {
        ArrayList<String[]> all_lines = getLines(expr);

        String variable;
        for (String[] line : all_lines) {
            if (line[0] == "return") {
                variable = "__return__";
            }
            else {
                variable = line[0].trim();
            }
            treeNode tree = constructTree(line[1]);
            double value = evaluateTree(tree);
            varDict.put(variable, value);
        }
        return varDict.get("__return__");
    }


    public static void main(String[] args) {
        Calculator a = new Calculator();
        System.out.println(a.calc(args[0]));
    }
}
