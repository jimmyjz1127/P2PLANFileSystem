JAVAC = javac
JAVA_FILES = $(wildcard *.java)
CLASS_FILES = $(patsubst %.java, %.class, $(JAVA_FILES))


all: $(CLASS_FILES)

%.class: %.java
	@$(JAVAC) $<


clean:
	@rm -f *.class

.PHONY: all clean
