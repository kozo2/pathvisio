# $Id: csv.pm,v 1.5 2005/09/26 14:32:01 gontran Exp $
# A Proof of Concept for gmml::writer::mapp

package gmml::writer::csv;

use gmml::writer;
@ISA = qw( gmml::writer);

sub _write_object_dataline
{
	my $self = shift;

	# setup a local var to hold the IO::FILE handle
	my $dbgfh;

	if( $self->{debug})
	{ 
		$dbgfh = ${$self->{debug_fh}};
		print  $dbgfh "csv> _write_object_dataline\n";
	}

	my @output = ();

	# get a copy of the MAPP.Object table headers
	my @hdrs = gmml::writer::get_Object_headers();

	# tack on the incrementing object id to the first column of the output
	# row.
	push @output, $self->{obj_id}++;

	# create an output line based on GenMAPP field order obtained
	# from gmml::writer::get_Object_headers()
	foreach $f ( @hdrs)
	{
		# handle NULL lines by default.
		my $val = $self->{null};
		if( defined $self->{obj_row}->{$f})
		{
			$val = $self->{obj_row}->{$f};
		}

		if( $self->{debug}) { print $dbgfh "-\t $f = \t$val\n";}

		push @output, $val;
	}

	# write it to the output file
	print ${$self->{fh}} join (',', @output) . "\n";

	return undef;
}


# filthy cut and paste action.  Hot!

sub _write_info_dataline
{
	my $self = shift;
	my $dbgfh;

	if( $self->{debug})
	{	 
		$dbgfh = ${$self->{debug_fh}};
		print $dbgfh "csv> _write_info_dataline\n";
	}

	my @output = ();

	# get a copy of an anonymous array ref 
	my @hdrs = gmml::writer::get_Info_headers();

	# tack on the incrementing object id to the first column of the output
	# row.
	push @output, $self->{obj_id}++;

	# create an output line ordered based on GenMAPP field order obtained
	# from mapp_fields::get_Info_headers()
	foreach $f ( @hdrs)
	{
		# handle NULL lines by default.
		my $val = $self->{null};
		if( defined $self->{info_row}->{$f})
		{
			$val = $self->{info_row}->{$f};
		}

		if( $self->{debug}) { print $dbgfh "-\t $f = \t$val\n";}

		push @output, $val;
	}

	# write it to the output file
	print ${$self->{fh}} join (',', @output) . "\n";

	return undef;
}

1;
__END__
vim:ts=4:
