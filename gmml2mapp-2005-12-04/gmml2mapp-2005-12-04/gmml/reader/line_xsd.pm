# $Id: line_xsd.pm,v 1.1 2005/09/22 12:34:22 gontran Exp $

package gmml::reader::line_xsd;

# also used for lineshape_xsd

sub new
{
	my $class = shift;
	my $self =
	{
		# attributes, Line

		'Type' 	=> undef,
		'Style'	=> undef,

		# sequence elements for Line
		'Comment'					=> undef,
		'Graphics'					=>
		{ 
			# attribs, graphics
			StartX 	=> undef, 
			StartY 	=> undef, 
			EndX 	=> undef, 
			EndY 	=> undef,
			Color 	=> undef
		},
		# although not widely used, included
		Notes	=> undef,
		#
		# boolean for tracking use of line class for LineShap elements
		#
		is_lineshape	=> 0,
	};

	return bless $self, $class;
} 


1;
__END__
vim:ts=4:
