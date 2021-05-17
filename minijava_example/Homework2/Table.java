import java.util.ArrayList;

class Table {
    ArrayList <Entry> table = new ArrayList <Entry> ();
    ArrayList <MethodEntry> methods = new ArrayList <MethodEntry> ();

    public ArrayList <Entry> getTable() {
        return this.table;
    }

    public ArrayList <MethodEntry> getMethods() {
        return this.methods;
    }
}
