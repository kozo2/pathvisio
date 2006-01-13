# $Id: Makefile,v 1.3 2005/10/21 12:29:31 gontran Exp $
#
# Note this makefile working for Win32 + Cygwin gnu make and j2se sdk 1.4.5
# or so.
#

#cpth = H:\\src\\genmapp_2_gmml-grz\\src\\lib
#		-cp .:$(cpth)/jaxb/jaxb-api.jar:$(cpth)/jaxb/jaxb-impl.jar:$(cpth)/jaxb/jaxb-libs.jar:$(cpth)/jaxb/jaxb-xjc.jar 
#cpre = /cygdrive/h/src/genmapp_2_gmml-grz/src/lib

cpre = lib

myclasspath = .\;$(cpre)/jaxb/jaxb-api.jar\;$(cpre)/jaxb/jaxb-impl.jar\;$(cpre)/jaxb/jaxb-libs.jar\;$(cpre)/jaxb/jaxb-xjc.jar\;$(cpre)/relaxngDatatype.jar\;$(cpre)/xsdlib.jar

classdir = gmapp2gmml_classes

clean : 
	find ./fileInOut -iname '*.class' | xargs rm -fv
	find ./gmml -iname '*.class' | xargs rm -fv
	find ./converter -iname '*.class' | xargs rm -fv
	rm -f Converter.class

classes :
	javac -classpath $(myclasspath) @java_srclist.txt
#	javac -Xlint:unchecked -d gmapp2gmml_classes -classpath $(myclasspath) @java_srclist.txt


jar : classes 
	jar cmf Manifest.txt ../genmap2gmml.jar \
		Converter.class \
		converter/*.class  \
		fileInOut/*.class \
		gmml/*.class  \
		gmml/impl/*.class \
		gmml/impl/runtime/*.class \
		gmml/jaxb.properties \

foo : 
	javac -classpath $(myclasspath) converter/foo.java
		gmml/impl/runtime/*.class
