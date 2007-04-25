<?php
require_once("QueryPage.php");
require_once($wgScriptPath . "wpi/wpi.php");

class MostEditedPathwaysPage extends SpecialPage
{		
        function MostEditedPathwaysPage() {
                SpecialPage::SpecialPage("MostEditedPathwaysPage");
                self::loadMessages();
        }

        function execute( $par ) {
                global $wgRequest, $wgOut;
                
                $this->setHeaders();

                list( $limit, $offset ) = wfCheckLimits();
				
				//Most revisioned pathway images
				$wgOut->addWikiText("== Most edited pathway files ==");
				$ppp = new MEGPMLQueryPage();

				$ppp->doQuery( $offset, $limit );
				
				//Most edited pathway articles
				$wgOut->addWikiText("== Most edited pathway pages ==");
				$ppp = new MEPQueryPage();

				$ppp->doQuery( $offset, $limit );
							
				return true;
        }

        function loadMessages() {
                static $messagesLoaded = false;
                global $wgMessageCache;
                if ( $messagesLoaded ) return;
                $messagesLoaded = true;

                require( dirname( __FILE__ ) . '/MostEditedPathwaysPage.i18n.php' );
                foreach ( $allMessages as $lang => $langMessages ) {
                        $wgMessageCache->addMessages( $langMessages, $lang );
                }
        }
		
}

class MEPQueryPage extends QueryPage {

	function getName() {
		return "MostEditedPathwaysPage";
	}

	function isExpensive() {
		# page_counter is not indexed
		return true;
	}
	function isSyndicated() { return false; }

	function getSQL() {
		$dbr =& wfGetDB( DB_SLAVE );
		list( $revision, $page ) = $dbr->tableNamesN( 'revision', 'page' );
		return
			"
			SELECT
				'Mostrevisions' as type,
				page_namespace as namespace,
				page_title as title,
				COUNT(*) as value
			FROM $revision
			JOIN $page ON page_id = rev_page
			WHERE page_namespace = " . NS_PATHWAY . "
			AND page_title NOT LIKE '%Sandbox%'
			GROUP BY 1,2,3
			HAVING COUNT(*) > 1
			";
	}

	function formatResult( $skin, $result ) {
		global $wgLang, $wgContLang;

		$nt = Title::makeTitle( $result->namespace, $result->title );
		$text = $wgContLang->convert( $nt->getPrefixedText() );

		$plink = $skin->makeKnownLinkObj( $nt, $text );

		$nl = wfMsgExt( 'nrevisions', array( 'parsemag', 'escape'),
			$wgLang->formatNum( $result->value ) );
		$nlink = $skin->makeKnownLinkObj( $nt, $nl, 'action=history' );

		return wfSpecialList($plink, $nlink);
	}
}

class MEGPMLQueryPage extends QueryPage {

	function getName() {
		return "MostEditedPathwaysPage";
	}

	function isExpensive() {
		# page_counter is not indexed
		return true;
	}
	function isSyndicated() { return false; }

	function getSQL() {
		$dbr =& wfGetDB( DB_SLAVE );
		list( $revision, $page ) = $dbr->tableNamesN( 'oldimage', 'page' );
		return
			"
			SELECT
				'Mostrevisions' as type,
				page_namespace as namespace,
				page_title as title,
				COUNT(*) as value
			FROM oldimage
			JOIN page ON page_title = oi_name
			WHERE page_title LIKE '%.gpml'
			AND page_title NOT LIKE '%Sandbox%'
			GROUP BY 1,2,3
			";
	}

	function formatResult( $skin, $result ) {
		global $wgLang, $wgContLang;
		
		try {

		$pathway = Pathway::newFromFileTitle($result->title);
		
		$nt = $pathway->getTitleObject();
		$text = $wgContLang->convert("$result->value revisions");

		$plink = $skin->makeKnownLinkObj( $nt);
		/* No link to history for now, lateron link to our own pathway history
		$nl = wfMsgExt( 'nrevisions', array( 'parsemag', 'escape'),
			$wgLang->formatNum( $result->value ) );
		$nlink = $skin->makeKnownLinkObj( $nt, $nl, 'action=history' );
		*/
		
		return wfSpecialList($plink, $text, $nlink);
		} catch(Exception $e) {
			return '';
		}
	}
}

?>
