<?php

error_reporting(E_ERROR); //Supress warnings etc...will disrupt the rpc response
//Load XML-RCP libraries
require("includes/xmlrpc.inc");
require("includes/xmlrpcs.inc");

//Load WikiPathways Interface
require("wpi.php");

//Definition of functions
$updatePathway_sig=array(array(
							$xmlrpcBoolean, 
							$xmlrpcString, $xmlrpcString, $xmlrpcBase64
						));

$updatePathway_doc='updatePathway';

//Definition of dispatch map
$disp_map=array(     "WikiPathways.updatePathway" => 
                           array("function" => "updatePathway",
                           "signature" => $updatePathway_sig,
                           "docstring" => $updatePathway_doc),
);

//Setup the XML-RPC server
$s=new xmlrpc_server($disp_map,0);
$s->functions_parameters_type = 'phpvals';
//$s->setDebug(3);
$s->service();

//Functions
function updatePathway($pwName, $pwSpecies, $gpmlData64) {
	global $xmlrpcerruser;
	
	try {
		$pathway = new Pathway($pwName, $pwSpecies);
		$gpmlData = base64_decode($gpmlData64);
		$pathway->updatePathway($gpmlData);
	} catch(Exception $e) {
		$resp = new xmlrpcresp(0, $xmlrpcerruser, $e->getMessage());
		return $resp;
	}
	return TRUE;
}
?>
