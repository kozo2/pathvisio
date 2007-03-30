<?php
define("FILETYPE_IMG", "img");
define("FILETYPE_GPML", "gpml");
define("FILETYPE_MAPP", "mapp");

//Initialize MediaWiki
#define( 'MEDIAWIKI', true );
#require_once( '../includes/Defines.php' );
#require_once( '../LocalSettings.php' );
$dir = getcwd();
chdir("../");
#require_once( '.StartProfiler.php' );
require_once ( 'includes/WebStart.php');
require_once( 'includes/Wiki.php' );
chdir($dir);

$wgDebugLogFile = 'debug.txt';

//Parse HTTP request
$action = $_GET['action'];
switch($action) {
case 'launchPathVisio':
	launchPathVisio($_GET['pwTitle']);
	break;
}

function launchPathVisio($pwTitle) {
	$pathway = Pathway::newFromTitle($pwTitle);
	$gpml = Image::newFromName( $pathway->getFileName(FILETYPE_GPML) );
	if(!$gpml->exists()) {
		echo "Image does not exist<BR>";
		exit;
	}
	
	$webstart = file_get_contents("bin/pathvisio_wikipathways.jnlp");
	
	//Add cookies
	foreach (array_keys($_COOKIE) as $key) {
		$arg .= createJnlpArg("-c", $key . "=" . $_COOKIE[$key]);
	} 
	//Add pathway name
	$arg .= createJnlpArg("-pwName", $pathway->name());
	$arg .= createJnlpArg("-pwSpecies", $pathway->species());
	//Add pathway url
	$arg .= createJnlpArg("-pwUrl", "http://" . $_SERVER['HTTP_HOST'] . $gpml->getURL());
	$arg .= createJnlpArg("-rpcUrl", "http://" . $_SERVER['HTTP_HOST'] . "/wikipathways/wpi/wpi_rpc.php");
	
	//Add commandline arguments (replace <!--ARG-->)
	$webstart = str_replace("<!--ARG-->", $arg, $webstart);

	$os = getClientOs();
	if($os == 'linux') { //return shell script that sets MOZILLA_FIVE_HOME and opens webstart
		header("Content-type: application/x-shellscript");
		header("Content-Disposition: attachment; filename=\"PathVisio.sh\"");
		echo "#!/bin/sh\n";
		echo "export MOZILLA_FIVE_HOME=/usr/lib/firefox\n";
		echo "LD_LIBRARY_PATH=/usr/lib/firefox:$LD_LIBRARY_PATH\n";
		$wsFile = tempnam(getcwd() . "/tmp",$pathway->name());
		writeFile($wsFile, $webstart);
		//echo 'javaws "http://' . $_SERVER['HTTP_HOST'] . '/wpi/tmp/' . basename($wsFile) . '"'; #For local tests
		echo 'javaws "http://' . $_SERVER['HTTP_HOST'] . '/wikipathways/wpi/tmp/' . basename($wsFile) . '"';
	} else { //return webstart file directly
		header("Content-type: application/x-java-jnlp-file");
		header("Content-Disposition: attachment; filename=\"PathVisio.jnlp\"");
		echo $webstart;
	}
	exit;
}

function getClientOs() {
	$regex = array(
		'windows' => '([^dar]win[dows]*)[\s]?([0-9a-z]*)[\w\s]?([a-z0-9.]*)',
		'mac' => '(68[k0]{1,3})|(ppc mac os x)|([p\S]{1,5}pc)|(darwin)',
		'linux' => 'x11|inux');
	$ua = $_SERVER['HTTP_USER_AGENT'];
	foreach (array_keys($regex) as $os) {
		if(eregi($regex[$os], $ua)) return $os;
	}	
}
 
function createJnlpArg($flag, $value) {
	//return "<argument>" . $flag . ' "' . $value . '"' . "</argument>\n";
	return "<argument>" . $flag . "</argument>\n<argument>" . $value . "</argument>\n";
}

$spName2Code = array('Human' => 'Hs', 'Rat' => 'Rn', 'Mouse' => 'Mm');//TODO: complete

class Pathway {
	private static $spName2Code = array('Human' => 'Hs', 'Rat' => 'Rn', 'Mouse' => 'Mm');//TODO: complete
	private $file_ext = array(FILETYPE_IMG => 'svg', FILETYPE_GPML => 'gpml', FILETYPE_MAPP => 'mapp');

	private $spCode2Name;
	private $pwName;
	private $pwSpecies;

	function __construct($name, $species) {
		if(!$name) throw new Exception("name argument missing in constructor for Pathway");
		if(!$species) throw new Exception("species argument missing in constructor for Pathway");

		wfDebug("=== New pathway:\n\tpwName: $name\n\tpwSpecies: $species\n");
		$this->pwName = $name;
		$this->pwSpecies = $species;
	}
	
	public static function newFromTitle($title) {
		$name = Pathway::nameFromTitle($title);
		$species = Pathway::speciesFromTitle($title);
		$code = Pathway::$spName2Code[$species]; //Check whether this is a valid species
		if($name && $code) {
			return new Pathway($name, $species);
		} else {
			throw new Exception("Couldn't parse pathway article title: $title");
		}
	}
	
	private static function nameFromTitle($title) {
		$parts = explode(':', $title);

		if(count($parts) < 2) {
			throw new Exception("Invalid pathway article title: $title");
		}
		return array_pop($parts);
	}

	private static function speciesFromTitle($title) {
		$parts = explode(':', $title);

		if(count($parts) < 2) {
			throw new Exception("Invalid pathway article title: $title");
		}
		$species = array_slice($parts, -2, 1);
		return array_pop($species);
	}

	public function name($name = NULL) {
		if($name) {
			$this->pwName = $name;
		}
		return $this->pwName;
	}
	
	public function species($species = NULL) {
		if($species) {
			$this->pwSpecies = $species;
		}
		return $this->pwSpecies;
	}
	
	public function getSpeciesCode() {
		return Pathway::$spName2Code[$this->pwSpecies];
	}

	public function getFileName($fileType) {
		return $this->getFileTitle($fileType)->getDBKey();
	}
	
	public function getFileTitle($fileType) {
		$fileName = $this->getSpeciesCode() . "_" . $this->pwName . "." . $this->file_ext[$fileType];
		/*
		 * Filter out illegal characters, and try to make a legible name
		 * out of it. We'll strip some silently that Title would die on.
		 */
		$filtered = preg_replace ( "/[^".Title::legalChars()."]|:/", '-', $fileName );
		$title = Title::newFromText( $filtered, NS_IMAGE );
		if(!$title) {
			throw new Exception("Invalid file title for pathway " + $fileName);
		}
		return $title;
	}

	public function updatePathway($gpmlData, $description) {
		$gpmlFile = $this->saveGpml($gpmlData, $description);
		$this->saveImage($gpmlFile, "Converted from GPML");
		$this->saveMAPP($gpmlFile, "Converted from GPML");
	}

	private function saveImage($gpmlFile, $description) {
		# Convert gpml to svg
		$gpmlFile = realpath($gpmlFile);
		$imgName = $this->getFileName(FILETYPE_IMG);
		$imgFile = realpath(".") . "/" . $imgName;

		exec("java -jar bin/pathvisio_convert.jar $gpmlFile $imgFile", $output, $status);
		
		foreach ($output as $line) {
			$msg .= $line . "\n";
		}
		wfDebug("Converting to SVG:\nStatus:$status\nMessage:$msg");
		if($status != 0 ) {
			throw new Exception("Unable to convert to SVG:\nStatus:$status\nMessage:$msg");
		}
		# Upload svg file to wiki
		return Pathway::saveFileToWiki($imgFile, $imgName, $description);
	}

	private function saveMAPP($gpmlFile, $description) {
		//TODO: implement gpml->mapp conversion
	}

	public function saveGpml($gpmlData, $description) {		
		$file = $this->getFileName(FILETYPE_GPML);
		wfDebug("Saving GPML file: $file\n");
		$tmp = "tmp/" . $file;
	
		writeFile($tmp, $gpmlData);
		return Pathway::saveFileToWiki($tmp, $file, $description);
	}
	
	## Based on SpecialUploadForm.php
	## Assumes $saveName is already checked to be a valid Title
	//TODO: run hooks
	static function saveFileToWiki( $fileName, $saveName, $description ) {
		global $wgLoadBalancer, $wgUser;
				
		# Check uploading enabled
		#if( !$wgEnableUploads ) {
		#	return "Uploading is disabled";
		#}
		
		# Check permissions
		if( $wgUser->isLoggedIn() ) {
			if( !$wgUser->isAllowed( 'upload' ) ) {
				throw new Exception( "User has no permission to upload" );
			}
		} else {
			//Print out http headers (for debugging)
			$hds = apache_request_headers();
			wfDebug("REQUEST HEADERS\n");
			foreach (array_keys($hds) as $key) {
				wfDebug($key . "=" . $hds[$key] . "\n");
			}
			throw new Exception( "User not logged on" );
		}

		# Check blocks
		if( $wgUser->isBlocked() ) {
			throw new Exception( "User is blocked" );
		}

		if( wfReadOnly() ) {
			throw new Exception( "Page is read-only" );
		}

		# Move the file to the proper directory
		$dest = wfImageDir( $saveName );
		$archive = wfImageArchiveDir( $saveName );
		
		$toFile = "{$dest}/{$saveName}";
		if( is_file( $toFile) ) {
			$oldVersion = gmdate( 'YmdHis' ) . "!{$saveName}";
			$success = rename($toFile, "{$archive}/{$oldVersion}");
			if(!$success) {
				throw new Exception( 
					"Unable to rename file $olddVersion to {$archive}/{$oldVersion}" );
			}
		}
		rename($fileName, $toFile);
		if(!$success) {
			throw new Exception( "Unable to rename file $fileName to $toFile" );
		}
		chmod($toFile, 0644);
		
		# Update the image page
		$img = Image::newFromName( $saveName );
		$success = $img->recordUpload( $oldVersion,
			                           $description,
			                           wfMsgHtml( 'license' ),
			                           "", //Copyright
			                           $fileName,
			                           FALSE ); //Watchthis
		if(!$success) {
			throw new Exception( "Couldn't create description page" );
		}

		$wgLoadBalancer->commitAll();
		return $toFile; # return the saved file
	}
}

function writeFile($filename, $data) {
	$handle = fopen($filename, 'w');
	if(!$handle) {
		throw new Exception ("Couldn't open file $filename");
	}
	fwrite($handle, $data);
	fclose($handle);
}
?>
