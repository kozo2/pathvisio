# $Id: mapp.pm,v 1.6 2005/10/21 12:37:24 gontran Exp $

package gmml::writer::mapp;

use gmml::writer;
@ISA = qw( gmml::writer);

use Carp;
use DBI;
use Math::BigInt;
use Math::BigFloat;
use Win32::ODBC;

use strict;
use warnings;

sub write_legend
{
	my $self = shift;

	# setup a local var to hold the IO::FILE handle
	my $dbgfh;

	#if( $self->{debug})
	#{ 
	#	# since this is classically called after all the writer business,
	#	# open up the debug file again, but with append!
	#	$self->open_debugfile( 'append');
#
#		$dbgfh = ${$self->{debug_fh}};
#		print  $dbgfh "writer::mapp> write_legend\n";
#	}

	# dump a line of InfoBox to the Object table.
	my $sql = 
		'INSERT INTO Objects (Type, CenterX, CenterY, SecondX, '
		. 'SecondY, Width, Height, Rotation, Color) VALUES '
		. "( 'InfoBox', 0, 0, 0, 0, 0, 0, 0, -1)";

#	if( $self->{debug})
#	{
#		print $dbgfh "writer::mapp::write_legend> sql: \n\t$sql\n";
#	}

	my $sth = $self->{dbh}->prepare( $sql) or croak;

	# write it to the output file
	$sth->execute() 
		or croak( "Can't execute insert statement: $DBI::errstr");

	$sth->finish;

	return undef;
}

sub _write_object_dataline
{
	my $self = shift;

	# setup a local var to hold the IO::FILE handle
	my $dbgfh;

	if( $self->{debug})
	{ 
		$dbgfh = ${$self->{debug_fh}};
		print  $dbgfh "writer::mapp> _write_object_dataline\n";
	}

	my @output = (); 

	# get a copy of a list for ordered headers
	my @hdrs = gmml::writer::get_Object_headers();

	# create an output line ordered based on GenMAPP field header list
	foreach my $f ( @hdrs)
	{
		# handle NULL lines by default.
		my $val = $self->{null};
		if( defined $self->{obj_row}->{$f})
		{
			$val = $self->{obj_row}->{$f};
		}

		# handle the ObjKey specifically
		if( $f eq "ObjKey")
		{
			# Insert the AUTO_INCREMENT style object id
			push @output, $self->{obj_id}++;
		}
		else
		{
			push @output, $val;
		}

		#if( $self->{debug}) { print $dbgfh "-\t $f = \t$val\n";}
	}

	# Prepare a suitable insert statement for MS Access, being careful to
	# quote only string values.
	my @qtd_output = ();
	foreach my $v ( @output)
	{
		# make the number a decimal if it has a decimal point in it.
		my $i;
		if( $v =~ m/\./)
		{
			$i = Math::BigFloat->new( $v);
		}
		else
		{
			$i = Math::BigInt->new( $v);
		}

		# the new formatted, quoted, value.
		my $nv;

		# if the value is a number then it will be interpolated by perl,
		# resulting in values less than one if $v != 0, if value is a
		# string, the result will be 1.  Also do not quote Null
		# identifiers.
		if( $i->is_nan() && ( $v ne $self->{null}))
		{
			# it's a string
			$nv = "'" . $v . "'";
		}
		else
		{
			# it's a 'Null' placeholder or a number, if it's a number, does
			# it have decimal points and what locale are we in.
			if( $self->{decimal_format} eq 'eu' && $v =~ m/\./)
			{
				# switch out the periods for commas
				# and the commas for periods, using a placeholder
				$v =~ s/\,/\|/g;
				$v =~ s/\./\,/;
				$v =~ s/\|/\./g;

				# also since commas are used by sql to delimit entries, we
				# need to quote this properly.
				$v = "'" . $v . "'";
			}

			$nv = $v
			
		}

		push @qtd_output, $nv;
	}

	my $sql = 'INSERT INTO Objects (' . join ( ", ", @hdrs) 
		. ") VALUES (" . join( ", ", @qtd_output) . ")";

	if( $self->{debug}) { print $dbgfh "writer::mapp> sql: \n\t$sql\n";}

	my $sth = $self->{dbh}->prepare( $sql) or croak;

	# write it to the output file
	$sth->execute() 
		or croak( "Can't execute insert statement: $DBI::errstr");

	$sth->finish;

	return undef;
};


# Filthy Cut and Paste Action, hot!

sub _write_info_dataline
{
	my $self = shift;

	# setup a local var to hold the IO::FILE handle
	my $dbgfh;

	if( $self->{debug})
	{ 
		$dbgfh = ${$self->{debug_fh}};
		print  $dbgfh "writer::mapp> _write_info_dataline\n";
	}

	my @output = (); 

	# get a copy of a list for ordered headers
	my @hdrs = gmml::writer::get_Info_headers();

	# create an output line ordered based on GenMAPP field header list
	foreach my $f ( @hdrs)
	{
		# handle NULL lines by default.
		my $val = $self->{null};
		if( defined $self->{info_row}->{$f})
		{
			$val = $self->{info_row}->{$f};
		}

		push @output, $val;

		if( $self->{debug}) { print $dbgfh "-\t $f = \t$val\n";}
	}

	# Prepare a suitable insert statement for MS Access, being careful to
	# quote only string values.
	my @qtd_output = ();
	foreach my $v ( @output)
	{
		# testing for numberness
		my $i = Math::BigInt->new( $v);

		# the new formatted, quoted, value.
		my $nv;

		# if the value is a number then it will be interpolated by perl,
		# resulting in values less than one if $v != 0, if value is a
		# string, the result will be 1.  Also do not quote Null
		# identifiers.
		if( $i->is_nan() && ( $v ne $self->{null}))
		{
			# it's a string
			$nv = "'" . $v . "'";
		}
		else
		{
			# it's a 'Null' placeholder or a number, 
			$nv = $v
		}

		push @qtd_output, $nv;
	}

	# there's only one info line per table.  However, if the first line is
	# empty, GenMAPP bitches that it's from an old version.  Ensure that
	# there are no lines in the Info table prior to the record we are about
	# to enter.

	my $sql = 'DELETE * FROM Info';
	my $sth = $self->{dbh}->prepare( $sql) or croak;
	$sth->execute() 
		or croak( "Can't execute delete statement: $DBI::errstr");

	$sql = 'INSERT INTO Info (' . join ( ", ", @hdrs) 
		. ") VALUES (" . join( ", ", @qtd_output) . ")";

	if( $self->{debug}) { print $dbgfh "writer::mapp> sql: \n\t$sql\n";}

	$sth = $self->{dbh}->prepare( $sql) or croak;

	# write it to the output file
	$sth->execute() 
		or croak( "Can't execute insert statement: $DBI::errstr");

	$sth->finish;

	return undef
};


1;
__END__

# Special Thanks to Martijn Van Iresel for getting the paramters for
# ConfigDSN figured out.

vim:ts=4:
