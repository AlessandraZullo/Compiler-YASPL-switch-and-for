package Exception;

/**
 * Created by a on 14/01/17.
 */
public class DeclarationException extends  Exception{

    public DeclarationException(){

            super("Errore la dichiarazione della variabile");

        }


    public DeclarationException(String name){
        super("\n ************************** Errore **********************\n La dichiarazione della variabile "+name +" è già presente");
    }

}

