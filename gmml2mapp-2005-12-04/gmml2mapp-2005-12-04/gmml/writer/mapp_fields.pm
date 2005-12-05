# $Id: mapp_fields.pm,v 1.1 2005/09/22 12:35:01 gontran Exp $

package mapp_fields;

sub get_Info_headers
{

	# table header order, and mappings 
	@Info_headers =
		( 
			'Title', 'MAPP', 'GeneDB', 'GeneDBVersion', 'Version',
			'Author', 'Maint', 'Email', 'Copyright', 'Modify', 'Remarks',
			'BoardWidth', 'BoardHeight', 'WindowWidth', 'WindowHeight',
			'Notes'
		);
	
	return @Info_headers;
}

sub get_Object_headers
{

	@Object_headers = 
		(
			'ObjKey', 'ID', 'SystemCode', 'Type', 'CenterX', 'CenterY',
			'SecondX', 'SecondY', 'Width', 'Height', 'Rotation', 'Color',
			'Label', 'Head', 'Remarks', 'Image', 'Links', 'Notes'	
		);
	
	return @Object_headers;
}

1;
__END__
vim:ts=4:
