-- $Id: mapp_schema.sql,v 1.2 2005/08/24 10:53:50 gontran Exp $
-- Based on output from mdb-schema from mdbtools 0.5
-- 
DROP TABLE Objects;
CREATE TABLE Objects
(
ObjKey		Text (40),
ID		Text (40),
SystemCode	Single (4),
Type		Single (4),
CenterX		Single (4),
CenterY 	Single (4),
SecondX 	Single (4),
SecondY 	Single (4),
Width 		Single (4),
Height 		Long Integer (4),
Rotation 	Text (100),
Color 		Text (100),
Label 		Memo/Hyperlink,
Head 		Memo/Hyperlink,
Remarks 	Memo/Hyperlink,
Image 		Memo/Hyperlink,
Links 		Long Integer (4),
Notes 		Text (4)
);

DROP TABLE Info;
CREATE TABLE Info
(
Title 		Text (100),
MAPP 		Text (20),
GeneDB 		Text (30),
GeneDBVersion 	Text (100),
Version 	Text (100),
Author 		Single (4),
Maint 		Single (4),
Email 		Single (4),
Copyright 	Single (4),
Modify 		Memo/Hyperlink,
Remarks 	Text (100),
BoardWidth 	Text (100),
BoardHeight 	Text (100),
WindowWidth 	Memo/Hyperlink,
WindowHeight 	Memo/Hyperlink,
Notes 		Text (20)
);
