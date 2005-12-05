$Id: README.txt,v 1.1 2005/12/04 21:52:39 gontran Exp $

To run this gmml->genmapp converter you must be:

(a) satisfy all the perl modules dependencies listed in 'requirements.txt'.

(b) run the code on a win32 machine.

(c) use the pre-written script 'qc_gmml2mapp.pl', after modifying various
file paths and debugging variables to suit.

(c) have a valid gmml document to convert.

usage for qc_gmml2mapp.pl is, for example:

c:\> qc_gmml2mapp.pl gmml_file.xml

See qc_gmml2mapp.pl comments for more details.  There should be no need to
modify the gmml/* modules.  

One caveat: in the gmml.xsd the GeneProduct Name and Id elements are
transposed, this causes no difficulty in the usage of this program or the
java genmapp to gmml converter.

gontran.zepeda@bigcat.unimaas.nl
Sun Dec  4 22:51:01 CET 2005
