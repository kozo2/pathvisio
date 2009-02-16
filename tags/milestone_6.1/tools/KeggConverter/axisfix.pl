#!/usr/bin/perl -i

# script to fix KEGGBindingStub.java generated by axis-1_2alpha

while (<>) {
	s/org\.apache\.axis\.utils\.//;
	s/org\.apache\.axis\.soap\.//;
	print;
	if (/^package/) {
		print "import org.apache.axis.utils.*;\n";
		print "import org.apache.axis.soap.*;\n";
	}
}
