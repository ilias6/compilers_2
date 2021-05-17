class ClassEntry extends Entry {
    ClassEntry inheritsFrom = null;
    Table table = new Table ();
    int varCounter;
    int methodCounter;

    public Table getTable() {
        return this.table;
    }

    public ClassEntry getInhClass() {
        return this.inheritsFrom;
    }

    public void setInhClass(ClassEntry e) {
        this.inheritsFrom = e;
    }

    public int getVarCounter() {
        return this.varCounter;
    }

    public int getMethodCounter() {
        return this.methodCounter;
    }

    public void raiseVarCounter(int val) {
        this.varCounter += val;
    }

    public void raiseMethodCounter(int val) {
        this.methodCounter += val;
    }

    public void setCounters() {
        this.varCounter = 0;
        this.methodCounter = 0;
    }

    public void setCounters(int c1, int c2) {
        this.varCounter = c1;
        this.methodCounter = c2;
    }
}
