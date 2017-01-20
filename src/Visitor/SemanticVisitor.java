package Visitor;

import Exception.DeclarationException;
import Exception.NonDeclarationException;
import supportItems.Constant;
import supportItems.MyStack;
import supportItems.Node;

import java.util.Iterator;


/**
 * Created by a on 13/01/17.
 */
public class SemanticVisitor implements Visitor {

    public MyStack<Node> stack = new MyStack<Node>();
    public String type = "";


    @Override
    public String visit(Visitable node) {
        //Controllo del nodo padre
        VisitableNode<Node> child = null;
        // VisitableNode<Node> currentNode;
        Iterator<VisitableNode<Node>> childs;
        String var = "";

        //Se sei in un nuovo SCOPE (punto A) aggiungi il nodo al top dello stack
        if (((VisitableNode<Node>) node).data().newScope()) {
            stack.push(((VisitableNode<Node>) node).data());
            stack.showStack();
        }
        childs = ((VisitableNode) node).subtrees().iterator();
        while (childs.hasNext()) {
            VisitableNode<Node> currentNode = childs.next();
            String nameCurrentNode = currentNode.data().getName();
            if (!(currentNode.isLeaf())) {
                //Se il nodo è un ConstOp (REGOLA B)
                if (nameCurrentNode.equals(Constant.CONST_NODE))
                    checkConstOp(currentNode);
                else   // altrimenti se il nodo è legato ad un uso di un identificatore (REGOLA A)
                    try {
                        checkRuleA(currentNode);
                    } catch (NonDeclarationException e) {
                        System.out.println(e.getMessage());
                        System.exit(0);
                    }
                //Se il nodo è un READ_OP o un PROC_OP
                if (nameCurrentNode.equals(Constant.READ_NODE) || nameCurrentNode.equals(Constant.PROC_NODE))
                    checkReadProcOp(currentNode);
                //Se il nodo è un WRITE_OP
                if (nameCurrentNode.equals(Constant.WRITE_NODE))
                    checkWriteOp(currentNode);
                // Se il nodo è un COMP_OP
                if (nameCurrentNode.equals(Constant.COMP_NODE))
                    checkCompoundOpAndIfAndWhile(currentNode);

                // Se il nodo è un IF
                if (nameCurrentNode.equals(Constant.IF_THEN_ELSE_NODE) || nameCurrentNode.equals(Constant.IF_THEN_NODE))
                    checkIfAndWhile(currentNode);

                // Se il nodo è un WHILE
                if (nameCurrentNode.equals(Constant.WHILE_NODE))
                    checkIfAndWhile(currentNode);

                if (nameCurrentNode.equals(Constant.CALL_OP_NODE))
                    try {
                        checkIdentifier(currentNode);
                    } catch (NonDeclarationException e) {
                        System.out.println(e.getMessage());
                    }
                // Se il nodo è una istruzione di assegnamento (ASSIGN OP) esegui il type system
                if (nameCurrentNode.equals(Constant.ASSIGN_OP))
                    checkAssignOp(currentNode);
                    // Se il nodo è una nuova procedura
                else if (currentNode.data().getName().equals(Constant.PROC_NODE))
                    type = "procedure";
                //invoca il metodo sui figli di procedure
                currentNode.accept(this);
            }
            // se il nodo è una foglia controlla se è una COSTANTE
            else if (currentNode.data().isConstant())
                type = currentNode.data().getName();

                // altrimenti se è un identificatore deve essere aggiunto alla tabella dei simboli del nodo presente sullo stack
            else if (currentNode.data().added()) {
                if (type.equals("procedure"))
                    addProcedure(currentNode);
                else {
                    Node pop = stack.peek();
                    currentNode.data().setType(type);
                    try {
                        pop.addToTable(currentNode.data().getName(), currentNode.data());
                    } catch (DeclarationException e) {
                        System.out.println(e.getMessage());
                        System.exit(0);
                    }
                }
            } else if (!currentNode.isLeaf())
                currentNode.accept(this);
        }
        if (((VisitableNode<Node>) node).data().newScope()) {
            stack.pop();
            stack.showStack();
        }
        return null;
    }


    public void checkRuleA(VisitableNode<Node> currentNode) throws NonDeclarationException {
        String nameCurrentNode = currentNode.data().getName();
        if (nameCurrentNode.equals(Constant.ASSIGN_OP) || nameCurrentNode.equals(Constant.READ_NODE) || currentNode.equals(Constant.CALL_OP_NODE) || nameCurrentNode.equals(Constant.VAR_NODE))
            checkIdentifier(currentNode);

    }


        //REGOLA C

    public void checkIdentifier(VisitableNode<Node> node) throws NonDeclarationException {
        VisitableNode<Node> identifier = node.getChild(0);
        VisitableNode<Node> value = identifier.getChild(0);
        //controllo per aggiunta di identificatore di procedure
        // nello scope precedente

        Node entry;
        boolean flag = false;
        int i;
        Node top;
        String type = "";
        i = stack.size() - 1;
        for (; i >= 0; i--) {
            if (flag != true) {
                top = stack.get(i);
                if (top.checkElement(value.data().getName())) {
                    entry = top.getEntry(value.data().getName());
                    node.data().setType((entry.getType()));
                    type = entry.getType();
                    flag = true;
                }
            }
        }
        if (!flag)
            throw new NonDeclarationException(value.data().getName());
        else {
            node.data().setType(type);
            value.data().setType(type);
            identifier.data().setType(type);
        }
    }


    //TYPE SYSTEM
    //ASSIGN_OP = variable:value ASSIGN expression:node
    //Effettua il typechecking sulla variabile (controlla che sia presente la sua dichiarazione e prendi il type)
    // Effettua il type checking sulla espressione
    // Controlla i due type
    public void checkAssignOp(VisitableNode<Node> node) {
        VisitableNode<Node> variable = node.getChild(0);
        VisitableNode<Node> expression = node.getChild(1);
        try {
            checkIdentifier(node);
        } catch (NonDeclarationException e) {
            System.out.println(e.getMessage());
        }
        checkExprOp(expression);
        String type;
        if (variable.data().getType() == expression.data().getType()) {
            type = variable.data().getType();
            node.data().setType(type);
        } else {
            System.out.println("************ TYPE MISMATCH **************   \n"
                    + node + "\n" +
                    "la variabile " + variable.firstChild().data().getName() + " è un " + variable.data().getType());
            System.exit(0);
        }
    }

    // READ_ OP & PROCEDURE_STATEMENT
    // READ LPAR input_variable:ptr_st read_variables
    // variable = identificatore
    // Assegna al nodo ReadOp o ProcOp il tipo della prima variabile
    public void checkReadProcOp(VisitableNode<Node> node) {
        VisitableNode<Node> identifier = node.getChild(0);
        String type = identifier.getChild(0).data().getType();
        identifier.data().setType(type);
        node.data().setType(type);
    }

    // Scorri i figli e verifica che hanno tutti lo stesso tipo, se anche uno non ha lo stesso tipo restituisci errore
    //  WRITE LPAR output_value:node1 output_values:node2 RPAR
    // output_value = expression
    // Prendi tutti i figli (che sono expression) e controlla che abbiano tutti lo stesso type
    public void checkWriteOp(VisitableNode<Node> node) {
        VisitableNode<Node> output_node;
        VisitableNode<Node> expression_node;
        String type = "";
        String typeChild;
        for (int i = 0; i < node.numChild(); i++) {
            output_node = node.getChild(i);
            expression_node = output_node.firstChild();
            checkExprOp(output_node);
            typeChild = expression_node.data().getType();
            if (type.equals(""))
                type = typeChild;
            else if (!(type.equals(typeChild)))
                System.out.println("Type mismatch in una operazione di write.");
        }
        node.data().setType(type);
    }

    // metodo per il type checking di structured_statement
    // nodo.type = tipo dell’ultimo nodo figlio
    // compound = BEGIN statement:ptr_st statements:ptr_sts
    // statement := simpleStatement or StructuredStatement
    public void checkCompoundOpAndIfAndWhile(VisitableNode<Node> node) {
        String name = node.data().getName();
        switch (name) {
            case Constant.COMP_NODE: {
                checkCompoundOpAndIfAndWhile(node.firstChild());
                break;
            }
            case Constant.IF_THEN_ELSE_NODE: {
                checkIfAndWhile(node);
                break;
            }
            case Constant.IF_THEN_NODE: {
                checkIfAndWhile(node);
                break;
            }
            case Constant.WHILE_NODE: {
                checkIfAndWhile(node);
                break;
            }
            case Constant.FOR_NODE: {
                checkFor(node);
                break;
            }
            case Constant.SWITCH_NODE: {
                typeCheckingSwitch(node);
                break;
            }
        }
        int num = node.numChild();
        num -= 1;
        VisitableNode<Node> lastChild = node.getChild(num);
        String typeChild = lastChild.data().getType();
        node.data().setType(typeChild);

    }


    // effettua typechecking su expression
    // effettua typechecking case
    // effettua type checking sul default
    public void typeCheckingSwitch(VisitableNode<Node> node) {
        VisitableNode<Node> switchExpression = node.firstChild();
        VisitableNode<Node> child;
        String type;
        String typeChild;
        checkSwitchExpression(switchExpression.firstChild());
        switchExpression.data().setType(switchExpression.firstChild().data().getType());
        type = switchExpression.data().getType();
        // effettua IL TYPE CHECKING sui figli
        int numChild = node.numChild();
        for (int i = 1; i < numChild; i++) {
            child = node.getChild(i);
            if(child.data().getName().equals(Constant.CASE_NODE))
                checkCase(child);
            else checkDefault(child);
            typeChild= child.data().getType();
            if(!type.equals(typeChild)){
                System.out.println("********** TYPE MISMATCH ********** \n" +
                      "Switch e Case hanno type differenti");
                System.exit(0);
            }
        }
        node.data().setType(type);
        }


    public void checkDefault(VisitableNode<Node> node){
        VisitableNode<Node> defaultNode = node.firstChild();
        checkStatement(defaultNode);
        node.data().setType(defaultNode.data().getType());
    }
    public void checkCase(VisitableNode<Node> node){
        VisitableNode<Node> constantNode = node.firstChild();
        VisitableNode<Node> statement = node.getChild(1);
        checkConstOp(constantNode);
        checkStatement(statement);
        node.data().setType(constantNode.data().getType());
    }

    // type checking statement (può essere simple statement oppure structured statement)
    public void checkStatement(VisitableNode<Node> node) {
        String nameCurrentNode = node.data().getName();
        switch (nameCurrentNode) {
            case(Constant.ASSIGN_OP):{
                checkAssignOp(node);
            break;
            }
            case (Constant.READ_NODE):{
                checkReadProcOp(node);
                break;
            }
            case(Constant.PROC_NODE):{
                checkReadProcOp(node);
                break;
            }

            case(Constant.WRITE_NODE):{
                checkWriteOp(node);
            break;
            }
            case Constant.COMP_NODE: {
                checkCompoundOpAndIfAndWhile(node.firstChild());
                break;
            }
            case Constant.IF_THEN_ELSE_NODE: {
                checkIfAndWhile(node);
                break;
            }
            case Constant.IF_THEN_NODE: {
                checkIfAndWhile(node);
                break;
            }
            case Constant.WHILE_NODE: {
                checkIfAndWhile(node);
                break;
            }
            case Constant.FOR_NODE: {
                checkFor(node);
                break;
            }
            case Constant.SWITCH_NODE: {
                typeCheckingSwitch(node);
                break;
            }
        }
    }


    // Metodo usato per effettuare il type checking sulla espressione accettata dallo switch
    // Tale espressione può essere RELOP o una simple expression tranne constant
    public void checkSwitchExpression(VisitableNode<Node> node) {
        switch (node.data().getName()) {
            case Constant.REL_OP: {
                checkRelOp(node);
                break;
            }
            default: {
                typeCheckingSimpleExpr(node.firstChild());
                node.data().setType(node.firstChild().data().getType());
            }
        }
    }

    public void checkIfAndWhile(VisitableNode<Node> node) {
        VisitableNode<Node> child = node.firstChild();
        checkExprOp(child);
    }

    // type checking per for
    // controlla che per ogni figlio il type checking sia rispettato
    public void checkFor(VisitableNode<Node> Fornode) {
        VisitableNode<Node> node = Fornode.firstChild();
        VisitableNode<Node> assign_node;
        VisitableNode<Node> inner_node;
        VisitableNode<Node> statement_node;
        if (node.data().getName().equals(Constant.FOR_EXPR_NODE)) {

            if (node.getChild(0) != null) {
                assign_node = node.getChild(0);
                checkAssignOp(assign_node.firstChild());
            }
            if (node.getChild(1) != null) {
                inner_node = node.getChild(1);
                checkExprOp(inner_node.firstChild());
            }
            if (node.getChild(2) != null) {
                statement_node = node.getChild(2);
                statement_node.data().setType(statement_node.firstChild().data().getType());
            }
        }


    }


    // simple_expression:node1 adding_operator:value simple_expression:node2
    // or
    // simple_expression:node1 MULT_OPERATOR:value simple_expression:node2
    public void checkAddingMultOperator(VisitableNode<Node> node) {
        VisitableNode<Node> node1 = node.getChild(1);
        VisitableNode<Node> node2 = node.getChild(2);
        // assegniamo i tipi ai figli
        typeCheckingSimpleExpr(node1);
        typeCheckingSimpleExpr(node2);
        String type1 = node1.data().getType();
        String type2 = node2.data().getType();
        if (!(type1.equals(type2))) {
            System.out.println("********** TYPE MISMATCH ********** \n" +
                    node +
                    "ERRORE RISCONTRATO: \n" +
                    "Nodo: " + node1.data().getName() + " Tipo: " + type1 +
                    "\nNodo: " + node2.data().getName() + " Tipo: " + type2);
            System.exit(0);
        } else
            node.data().setType(type1);
    }


    // MINUS simple_expression:node
    // LPAR expression:node RPAR
    // NOT expression: node

    public void checkUnaryMinus(VisitableNode<Node> node) {
        VisitableNode<Node> child = node.firstChild();
        typeCheckingSimpleExpr(child);
        node.data().setType(child.data().getType());
    }

    public void checkOpAndNotAndExpr(VisitableNode<Node> node) {
        VisitableNode<Node> child = node.getChild(0);
        checkExprOp(child);
        String type = child.data().getType();
        node.data().setType(type);
    }

    // simple_expression
    // quando invocato bisogna passare il figlio
    // prendo il primo figlio e applico il type checking in base alla produzione da applicare
    public void typeCheckingSimpleExpr(VisitableNode<Node> firstChild) {
        switch (firstChild.data().getName()) {

            case Constant.ADD_OP: {
                checkAddingMultOperator(firstChild);
                break;
            }
            case Constant.MUL_OP: {
                checkAddingMultOperator(firstChild);
                break;
            }
            case Constant.VAR_NODE: {
                try {
                    checkIdentifier(firstChild);
                    break;
                } catch (NonDeclarationException e) {
                    System.out.println(e.getMessage());
                    System.exit(0);


                }
            }
            case Constant.CONST_NODE: {
                checkConstOp(firstChild);
                break;
            }
            case Constant.EXPR_NODE: {
                checkExprOp(firstChild.firstChild());
                break;
            }
            case Constant.NOT_NODE: {
                checkOpAndNotAndExpr(firstChild);
                break;
            }
            case Constant.UNARY_MINUS_NODE: {
                checkUnaryMinus(firstChild);
                break;
            }
        }

    }

    public void checkRelOp(VisitableNode<Node> node) {
        VisitableNode<Node> node1 = node.getChild(1);
        VisitableNode<Node> node2 = node.getChild(2);
        //assegna il tipo ai figli
        switch (node1.data().getName()) {
            case Constant.VAR_NODE: {
                try {
                    checkIdentifier(node1);
                    break;
                } catch (NonDeclarationException e) {
                    System.out.println(e.getMessage());
                    System.exit(0);

                }
            }
            case Constant.CONST_NODE: {
                checkConstOp(node1);
                break;
            }
        }

        switch (node2.data().getName()) {
            case Constant.VAR_NODE: {
                try {
                    checkIdentifier(node2);
                    break;
                } catch (NonDeclarationException e) {
                    System.out.println(e.getMessage());
                    System.exit(0);

                }
            }
            case Constant.CONST_NODE: {
                checkConstOp(node2);
                break;
            }
        }
        String type1 = node1.data().getType();
        String type2 = node2.data().getType();
        if (!(type1.equals(type2))) {
            System.out.println("********** TYPE MISMATCH ********** \n" +
                    node +
                    "ERRORE RISCONTRATO: \n" +
                    "Variabile : " + node1.firstChild().firstChild().data().getName() + " Tipo: " + type1 +
                    "\nVariabile : " + node2.firstChild().firstChild().data().getName() + " Tipo: " + type2);
            System.exit(0);

        } else node.data().setType(type1);
    }

    // expression: valuta il tipo di expression valutando il type system dei figli
    // i figli di expression possono essere o SimpleExprOp o RelationalOP
    public void checkExprOp(VisitableNode<Node> node) {
        VisitableNode<Node> child = node.getChild(0);
        switch (node.data().getName()) {
            case Constant.SIMPLE_NODE: {
                typeCheckingSimpleExpr(child);
                node.data().setType(child.data().getType());
                break;
            }
            case Constant.REL_OP: {
                checkRelOp(node);
                break;
            }
        }
    }

    public void addProcedure(VisitableNode<Node> node) {
        int i = (stack.size()) - 2;
        Node lastTop = stack.get(i);
        try {
            lastTop.addToTable(node.data().getName(), node.data());
        } catch (DeclarationException e) {
            System.out.println(e.getMessage());
        }
    }

    public void checkConstOp(VisitableNode<Node> node) {
        VisitableNode<Node> constantNode = node.getChild(0);
        VisitableNode<Node> valueNode = constantNode.getChild(0);
        String type;
        if (valueNode.data().getName().startsWith("'"))
            type = "character";
        else if (valueNode.data().getName().startsWith("\""))
            type = "string";
        else if (valueNode.data().getName().equals("true") || valueNode.data().getName().equals("false"))
            type = "boolean";
        else
            type = "integer";
        valueNode.data().setType(type);
        constantNode.data().setType(type);
        node.data().setType(type);
    }


}









