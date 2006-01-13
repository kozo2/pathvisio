# $Id: line.pm,v 1.3 2005/09/26 14:40:37 gontran Exp $


package gmml::reader::line;

# FIXME: breakout lineshape into a class that inherits itself from line.
# also is used for lineshape

use gmml::reader::line_xsd;
@ISA = qw( gmml::reader::line_xsd);

sub init_mapp_object_fields
{
	my $self = shift;

	# mapping GenMAPP field names (also self class properties) to GMML
	# identifiers

	$self->{ '_Object_table'} = 
		{
			'ID'			=> undef,
			'SystemCode'	=> undef,
			'Type'			=> $self->get_genmapp_line_type(),
			'CenterX'		=> $self->{'Graphics'}->{StartX},
			'CenterY'		=> $self->{'Graphics'}->{StartY},
			'SecondX'		=> $self->{'Graphics'}->{EndX},
			'SecondY'		=> $self->{'Graphics'}->{EndY},
			'Width'			=> undef,
			'Height'		=> undef,
			'Rotation'		=> undef,
			'Color'			=> $self->gmml_color_to_decimal(),
			'Label'			=> undef,
			'Head'			=> undef,
			'Remarks'		=> $self->{'Comment'},
			'Image'			=> undef,
			'Links'			=> undef,
			'Notes'			=> $self->{'Notes'},
		};
}

sub get_genmapp_line_type
{
	my $self = shift;

	# don't get line type for LineShape element -- the only distinction
	return $self->get_genmapp_lineshape_type() if ( $self->{is_lineshape} == 1);

	# gmml line type
	my $gmml_lt = $self->{Type};

	# GenMAPP 'DottedLine' => gmml.type=line, gmml.style=broken
	# GenMAPP 'Line' => gmml.type=line, gmml.style=solid
	if( $gmml_lt =~ /line/i)
	{
		# style solid or style broken
		if( $self->trimws( $self->{Style}) =~ m/broken/i)
		{
			return "DottedLine";
		}
		else
		{
			# gmml style is "solid"
			return "Line";
		}
	}

	# GenMAPP 'DottedArrow' => gmml.type=arrow, gmml.style=broken
	# GenMAPP 'Arrow' => gmml.type=arrow, gmml.style=solid
	else # gmml_ls =~ /arrow/i
	{
		# copy paste is great!
		if( $self->trimws( $self->{Style}) =~ m/broken/i)
		{
			return "DottedArrow";
		}
		else
		{
			# gmml style is "solid"
			return "Arrow";
		}
	}
}

# FIXME: uncomment last line if GMML get's fixed up to take all of GenMAPP
# types.
sub get_genmapp_lineshape_type
{
	my $self = shift;

	# format is key->GMML lineshape type, value->GenMAPP lineshape type
	%gmml_lkup = (
				"Tbar"				=> "TBar",
				"ReceptorSquare"	=> "ReceptorSq",
				"LigandSquare"		=> "LigandSq",
				"LigandRound"		=> "LigandRd",
				"ReceptorRound"		=> "ReceptorRd"
				#"Receptor"			=> "Receptor"
				);
	
	while ( my( $gml, $gmp) = each( %gmml_lkup))
	{
		if( $self->{Type} =~ m/$gml/)
		{
			return $gmp;
			last;
		}
	}

	# in the last case, TODO: add error condition for finalized gmml for
	# these cases.
	return undef;
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

# FIXME: put this in base class.
sub gmml_color_to_decimal
{
	my $self = shift;
	return hex( $self->{Graphics}->{Color});
}

1;
__END__
vim:ts=4:
