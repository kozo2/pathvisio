#!/usr/bin/perl

#Converts all .gpml files in a directory (including subdirectories) to SVG
#Depends on pathvisio_convert.jar (build this jar using ant)
#Usage:
#perl convert2svg.pl directory

use strict;
use warnings;
use File::Find;

my $dir = shift @ARGV;

my @files;

find(\&wanted, $dir);

for my $f (@files) {
	print "java -jar ../pathwaywiki/www/bin/pathvisio_convert.jar $f .svg";
	system("java", "-jar", "../pathwaywiki/www/bin/pathvisio_convert.jar", $f, ".svg");
}

sub wanted {
	if(m/.gpml$/) {
		print $_ . "\n";
		push @files, $File::Find::name;
	}
}
