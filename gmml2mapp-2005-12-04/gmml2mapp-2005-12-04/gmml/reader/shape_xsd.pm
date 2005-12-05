# $Id: shape_xsd.pm,v 1.1 2005/09/22 12:34:22 gontran Exp $

package gmml::reader::shape_xsd;

# no attributes, only sequence in xsd

sub new
{
	my $class = shift;

	my $self =
	{
		# xsd:attribute: Shape Type:
		Type			=> undef,

		# xsd:sequence:
		# although not widely used, included
		Notes			=> undef,
		Comment			=> undef,
		Graphics		=>
		{ 
			# xsd:attribs, graphics
			StartX 		=> undef, 
			StartY 		=> undef, 
			Width 		=> undef,
			Height 		=> undef, 
			Color 		=> undef,
			Rotation	=> undef,
		},
	};

	return bless $self, $class;
} 


1;
__END__
vim:ts=4:
