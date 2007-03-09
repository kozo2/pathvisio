<?php
define("FILETYPE_IMG", "img");
define("FILETYPE_GPML", "gpml");
define("FILETYPE_MAPP", "mapp");

//Initialize MediaWiki
define( 'MEDIAWIKI', true );
require_once( '../wikipathways/includes/Defines.php' );
require_once( '../wikipathways/LocalSettings.php' );
require_once( '../wikipathways/includes/Setup.php' );
require_once( "../wikipathways/includes/Wiki.php" );

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
	//Add pathway url
	$arg .= createJnlpArg("-pwUrl", "http://" . $_SERVER['HTTP_HOST'] . $gpml->getURL());
	$arg .= createJnlpArg("-rpcUrl", "http://" . $_SERVER['HTTP_HOST'] . "/wpi/wpi_rpc.php");
	
	//Add commandline arguments (replace <!--ARG-->)
	$webstart = str_replace("<!--ARG-->", $arg, $webstart);
	header("Content-type: application/x-java-jnlp-file");
	header("Content-Disposition: attachment; filename=\"PathVisio.jnlp\"");
	echo $webstart;
	exit;
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

	function __construct($name) {
		$this->pwName = $name;
	}
	
	public static function newFromTitle($title) {
		$name = Pathway::nameFromTitle($title);
		$code = Pathway::$spName2Code[Pathway::speciesFromTitle($title)];
		if($name && $code) {
			return new Pathway($code . "_" . $name);
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
		return array_pop(array_slice($parts, -2, 1));
	}

	public function name($name = NULL) {
		if($name) {
			$this->pwName = $name;
		}
		return $this->pwName;
	}
	
	public function getFileName($fileType) {
		return $this->getFileTitle($fileType)->getDBKey();
	}
	
	public function getFileTitle($fileType) {
		$fileName = $this->pwName . "." . $this->file_ext[$fileType];
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

	public function updatePathway($gpmlData) {
		$gpmlFile = $this->saveGpml($gpmlData);
		$this->saveImage($gpmlFile);
		$this->saveMAPP($gpmlFile);
	}

	private function saveImage($gpmlFile) {
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
		return Pathway::saveFileToWiki($imgFile, $imgName);
	}

	private function saveMAPP($gpmlFile) {
		//TODO: implement gpml->mapp conversion
	}

	public function saveGpml($gpmlData) {		
		$file = $this->getFileName(FILETYPE_GPML);
	
		writeFile($file, $gpmlData);
		return Pathway::saveFileToWiki($file, $file);
	}
	
	## Based on SpecialUploadForm.php
	## Assumes $saveName is already checked to be a valid Title
	//TODO: run hooks
	static function saveFileToWiki( $fileName, $saveName ) {
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
			                           'Uploaded from wpi.php',
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
