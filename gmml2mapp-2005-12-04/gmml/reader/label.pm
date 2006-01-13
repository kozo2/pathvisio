# $Id: label.pm,v 1.1 2005/09/22 12:34:22 gontran Exp $

package gmml::reader::label;

use gmml::reader::label_xsd;
@ISA = qw( gmml::reader::label_xsd);

sub init_mapp_object_fields
{
	my $self = shift;

	# mapping GenMAPP field names (also self class properties) to GMML
	# identifiers

	$self->{ '_Object_table'} = 
		{
			'ID'			=> $self->{'Graphics'}->{FontName},
			'SystemCode'	=> $self->get_format_to_genmapp_syscode(),
			'Type'			=> "Label",
			'CenterX'		=> $self->{'Graphics'}->{CenterX},
			'CenterY'		=> $self->{'Graphics'}->{CenterY},
			'SecondX'		=> $self->{'Graphics'}->{FontSize},
			'SecondY'		=> undef,
			'Width'			=> $self->{'Graphics'}->{Width},
			'Height'		=> $self->{'Graphics'}->{Height},
			'Rotation'		=> undef,
			'Color'			=> $self->gmml_color_to_decimal(),
			'Label'			=> $self->{TextLabel},
			'Head'			=> undef,
			'Remarks'		=> $self->{'Comment'},
			'Image'			=> undef,
			'Links'			=> undef,
			'Notes'			=> $self->{'Notes'},
		};
};

sub gmml_color_to_decimal
{
	my $self = shift;
	return hex( $self->{Graphics}->{Color});
}

# FIXME: There is bad correlation to return to GenMAPP system codes based
# on the lossy conversion in the GenMAPP -> GMML in Kelder's app.  Fix this
# after Kelder's app is made lossless.
sub get_format_to_genmapp_syscode
{
	my $self = shift;

	# gmml elements which are split from SystemCode control characters when
	# converted to gmml
	my $fw = $self->{Graphics}->{FontWeight};
	my $fs = $self->{Graphics}->{FontStyle};

	#print "FW: $fw\tFS: $fs\n";

	# GenMAPP SystemCode(s) for when Object.Type = 'Label' and their
	# mappings.

	# FIXME: the term ^T is represented twice in this list, but is defined
	# as two different font format's in kelder's mapping.  What's the
	# missing control character?

	# format cc => [ fontWeight, fontStyle ]

	my %cchars =( 
		"\cA" 	=> [ 'Bold', 	 undef 	], 
		# the "Normal" case
		#"\cP" 	=> [ undef, 	 undef 	], 
		"\cQ" 	=> [ 'Bold', 	 undef 	],
		"\cR" 	=> [ undef, 	'Italic' 	],
		"\cS"	=> [ 'Bold', 	'Italic' 	], 
		#"\cT"	=> [],
		"\cT" 	=> [  undef,	'Underscore' ],
		"\cV" 	=> [  undef,	'Underscore' ],
		"\cW" 	=> [ 'Bold',	'Underscore' ],
		"\cX" 	=> [  undef,	'Strikethrough' ],
		"\cY" 	=> [ 'Bold', 	'Strikethrough' ],
		"-" 	=> [  undef,	'Strikethrough' ],
		"\c[" 	=> [  undef,	'Strikethrough' ],
		"\c]" 	=> [  undef,	'Strikethrough' ],
		"\c^" 	=> [  undef,	'Strikethrough' ],
		"\c_" 	=> [ 'Bold',	'Strikethrough' ]
	);

	# default to normal.
	my $gmapp_label_syscode = "\cP";

	my $gmml_fw, $gmml_fs;

	# It's a short list, brute force a determination.  Lots of necessary
	# check for definedness to make correct matches.  undef tends to match
	# any value in the context of '=~'.

	# use keys to check matches starting from the top of the list above to
	# the bottom.
	foreach my $cc ( sort keys %cchars)
	{
		#print "0: " . $cchars{$cc}->[0] . "\t1: " . $cchars{$cc}->[1] . "\n";

		my $cfw = $cchars{$cc}->[0];
		my $cfs = $cchars{$cc}->[1];

		#print "$fw ? " . $cfw . "\t\t$fs ? " . $cfs . "\n";

		if( defined( $fs) && defined( $fw))
		{
			#print "$cc: $fw ? " . $cfw . "\t\t$fs ? " . $cfs . "\n";
			if( (defined $cfw && $fw =~ m/$cfw/i) && (defined $cfs && $fs =~ m/$cfs/i))
			{
				#print "Match\n";
				$gmapp_label_syscode = $cc;
				last;
			}
		}
		elsif( ! defined( $fs) && defined( $fw))
		{
			if( defined $cfw && $fw =~ m/$cfw/)
			{
				$gmapp_label_syscode = $cc;
				last;
			}
		}
		elsif( defined( $fs) && ! defined( $fw))
		{
			if( defined $cfs && $fs =~ m/$cfs/)
			{
				$gmapp_label_syscode = $cc;
				last;
			}
		}
	}

	return $gmapp_label_syscode;
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
