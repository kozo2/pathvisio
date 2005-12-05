<--- GenMAPP to GMML converter Commandline version 	--->
<--- Thomas Kelder, 2005 - t.a.j.kelder@student.tue.nl	--->

Converts a GenMAPP pathway mapp to an xml file formatted by the GMML xml schema

Usage:
	java -jar GenMAPPtoGMML.jar "output directory" "input file"
	
	"Output directory" is optional

Example:
	java -jar GenMAPPtoGMML.jar output/ input/Hs_Fatty_Acid_Degradation.mapp


$Id: Readme.txt,v 1.2 2005/12/04 22:30:27 gontran Exp $

This was the Readme for Kelder's original jar file and distribution.  This
has been modified slightly on rebuilding the genmapp2gmml.jar file based on
modifications of the gmml.xsd and regeneration of the gmml source files in
the object factory in the src directory.

Usage should be as exemplified in the 'hs_contrib_convert.sh' file, which
can be used from a cygwin shell, or otherwise understood and used from a
windows command.com prompt.

There are some urls and informations in the 'build_reqs' directory which I
recommend for getting the pre-requisites for getting this bad boy to work.
Also, you'll be happy if you're haking this code to have cygwin installed
and to use the Makefile in the src dir -- it's worth it.

Feel free to contact me for discussion: gontran.zepeda@bigcat.unimaas.nl
Sun Dec  4 23:30:09 CET 2005
