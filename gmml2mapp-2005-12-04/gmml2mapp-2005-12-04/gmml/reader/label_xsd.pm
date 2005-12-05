# $Id: label_xsd.pm,v 1.1 2005/09/22 12:34:22 gontran Exp $

package gmml::reader::label_xsd;

sub new
{
	my $class = shift;

	my $self =
	{
		# attributes, Label
		'TextLabel' 	=> undef,

		# sequence elements for Line
		'Comment'					=> undef,
		# although not widely used, included
		Notes	=> undef,

		'Graphics'					=>
		{ 
			# attribs, graphics
			FontName 	=> undef, 
			FontSize 	=> undef, 
			FontWeight 	=> undef, 
			FontStyle	=> undef,
			Color 		=> undef,
			Width 		=> undef,
			Height 		=> undef, 
			CenterX 	=> undef,
			CenterY 	=> undef,
		},
	};

	return bless $self, $class;
} 


1;
__END__
vim:ts=4:
