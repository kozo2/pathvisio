# $Id: pathway.pm,v 1.1 2005/09/22 12:34:22 gontran Exp $

package gmml::reader::pathway;

use gmml::reader::pathway_xsd;
@ISA = qw( gmml::reader::pathway_xsd);

# info_box from the Object table seems to be mapped from 
sub init_mapp_info_fields
{
	my $self = shift;

	# mapping GenMAPP field names (also self class properties) to GMML
	# identifiers.  Those labelled undef have no GMML representation as of
	# 2005-09-01

	$self->{ '_Info_table'} = 
		{
			'Title'			=> $self->{'Name'},
			'MAPP'			=> undef,
			'GeneDB'		=> undef,
			'GeneDBVersion'	=> undef,
			'Version'		=> $self->{Version},
			'Author'		=> $self->{Author},
			'Maint'			=> $self->{'Maintained-By'},
			'Email'			=> $self->{'Email'},
			'Copyright'		=> $self->{'Availability'},
			'Modify'		=> $self->{'Last-Modified'},
			'Remarks'		=> $self->{'Comment'},
			'BoardWidth'	=> $self->{Graphics}->{'BoardWidth'},
			'BoardHeight'	=> $self->{Graphics}->{'BoardHeight'},
			# no GMML correlary yet, put them in anyway
			'WindowWidth'	=> $self->{Graphics}->{'WindowWidth'},
			'WindowHeight'	=> $self->{Graphics}->{'WindowHeight'},
			'Notes'			=> $self->{'Notes'},
		};
};

sub gm_Info_table_fields
{
	my $self = shift;

	# table header order, and mappings 
	$self->{'_tho_Info'} =
		[ 
			'Title', 'MAPP', 'GeneDB', 'GeneDBVersion', 'Version',
			'Author', 'Maint', 'Email', 'Copyright', 'Modify', 'Remarks',
			'BoardWidth', 'BoardHeight', 'WindowWidth', 'WindowHeight',
			'Notes'
		];

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
