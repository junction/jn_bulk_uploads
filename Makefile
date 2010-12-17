#
# define compiler and compiler flag variables
#

JFLAGS = -g -classpath lib:classes:. -d classes -Xlint:unchecked
JC = javac
J  = java -classpath lib:classes:.
SRC = src/
OUT = classes/
#
# Clear any default targets for building .class files from .java files; we 
# will provide our own target entry to do this in this makefile.
# make has a set of default targets for different suffixes (like .c.o) 
# Currently, clearing the default for .java.class is not necessary since 
# make does not have a definition for this target, but later versions of 
# make may, so it doesn't hurt to make sure that we clear any default 
# definitions for these
#

.SUFFIXES: .java .class

#
# Here is our target entry for creating .class files from .java files 
# This is a target entry that uses the suffix rule syntax:
#	DSTS:
#		rule
# 'TS' is the suffix of the target file, 'DS' is the suffix of the dependency 
# file, and 'rule'  is the rule for building a target	
# '$*' is a built-in macro that gets the basename of the current target 
# Remember that there must be a < tab > before the command line ('rule') 
#

.java.class:
	$(JC) $(JFLAGS) $*.java

#
# CLASSES is a macro consisting of 4 words (one for each java source file)
#

CLASSES = 

#
# The default make target entry
#

default: classes
	$(JC) $(JFLAGS) $(SRC)com/jctn/bulkupload/util/*.java				
	$(JC) $(JFLAGS) $(SRC)com/jctn/bulkupload/model/json/*.java				
	$(JC) $(JFLAGS) $(SRC)com/jctn/bulkupload/model/*.java				
	$(JC) $(JFLAGS) $(SRC)com/jctn/bulkupload/service/ws/*.java				
	$(JC) $(JFLAGS) $(SRC)com/jctn/bulkupload/controller/*.java		
	$(JC) $(JFLAGS) $(SRC)com/jctn/bulkupload/*.java	
	cp -r $(SRC)resources $(OUT)com/jctn/bulkupload/resources
#
# This target entry uses Suffix Replacement within a macro: 
# $(name:string1=string2)
# 	In the words in the macro named 'name' replace 'string1' with 'string2'
# Below we are replacing the suffix .java of all words in the macro CLASSES 
# with the .class suffix
#

classes: $(CLASSES:.java=.class)

#
# RM is a predefined macro in make (RM = rm -f)
#

clean:
	$(RM) -r $(OUT)com
	$(RM) -r $(OUT)resources
run:
	$(J) com.jctn.bulkupload.BulkUploader

phone_add:
	$(J) com.jctn.bulkupload.service.ws.PhoneAdd
