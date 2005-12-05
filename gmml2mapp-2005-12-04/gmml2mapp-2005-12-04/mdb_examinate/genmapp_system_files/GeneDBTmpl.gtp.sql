-- $Id: GeneDBTmpl.gtp.sql,v 1.2 2005/08/24 10:54:25 gontran Exp $
-- 
DROP TABLE Info;
CREATE TABLE Info
(
Owner                   Text (400),
Version                 Text (20),
MODSystem               Memo/Hyperlink,
Species                 Text (20),
Modify			Memo/Hyperlink,
Notes			Text (40)

 );
-- CREATE ANY INDEXES ...

DROP TABLE Other;
CREATE TABLE Other
(
ID 		Text (60),
SystemCode 	Text (4),
Name 		Memo/Hyperlink,
Annotations 	Memo/Hyperlink,
Species 	Memo/Hyperlink,
Date 		DateTime (Short) (8),
Remarks 	Memo/Hyperlink

 );
-- CREATE ANY INDEXES ...

DROP TABLE Relations;
CREATE TABLE Relations
(
SystemCode 	Text (20),
RelatedCode 	Memo/Hyperlink,
Relation 	Text (6),
Type 		Text (6),
Source 		Text (122)

 );
-- CREATE ANY INDEXES ...

-- Table systems has types for mysql fixed up based on examining the data
-- from the ms access design view for that table.
DROP TABLE Systems;
CREATE TABLE Systems
(
	System 		CHAR( 30) NULL,
	SystemCode 	CHAR( 3) BINARY NULL,
	SystemName 	VARCHAR( 200) NULL,
	_Date 		DATE NULL;
	Columns 	TEXT NULL,
	Species 	TEXT NULL,
	MOD 		TEXT NULL,
	Link 		VARCHAR( 250) NULL,
	Misc 		TEXT NULL,
	Source 		TEXT NULL
);
-- vim:ts=4:
