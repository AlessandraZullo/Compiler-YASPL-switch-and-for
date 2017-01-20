package supportItems;

import java.util.HashMap;
import java.util.*;
import Exception.*;
/**
 * Created by a on 13/01/17.
 */
public class Node {

    private String name; //identificatore del nodo
    private String type;
    private HashMap<String,Node> table_of_symbols;
    private boolean added = false;

    public Node(String name) {

        this.name = name;
        table_of_symbols = new HashMap<String,Node>();
    }

    public String toString(){
        return this.getName();
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addToTable(String name, Node node) throws DeclarationException{

        if( (this.table_of_symbols.put(name,node))== null){
            System.out.println("Identificatore "+name+ " aggiunto alla tabella");
        this.printTable();}
        else
            throw  new DeclarationException(name);
    }

    // metodo per controllare la presenza di un nodo nella tabella dei simboli
    public boolean checkElement(String name){
        return table_of_symbols.get(name)!=null;
    }

    //metodo per ottenere una entry presente nella tabella dei simboli
    public Node getEntry(String name){
        return table_of_symbols.get(name);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    //Da invocare per capire se bisogna creare un nuovo scope
    public boolean newScope(){
        if(this.name.equals(Constant.PROGRAM_NODE) || this.name.equals(Constant.PROC_NODE))
            return true;
        return false;}

    public boolean added() {
        return added;
    }

    public void setIdentifier(boolean identifier) {
        this.added = identifier;
    }

    public boolean isConstant(){
        if(this.name.equals("boolean") || this.name.equals("integer")){
            return true;}
        return false;
    }

    public String infoNode(){
        return "Node_name:" + this.getName()+" Node_type:" +this.getType();
    }

    public void printTable(){}

    public void printTable2() {
        System.out.println();
        System.out.println("------- La tabella di "+this.getName()+" contiene: -------");

        for (Map.Entry<String, Node> entry : table_of_symbols.entrySet()) {
            System.out.println("Name : " + entry.getKey());
            System.out.println(entry.getValue().infoNode());
            System.out.println("......................................");
        }
        System.out.println("------- Fine stampa tabella -------");
    }


    }

