package supportItems;

/**
 * Created by a on 10/20/2016.
 */
public class Attributo {
    
    private String key, value;

    public Attributo(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public Attributo(String key, int value) {
        this.key = key;
        this.value = String.format("%d", value);
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    
}
