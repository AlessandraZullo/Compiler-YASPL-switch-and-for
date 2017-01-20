package Exception;

/**
 * Created by a on 14/01/17.
 */
public class NonDeclarationException extends Exception {

    public NonDeclarationException(){

        super("La variabile non è stata dichiarata");

    }


    public NonDeclarationException(String name){
        super("\n ************************** Errore **********************\n La dichiarazione della variabile "+name +" non è presente");
    }

}
