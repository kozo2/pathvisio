# $Id: proteincomponentshape.pm,v 1.1 2005/09/22 12:34:22 gontran Exp $

package gmml::reader::proteincomponentshape;

use gmml::reader::proteincomponentshape_xsd;
@ISA = qw( gmml::reader::proteincomponentshape_xsd);

sub init_mapp_object_fields
{
	my $self = shift;

	# mapping GenMAPP field names (also self class properties) to GMML
	# identifiers

	$self->{ '_Object_table'} = 
		{
			'ID'			=> undef,
			'SystemCode'	=> undef,
			'Type'			=> 'ProteinB',
			'CenterX'		=> $self->{'Graphics'}->{CenterX},
			'CenterY'		=> $self->{'Graphics'}->{CenterY},
			'SecondX'		=> undef,
			'SecondY'		=> undef,
			'Width'			=> $self->{'Graphics'}->{Width},
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
