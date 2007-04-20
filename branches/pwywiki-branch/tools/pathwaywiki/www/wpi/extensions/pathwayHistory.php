<?php
require_once('wpi/wpi.php');

$wgExtensionFunctions[] = "wfPathwayHistory";

function wfPathwayHistory() {
    global $wgParser;
    $wgParser->setHook( "pathwayHistory", "history" );
}

function history( $input, $argv, &$parser ) {
	try {
		$pathway = Pathway::newFromTitle($parser->mTitle);
		return getHistory($pathway);
	} catch(Exception $e) {
		return "Error: $e";
	}
}

function getHistory($pathway) {
		global $wgUser, $wpiScriptURL;
		$sk = $wgUser->getSkin();
		
		$imgTitle = $pathway->getFileTitle(FILETYPE_GPML);
		$img = new Image($imgTitle);
		$line = $img->nextHistoryLine();
		$nrShow = 4;
		$buttonStyle = 'color:#0000FF';
		$expand = "<B>View all</B>";
		$collapse = "<B>View last " . ($nrShow - 1) . "</B>";
		if ( $line ) {
			$table = "<TABLE  id='historyTable' class='wikitable'><TR><TH><TH>Time<TH>User<TH>Comment";
			$table .= historyRow(historyLine(true, $line, $pathway), '');
			$nr = 0;
			while($line = $img->nextHistoryLine()) {
				$h = historyLine(false, $line, $pathway);
				$style = $n<$nrShow ? '' : 'style="display:none"';
				$table .= historyRow($h, $style);
				$n++;
			}
			$table .= "</TABLE>";
		}
		if($n >= $nrShow - 1) {
			$button = "<p onClick='toggleRows(\"historyTable\", this, \"$expand\", 
				\"$collapse\", $nrShow, true)' style='cursor:pointer;color:#0000FF'>$expand</p>";
			$table = $button . $table;
		}
		if($wgUser->isAllowed('delete')) {
			$pwTitle = $pathway->getTitleObject()->getDBKey();
			$delete = "<p><a href=$wpiScriptURL?action=delete&pwTitle=$pwTitle>Delete this pathway</a></p>";
			$table = $delete . $table;
		}
		return $table;
}

function historyRow($h, $style) {
	return "<TR $style><TD>$h[rev]$h[view]<TD>$h[date]<TD>$h[user]<TD>$h[descr]";
}

function historyLine($cur, $line, $pathway) {
	global $wpiScript, $wgLang, $wgUser, $wgTitle;
	$revUrl = 'http://'.$_SERVER['HTTP_HOST'] . '/' .$wpiScript . '?action=revert&pwTitle=' .
				$pathway->getTitleObject()->getPartialURL() .
				"&toFile=$line->oi_archive_name&toDate=$line->img_timestamp";
	
	if($wgUser->getID() != 0 && $wgTitle && $wgTitle->userCanEdit()) {
		$rev = $cur ? "" : "(<A href=$revUrl>revert</A>), ";
	}
	$viewUrl = $cur ? $pathway->getFileURL(FILETYPE_IMG) : wfImageArchiveUrl( $line->oi_archive_name );
	$view = '(<A href="' . $viewUrl . '" target="blank">view</A>)';
	$date = $wgLang->timeanddate( $line->img_timestamp, true );
	$user = $wgUser->getSkin()->userLink( $line->img_user, $line->img_user_text );
	$descr = $line->img_description;
	return array('rev'=>$rev, 'view'=>$view, 'date'=>$date, 'user'=>$user, 'descr'=>$descr);
}
?>
