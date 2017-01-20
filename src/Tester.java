import java_cup.runtime.Symbol;
import Visitor.*;

import javax.xml.transform.Result;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by a on 10/21/2016.
 */
public class Tester {

    public static void main(String[] args) throws Exception {

        Lexer l = new Lexer(new FileReader("programmaOutput"));

        Symbol s = null;
        while ((s = l.next_token()).sym != 0)
            ;//System.out.println(s.sym);


        parser p = new parser(new Lexer(new FileReader("programmaOutput")), "");
          try {
        //RESULT contiene l'albero sintattico
       Object RESULT = p.parse().value;
            SemanticVisitor visitor = new SemanticVisitor();
            visitor.visit((Visitable) RESULT);
            CVisitor cvisitor = new CVisitor();
           String r=  cvisitor.visit((Visitable) RESULT);
            System.out.println(r);
            } catch(Exception e){
          e.printStackTrace();
        }

    }
    }


