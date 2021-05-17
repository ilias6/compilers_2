import syntaxtree.*;
import visitor.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        if(args.length != 1){
            System.err.println("Usage: java Main <inputFile>");
            System.exit(1);
        }

        FileInputStream fis = null;
        try{
            fis = new FileInputStream(args[0]);
            MiniJavaParser parser = new MiniJavaParser(fis);

            Goal root = parser.Goal();

            System.err.println("Program parsed successfully.");

            SymbolVisitor eval1 = new SymbolVisitor();
            root.accept(eval1, null);

            SymbolTable table = eval1.getTable();

            String type = table.checkObjects();
            if (type != null)
                throw new Exception("Unknown type: " + type);

            TypeVisitor eval2 = new TypeVisitor(table);
            root.accept(eval2, null);

            System.out.println(table.getOffset());

        }
        catch(ParseException ex){
            System.out.println(ex.getMessage());
        }
        catch(FileNotFoundException ex){
            System.err.println(ex.getMessage());
        }
        finally{
            try{
                if(fis != null) fis.close();
            }
            catch(IOException ex){
                System.err.println(ex.getMessage());
            }
        }
    }
}


// class SymbolNode {
//
//   ArrayList <Entry> table = null;
//   ArrayList <SymbolNode> nextScopes = null;
//   int level = -1;
//   int maxLevel = -1;
//
//   public SymbolNode() {
//       this.nextScopes = new ArrayList <SymbolNode> ();
//       this.level = 0;
//       this.maxLevel = 0;
//   }
//
//   public void enter() {
//       SymbolNode n = this.enterScope(this.level);
//       n.nextScopes.add(new SymbolNode());
//       n.nextScopes.get(n.nextScopes.size() -1).nextScopes = new ArrayList <SymbolNode> ();
//
//
//       if (this.level == this.maxLevel)
//           this.maxLevel += 1;
//       this.level += 1;
//
//   }
//
//   public SymbolNode enterScope(int i) {
//       if (i == 0)
//           return this;
//
//       return this.nextScopes.get(this.nextScopes.size() -1).enterScope(i-1);
//   }
//
//   public boolean insert(Entry name) {
//       SymbolNode n = this.enterScope(this.level);
//
//       if (n.table == null)
//           n.table = new ArrayList <Entry> ();
//
//       for (int i = 0; i < n.table.size(); ++i)
//           if (n.table.get(i).getName() == name.getName())
//               return false;
//
//       n.table.add(name);
//       return true;
//   }
//
//   public String lookup(String ident) {
//       if (this == null)
//           return null;
//       String str = null;
//       if (this.nextScopes.size() > 0)
//           str = this.nextScopes.get(this.nextScopes.size() -1).lookup(ident);
//       // System.out.println("------"+str);
//       for (int i = 0; i < this.table.size(); ++i)
//           if (this.table.get(i).getName() == ident)
//               return this.table.get(i).getType();
//
//       return str;
//
//   }
//
//   public void exit() {
//       if (this.level > 0)
//           this.level -= 1;
//   }
//
// }
