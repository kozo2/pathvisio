# $Id: pathway_xsd.pm,v 1.1 2005/09/22 12:34:22 gontran Exp $

package gmml::reader::pathway_xsd;

sub new
{
	my $class = shift;
	my $self =
	{
		# attributes, Pathway
		'Name'						=> undef,

		'Organism'					=> undef,
		'Data-Source'				=> undef,
		'Version' 					=> undef,
		'Author'					=> undef,
		'Maintained-By'				=> undef,
		'Email'						=> undef,
		'Availability'				=> undef,
		'Last-Modified'				=> undef,

		# sequence elements
		'Notes'						=> undef,
		'Comment'					=> undef,
		'Graphics'					=>
		{ 
			# optional attribs, graphics
			BoardWidth 		=> undef, 
			BoardHeight 	=> undef, 
			# to be added soonly
			WindowWidth 		=> undef, 
			WindowHeight 	=> undef, 
			MapInfoLeft 	=> undef, 
			MapInfoTop 		=> undef, 
			Height 			=> undef
		},
	
		# These are all the elements that are in xsd:sequence Pathway.  The
		# thing is that the following sequence elements are mapped to the
		# Object table while the above information is from or destined for
		# the Info table.  
		#
		# GeneProduct		=> undef,
		# Line				=> undef,
		# LineShape			=> undef,
		# Arc				=> undef,
		# Label				=> undef,
		# Shape				=> undef,
		# Brace				=> undef,
		# CellShape			=> undef,
		# CellComponentShape			=> undef,
		# ProteinComponentShape		=> undef,
	};

	return bless $self, $class;
} 

1;
__END__
vim:ts=4:
