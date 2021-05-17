import syntaxtree.*;
import visitor.*;
import java.util.ArrayList;


class TypeVisitor extends GJDepthFirst<String, Boolean>{

    SymbolTable table = null;

    public TypeVisitor(SymbolTable t) {
        this.table = t;
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
    public String visit(MainClass n, Boolean argu) throws Exception {

        String classname = n.f1.accept(this, true);
        this.table.enterClass(classname);

        for (Node node: n.f15.nodes)
            node.accept(this, false);

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
    public String visit(ClassDeclaration n, Boolean argu) throws Exception {

        String classname = n.f1.accept(this, true);
        this.table.enterClass(classname);

        for (Node node: n.f4.nodes)
            node.accept(this, false);

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
    public String visit(ClassExtendsDeclaration n, Boolean argu) throws Exception {

        String classname = n.f1.accept(this, true);
        this.table.enterClass(classname);

        for (Node node: n.f6.nodes)
            node.accept(this, false);

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
    public String visit(MethodDeclaration n, Boolean argu) throws Exception {

        // String myType = n.f1.accept(this, false);
        String myName = n.f2.accept(this, true);
        this.table.enterMethod(myName);

        for (Node node: n.f8.nodes)
            node.accept(this, false);

        String retType = n.f10.accept(this, false);
        String methodType = this.table.getMethodType(this.table.getClassName(), myName);
        if (!retType.equals(methodType)) {
          boolean flag = false;
          if (retType != "int" && retType != "int[]" && retType != "boolean" && retType != "String[]") {
              ArrayList <String> superClasses = this.table.getInhClasses(retType);
              for (int j = 0; j < superClasses.size(); ++j) {
                  String name = superClasses.get(j);
                  if (name.equals(methodType)) {
                      flag = true;
                      break;
                  }
              }
          }

          if (!flag)
              throw new Exception("Incompatible types: " + retType + " " + methodType);
        }

        this.table.exitMethod();

        return null;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    @Override
    public String visit(AssignmentStatement n, Boolean argu) throws Exception {

        String ident = n.f0.accept(this, true);
        String type1 = this.table.lookup(ident, 1);

        if (type1 == null)
            throw new Exception(ident + " is not declared");

        String type2 = n.f2.accept(this, false);

        boolean flag = false;
        if (type1 != type2) {
            if (type2 != "int" && type2 != "int[]" && type2 != "boolean" && type2 != "String[]") {
                ArrayList <String> superClasses = this.table.getInhClasses(type2);
                for (int j = 0; j < superClasses.size(); ++j) {
                    String name = superClasses.get(j);
                    if (name.equals(type1)) {
                        flag = true;
                        break;
                    }
                }
            }

            if (!flag)
                throw new Exception("Invalid assignment: " + type1 + " = " + type2);
        }

        return null;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    @Override
    public String visit(ArrayAssignmentStatement n, Boolean argu) throws Exception {
        String ident = n.f0.accept(this, true);
        String type1 = this.table.lookup(ident, 1);
        if (type1 != "int[]")
            throw new Exception(ident + " is not an array!");

        String type2 = n.f2.accept(this, false);
        if (type2 != "int")
            throw new Exception("Invalid index type: " + type2);

        String type3 = n.f5.accept(this, false);
        if (type3 != "int")
            throw new Exception("Invalid assignment: int" + " = " + type3);

        return null;
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    @Override
    public String visit(IfStatement n, Boolean argu) throws Exception {
        String ifExpr = n.f2.accept(this, false);
        if (ifExpr != "boolean")
            throw new Exception("if expression is not boolean but: " + ifExpr);

        n.f4.accept(this, false);
        n.f6.accept(this, false);
        return null;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    @Override
    public String visit(WhileStatement n, Boolean argu) throws Exception {
        String whileExpr = n.f2.accept(this, false);
        if (whileExpr != "boolean")
            throw new Exception("while expression is not boolean but: " + whileExpr);

        n.f4.accept(this, false);
        return null;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    @Override
    public String visit(PrintStatement n, Boolean argu) throws Exception {
        String printType = n.f2.accept(this, false);
        if (printType != "int")
            throw new Exception("println argument is not int but " + printType);

        return null;
    }

    /**
     * f0 -> AndExpression()
     *       | CompareExpression()
     *       | PlusExpression()
     *       | MinusExpression()
     *       | TimesExpression()
     *       | ArrayLookup()
     *       | ArrayLength()
     *       | MessageSend()
     *       | PrimaryExpression()
     */
    @Override
    public String visit(Expression n, Boolean argu) throws Exception {
        String retVal = n.f0.accept(this, false);
        if (retVal == "this")
            return this.table.getClassName();
        return retVal;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "&&"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(AndExpression n, Boolean argu) throws Exception {
       String type1 = n.f0.accept(this, argu);
       String type2 = n.f2.accept(this, argu);

       if ((type1 != "boolean") || (type2 != "boolean"))
          throw new Exception("And (&&) expression between: "+ type1 + " and " + type2);

       return "boolean";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(CompareExpression n, Boolean argu) throws Exception {
      String type1 = n.f0.accept(this, argu);
      String type2 = n.f2.accept(this, argu);

      if ((type1 != type2) || (type1 != "int") || (type2 != "int"))
         throw new Exception("Compare (<) expression between: "+ type1 + " and " + type2);

      return "boolean";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(PlusExpression n, Boolean argu) throws Exception {
      String type1 = n.f0.accept(this, argu);
      String type2 = n.f2.accept(this, argu);

      if ((type1 != type2) || (type1 != "int") || (type2 != "int"))
         throw new Exception("Plus (+) expression between: "+ type1 + " and " + type2);

      return "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(MinusExpression n, Boolean argu) throws Exception {
      String type1 = n.f0.accept(this, argu);
      String type2 = n.f2.accept(this, argu);

      if ((type1 != type2) || (type1 != "int") || (type2 != "int"))
         throw new Exception("Minus (-) expression between: "+ type1 + " and " + type2);

      return "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    @Override
    public String visit(TimesExpression n, Boolean argu) throws Exception {
      String type1 = n.f0.accept(this, argu);
      String type2 = n.f2.accept(this, argu);

      if ((type1 != type2) || (type1 != "int") || (type2 != "int"))
         throw new Exception("Times (*) expression between: "+ type1 + " and " + type2);

      return "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    @Override
    public String visit(ArrayLookup n, Boolean argu) throws Exception {
      String type1 = n.f0.accept(this, argu);
      String type2 = n.f2.accept(this, argu);

      if (type1 != "int[]")
          throw new Exception("Type is not int[] but "+ type1);
      if (type2 != "int")
          throw new Exception("Array index is not int but " + type2);


      return "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */

    @Override
    public String visit(ArrayLength n, Boolean argu) throws Exception {
      String type = n.f0.accept(this, argu);

      if (type != "int[]")
         throw new Exception(type + " has not attribute length");

      return "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */

    @Override
    public String visit(MessageSend n, Boolean argu) throws Exception {
      String clName = n.f0.accept(this, true);
      String methodName = n.f2.accept(this, true);
      String className = this.table.lookup(clName, 1);
      if (className == null)
          className = clName;

      String paramList1 = n.f4.accept(this, null);
      String[] types;
      if (paramList1 != null)
          types = paramList1.split(" ");
      else
          types = null;

      ArrayList <Entry> paramList2 = this.table.getMethodParam(className, methodName);

      if ((paramList2 != null) && (paramList1 != null) && (types.length != paramList2.size()))
          throw new Exception("Different number of parameters passed to: "+
                              className + "." + methodName + "()");


      if (paramList2 == null)
          throw new Exception("No method "+ methodName+ "() found in class " + className);

      if (paramList2.size() == 0)
          return this.table.getMethodType(className, methodName);


      for (int i = 0; i < types.length; ++i) {
          boolean flag = false;
          String paramType = paramList2.get(i).getType();

          if (paramType != "int" && paramType != "int[]" && paramType != "boolean" && paramType != "String[]") {
              if (!types[i].equals(paramType)) {
                  ArrayList <String> superClasses = this.table.getInhClasses(types[i]);
                  for (int j = 0; j < superClasses.size(); ++j) {
                      String name = superClasses.get(j);
                      if (name.equals(paramType)) {
                          flag = true;
                          break;
                      }
                  }

              }
              else {
                  flag = true;
              }
              if (!flag)
                  throw new Exception("Wrong parameter passed to: "+
                        className + "." + methodName + "\n\t\tExpected: "
                        + paramList2.get(i).getType() +
                        " Given: " + types[i]);

          }
          else if (!types[i].equals(paramType)) {
              throw new Exception("Wrong parameter passed to: "+
                    className + "." + methodName + "\n\t\tExpected: "
                    + paramList2.get(i).getType() +
                    " Given: " + types[i]);
          }
      }

      return this.table.getMethodType(className, methodName);
    }

    /**
     * f0 -> Expression()
     * f1 -> ExpressionTail()
     */
    @Override
    public String visit(ExpressionList n, Boolean argu) throws Exception {
        String ret = n.f0.accept(this, false);
        ret += n.f1.accept(this, false);
        return ret;
    }

    /**
     * f0 -> ( ExpressionTerm() )*
     */
    @Override
    public String visit(ExpressionTail n, Boolean argu) throws Exception {
      String ret = "";
      for (Node node: n.f0.nodes)
          ret += " " + node.accept(this, true);
       return ret;
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    @Override
    public String visit(ExpressionTerm n, Boolean argu) throws Exception {
       return n.f1.accept(this, argu);
    }


    /**
     * f0 -> IntegerLiteral()
     *       | TrueLiteral()
     *       | FalseLiteral()
     *       | Identifier()
     *       | ThisExpression()
     *       | ArrayAllocationExpression()
     *       | AllocationExpression()
     *       | NotExpression()
     *       | BracketExpression()
     */
    public String visit(PrimaryExpression n, Boolean argu) throws Exception {
       return n.f0.accept(this, argu);
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    @Override
    public String visit(ArrayAllocationExpression n, Boolean argu) throws Exception {
       String type = n.f3.accept(this, false);
       if (type != "int")
          throw new Exception("Wrong array allocation value: " + type);

       return "int[]";
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    @Override
    public String visit(AllocationExpression n, Boolean argu) throws Exception {

      String className = n.f1.accept(this, true);
      if (this.table.findClass(className) == null)
            throw new Exception("new " + className + "\n\t\t" + className + ": is not a class!");

       return className;
    }

    /**
     * f0 -> "!"
     * f1 -> PrimaryExpression()
     */
    @Override
    public String visit(NotExpression n, Boolean argu) throws Exception {
       return n.f1.accept(this, argu);
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    @Override
    public String visit(BracketExpression n, Boolean argu) throws Exception {
       return n.f1.accept(this, argu);
    }

    @Override
    public String visit(Identifier n, Boolean argu) throws Exception {
        if (argu)
            return n.f0.toString();

        String type = this.table.lookup(n.f0.toString(), 1);
        if (type == null)
            throw new Exception(n.f0.toString() + " is not declared!");

        return type;
    }

    @Override
    public String visit(IntegerLiteral n, Boolean argu) {
            return "int";
    }

    @Override
    public String visit(TrueLiteral n, Boolean argu) {
            return "boolean";
    }

    @Override
    public String visit(FalseLiteral n, Boolean argu) {
            return "boolean";
    }

    @Override
    public String visit(ThisExpression n, Boolean argu) {
            return n.f0.toString();
    }
}
