#!/usr/bin/perl
# $Id: qc_gmml2mapp.pl,v 1.3 2005/12/04 21:52:39 gontran Exp $

# you'll need to edit up some of the variables and debuggin information to
# suit your needs below.

use strict;
use warnings;

use File::Basename;
use File::Copy;

use gmml::reader;
use gmml::writer::mapp;

sub usage
{
	my $prgm = basename( $0);
	print "Usage: $prgm gmml_file.xml\n";
	my $addl = shift;
	if (defined $addl)
	{
		print $addl . "\n";
	}
	exit(1);
}

# sanity check
usage( "Supply a GMML filename on commandline.") 
	unless defined $ARGV[0];
usage( "The GMML input must be a regular file. exiting.") 
	unless ( -f $ARGV[0]);


package main;

# you'll want to fix these up to the path of the template file and the
# location/folder you'd like the output put in.
my $gm_tpl = "l:\\bigcat\\reactome_genmapp\\dev\\MAPPTmpl.gtp";
my $outpath = 'l:\tmp\gm2gmml\out\cp';

# Reading
my $reader = gmml::reader->new();

# set the file to parse
$reader->{infile} = $ARGV[0];

# debuggin?  Open up the debugfile here so we can pass the same handle to
# the writer and integrate debugging output.

# turn off debugging with 0, no log then for the reader
$reader->{debug} 			= 1; 
$reader->{debug_logfile} 	= "__gmml2mapp.log";
$reader->open_debugfile();

# wRiting
my $riter = new gmml::writer::mapp;

# set your number format:  default is 'us'
#$riter->set_number_fmt( 'us'); 
$riter->set_number_fmt( 'eu'); 

# debugging?  Similar to above.
$riter->{debug} = 1;
# pass in a debug file handle (optional)
$riter->{debug_fh} = $reader->{debug_fh};

# NO NEED TO MODIFY ANYTHING BELOW HERE....

# get a basename for a new output extension
my $bn = basename( $ARGV[0], '.xml', '.mapp');
my $out_nm = $bn . "-rt";
my $output = $outpath . "\\" . $out_nm . ".mapp";

# where the writer is to put the output.  Copy the template GenMAPP mapp
# file to the new output location.
copy( $gm_tpl, $output) or die "Copy GenMAPP template file failed: $!";

$riter->set_outfile_path( $output);

# what we are doing here is to link up the reader and writer modules to
# work together simultaneously.  The rationale for this is that xml files
# are potentially large and the mapp files should be created one line at a
# time while the xml file is being read. 

# The aRithmetic: pass the gmml::writer to the parser
$reader->{my_writer} = $riter;

# parse it up -- the whole shot: xml::twig simultaneous examination of gmml
# document and integrated output of csv or mapp file via $riter.
$reader->gmml_parse();

# after we parse up all the goodies, add in the legend and info box
$riter->write_legend();

# different for creating mapps vs. csv files, be sure to cleanup the
# "dynamic" dsn and the DBI handle.
$riter->cleanup();

__END__
vim:ts=4:
