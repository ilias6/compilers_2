all: compile

compile:
	java -jar ../jtb132di.jar -te minijava.jj
	java -jar ../javacc5.jar minijava-jtb.jj
	javac Main.java SymbolVisitor.java TypeVisitor.java Table.java MethodEntry.java ClassEntry.java SymbolTable.java Entry.java

clean:
	rm -f *.class *~
