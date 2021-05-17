class Entry {
    String type;
    String ident;

    // public Entry(String t, String i) {
    //    this.type = t;
    //    this.ident = i;
    // }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.ident;
    }

    public void setType(String t) {
        this.type = t;
    }

    public void setName(String i) {
        this.ident = i;
    }

    public void print() {
        System.out.println(this.type + " " + this.ident);
    }
}
