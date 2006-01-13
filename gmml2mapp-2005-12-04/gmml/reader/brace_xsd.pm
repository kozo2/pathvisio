# $Id: brace_xsd.pm,v 1.1 2005/09/22 12:34:21 gontran Exp $

package gmml::reader::brace_xsd;

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
			PicPointOffset 	=> undef, 
			Color 			=> undef,
			Orientation		=> undef,
		},
	};

	return bless $self, $class;
} 


1;
__END__
vim:ts=4:
