package supportItems;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by a on 10/21/2016.
 */
public class Token {

    private int tipo;
    private String lessema;
    private String valore;
    private Map.Entry<String, HashMap<String, String>> entry;

    public Token(int tipo, String lessema) {
        this.tipo = tipo;
        this.lessema = lessema;
    }

    public Token(String valore, int tipo) {
        this.tipo = tipo;
        this.valore = valore;
    }

    public void setEntry(Map.Entry<String, HashMap<String, String>> entry){
        this.entry = entry;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getLessema() {
        return lessema;
    }

    public void setLessema(String lessema) {
        this.lessema = lessema;
    }

    public String getValore() {
        return valore;
    }

    public void setValore(String valore) {
        this.valore = valore;
    }

    public boolean isInTable() {
        return !(entry == null);
    }

    @Override
    public String toString() {

        String to_return = "Tipo: " + tipo + (valore != null ? ", Valore: " + valore : "")
                + (lessema != null ? ", Lessema: " + lessema : "");

        if (isInTable()) {
            String s_entry = " KEY " + entry.getKey() + "{";

            int i = 0;


            for (Map.Entry<String, String> attribute : entry.getValue().entrySet()) {

                s_entry += "Attribute " + i++ + " " + attribute.getKey() + ": " + attribute.getValue();

            }

            s_entry += "}";

            to_return += s_entry;
        }
        return to_return;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token = (Token) o;

        if (tipo != token.tipo) return false;
        if (lessema != null ? !lessema.equals(token.lessema) : token.lessema != null) return false;
        if (valore != null ? !valore.equals(token.valore) : token.valore != null) return false;
        return entry != null ? entry.equals(token.entry) : token.entry == null;

    }

}

