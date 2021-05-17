import java.util.ArrayList;

class MethodEntry {
    ArrayList <Entry> table = new ArrayList <Entry>();
    ArrayList <Entry> param = new ArrayList <Entry>();
    Entry method = new Entry();

    public ArrayList <Entry> getTable() {
        return this.table;
    }

    public ArrayList <Entry> getParam() {
        return this.param;
    }

    public Entry getMethod() {
        return this.method;
    }
}
