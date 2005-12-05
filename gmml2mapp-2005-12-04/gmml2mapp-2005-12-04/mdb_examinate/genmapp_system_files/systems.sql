-- $Id: systems.sql,v 1.1 2005/08/24 10:54:53 gontran Exp $
-- 
-- Table systems has types for mysql fixed up based on examining the data
-- from the ms access design view for that table.
-- 
-- 
DROP TABLE IF EXISTS Systems;

CREATE TABLE Systems
(
	System 		CHAR( 30) NULL,
	SystemCode 	CHAR( 3) BINARY NULL,
	SystemName 	VARCHAR( 200) NULL,
	_Date 		DATE NULL,
	_Columns 	TEXT NULL,
	Species 	TEXT NULL,
	MOD 		TEXT NULL,
	Link 		VARCHAR( 250) NULL,
	Misc 		TEXT NULL,
	Source 		TEXT NULL
);
-- vim:ts=4:
