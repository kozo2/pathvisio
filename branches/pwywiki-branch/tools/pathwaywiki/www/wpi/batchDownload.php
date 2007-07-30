<?php
require_once('includes/zip.lib.php');
require_once('wpi.php');

//As mediawiki extension
$wgExtensionFunctions[] = "wfBatchDownload";

function wfBatchDownload() {
    global $wgParser;
    $wgParser->setHook( "batchDownload", "createDownloadLinks" );
}

//To be called directly
if(realpath($_SERVER['SCRIPT_FILENAME']) == realpath(__FILE__)) {
	$species = $_GET['species'];
	$fileType = $_GET['filetype'];

	if($species) {
		batchDownload($species, $fileType ? $fileType : FILETYPE_GPML);
	}
}

function createDownloadLinks($input, $argv, &$parser) {
	$fileType = $argv['filetype'];
	foreach(Pathway::getAvailableSpecies() as $species) {
		$html .= tag('li', 
					tag('a',$species,array('href'=> WPI_URL . '/' . "batchDownload.php?species=$species&fileType=$fileType", 'target'=>'_blank')));
	}
	$html = tag('ul', $html);
	return $html;
}

function batchDownload($species, $fileType) {
	if(!(
		$fileType == FILETYPE_GPML ||
		$fileType == FILETYPE_IMG ||
		$fileType == FILETYPE_PNG)) {
		throw new Exception("Invalid file type: $fileType");
	}
	$pathways = getPathways(array(
		"page_title LIKE '$species%'"		
	));
	doDownload($pathways, $fileType); //Exits script
}

function getPathways($conditions = array()) {
	$conditions = array_merge($conditions,
		array(
			'page_namespace' => NS_PATHWAY,
			'page_is_redirect' => 0,
			"page_title != 'Human:Sandbox'"
		)
	);
	$dbr =& wfGetDB( DB_SLAVE );
	$res = $dbr->select( 'page',
		array( 'page_namespace', 'page_title', 'page_is_redirect' ),
		$conditions
	);

	$pathways = array();
	while($s = $dbr->fetchObject( $res ) ) {
			$t = Title::makeTitle( $s->page_namespace, $s->page_title );
			try {
				$pw = Pathway::newFromTitle($t);
				array_unshift($pathways, $pw);
			} catch(Exception $e) {
				wfDebug("Unable to create pathway object", $e);
			}
	}
	return $pathways;
}

function doDownload($pathways, $fileType) {
	$zipFile = tempnam(WPI_TMP_PATH, 'batchDownload') . '.zip';

	foreach($pathways as $pw) {
		$files .= $pw->getFileLocation($fileType) . ' ';
	}

	$cmd = "zip $zipFile $files 2>&1";
	wfDebug("ZIPPING: $cmd\n");
	exec($cmd, $output, $status);	
	foreach ($output as $line) {
		$msg .= $line . "\n";
	}

	//header("Location: " . WPI_TMP_URL . '/' . basename($zipFile));
	ob_clean();
	header('Content-disposition: attachment; filename=wikipathways.zip');
	header('Content-type: application/zip');
	header('Content-size: ' . $filesize($zipFile));
	readfile($zipFile);
	unlink($zipFile); //Clean up after yourself
	exit;
}

?>
