<?php

define("JS_OPEN_EDITOR_APPLET", "JS_OPEN_EDITOR_APPLET");

$wgExtensionFunctions[] = 'wfPathwayThumb';
$wgHooks['LanguageGetMagic'][]  = 'wfPathwayThumb_Magic';

function wfPathwayThumb() {
    global $wgParser;
    $wgParser->setFunctionHook( "pwImage", "renderPathwayImage" );
}

function wfPathwayThumb_Magic( &$magicWords, $langCode ) {
        $magicWords['pwImage'] = array( 0, 'pwImage' );
        return true;
}



function renderPathwayImage( &$parser, $pwTitle, $width = 0, $align = '', $caption = '', $href = '', $tooltip = '', $id='pwthumb') {    
        try {
                $pathway = Pathway::newFromTitle($pwTitle);
                $img = new Image($pathway->getFileTitle(FILETYPE_IMG));
                switch($href) {
                        case 'svg':
                                $href = Image::imageUrl($pathway->getFileTitle(FILETYPE_IMG)->getPartialURL());
                                break;
                        case 'pathway':
                                $href = $pathway->getFullURL();
                                break;
                        default:
                                if(!$href) $href = $pathway->getFullURL();
                }
                $doApplet = strpos($caption, JS_OPEN_EDITOR_APPLET);
                if($doApplet) { //Add a link to the editor applet to the caption
                        $appletCode = makeAppletFunctionCall($pathway, $id, $new);
                }
                $caption = html_entity_decode($caption);        //This can be quite dangerous (injection),
                                                                //we would rather parse wikitext, let me know if
                                                                //you know a way to do that (TK)
                $output = makeThumbLinkObj($pathway, $caption, $href, $tooltip, $align, $id, $width);
                
                if($doApplet) {
                  //Replace JS_OPEN_EDITOR_APPLET with applet call
                  $output = str_replace(JS_OPEN_EDITOR_APPLET, $appletCode, $output);
                  //Add import javascript files (also for resize)
                  $output = scriptTag('', JS_SRC_EDITAPPLET) . scriptTag('', '/wpi/js/prototype.js') . scriptTag('', JS_SRC_RESIZE) . $output;
                }
        } catch(Exception $e) {
                return "invalid pathway title: $e";
        }
       // echo($output);
        return array($output, 'isHTML'=>1, 'noparse'=>1);
}

    function scriptTag($code, $src = '') {
      $src = $src ? 'src="' . $src . '"' : '';
      return '<script type="text/javascript"' . $src . '">' . $code . '</script>';
    }

    function makeAppletFunctionCall($pathway, $id, $new) {
        global $wgUser;

  			if($new) {
     	         	$pwUrl = $pathway->getTitleObject()->getFullURL();
     			} else {
     	         	$pwUrl = $pathway->getFileURL(FILETYPE_GPML);
      		}

  			$args = array(
  					'rpcUrl' => "http://" . $_SERVER['HTTP_HOST'] . "/wpi/wpi_rpc.php",
  					'pwName' => 	$pathway->name(),	
  					'pwSpecies' => $pathway->species(),
  					'pwUrl' => $pwUrl
  			);
  			if($wgUser && $wgUser->isLoggedIn()) {
             		$args = array_merge($args, array('user', $wgUser->getRealName()));
     		}
      	if($new) {
              	$args = array_merge($args, array('new' => true));
      	}
        $keys = createJsArray(array_keys($args));
        $values = createJsArray(array_values($args));
        //return "replaceWithApplet('{$id}', 'applet', {$keys}, {$values});new Resizeable('applet', {bottom: 10, right: 10, left: 0, top: 0});";
	return "replaceWithApplet('{$id}', 'applet', {$keys}, {$values});";
    }

    function createJsArray($array) {
      $jsa = "new Array(";      
      foreach($array as $elm) {
        $jsa .= "'{$elm}', ";
      }
      return substr($jsa, 0, strlen($jsa) - 2) . ')';
    }

    /** MODIFIED FROM Linker.php
        * Make HTML for a thumbnail including image, border and caption
        * $img is an Image object
        */
    function makeThumbLinkObj( $pathway, $label = '', $href = '', $alt, $align = 'right', $id = 'thumb', $boxwidth = 180, $boxheight=false, $framed=false ) {
            global $wgStylePath, $wgContLang;

            $img = new Image($pathway->getFileTitle(FILETYPE_IMG));
            $imgURL = $img->getURL();

            $thumbUrl = '';
            $error = '';

            $width = $height = 0;
            if ( $img->exists() ) {
                    $width  = $img->getWidth();
                    $height = $img->getHeight();
            }
            if ( 0 == $width || 0 == $height ) {
                    $width = $height = 180;
            }
            if ( $boxwidth == 0 ) {
                    $boxwidth = 180;
            }
            if ( $framed ) {
                    // Use image dimensions, don't scale
                    $boxwidth  = $width;
                    $boxheight = $height;
                    $thumbUrl  = $img->getViewURL();
            } else {
                    if ( $boxheight === false ) $boxheight = -1;
                    $thumb = $img->getThumbnail( $boxwidth, $boxheight );
                    if ( $thumb ) {
                            $thumbUrl = $thumb->getUrl();
                            $boxwidth = $thumb->width;
                            $boxheight = $thumb->height;
                    } else {
                            $error = $img->getLastError();
                    }
            }
            $oboxwidth = $boxwidth + 2;

            $more = htmlspecialchars( wfMsg( 'thumbnail-more' ) );
            $magnifyalign = $wgContLang->isRTL() ? 'left' : 'right';
            $textalign = $wgContLang->isRTL() ? ' style="text-align:right"' : '';

            $s = "<div id=\"{$id}\" class=\"thumb t{$align}\"><div class=\"thumbinner\" style=\"width:{$oboxwidth}px;\">";
            if( $thumbUrl == '' ) {
                    // Couldn't generate thumbnail? Scale the image client-side.
                    $thumbUrl = $img->getViewURL();
                    if( $boxheight == -1 ) {
                            // Approximate...
                            $boxheight = intval( $height * $boxwidth / $width );
                    }
            }
            if ( $error ) {
                    $s .= htmlspecialchars( $error );
                    $zoomicon = '';
            } elseif( !$img->exists() ) {
                    $s .= "Image does not exist";
                    $zoomicon = '';
            } else {
                    $s .= '<a href="'.$href.'" class="internal" title="'.$alt.'">'.
                            '<img src="'.$thumbUrl.'" alt="'.$alt.'" ' .
                            'width="'.$boxwidth.'" height="'.$boxheight.'" ' .
                            'longdesc="'.$href.'" class="thumbimage" /></a>';
                    if ( $framed ) {
                            $zoomicon="";
                    } else {
                            $zoomicon =  '<div class="magnify" style="float:'.$magnifyalign.'">'.
                                    '<a href="'.$imgURL.'" class="internal" title="'.$more.'">'.
                                    '<img src="'.$wgStylePath.'/common/images/magnify-clip.png" ' .
                                    'width="15" height="11" alt="" /></a></div>';
                    }
            }
            $s .= '  <div class="thumbcaption"'.$textalign.'>'.$zoomicon.$label."</div></div></div>";
            return str_replace("\n", ' ', $s);
            //return $s;
    }

?>

