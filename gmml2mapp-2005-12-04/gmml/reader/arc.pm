# $Id: arc.pm,v 1.2 2005/10/21 12:36:29 gontran Exp $

package gmml::reader::arc;

use gmml::reader::arc_xsd;
@ISA = qw( gmml::reader::arc_xsd);

sub init_mapp_object_fields
{
	my $self = shift;

	# mapping GenMAPP field names (also self class properties) to GMML
	# identifiers

	$self->{ '_Object_table'} = 
		{
			'ID'			=> undef,
			'SystemCode'	=> undef,
			'Type'			=> "Arc",
			'CenterX'		=> $self->{'Graphics'}->{StartX},
			'CenterY'		=> $self->{'Graphics'}->{StartY},
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
	return hex( $self->{Graphics}->{Color});
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
