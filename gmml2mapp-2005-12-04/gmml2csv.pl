#!/usr/bin/perl
# $Id: gmml2csv.pl,v 1.4 2005/09/29 08:58:51 gontran Exp $

use strict;
use warnings;

use File::Basename;

use gmml::reader;
use gmml::writer::csv;

die "supply filename on commandline." unless defined $ARGV[0];
die "gmml input must be a regular file. exiting." unless ( -f $ARGV[0]);


package main;

# Reading
my $reader = gmml::reader->new();

# set the file to parse
$reader->{infile} = $ARGV[0];

# debuggin?  Open up the debugfile here so we can pass the same handle to
# the writer and integrate debugging output.
$reader->{debug} = 1;
$reader->{debug_logfile} = "__gmml2csv.log";
$reader->open_debugfile;

# wRiting
my $riter = new gmml::writer::csv;

# get a basename for a new output extension
my $bn = basename( $ARGV[0], '.xml', '.mapp');

# where the writer is to put the output
$riter->set_outfile_path( "$bn.csv");

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

# parse it up.
$reader->gmml_parse();

# done.
__END__
vim:ts=4:
