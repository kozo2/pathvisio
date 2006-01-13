-- $Id: MAPPTmpl.gtp.sql,v 1.2 2005/09/01 13:30:01 gontran Exp $
-- 
DROP TABLE Objects;
CREATE TABLE Objects
(
 ObjKey                  Text (40),
 ID                      Text (40),
 SystemCode              Single (4),
 Type                    Single (4),
 CenterX                 Single (4),
 CenterY		Single (4),
 SecondX 		Single (4),
 SecondY 		Single (4),
 Width 			Single (4),
 Height 		Long Integer (4),
 Rotation 		Text (100),
Color 		Text (100),
Label			Memo/Hyperlink,
Head			Memo/Hyperlink,
Remarks			Memo/Hyperlink,
Image			Memo/Hyperlink,
Links 			Long Integer (4),
Notes 			Text (4) 
);
-- CREATE ANY INDEXES ...

DROP TABLE Info;
CREATE TABLE Info
(
0	Title 			Text (100),
1	MAPP 			Text (20),
2	GeneDB 			Text (30),
3	GeneDBVersion 	Text (100),
4	Version 		Text (100),
5	Author 			Single (4),
6	Maint 			Single (4),
7	Email 			Single (4),
8	Copyright 		Single (4),
9	Modify 			Memo/Hyperlink,
10	Remarks 		Text (100),
11	BoardWidth 		Text (100),
12	BoardHeight 	Text (100),
13	WindowWidth		Memo/Hyperlink,
14	WindowHeight	Memo/Hyperlink,
15	Notes 			Text (20)

);
-- vim:ts=4:
