#!/usr/bin/perl
# $Id: gmml2mapp.pl,v 1.4 2005/09/30 16:32:26 gontran Exp $

use strict;
use warnings;

use File::Basename;
use File::Copy;

use gmml::reader;
use gmml::writer::mapp;

die "Supply GMML filename on commandline." unless defined $ARGV[0];
die "GMML input must be a regular file. exiting." unless ( -f $ARGV[0]);


package main;

my $gm_tpl = "h:\\bigcat_just_cvs\\reactome_genmapp\\dev\\MAPPTmpl.gtp";

# Reading
my $reader = gmml::reader->new();

# set the file to parse
$reader->{infile} = $ARGV[0];

# debuggin?  Open up the debugfile here so we can pass the same handle to
# the writer and integrate debugging output.
$reader->{debug} 			= 1;
$reader->{debug_logfile} 	= "__gmml2mapp.log";
$reader->open_debugfile();

# wRiting
my $riter = new gmml::writer::mapp;

# get a basename for a new output extension
my $bn = basename( $ARGV[0], '.xml', '.mapp');

# where the writer is to put the output.  Copy the template GenMAPP mapp
# file to the new output location.
copy( $gm_tpl, "$bn.mapp") or die "Copy GenMAPP template file failed: $!";

$riter->set_outfile_path( "$bn.mapp");

# debugging?
$riter->{debug} = 1;
# pass in a debug file handle (optional)
$riter->{debug_fh} = $reader->{debug_fh};


# what we really want to do is to link up the reader and writer modules to
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

# different for creating mapps, be sure to cleanup the "dynamic" dsn and
# the DBI handle.
$riter->cleanup();

__END__
vim:ts=4:
