# $Id: reader.pm,v 1.5 2005/09/30 16:34:38 gontran Exp $

package gmml::reader;

# FIXME: the reader classes don't require predefinition of the variables in
# each of the XSDs, they could be loaded automatically using the below
# attribute loading process in the generic handler.  Ummm...

use strict;
use warnings;

use gmml::reader::pathway;
use gmml::writer;

use XML::Twig;
use Data::Dumper;


sub new
{
	my $class = shift;
	my $self = {};

	$self->{debug} = 0;
	$self->{debug_logfile} = '__foo.log';
	$self->{debug_fh} = undef;

	return bless $self, $class;
}

# generic twig handler for any pathway sub element -- NOT for pathway
# information specifically.
sub get_pw_element
{
	my ($tw, $el) = @_;

	# debugging output to handle?
	my $dbg = $tw->{debug};
	
	# declaration for gmml element parsing object.
	my $l;

	# what's the name of this element.
	my $sel = $el->local_name;

	my $dbgfh;

	if( $dbg)
	{
		$dbgfh = ${$tw->{debug_fh}};
		print $dbgfh ">> $sel\n";
	}

	# instantiate the new object, based on the name of our input twig
	if( $sel =~ /Label/i)
	{
		use gmml::reader::label;
		$l = new gmml::reader::label;
	}
	elsif( $sel =~ /GeneProduct/i)
	{
		use gmml::reader::geneproduct;
		$l = new gmml::reader::geneproduct;
	}
	elsif( $sel =~ /^Line/i)
	{
		use gmml::reader::line;
		$l = new gmml::reader::line;
		# check if it's Lineshape
		if( $sel =~ /LineShape/i)
		{
			$l->{is_lineshape} = 1;
		}
	}
	elsif( $sel =~ /Arc/i)
	{
		use gmml::reader::arc;
		$l = new gmml::reader::arc;
	}
	elsif( $sel =~ /^Shape$/i)
	{
		use gmml::reader::shape;
		$l = new gmml::reader::shape;
	}
	elsif( $sel =~ /Brace/i)
	{
		use gmml::reader::brace;
		$l = new gmml::reader::brace;
	}
	elsif( $sel =~ /CellShape/i)
	{
		use gmml::reader::cellshape;
		$l = new gmml::reader::cellshape;
	}
	elsif( $sel =~ /CellComponentShape/i)
	{
		use gmml::reader::cellcomponentshape;
		$l = new gmml::reader::cellcomponentshape;
	}
	elsif( $sel =~ /ProteinComponentShape/i)
	{
		use gmml::reader::proteincomponentshape;
		$l = new gmml::reader::proteincomponentshape;
	}
	else
	{
		die ">> gmml::reader->get_pw_element()\n\tCan't find object to "
			. "instantiate for $sel...dying.\n";
	}

	# Fish out the attributes for the Element.
	foreach my $a ( keys ( %{$el->{att}} ) )
	{
		if( $dbg)
		{
			print $dbgfh "\tAtt: $a, value: " . $el->{att}->{$a} . "\n";
		}

		# add the attribute information to the object if it's not empty
		if ( defined( $el->{att}->{$a}) 
			&& $l->trimws( $el->{att}->{$a}) ne '')
		{
			$l->{$a} = $el->{att}->{$a} 
		}
	}

	if( $el->children_count > 0)
	{
		foreach my $k ($el->children())
		{
			my $kidname = $k->local_name;

			# handle the Comment, Note or other xsd:sequence elements.
			if( $kidname !~ /Graphics/)
			{
				if( $dbg)
				{
					print $dbgfh ">> $sel has child " . $kidname 
						. " met value \t >" .  $k->text() . "<\n";
				}

				# get the text into the object if it's not empty
				$l->{$kidname} = $k->text
					if ( $l->trimws( $k->text) ne '');
			}
			else
			{
				# handle graphics attributes
				if ($dbg)
				{
					print $dbgfh ">> $sel has child " . $kidname 
						. " with attribs\n";
				}

				foreach my $a ( keys ( %{$k->{att}} ) )
				{
					if( $dbg)
					{
						print $dbgfh "\tAtt: $a, value: " . $k->{att}->{$a} 
							. "\n";
					}

					# get data into object data
					if( defined( $k->{att}->{$a}))
					{
						#print "> att:$a - value defined\n";
						if( $l->trimws( $k->{att}->{$a}) ne '')
						{
							$l->{$kidname}->{$a} = $k->{att}->{$a} 
						}
					}
					else
					{
						warn "!> $sel -> $kidname att:$a - value empty\n";
					}
				}
			}
		}
	}

	# mapp up the collected xml data to the GenMAPP Object fields:
	$l->init_mapp_object_fields();

	# Write data line to a file.
	if( $dbg) { print $dbgfh Dumper( $l);}

	# Write the data line to the gmml::writer with no cognisance of the
	# output format!
	my $writer = $tw->{output_handler};

	if( $dbg && 0) { print $dbgfh Dumper( $writer);}

	# Pass the writer a copy of the "mapped" data in an anonymous hash, in
	# this case for Object Table fields.
	$writer->set_object_row( $l->{'_Object_table'});

	$writer->flush();

	return 1;
}

sub get_pw_sequence
{
	my ( $tw, $el) = @_;
	my $dbg = $tw->{debug};
	my $dbgfh = ${$tw->{debug_fh}};

	# what's the name of this element.
	if( $dbg) { print $dbgfh "gpws>> " . $el->local_name . "...\n"; }
	
	# what's the name of this element.
	my $sel = $el->local_name;

	if( $dbg) { print $dbgfh "gpws>> $sel\n"; } 

	# retrieve
	my $p = $tw->{my_pathway};

	# instantiate the new object, based on the name of our input twig
	if( $sel !~ /Graphics/i)
	{
		if( $dbg) 
		{ 
			print $dbgfh "gpws>> $sel has value" . " \t >" .  $el->text() 
				. "<\n";
		}
		# get the text into the object if it's not empty
		$p->{$sel} = $el->text if ( $p->trimws( $el->text) ne '');
	}

	# handle graphics attributes
	else
	{
		foreach my $a ( keys ( %{$el->{att}} ) )
		{
			if( $dbg) 
			{ print  $dbgfh "\tAtt: $a, value: " . $el->{att}->{$a} .  "\n"; }

			# add the attribute information to the object if it's not empty
			if ( defined( $el->{att}->{$a}) 
				&& $p->trimws( $el->{att}->{$a}) ne '')
			{
				$p->{'Graphics'}{$a} = $el->{att}->{$a} 
			}
		}

	}

	# restore
	$tw->{my_pathway} = $p;

	return 1;
}

sub get_pw_attribs
{
	my ($tw, $el) = @_;
	my $dbg = $tw->{debug};
	my $dbgfh;

	# get the reference to our pathway object into the subroutine scope
	my $p = $tw->{my_pathway}; 

	# what's the name of this element.
	my $sel = $el->local_name;

	if( $dbg)
	{
		$dbgfh = ${$tw->{debug_fh}};
		print $dbgfh "gpwa>> $sel\n";
	}

	# Fish out the attributes for the Line Element.
	foreach my $a ( keys ( %{$el->{att}} ) )
	{
		if( $dbg) 
		{ print $dbgfh "\tAtt: $a, value: " . $el->{att}->{$a} .  "\n"; }

		# add the attribute information to the object if it's not empty
		if ( defined( $el->{att}->{$a}) 
			&& $p->trimws( $el->{att}->{$a}) ne '')
		{
			$p->{$a} = $el->{att}->{$a} 
		}
	}

	# refresh our instance of pathway in the twig
	$tw->{my_pathway} = $p;

	return 1;
}


sub gmml_parse
{
	my $self = shift;
	my $dbgfh;

	my $t = XML::Twig->new
	(
		do_not_chain_handlers => 1,

		twig_roots =>
			{

				'/Pathway'							=> \&get_pw_attribs,
				'/Pathway/Comment'					=> \&get_pw_sequence,
				'/Pathway/Notes'					=> \&get_pw_sequence,
				'/Pathway/Graphics'					=> \&get_pw_sequence,
			
				# maybe tell twig to process only the major twigs that
				# we're looking at and iterate through them ourselves, as
				# the default behaviour of twig is to work from the
				# innermost child to the outer, and this doesn't work for
				# our tricky class loading setup in get_pw_element: so
				# DON'T USE IT.
				###'_default_'						=> \&get_pw_element

				'/Pathway/GeneProduct'				=> \&get_pw_element,
				'/Pathway/Line'						=> \&get_pw_element,
				'/Pathway/LineShape'				=> \&get_pw_element,
				'/Pathway/Arc'						=> \&get_pw_element,
				'/Pathway/Label'					=> \&get_pw_element,
				'/Pathway/Shape'					=> \&get_pw_element,
				'/Pathway/Brace'					=> \&get_pw_element,
				'/Pathway/CellShape'				=> \&get_pw_element,
				'/Pathway/CellComponentShape'		=> \&get_pw_element,
				'/Pathway/ProteinComponentShape'	=> \&get_pw_element,

			},
	);
	
	# pass whether we're looking for debugging output into the twig
	$t->{debug} = $self->{debug};

	# if debugging, open up a debug handle
	if( $t->{debug})
	{ 
		$self->open_debugfile;
		$t->{debug_fh} = $self->{debug_fh};
		$dbgfh = ${$self->{debug_fh}};
	}

	# strap an instance of the pathway reader pathway class onto the twig
	# so we can collect a single instance of the pathway level info during
	# various itterations of the reader.
	$t->{my_pathway} = new gmml::reader::pathway;

	# fixup the debug filehandles for the writer if it's been flagged to
	# debug by the user and they forgot to setup an output filehandle for
	# it.

	my $writer = $self->{my_writer};

	if( $writer->{debug} && ! defined $writer->{debug_fh})
	{
		$writer->{debug_fh} = $self->{debug_fh};
	}

	# sneak the gmml::writer handler into the twig where the writer output
	# subroutines are called.
	$t->{output_handler} = $writer;

	# XML::Twig ACCION:  does the whole doo.
	$t->parsefile( $self->{infile});

	# Since the Pathway data is collected at various stages of the parsing
	# (*grrr*) wait till the whole document is parsed and then write out
	# the Info table.

	# get at the gmml::writer instance
	$writer = $t->{output_handler};

	# get the gmml::reader::pathway instance off of the twig
	my $pathway = $t->{my_pathway};

	# re-initialize or initilaize the mapping of the gmml data into the
	# pathway objects datastructure in preparation for output.
	$pathway->init_mapp_info_fields;

	# Write data line to a file.
	if( $self->{debug}) { print $dbgfh Dumper( $pathway); }

	# Write the data line to the gmml::writer with no cognisance of the
	# output format!  Pass the writer a copy of the "mapped" data in an
	# anonymous hash, in this case for Info Table
	$writer->set_info_row( $pathway->{'_Info_table'});

	# flush that info row to file
	$writer->flush;

	# Purge the twig: use purge to ensure there is no output of the parsed
	# xml
	$t->purge;

	if( $t->{debug} != 0)
	{
		close ${$self->{debug_fh}} or warn "Failed to close debug handle, $!";
	}
}

# Even More Private Methods -- FIXME: get these named properly
sub open_debugfile
{
	my $self = shift;

	if( ! defined $self->{debug_fh})
	{
		$self->{debug_fh} = IO::File->new( ">" . $self->{debug_logfile});
	}
	return undef;
}

1;
__END__
vim:ts=4:
