# $Id: brace.pm,v 1.1 2005/09/22 12:34:21 gontran Exp $

package gmml::reader::brace;

use gmml::reader::brace_xsd;
@ISA = qw( gmml::reader::brace_xsd);

sub init_mapp_object_fields
{
	my $self = shift;

	# mapping GenMAPP field names (also self class properties) to GMML
	# identifiers

	$self->{ '_Object_table'} = 
		{
			'ID'			=> undef,
			'SystemCode'	=> undef,
			'Type'			=> "Brace",
			'CenterX'		=> $self->{'Graphics'}->{CenterX},
			'CenterY'		=> $self->{'Graphics'}->{CenterY},
			'SecondX'		=> undef,
			'SecondY'		=> undef,
			'Width'			=> $self->{'Graphics'}->{Width},
			'Height'		=> $self->{'Graphics'}->{PicPointOffset},
			'Rotation'		=> $self->set_rotation(),
			'Color'			=> $self->gmml_color_to_decimal(),
			'Label'			=> undef,
			'Head'			=> undef,
			'Remarks'		=> undef,
			'Image'			=> undef,
			'Links'			=> undef,
			'Notes'			=> $self->{'Notes'},
		};
}

# per the GMML XSD and Kelder....
sub set_rotation
{
	my $self = shift;

	my $o = $self->{'Graphics'}->{Orientation};

	if( $o =~ /top/i)
	{
		return 0;
	}
	elsif( $o =~ /right/i)
	{
		return 1;
	}
	elsif( $o =~ /bottom/i)
	{
		return 2;
	}
	#elsif( $o =~ /left/i)
	else
	{
		return 3;
	}
}


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
