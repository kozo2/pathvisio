# $Id: geneproduct.pm,v 1.3 2005/09/30 16:33:07 gontran Exp $
#
# package gp_mapping inherits from geneproduct and maps the GenMAPP Data
# Table field names to object properites.
#
package gmml::reader::geneproduct;

use gmml::reader::geneproduct_xsd;
@ISA = qw( gmml::reader::geneproduct_xsd);

# sub new { # init geneproduct, and this? and init mappings? }

sub init_mapp_object_fields
{
	my $self = shift;

	# mapping GenMAPP field names (also self class properties) to GMML
	# identifiers
	$self->{ '_Object_table'} = 
		{
			'ID'			=> $self->{'Name'},
			'SystemCode'	=> $self->lookup_gm_syscode,
			undef			=> $self->{'Type'},
			'Type'			=> 'Gene',
			'CenterX'		=> $self->{'Graphics'}->{CenterX},
			'CenterY'		=> $self->{'Graphics'}->{CenterY},
			# Not Used for Object.Type = 'gene' aka geneproduct, but
			# populated anyway as NULL
			'SecondX'		=> undef,
			'SecondY'		=> undef,
			'Width'			=> $self->{'Graphics'}->{'Width'},
			'Height'		=> $self->{'Graphics'}->{'Height'},
			'Rotation'		=> undef,
			'Color'			=> undef,
			'Label'			=> $self->{'GeneID'},
			'Head'			=> $self->{'BackpageHead'},
			'Remarks'		=> $self->{'Comment'},
			'Image'			=> undef,
			'Links'			=> $self->{'Xref'},
			'Notes'			=> $self->{'Notes'},
		};

};

sub lookup_gm_syscode
{
	my $self = shift;
	my $gpds = $self->{'GeneProduct-Data-Source'};

	my %h = (
		D => "SGD",
		F => "FlyBase",
		G => "GenBank",
		I => "InterPro",
		L => "LocusLink",
		M => "MGI",
		Q => "RefSeq",
		R => "RGD",
		S => "SwissProt",
		T => "GeneOntology",
		U => "UniGene",
		W => "WormBase",
		Z => "ZFIN",
		X => "Affy",
		O => 'Other'
	);

	my $syscode = undef;

	while (my ($k, $v) = each( %h))
	{
		if( $v =~ m/$gpds/i)
		{
			$syscode = $k;
			last;
		}
	}

	return $syscode;
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
