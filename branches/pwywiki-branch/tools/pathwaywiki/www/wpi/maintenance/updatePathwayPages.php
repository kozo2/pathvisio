<?php

$dir = getcwd();
chdir("../"); //Ugly, but we need to change to the MediaWiki install dir to include these files, otherwise we'll get an error
require_once('wpi.php');
chdir($dir);

$dbr =& wfGetDB(DB_SLAVE);
$res = $dbr->select( "page", array("page_title"), array("page_namespace" => NS_PATHWAY));
$np = $dbr->numRows( $res );
while( $row = $dbr->fetchRow( $res )) {
	echo "Updating $row[0]<br>";
	$pathway = Pathway::newFromTitle($row[0]);
	$title = $pathway->getTitleObject();
	$revision = Revision::newFromTitle($title);
	$article = new Article($title);
	$text = $revision->getText();
	
	$regex = "\{\{Template:PathwayPage.*\}\}";
	$imagePage = $pathway->getFileTitle(FILETYPE_IMG)->getFullText();
	$gpmlPage = $pathway->getFileTitle(FILETYPE_GPML)->getFullText();
	
	$text = ereg_replace($regex, "{{Template:PathwayPage|imagePage=$imagePage|gpmlPage=$gpmlPage|pathwayPage={{FULLPAGENAME}} }}", $text);

	if($article->doEdit($text, 'Updated template', EDIT_UPDATE | EDIT_FORCE_BOT)) {
		echo "Updated to: $text<br>";
	} else {
		echo "UPDATE FAILED";
	}
	$wgLoadBalancer->commitAll();
}
?>