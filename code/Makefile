# Define build directory
BUILD_DIR := ../bin

# Find all .java files in the current directory and subdirectories
SOURCES := $(wildcard *.java */*.java)

# Compiler and flags
JAVAC := javac
JAVAC_FLAGS := -d $(BUILD_DIR)

# Default target: Compile all files together
all:
	@mkdir -p $(BUILD_DIR)
	@$(JAVAC) $(JAVAC_FLAGS) $(SOURCES)

# Clean compiled files
clean:
	@rm -rf $(BUILD_DIR)

.PHONY: all clean