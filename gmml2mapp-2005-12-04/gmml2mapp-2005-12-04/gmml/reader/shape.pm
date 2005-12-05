# $Id: shape.pm,v 1.2 2005/09/30 16:33:29 gontran Exp $

package gmml::reader::shape;

use gmml::reader::shape_xsd;
@ISA = qw( gmml::reader::shape_xsd);

sub init_mapp_object_fields
{
	my $self = shift;

	# mapping GenMAPP field names (also self class properties) to GMML
	# identifiers

	$self->{ '_Object_table'} = 
		{
			'ID'			=> undef,
			'SystemCode'	=> undef,
			'Type'			=> $self->{Type},
			'CenterX'		=> $self->{'Graphics'}->{CenterX},
			'CenterY'		=> $self->{'Graphics'}->{CenterY},
			'SecondX'		=> undef,
			'SecondY'		=> undef,
			'Width'			=> $self->{'Graphics'}->{Width},
			'Height'		=> $self->{'Graphics'}->{Height},
			'Rotation'		=> $self->{'Graphics'}->{Rotation},
			'Color'			=> $self->gmml_color_to_decimal(),
			'Label'			=> undef,
			'Head'			=> undef,
			'Remarks'		=> $self->{'Comment'},
			'Image'			=> undef,
			'Links'			=> undef,
			'Notes'			=> $self->{'Notes'},
		};
};

# FIXME: put this in base class.
sub gmml_color_to_decimal
{
	my $self = shift;

	my $gc = $self->{Graphics}->{Color};

	return (-1) if  $gc == -1;

	return hex( $gc);
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
