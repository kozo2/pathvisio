<?php

$wgExtensionFunctions[] = "wfButton";

function wfButton() {
    global $wgParser;
    $wgParser->setHook( "button", "renderButton" );
}

function renderButton( $input, $argv, &$parser ) {
    $href = $argv['href'];
	$img = $argv['image'];

	$output = "<a href='$href'><img src='$img'></a>";
    return $output;
}



?>
