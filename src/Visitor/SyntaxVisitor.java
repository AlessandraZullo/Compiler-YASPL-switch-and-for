package Visitor;

import java.util.Iterator;
import java.io.*;

/**
 * Created by a on 12/01/17.
 */
public class SyntaxVisitor implements Visitor {

    public String content = "";
    public int i=0;
    @Override
    public String visit(Visitable node) {

        this.content = String.format("<%s>",((VisitableNode)node).data());
        Iterator<VisitableNode> childs = ((VisitableNode)node).subtrees().iterator();
        VisitableNode currentNode;
        while(childs.hasNext()) {
            if(!(currentNode = childs.next()).isLeaf()){
                this.content += currentNode.accept(this);
            }
            else{
                if(currentNode.data().toString().equals("integer") || currentNode.data().toString().equals("boolean") || currentNode.data().toString().equals("BreakOp") )
                    this.content += String.format("<%s/>",currentNode.data());
                else
                    this.content += String.format("%s",currentNode.data());
            }

        }
        this.content += String.format("</%s>",((VisitableNode)node).data());
        return this.content;
    }

    public void saveFileXML(){
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("file.xml"), "utf-8"));
            writer.write(this.content);
            writer.close();
        } catch (IOException ex) {
            System.out.println("Errore nella scrittura del file");
        } finally {
            try {writer.close();} catch (Exception ex) {
                System.out.println("Errore durante la chiusura del file");
            }
        }
    }


}
