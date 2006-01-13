# $Id: cellcomponentshape.pm,v 1.1 2005/09/22 12:34:21 gontran Exp $

package gmml::reader::cellcomponentshape;

use gmml::reader::cellcomponentshape_xsd;
@ISA = qw( gmml::reader::cellcomponentshape_xsd);

sub init_mapp_object_fields
{
	my $self = shift;

	# mapping GenMAPP field names (also self class properties) to GMML
	# identifiers

	$self->{ '_Object_table'} = 
		{
			'ID'			=> undef,
			'SystemCode'	=> undef,
			'Type'			=> $self->{'Type'},
			'CenterX'		=> $self->{'Graphics'}->{CenterX},
			'CenterY'		=> $self->{'Graphics'}->{CenterY},
			'SecondX'		=> undef,
			'SecondY'		=> undef,
			'Width'			=> undef,
			'Height'		=> undef,
			'Rotation'		=> undef,
			'Color'			=> undef,
			'Label'			=> undef,
			'Head'			=> undef,
			'Remarks'		=> undef,
			'Image'			=> undef,
			'Links'			=> undef,
			'Notes'			=> $self->{'Notes'},
		};
}



# trimwhitespace from http://www.somacon.com/p114.php
sub trimws()
{
		my ($self, $string) = @_;
		$string =~ s/^\s+//;
		$string =~ s/\s+$//;
		#print ".tws: $string\n";
		return $string;
}

1;
__END__
vim:ts=4:
