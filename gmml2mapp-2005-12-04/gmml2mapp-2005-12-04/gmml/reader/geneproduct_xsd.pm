# $Id: geneproduct_xsd.pm,v 1.1 2005/09/22 12:34:22 gontran Exp $

package gmml::reader::geneproduct_xsd;

sub new
{
	my $class = shift;
	my $self =
	{
		# attributes, GeneProduct
		'Name'						=> undef,
		'Type' 						=> undef,
		'GeneProduct-Data-Source'	=> undef,
		'Short-Name' 				=> undef,
		'Xref'						=> undef,
		'BackpageHead'				=> undef,
		# sequence elements
		'Notes'						=> undef,
		'Comment'					=> undef,
		'Graphics'					=>
		{ 
			# attribs, graphics
			CenterX 	=> undef, 
			CenterY 	=> undef, 
			Width 		=> undef, 
			Height 		=> undef
		},
	};

	return bless $self, $class;
} 


1;
__END__
vim:ts=4:
