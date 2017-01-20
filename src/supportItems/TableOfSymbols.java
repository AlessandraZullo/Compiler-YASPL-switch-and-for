package supportItems;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by a on 10/20/2016.
 */
public class TableOfSymbols {

    private static HashMap<String, HashMap<String, String>> table;

    public TableOfSymbols() {
        this.table = new HashMap<>();
    }

    public static void printTable() {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("------- Inizio stampa tabella -------");

        for (Map.Entry<String, HashMap<String, String>> entry : table.entrySet()) {
            System.out.println("Key: " + entry.getKey());

            for (Map.Entry<String, String> attributes : entry.getValue().entrySet()) {
                System.out.println(attributes.getKey() + ": " + attributes.getValue());
            }
            System.out.println();
        }
    }

    public Map.Entry<String,HashMap<String, String>> addLexem(String lexem){
        if(!table.containsValue(lexem))
            table.put(lexem, new HashMap<>());

        for(Map.Entry<String, HashMap<String, String>> entry : table.entrySet()){
            if(entry.getKey().equals(lexem))
                return entry;
        }

        return null;
    }

    public String getAttribute(String keyOfEntry, String attributeName){
        if(table.containsKey(keyOfEntry))
            return table.get(keyOfEntry).get(attributeName);

        return null;

    }

    public void addAttribute(String keyOfEntry, Attributo attributo){
        if(table.containsKey(keyOfEntry))
            table.get(keyOfEntry).put(attributo.getKey(), attributo.getValue());

    }
}
