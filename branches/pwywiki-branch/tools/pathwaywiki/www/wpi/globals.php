<?php

//File types
define("FILETYPE_IMG", "svg");
define("FILETYPE_GPML", "gpml");
define("FILETYPE_MAPP", "mapp");
define("FILETYPE_PNG", "png");

//Script info
$wpiScriptPath = 'wpi';
$wpiScriptFile = 'wpi.php';
$wpiScript = "$wpiScriptPath/$wpiScriptFile"; 
$wpiScriptURL =  "http://" . $_SERVER['HTTP_HOST'] . '/' . $wpiScript; //TODO: use these variables


//JS info
define("JS_SRC_EDITAPPLET", "/wpi/js/editapplet.js");
define("JS_SRC_RESIZE", "/wpi/js/resize.js");
define("JS_SRC_PROTOTYPE", "/wpi/js/prototype.js");
?>
