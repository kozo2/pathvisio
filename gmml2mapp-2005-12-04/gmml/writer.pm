# $Id: writer.pm,v 1.7 2005/10/21 12:35:57 gontran Exp $

package gmml::writer;

use Carp;
use IO::File;

use strict;
use warnings;


sub new
{
	my $class = shift;
	my @args = shift; 

	my $self =
	{ 
		# if we're debugging, and where to send debug info to.
		debug 		=> 0,
		debug_logfile	=> "__gmml_writer.log",
		debug_fh	=> undef,

		# the output path for our final file whether that be a csv or a
		# mapp file.
		outpath		=> undef,

		# if the output is a mapp (MS Access MDB file) the DSN for that
		# ODBC resource, what this is called does not matter.
		odbc_dsn	=> 'my_gmml_writer_dsn',

		# DBI dbh handle for the database connection
		dbh			=> undef,

		# mapp output requires an obj_id similar to AUTO_INCREMENT in the
		# data, so we track it here for our iterations over the elements.
		obj_id		=> 1,

		# keep a IO::File data handle in the self so we don't have to open
		# and close.
		fh			=> undef,

		# how the NULL or empty/undefined values are output by the writer
		null		=> 'Null',

		# how to write out decimal numbers 
		decimal_format	=> 'us',
	};

	return bless $self, $class;
}

# track and output decimals in consistent format for eu or us regions.  EG,
# eu region will have decimal seperator set to ',', while us will have to
# default, '.'.
sub set_number_fmt
{
	my $self = shift;
	my $fmt = shift;

	$self->{decimal_format} = $fmt;
	return undef;
}

# Allow retrieval of GenMAPP.Info and GenMAPP.Object field names in an
# ordered fashion so as to enable creation of ordered output.
sub get_Info_headers
{
	# table header order, and mappings 
	my @Info_headers =
		( 
			'Title', 'MAPP', 'GeneDB', 'GeneDBVersion', 'Version',
			'Author', 'Maint', 'Email', 'Copyright', 'Modify', 'Remarks',
			'BoardWidth', 'BoardHeight', 'WindowWidth', 'WindowHeight',
			'Notes'
		);
	
	return @Info_headers;
}

sub get_Object_headers
{
	my @Object_headers = 
		(
			'ObjKey', 'ID', 'SystemCode', 'Type', 'CenterX', 'CenterY',
			'SecondX', 'SecondY', 'Width', 'Height', 'Rotation', 'Color',
			'Label', 'Head', 'Remarks', 'Image', 'Links', 'Notes'	
		);
	
	return @Object_headers;
}

# set the outfile path for the writer output, regardless of format.
sub set_outfile_path
{
	my $self = shift;
	my $ofile = shift;

	$self->{outpath} = $ofile;

	return undef;
}

# used by writer internally, not publik.
# receives an anonymous hash from XML::Twig handler after parsing a twig
# for an Object table entry.
# ENHANCEMENT:  integrate set_*_row into set_row( $table_type)
sub set_object_row
{
	my $self = shift;

	if( $self->{debug})
	{ 
		my $dbgfh = ${$self->{debug_fh}};
		print $dbgfh "writer> set_object_row\n";
	}

	# an anonymous hash
	$self->{obj_row} = shift;

	# flag internal for what kind of data we're writing
	$self->{is_obj_data} = 1;
	$self->{is_info_data} = 0;

	return undef;
}

# receives an anonymous hash from XML::Twig handler after parsing a twig
# for an Info (pathway) table entry.
sub set_info_row
{
	my $self = shift;

	if( $self->{debug})
	{ 
		my $dbgfh = ${$self->{debug_fh}};
		print $dbgfh "writer> set_info_row\n";
	}

	# an anonymous hash
	$self->{info_row} = shift;

	$self->{is_obj_data} = 0;
	$self->{is_info_data} = 1;

	return undef;
}

# Flushing happens one data row at a time, writer::flush invoked from
# within xml::twig callback routines for each completely parsed element.
sub flush
{
	my $self = shift;
	my $dbgfh;

	if( $self->{debug})
	{
		$dbgfh = ${$self->{debug_fh}};
		print $dbgfh "writer::flush> enter\n";
	}

	# if we're going to flush a mapp file data line, make sure that the dsn
	# is setup.  Or in the case of csv output that the file is opened.
	if( $self->isa( 'gmml::writer::mapp'))
	{
		# setup the dsn if it isn't already
		$self->_setup_mapp_dsn();
	}
	else # self->isa( gmml::writer::csv)
	{
		# open or verify opened csv output file.
		$self->_open_outfile();
	}

	# two different routines for two output tables.
	# ENHANCEMENT:  integrate write_*_dataline, tracking type of date in
	# $self.
	if( $self->{is_obj_data} == 1)
	{
		if( $self->{debug}) { print $dbgfh "writer::flush> is_obj_data\n";}
		$self->_write_object_dataline;
	}
	elsif ( $self->{is_info_data} == 1)
	{
		if( $self->{debug}) { print $dbgfh "writer::flush> is_info_data\n";}
		$self->_write_info_dataline;
	}

	# clear the flags
	$self->{is_obj_data} = 0;
	$self->{is_info_data} = 0;

	return undef;
}

# sub pandora
# more private methods
sub _open_outfile
{
	my $self = shift;

	# sanity
	croak( "Output file not setup, exiting.")
		unless defined $self->{outpath};

	# just open it once
	if( not defined $self->{fh})
	{
		$self->{fh} = IO::File->new( ">" . $self->{outpath});
	}

	return undef;
}


# Even More Semi Private Methods -- FIXME: get these named properly and put
# in gmml.pm

sub open_debugfile
{
	my $self = shift;
	my $open_args = shift;

	if( ! defined $self->{debug_fh})
	{
		if( defined $open_args && $open_args eq 'append')
		{
			$self->{debug_fh} = IO::File->new( ">>"
				. $self->{debug_logfile});
		}
		else
		{
			$self->{debug_fh} = IO::File->new( ">" 
				. $self->{debug_logfile});
		}

		croak "Failed to open debug file, $!" 
			unless defined $self->{debug_fh};

	}
	return undef;
}

# create a dynamic dsn for the mapp output and connect to the ms access mdb
# over ODBC via DBI (oh, my!)
sub _setup_mapp_dsn
{
	my $self = shift;
	my $dbgfh = ${$self->{debug_fh}};

	# only do this for writer::mapp instances
	return undef unless ( $self->isa( 'gmml::writer::mapp'));

	# only do this once per instantiation
	return undef if defined $self->{dbh};

	if( $self->{debug})
	{
		print $dbgfh "writer::_setup_mapp_dsn> isa mapp, setting up "
			. "dsn....\n";
	}

	# only request use of win32::odbc when we need it...developing mostly
	# on gnu/linux and too lazy atm to do os specific code in the use area
	# up top  8_).
	use Win32::ODBC;

	croak( $!) unless 
		Win32::ODBC::ConfigDSN( 
			Win32::ODBC::ODBC_ADD_DSN, 
			"Microsoft Access Driver (*.mdb)",
			"DSN=" . $self->{odbc_dsn},
			"DBQ=" . $self->{outpath}
		);

	my $dbi_dsn = "DBI:ODBC:" . $self->{odbc_dsn};
	$self->{dbh} = DBI->connect( $dbi_dsn );

	croak "gmml::writer> Cannot connect to ODBC DSN via DBI: $!" 
		unless $self->{dbh};

	return undef;
}

# cleanup our database resources in the case of mapp output file.  Remove
# the DSN and close the DBI handle.  Provide intuitive wrapper!
sub cleanup
{
	my $self = shift;

	$self->_destroy_mapp_dsn();

	return undef;
}

sub _destroy_mapp_dsn
{
	my $self = shift;

	# Sanity...
	# only do this for writer::mapp instances
	return undef unless ( $self->isa( 'gmml::writer::mapp'));

	# only remove dsn and disconnect if we're connected
	return undef if not defined $self->{dbh};

	# Accion
	use Win32::ODBC;

	carp( $!) unless 
		Win32::ODBC::ConfigDSN(
			Win32::ODBC::ODBC_REMOVE_DSN, 
			"Microsoft Access Driver (*.mdb)",
			"DSN=" . $self->{odbc_dsn},
			"DBQ=" . $self->{outpath}
		);

	$self->{dbh}->disconnect;

	return undef;
}

1;
__END__
vim:ts=4:
