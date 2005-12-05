# $Id: arc_xsd.pm,v 1.1 2005/09/22 12:34:21 gontran Exp $

package gmml::reader::arc_xsd;

# no attributes, only sequence in xsd

sub new
{
	my $class = shift;

	my $self =
	{
		# although not widely used, included
		Notes			=> undef,

		# sequence elements for Line
		'Comment'		=> undef,

		'Graphics'		=>
		{ 
			# attribs, graphics
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
