use strict;
use warnings;

package PathwayTools::WikiPathways;

=head1 NAME

PathwayTools::WikiPathways - proxy for the wikipathways webservice.

=head1 DESCRIPTION

These functions are provided by this module:

=over 3

=cut

use PathwayTools::Pathway;
use Frontier::Client;
use MIME::Base64;

=item new PathwayTools::WikiPathways(user => ..., pass => ..., url => ...[, debug => 1])

create a connection and log in to the wikipathways webservice

user
pass
url
debug: if true, print the xmlrpc requests and responses to stdout.

=cut

sub new
{
	my $class = shift;
	my $self = { @_ };

	$self->{server} = Frontier::Client->new('url' => $self->{url});
	
	$self->{server}->{debug} = $self->{debug};
	
	$self->{token} = 
		$self->{server}->call("WikiPathways.login", $self->{user}, $self->{pass});
	
	bless $self, $class;
}

=item $wikipathways->get_pathway_with_revision ($species, $name)

Download a pathway from wikipathways.

$species: a species latin name, e.g. "Homo sapiens"
$name: the name of the pathway.

returns an arrayref with two items:

$result->[0]: a PathwayTools::Pathway
$result->[1]: the revision of the downloaded pathway.

=cut

sub get_pathway_with_revision ($$)
{
	my $self = shift;
	
	my $species = shift;
	my $name = shift;
	
	my $result = $self->{server}->call("WikiPathways.getPathway", $name, $species);
	
	my $revision = $result->{revision};
	my $gpml = decode_base64($result->{gpml});
	
	my $pathway = new PathwayTools::Pathway();
	$pathway->from_string ($gpml);
	
	return [$pathway, $revision];
}

=item $wikipathways->update_pathway ($pathway, $species, $name, $revision, $message)

params:
$pathway - a PathwayTools::Pathway object
$species - species latin name.
$name - the name of the pathway
$revision - the revision that this updated pathway is based on.
 the webservice will check that this is the latest version available,
 to prevent you from overwriting someone else's changes
$message - a log message describing the changes to the pathway

=cut

sub update_pathway($$$$$)
{
	my $self = shift;
	my $pathway = shift;
	my $species = shift;
	my $name = shift;
	my $revision = shift;
	my $message = shift;
	
	my $result = $self->{server}->call ("WikiPathways.updatePathway", 
		$name, 
		$species, 
		$message, 
		$self->{server}->base64(encode_base64($pathway->to_string())), 
		$revision, 
		{'user' => $self->{user}, 'token' => $self->{token}}
	);
}

1;