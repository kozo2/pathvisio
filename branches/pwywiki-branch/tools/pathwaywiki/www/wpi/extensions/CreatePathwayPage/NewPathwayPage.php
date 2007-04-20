<?php
# Not a valid entry point, skip unless MEDIAWIKI is defined
if (!defined('MEDIAWIKI')) {
        echo <<<EOT
To install NewPathwayPage, put the following line in LocalSettings.php:
require_once( "$IP/extensions/NewPathwayPage/NewPathwayPage.php" );
EOT;
        exit( 1 );
}

$wgAutoloadClasses['NewPathwayPage'] = dirname(__FILE__) . '/NewPathwayPage_body.php';
$wgSpecialPages['NewPathwayPage'] = 'NewPathwayPage';
$wgHooks['LoadAllMessages'][] = 'NewPathwayPage::loadMessages';

?>