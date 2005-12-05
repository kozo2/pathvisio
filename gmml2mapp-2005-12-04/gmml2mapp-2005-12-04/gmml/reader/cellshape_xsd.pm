# $Id: cellshape_xsd.pm,v 1.1 2005/09/22 12:34:21 gontran Exp $

package gmml::reader::cellshape_xsd;

# no attributes, only sequence in xsd

sub new
{
	my $class = shift;

	my $self =
	{
		# although not widely used, included
		Notes			=> undef,
		
		# No Comments traced?

		'Graphics'		=>
		{ 
			# attribs, graphics
			CenterX 		=> undef, 
			CenterY 		=> undef, 
			Width 			=> undef,
			Height 			=> undef, 
			Rotation		=> undef,
		},
	};

	return bless $self, $class;
} 


1;
__END__
vim:ts=4:
