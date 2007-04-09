<?php
define("FILETYPE_IMG", "svg");
define("FILETYPE_GPML", "gpml");
define("FILETYPE_MAPP", "mapp");
define("FILETYPE_PNG", "png");
define("NS_PATHWAY", 100);
define("NS_PATHWAY_TALK", 101);

//Initialize MediaWiki
set_include_path(get_include_path().PATH_SEPARATOR.realpath('../includes').PATH_SEPARATOR.realpath('../').PATH_SEPARATOR);
$dir = getcwd();
chdir("../"); //Ugly, but we need to change to the MediaWiki install dir to include these files, otherwise we'll get an error
require_once ( 'WebStart.php');
require_once( 'Wiki.php' );
chdir($dir);

//Parse HTTP request
$action = $_GET['action'];
switch($action) {
case 'launchPathVisio':
	launchPathVisio($_GET['pwTitle']);
	break;
case 'downloadFile':
	downloadFile($_GET['type'], $_GET['pwTitle']);
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
	$arg .= createJnlpArg("-rpcUrl", "http://" . $_SERVER['HTTP_HOST'] . "/wpi/wpi_rpc.php");
	
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

function downloadFile($fileType, $pwTitle) {
	$pathway = Pathway::newFromTitle($pwTitle);
	$url = $pathway->getFileURL($fileType);
	header("Location: $url");
	//echo("{$pathway->name()} | Type: $fileType | URL: $url");
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
	private $file_ext = array(FILETYPE_IMG => 'svg', FILETYPE_GPML => 'gpml', FILETYPE_MAPP => 'mapp', FILETYPE_PNG => 'png');

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
	
	public function newFromTitle($title) {
		if($title instanceof Title) {
			$title = $title->getFullText();
		}
		
		$name = Pathway::nameFromTitle($title);
		$species = Pathway::speciesFromTitle($title);
		$code = Pathway::$spName2Code[$species]; //Check whether this is a valid species
		if($name && $code) {
			return new Pathway($name, $species);
		} else {
			throw new Exception("Couldn't parse pathway article title: $title->getText()");
		}
	}
	
	public function getTitleObject() {
		//wfDebug("TITLE OBJECT: $this->species():$this->name()\n");
		return Title::newFromText($this->species() . ':' . $this->name(), NS_PATHWAY);
	}
	
	public static function getAvailableSpecies() {
		return array_keys(Pathway::$spName2Code);
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
	
	public function getFileLocation($fileType) {
		$fn = $this->getFileName($fileType);
		return wfImageDir( $fn ) . "/$fn";
	}
	
	public function getFileUrl($fileType) {
		if($fileType == FILETYPE_PNG || $fileType == FILETYPE_MAPP) {
			$this->updateCache($fileType);
		}
		return "http://" . $_SERVER['HTTP_HOST'] . Image::imageURL($this->getFileName($fileType));
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

	public function getImageTitle() {
		return $this->getFileTitle(FILETYPE_IMG);
	}
	
	public function updatePathway($gpmlData, $description) {
		$gpmlFile = $this->saveGpml($gpmlData, $description);
		$this->saveImage($gpmlFile, "Converted from GPML");
	}
	
	public function updateImage() {
		$file = $this->getFileName(FILETYPE_GPML);
		$gpml = wfImageDir($file) . "/$file";
		$this->saveImage($gpml, "Updated from GPML");
	}
	
	private function saveImage($gpmlFile, $description) {
		# Convert gpml to svg
		$gpmlFile = realpath($gpmlFile);
		$imgName = $this->getFileName(FILETYPE_IMG);
		$imgFile = realpath('tmp') . '/' . $imgName;

		exec("java -jar bin/pathvisio_convert.jar $gpmlFile $imgFile 2>&1", $output, $status);
		
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

	private function saveMAPP() {
		//TODO: implement gpml->mapp conversion
	}

	private function saveGpml($gpmlData, $description) {		
		$file = $this->getFileName(FILETYPE_GPML);
		wfDebug("Saving GPML file: $file\n");
		$tmp = "tmp/" . $file;
	
		writeFile($tmp, $gpmlData);
		return Pathway::saveFileToWiki($tmp, $file, $description);
	}
	
	private function savePng() {
		global $wgSVGConverters, $wgSVGConverter, $wgSVGConverterPath;
		
		$input = $this->getFileLocation(FILETYPE_IMG);
		$output = $this->getFileLocation(FILETYPE_PNG);
		
		$width = 1000;
		$retval = 0;
		if(isset($wgSVGConverters[$wgSVGConverter])) {
			$cmd = str_replace( //TODO: calculate proper height for rsvg
				array( '$path/', '$width', '$input', '$output' ),
				array( $wgSVGConverterPath ? wfEscapeShellArg( "$wgSVGConverterPath/" ) : "",
				intval( $width ),
				wfEscapeShellArg( $input ),
				wfEscapeShellArg( $output ) ),
				$wgSVGConverters[$wgSVGConverter] ) . " 2>&1";
			$err = wfShellExec( $cmd, $retval );
			if($retval != 0 || !file_exists($output)) {
				throw new Exception("Unable to convert to png: $err\nCommand: $cmd");
			}
		} else {
			throw new Exception("Unable to convert to png, no SVG rasterizer found");
		}
	}
	
	private function updateCache($fileType) {
		if($this->isOutOfDate($fileType)) {
			wfDebug("Updating cached file for $fileType");
			switch($fileType) {
			case FILETYPE_PNG:
				$this->savePng();
				break;
			case FILETYPE_MAPP:
				$this->saveMapp();
				break;
			}	
		}
	}
	
	//Check if the cached version of the GPML derived file is out of date
	//valid for FILETYPE_MAPP and FILETYPE_PNG
	private function isOutOfDate($fileType) {
		if($fileType == FILETYPE_GPML || $fileType == FILETYPE_IMG) {
			return false; //These files are handled by MediaWiki
		}
		$gpml = $this->getFileLocation(FILETYPE_GPML);
		$file = $this->getFileLocation($fileType);
		if(file_exists($file)) {
			return filemtime($file) < filemtime($gpml);
		} else { //No cached version yet, so definitely out of date
			return true;
		}
	}
	
	## Based on SpecialUploadForm.php
	## Assumes $saveName is already checked to be a valid Title
	//TODO: run hooks
	static function saveFileToWiki( $fileName, $saveName, $description ) {
		global $wgLoadBalancer, $wgUser;
				
		wfDebug("========= UPLOADING FILE FOR WIKIPATHWAYS ==========\n");
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
			$hds = $_SERVER;
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
