import syntaxtree.*;
import visitor.*;

class SymbolVisitor extends GJDepthFirst<String, String >{
    SymbolTable table = new SymbolTable();

    public SymbolTable getTable() {
        return this.table;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    @Override
    public String visit(MainClass n, String argu) throws Exception {

        String className = n.f1.accept(this, null);
        Entry e = new Entry();
        e.setType("Class");
        e.setName(className);
        if (!this.table.insert(e, 0))
            throw new Exception("Duplicate class: " + className);

        String name = n.f11.accept(this, null);

        Entry e2 = new Entry();
        e2.setType("String[]");
        e2.setName(name);

        if (!this.table.insert(e2, 3))
            throw new Exception("Identifier " + name + " is already declared in " + className);

        this.visit(n.f14, null);
        this.visit(n.f15, argu);

        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    @Override
    public String visit(ClassDeclaration n, String argu) throws Exception {

        String className = n.f1.accept(this, null);

        Entry e = new Entry();
        e.setType("Class");
        e.setName(className);
        if (!this.table.insert(e, 0))
            throw new Exception("Duplicate class: " + className);

        this.visit(n.f3, className);
        this.visit(n.f4, className);

        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    @Override
    public String visit(ClassExtendsDeclaration n, String argu) throws Exception {

        String superName = n.f3.accept(this, null);
        String className = n.f1.accept(this, null);
        if (!this.table.parseInheritance(superName, className))
            throw new Exception("Cannot find class: " + superName);

        Entry e = new Entry();
        e.setType("Class");
        e.setName(className);
        if (!this.table.insert(e, superName))
            throw new Exception("Duplicate class: " + className);

        this.visit(n.f5, className);
        this.visit(n.f6, null);

        if (!this.table.checkOverload())
            throw new Exception("Overloading detected in class " + className);

        return null;
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    @Override
    public String visit(MethodDeclaration n, String argu) throws Exception {

        String myType = n.f1.accept(this, null);
        String myName = n.f2.accept(this, null);

        if (argu != null) {
            this.table.addString(argu + "." + myName, 1);
            this.table.raiseMethodCounter(8);
        }
        else {
            this.table.addExtendMethod(myName);
        }

        Entry e = new Entry();
        e.setType(myType);
        e.setName(myName);
        if (!this.table.insert(e, 1))
            throw new Exception("Method " + myName + "() is already declared");

        this.visit(n.f4, argu);
        this.visit(n.f7, null);
        this.visit(n.f8, argu);


        return null;
    }


    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    @Override
    public String visit(FormalParameterList n, String argu) throws Exception {

        this.visit(n.f0, argu);

        if (n.f1 != null)
            this.visit(n.f1, argu);

        return null;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    public String visit(FormalParameterTerm n, String argu) throws Exception {
        return n.f1.accept(this, argu);
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    @Override
    public String visit(FormalParameterTail n, String argu) throws Exception {

        this.visit(n.f0, argu);

        return null;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    @Override
    public String visit(FormalParameter n, String argu) throws Exception{
        String type = n.f0.accept(this, null);
        String name = n.f1.accept(this, null);

        Entry e = new Entry();
        e.setName(name);
        e.setType(type);
        if (!this.table.insert(e, 2))
            throw new Exception("Two parameters have the same name: " + name);

        return null;
    }

    @Override
    public String visit(VarDeclaration n, String argu) throws Exception {

        String myType = n.f0.accept(this, null);
        String myName = n.f1.accept(this, null);

        if (argu != null) {
            this.table.addString(argu + "." + myName, 0);

            switch (myType) {
                case "int" :
                    this.table.raiseVarCounter(4);
                    break;
                case "boolean" :
                    this.table.raiseVarCounter(1);
                    break;
                default :
                    this.table.raiseVarCounter(8);

            }
        }

        Entry e = new Entry();
        e.setName(myName);
        e.setType(myType);
        if (!this.table.insert(e, 3))
          throw new Exception("Identifier " + myName + " is already declared!");

        return null;
    }

    @Override
    public String visit(ArrayType n, String argu) {
        return "int[]";
    }

    public String visit(BooleanType n, String argu) {
        return "boolean";
    }

    public String visit(IntegerType n, String argu) {
        return "int";
    }

    @Override
    public String visit(Identifier n, String argu) {
            return n.f0.toString();
    }
}
