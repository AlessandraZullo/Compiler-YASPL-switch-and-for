package Visitor;

import supportItems.Constant;
import supportItems.Node;

/**
 * Created by a on 17/01/17.
 */
public class CVisitor implements Visitor {


    public String content;

    @Override
    public String visit(Visitable node) {

        String nameFile;
        content = "";
        VisitableNode<Node> nodeVisitable = (VisitableNode<Node>) node;
        switch (nodeVisitable.data().getName()) {

            case Constant.PROGRAM_NODE: {
                content += "#include <stdio.h>\n";
                content += "typedef int bool;\n" +
                        "#define true 1\n" +
                        "#define false 0\n";
                nameFile = nodeVisitable.firstChild().firstChild().data().getName();
                content += nodeVisitable.getChild(1).accept(this);
                break;
            }
            // BLOCK = VAR_DECL_PART , PROC_DECL_PART
            case Constant.BLOCK_OP: {
                int numChild = nodeVisitable.numChild();
                if(numChild == 3){
                    content += nodeVisitable.getChild(0).accept(this);
                    content += nodeVisitable.getChild(1).accept(this);
                    content += "\n" + "int main () {\n";
                    content += nodeVisitable.getChild(2).accept(this);
                }
                else if(numChild == 2){
                    content += "\n" + "int main () {\n";
                    content += nodeVisitable.getChild(0).accept(this);
                    content += nodeVisitable.getChild(1).accept(this);
                } else
                    content += nodeVisitable.getChild(0).accept(this);
                content += "\n }";
                break;
            }
            case Constant.VAR_DECL_NODE: {
                VisitableNode<Node> child = nodeVisitable.firstChild();
                String type = child.data().getName();
                if (type.equals("integer"))
                    type = "int";
                else type = "bool";
                content += "\n" + type + " ";
                for (int i = 1; i < nodeVisitable.numChild() - 1; i++)
                    content += nodeVisitable.getChild(i).accept(this) + ", ";
                content += nodeVisitable.getChild(nodeVisitable.numChild() - 1).accept(this) + "; \n";
                break;
            }
            case Constant.ID_NODE: {
                VisitableNode<Node> child = nodeVisitable.firstChild();
                content += child.data().getName();
                break;
            }

            case Constant.PROC_NODE: {
                content += " ";
                VisitableNode<Node> child = nodeVisitable.firstChild();
                String nameFunction = child.firstChild().data().getName();
                content += "\n" + nameFunction + " (   ){\n";
                content += nodeVisitable.getChild(1).accept(this);
                content += "\n }";
                break;
            }
            case Constant.REL_OP: {
                content += nodeVisitable.getChild(1).accept(this);
                content += nodeVisitable.firstChild().accept(this);
                content += nodeVisitable.getChild(2).accept(this);
                break;
            }
            case Constant.ADD_OP: {
                String operator = nodeVisitable.firstChild().firstChild().data().getName();
                content += nodeVisitable.getChild(1).accept(this);
                content += operator;
                content += nodeVisitable.getChild(2).accept(this);
                break;
            }
            case Constant.MUL_OP: {
                String operator = nodeVisitable.firstChild().firstChild().data().getName();
                content += nodeVisitable.getChild(1).accept(this);
                content += operator;
                content += nodeVisitable.getChild(2).accept(this);
                break;
            }
            case Constant.REL_NODE: {
                content += nodeVisitable.firstChild().data().getName() + " ";
                break;
            }
            case Constant.ASSIGN_OP: {
                content += nodeVisitable.firstChild().accept(this);
                content += " = ";
                content += nodeVisitable.getChild(1).accept(this);
                content +=";";
                break;
            }
            case Constant.READ_NODE: {
                int numChild = nodeVisitable.numChild();
                content += "scanf(\"";
                for (int i = 0; i < numChild; i++)
                    content += "%d ";
                for (int i = 0; i < nodeVisitable.numChild(); i++)
                    content += "\", &" + nodeVisitable.getChild(i).accept(this);
                content += ");";
                break;
            }
            case Constant.WRITE_NODE: {
                int numChild = nodeVisitable.numChild();
                VisitableNode<Node> child;
                String type;
                content += "printf(\"";
                for (int i = 0; i < numChild; i++) {
                    child = nodeVisitable.getChild(i);
                    type = child.data().getType();
                    switch (type) {
                        case "integer": {
                            content += "%d ";
                            break;
                        }
                        case "boolean": {
                            content += " %d ";
                            break;
                        }
                        case "string": {
                            content += "%s ";
                            break;
                        }
                        case "character": {
                            content += "%c ";
                            break;
                        }
                    }
                }
                content += "\"";
                for (int i = 0; i < numChild; i++)
                    content += " , " + nodeVisitable.getChild(i).accept(this);
                content += " );\n";
                break;
            }
            case Constant.CALL_OP_NODE: {
                content += nodeVisitable.firstChild().accept(this) + "(); \n";
                break;
            }

            case Constant.WHILE_NODE: {
                content += "while ( ";
                content += nodeVisitable.firstChild().accept(this);
                content += " ){\n";
                content += nodeVisitable.getChild(1).accept(this);
                content += "}\n";
                break;
            }
            case Constant.IF_THEN_ELSE_NODE: {
                content += "if( ";
                content += nodeVisitable.firstChild().accept(this);
                content += "){\n" + nodeVisitable.getChild(1).accept(this);
                content += "} else {\n ";
                content += nodeVisitable.getChild(2).accept(this) + "\n}";
                break;
            }
            case Constant.IF_THEN_NODE: {
                content += "if( ";
                content += nodeVisitable.firstChild().accept(this);
                content += "){\n" + nodeVisitable.getChild(1).accept(this) + "}";
                break;
            }
            case Constant.NOT_NODE: {
                content += "!(" + nodeVisitable.firstChild().accept(this) + ")";
                break;
            }
            case Constant.UNARY_MINUS_NODE: {
                content += "-(" + nodeVisitable.firstChild().accept(this) + ")";
                break;
            }
            case Constant.CONST_NODE: {
                VisitableNode<Node> constant = nodeVisitable.firstChild();
                content += constant.firstChild().data().getName();
                break;
            }
            case Constant.FOR_NODE: {
                content += "for (";
                if (!nodeVisitable.firstChild().data().getName().equals(Constant.FOR_EXPR_NODE))
                    content += ";";
                else
                    content += nodeVisitable.firstChild().accept(this);
                content += "){\n";
                content += nodeVisitable.getChild(1).accept(this);
                content += "\n}";
                break;
            }
            case Constant.FOR_EXPR_NODE: {
                int numChild = nodeVisitable.numChild();
                if (numChild == 3) {
                    content += nodeVisitable.firstChild().accept(this);
                    content += nodeVisitable.getChild(1).accept(this);
                    content += nodeVisitable.getChild(2).accept(this);
                } else if (numChild == 2) {
                    content += nodeVisitable.firstChild().accept(this);
                    content += nodeVisitable.getChild(1).accept(this);
                } else
                    content += nodeVisitable.firstChild().accept(this);
                break;
            }
            case Constant.FOR_ASSIGNMENT: {
                content += nodeVisitable.firstChild().accept(this);
                break;
            }
            case Constant.FOR_INNER_EXPRESSION: {
                content += nodeVisitable.firstChild().accept(this) + " ; ";
                break;
            }
            case Constant.FOR_SIMPLE_STATEMENT: {
                content += nodeVisitable.firstChild().accept(this);
                if(content.charAt(content.length()-1)==';')
                content = content.substring(0,content.length()-1);

                break;
            }


            default: {
                for (int i = 0; i < nodeVisitable.numChild(); i++)
                    content += nodeVisitable.getChild(i).accept(this);
            }

        }


        return content;
    }
}
