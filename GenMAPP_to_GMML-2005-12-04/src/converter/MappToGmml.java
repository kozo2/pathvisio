// $Id: MappToGmml.java,v 1.5 2005/10/21 12:33:27 gontran Exp $
package converter;

import java.util.*;
import java.math.BigDecimal;
import javax.xml.bind.*;
import gmml.*;

// don't need this for the commandline version!
//import javax.swing.*;


public class MappToGmml
{
    public static ObjectFactory o = new ObjectFactory();
    
	// This method copies the Info table of the genmapp mapp to a new gmml
	// pathway
	public static void copyMappInfo( String[][] mappInfo, Pathway p)
	{

	/* Data is lost when converting from GenMAPP to GMML:
	*
	* GenMAPP: 
	*		"Title", "MAPP", "Version", "Author","GeneDBVersion",
	* 		"Maint", "Email", "Copyright","Modify", 
	*		"Remarks", "BoardWidth", "BoardHeight","WindowWidth",
	*		"WindowHeight", "GeneDB", "Notes"
	* GMML:    
	*		"Name", NONE, Version, "Author", NONE, 
	*		"MaintainedBy", "Email", "Availability", "LastModified",
	*		"Comment", "BoardWidth", "BoardHeight", NONE, 
	*		NONE, NONE, "Notes"
	*
	*/

	System.out.println(">> CONVERTING INFO TABLE TO GMML");

	try
	{
		/*
		*
    	* from: fileInOut.MappFile.importMAPPInfo(String filename)
		* below mappInfo array is indexed in the following order -- NOT
		* like in the SQL order from the Info table.  *sigh*
		*
		* 0 "Title", 1 "MAPP", 2 "Version", 3 "Author", 4 "GeneDBVersion",
		* 5 "Maint", 6 "Email", 7 "Copyright", 8 "Modify", 9 "Remarks",
		* 10 "BoardWidth", 11 "BoardHeight", 12 "WindowWidth",
		* 13 "WindowHeight", 14 "GeneDB", 15 "Notes"
		*
		*/

		// Info.Title
		p.setName(mappInfo[1][0]);

		// ! in Info.
		p.setDataSource("GenMAPP 2.0");

		// Info.Version
		p.setVersion(mappInfo[1][2]);

		// Info.Author
		p.setAuthor(mappInfo[1][3]);

		// Info.Maint
		p.setMaintainedBy(mappInfo[1][5]);

		// Info.Email
		p.setEmail(mappInfo[1][6]);

		// Info.Copyright
		p.setAvailability(mappInfo[1][7]);

		// Info.Modify
		p.setLastModified(mappInfo[1][8]);

		// Set the simpleType elements !! IMPORTANT: these elements
		// have no representable class --> restrictions not implemented

		// Info.Notes
		p.setNotes(mappInfo[1][15]);
		// Info.Remarks
		p.setComment(mappInfo[1][9]);

		// Set the graphics element
		//System.out.println("\n>> Checking Board sizes:\n");

		PathwayType.GraphicsType g = o.createPathwayTypeGraphicsType();

		// Info.BoardWidth
		g.setBoardWidth( ConvertType.stringToBigInt( mappInfo[1][10]));
		//System.out.println(">> \tBoardWidth:" + g.getBoardWidth() + "\n");

		// Info.BoardHeight
		g.setBoardHeight( ConvertType.stringToBigInt( mappInfo[1][11]));
		//System.out.println(">> \tBoardHeight:" + g.getBoardHeight() + "\n");

		// Gontran added these:
		// Info.Windowwidth
		g.setWindowWidth( ConvertType.stringToBigInt( mappInfo[1][12] ) );
		// Info.WindowHeight
		g.setWindowHeight( ConvertType.stringToBigInt( mappInfo[1][13] ) );

		p.setGraphics( g);

		}
		catch (JAXBException ex)
		{
		    System.out.println( "-> Some elements could not be converted "
		    	+ "due to JAXBException '" + ex.getMessage() + "\n");
		}
	}
    
	// This list adds the elements from the OBJECTS table to the new gmml
	// pathway
    public static void copyMappObjects(String[][] mappObjects, Pathway p)
    {
        System.out.println(">> CONVERTING OBJECTS TABLE TO GMML");

		// Create the GenMAPP --> GMML mappings list for use in the switch
		// statement

        List mappingsList = createMappingsList(mappObjects[0]);

		String[] types = { 
				"Arrow", "DottedArrow", "DottedLine", "Line",
				"Brace", "Gene", "InfoBox", "Label", "Legend", "Oval",
				"Rectangle", "TBar", "Receptor", "LigandSq",  "ReceptorSq",
				"LigandRd", "ReceptorRd", "CellA", "Arc", "Ribosome",
				"OrganA", "OrganB", "OrganC", "ProteinB"
		};

		List typeslist = new ArrayList();
		int index = 0;

		for(int i=0; i<types.length; i++)
		{
				typeslist.add(types[i]);
		}

		/*index 0 are heades*//*last row is always null*/

		for(int i=1; i<mappObjects.length-1; i++)
		{
			index = typeslist.indexOf( mappObjects[i][3]);

			switch(index) {
			/*Arrow*/
					case 0:
							/*DottedArrow*/
					case 1:
							/*DottedLine"*/
					case 2:
							/*Line*/
					case 3:
							Line l = mapLineType(mappObjects[i], mappingsList);
							p.getLine().add(l);
							break;

							/*Brace*/
					case 4:
							Brace b = mapBraceType(mappObjects[i], mappingsList);
							p.getBrace().add(b);
							break;

							/*Gene*/
					case 5:
							GeneProduct g = mapGeneProductType(mappObjects[i], mappingsList);
							p.getGeneProduct().add(g);
							break;

							/*InfoBox*/
							// this seems like it might be overwriting our
							// previously created graphics type... maybe
							// handle the infobox another way....
							/*
					case 6:
							try
							{
								PathwayType.GraphicsType pg = o.createPathwayTypeGraphicsType();
								// MapInfoLeft  --> CenterX
								pg.setMapInfoLeft(ConvertType.stringToBigInt(mappObjects[i][mappingsList.indexOf("CenterX")]));
								// MapInfoTop   --> CenterY
								pg.setMapInfoTop(ConvertType.stringToBigInt(mappObjects[i][mappingsList.indexOf("CenterY")]));
								p.setGraphics(pg);
							}
							catch(JAXBException ex)
							{
								System.out.println( "-> element 'InfoBox' "
												+ "could not be converted due to JAXBException "
												+ ex.getMessage() + "\n");
							}
							break;

*/

							/*Label*/
					case 7:
							Label la = mapLabelType(mappObjects[i], mappingsList);
							p.getLabel().add(la);
							break;

							/*Legend*/
							/* !! GMML has no representation for the Legend object !!*/
					case 8:
							break;

							/*Oval*/
					case 9:
							/*Rectangle*/
					case 10:
							Shape s = mapShapeType( mappObjects[i], mappingsList);
							p.getShape().add(s);
							break;

							/*TBar*/
					case 11:
							/*Receptor*/
					case 12:
							/*LigandSq*/                        
					case 13:
							/*ReceptorRd*/                        
					case 14:
							/*ReceptorRd*/                        
					case 15:
							/*LigandRd*/                         
					case 16:
							LineShape ls = mapLineShapeType(mappObjects[i], mappingsList);
							p.getLineShape().add(ls);
							break;

							/*CellA*/
					case 17:
							CellShape cs = mapCellShapeType(mappObjects[i], mappingsList);
							p.getCellShape().add(cs);
							break;

							/*Arc*/
					case 18:
							Arc arc = mapArcType(mappObjects[i], mappingsList);
							p.getArc().add(arc);
							break;

							/*Ribosome*/
					case 19:
							/*OrganA*/
					case 20:
							/*OrganB*/
					case 21:
							/*OrganC*/
					case 22:
							CellComponentShape ccs = mapCellComponentShapeType(mappObjects[i], mappingsList);
							p.getCellComponentShape().add(ccs);
							break;

							/*ProteinB*/
					case 23:
							ProteinComplexShape pcs = mapProteinComplexShapeType(mappObjects[i], mappingsList);
							p.getProteinComplexShape().add(pcs);
							break;

// FIXME:  Huh?

			//JOptionPane.showMessageDialog(null, "SystemCode
			//'"+mappObjects[i][mappingsList.indexOf("SystemCode")]+"' is not
			//recognised as a GenMAPP type and is therefore not processed");
					case -1: 
							System.out.println( 
								"-> SystemCode '" 
									+ mappObjects[i][mappingsList.indexOf( "SystemCode")]
									+ "' is not recognised as a GenMAPP type "
									+ "and is therefore not processed.\n");
							break;
			}
		}
    }
    
    
	// This method creates a List which contains all strings in the header
	// of the imported mappobjects data
    public static List createMappingsList(String[] header)
	{
		List mappingsList = new ArrayList();

		for(int i=0; i<header.length; i++)
		{
				mappingsList.add(header[i]);
		}
		return mappingsList;
    }
    
	public static Line mapLineType(String [] mappObject, List mappingsList)
	{
			Line l = null;
			String gmmlType = null;
			String style = null;

			try {
					l = o.createLine();
					String type = mappObject[mappingsList.indexOf("Type")];
					if(type.equals("Line")) {
							gmmlType = "Line";
							style = "Solid";
					} else if(type.equals("Arrow")) {
							gmmlType = "Arrow";
							style = "Solid";
					} else if(type.equals("DottedArrow")) {
							gmmlType = "Arrow";
							style = "Broken";
					} else if(type.equals("DottedLine")) {
							gmmlType = "Line";
							style = "Broken";
					}
					l.setType(gmmlType);
					l.setStyle(style);
					l.setComment(mappObject[mappingsList.indexOf("Remarks")]);
					l.setNotes(mappObject[mappingsList.indexOf("Notes")]);

					LineType.GraphicsType lg = o.createLineTypeGraphicsType();
					lg.setColor(ConvertType.decToHex(mappObject[mappingsList.indexOf("Color")]));
					lg.setStartX(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("CenterX")]));
					lg.setStartY(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("CenterY")]));
					lg.setEndX(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("SecondX")]));
					lg.setEndY(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("SecondY")]));

					l.setGraphics(lg);
					return l;

			} catch ( JAXBException ex)
			{
					System.out.println(
						"-> element 'Line' could not be converted due to "
						+ "JAXBException '" + ex.getMessage() + "\n");
			}

			return l;
	}
    
    public static Brace mapBraceType(String[] mappObject, List mappingsList)
    {
        Brace b = null;

        try
	{
            b = o.createBrace();

            b.setNotes(mappObject[mappingsList.indexOf("Notes")]);

            BraceType.GraphicsType bg = o.createBraceTypeGraphicsType();

            bg.setColor(ConvertType.decToHex(mappObject[mappingsList.indexOf("Color")]));
            bg.setCenterX(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("CenterX")]));
            bg.setCenterY(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("CenterY")]));
            bg.setPicPointOffset(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("Height")]));
            bg.setWidth(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("Width")]));

            String orientation = mappObject[mappingsList.indexOf("Rotation")];

            if(orientation.equals("0.0")) {
                orientation = "top";
            } else if(orientation.equals("1.0")) {
                orientation = "right";
            } else if(orientation.equals("2.0")) {
                orientation = "bottom";
            } else if(orientation.equals("3.0")) {
                orientation = "left";
            } else {
                System.out.println("-> orientation '"+orientation+"' of element 'Brace' is not valid\n");
            }

            bg.setOrientation(orientation);

            b.setGraphics(bg);

        }
	catch (JAXBException ex)
	{
            System.out.println("-> element 'Brace' could not be converted due to JAXBException '"+ex.getMessage()+"\n");
        }

        return b;
    }
    

    public static GeneProduct mapGeneProductType( 
							String[] mappObject, List mappingsList)
	{
		GeneProduct g = null;

		String[] systemCodes = 
			{ 
			"D", "F", "G", "I", "L", "M",
			"Q", "R", "S", "T", "U",
			"W", "Z", "X", "O"
			};

		String[] dataSources = 
			{
			"SGD", "FlyBase", "GenBank", "InterPro" ,"LocusLink", "MGI",
			"RefSeq", "RGD", "SwissProt", "GeneOntology", "UniGene",
			"WormBase", "ZFIN", "Affy", "Other"
			};

		try
		{

			g = o.createGeneProduct();

			g.setBackpageHead( mappObject[ mappingsList.indexOf( "Head")]);
			g.setComment( mappObject[ mappingsList.indexOf( "Remarks")]);


			String geneProductDataSource = 
				mappObject[ mappingsList.indexOf( "SystemCode")];

			for( int i=0; i < systemCodes.length; i++)
			{
				if( systemCodes[i].equals( geneProductDataSource))
				{
					g.setGeneProductDataSource( dataSources[i]);
					break;
				}
				else if( i == systemCodes.length - 1)
				{
					// If the datasource is not in the dataSources list,
					// just copy the string from the GenMAPP SystemCode
					g.setGeneProductDataSource( geneProductDataSource);
				}
			}

			GeneProductType.GraphicsType gg = 
				o.createGeneProductTypeGraphicsType();

			gg.setCenterX( ConvertType.stringToBigInt(
				mappObject[ mappingsList.indexOf( "CenterX")]));

			gg.setCenterY( ConvertType.stringToBigInt(
				mappObject[ mappingsList.indexOf( "CenterY")]));

			gg.setHeight( ConvertType.stringToBigInt(
				mappObject[ mappingsList.indexOf( "Height")]));

			gg.setWidth( ConvertType.stringToBigInt(
				mappObject[ mappingsList.indexOf( "Width")]));

			g.setGraphics( gg);

			// ID is mapped to Name (according to Ferrante, 2004
			g.setName( mappObject[ mappingsList.indexOf( "ID")]);
			g.setNotes( mappObject[ mappingsList.indexOf( "Notes")]);

			// Label is mapped to ShortName (according to Ferrante, 2004
			//g.setShortName( mappObject[mappingsList.indexOf("Label")]);
			// Modified for new xsd 
			g.setGeneID( mappObject[ mappingsList.indexOf( "Label")]);

			// Can also be "protein", but not all GenMAPP "genes" are proteins!
			// FIXME
			g.setType( "unknown");

			// TODO:  for some IDs the type is known, e.g. SwissProt is alway a
			// protein, incorporate this knowledge to assign a type per ID
			g.setXref( mappObject[ mappingsList.indexOf( "Links")]);
		}

		catch ( JAXBException ex)
		{
			System.out.println( "-> element 'GeneProduct' could not be "
				+ "converted due to JAXBException '" + ex.getMessage() 
				+ "\n");
		}

		return g;
	}
    

    public static LineShape mapLineShapeType(String[] mappObject, List mappingsList) {
        LineShape ls = null;
        try {
            ls = o.createLineShape();
            ls.setComment(mappObject[mappingsList.indexOf("Remarks")]);
            ls.setNotes(mappObject[mappingsList.indexOf("Notes")]);

            String[] genmappTypes = {"TBar", "Receptor", "LigandSq", "ReceptorSq", "LigandRd", "ReceptorRd"};

	    //!! "Receptor" doesn't exist in GMML, so take receptorsquare
            String[] gmmlTypes = {
	    		"Tbar",			"ReceptorSquare", 
                    	"LigandSquare",		"ReceptorSquare",
			"LigandRound",		"ReceptorRound"};

                    String type = mappObject[mappingsList.indexOf("Type")];

                    for(int i=0; i<genmappTypes.length; i++) {
                        if(genmappTypes[i].equals(type)) {
                            ls.setType(gmmlTypes[i]);
                            break;
                        } else if(i == genmappTypes.length-1) {
                            System.out.println("-> given type "+'"'+type+'"'
			    	+" is not a valid GenMAPP type for this LineShape\n");
                        }
                    }

                    LineShapeType.GraphicsType lsg = o.createLineShapeTypeGraphicsType();

                    lsg.setColor(ConvertType.decToHex(mappObject[mappingsList.indexOf("Color")]));
                    lsg.setEndX(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("SecondX")]));
                    lsg.setEndY(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("SecondY")]));
                    lsg.setStartX(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("CenterX")]));
                    lsg.setStartY(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("CenterY")]));

                    ls.setGraphics(lsg);

        } catch(JAXBException ex) {
            System.out.println("-> element 'LineShape' could not be converted due to JAXBException '"+ex.getMessage()+"\n");
        }
        return ls;
    }

   	// 
	// cleaned up and added Rotation 2005-10-19
	//
    public static Arc mapArcType( String[] mappObject, List mappingsList)
	{
		Arc a = null;
		String rot = null;

		try
		{
			a = o.createArc();

			// elements
			a.setComment( mappObject[ mappingsList.indexOf( "Remarks")]);
			a.setNotes( mappObject[ mappingsList.indexOf( "Notes")]);

			// graphics element
			ArcType.GraphicsType ag = o.createArcTypeGraphicsType();

			// with attributes
			ag.setColor( ConvertType.decToHex(
				mappObject[ mappingsList.indexOf( "Color")]));

			ag.setHeight( ConvertType.stringToBigInt(
				mappObject[ mappingsList.indexOf( "Height")]));

			ag.setStartX( ConvertType.stringToBigInt(
				mappObject[ mappingsList.indexOf( "CenterX")]));

			ag.setStartY( ConvertType.stringToBigInt(
				mappObject[ mappingsList.indexOf( "CenterY")]));

			ag.setWidth( ConvertType.stringToBigInt(
				mappObject[ mappingsList.indexOf( "Width")]));

			// see about the rotation met debugging
			rot = mappObject[ mappingsList.indexOf( "Rotation")];
			System.out.println( ">>> Arc.graphics.Rotation : " + rot +"\n");

			ag.setRotation( ConvertType.str2bd( rot));

			System.out.println( ">>> converted BigDec : " +
			ag.getRotation() +"\n");

			// setup
			a.setGraphics(ag);

		}
		catch ( JAXBException ex)
		{
			System.out.println( "-> element 'Arc' could not be "
				+ "converted due to JAXBException '"
				+ ex.getMessage() + "\n");
		}
		return a;
	}
    
    public static Label mapLabelType(String[] mappObject, List mappingsList) {
        Label la = null;
        try {
            la = o.createLabel();

            la.setComment(mappObject[mappingsList.indexOf("Remarks")]);
            la.setNotes(mappObject[mappingsList.indexOf("Notes")]);
            la.setTextLabel(mappObject[mappingsList.indexOf("Label")]);

            Label.GraphicsType lag = o.createLabelTypeGraphicsType();
            lag.setCenterX(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("CenterX")]));
            lag.setCenterY(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("CenterY")]));
            lag.setColor(ConvertType.decToHex(mappObject[mappingsList.indexOf("Color")]));
            lag.setFontName(mappObject[mappingsList.indexOf("ID")]);
            lag.setFontSize(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("SecondX")]));

            /* FontStyle and FondWeight is encoded by strange symbols in a GenMAPP mapp:
             * 0"" --> standard (bold, but other symbol)
             * 1"" --> normal
             * 2"" --> bold
             * 3"" --> italic
             * 4"" --> bold / italic
             * 5"" --> normal / underscore
             * 6"" --> bold / underscore
             * 7"" --> italic / underscore
             * 8"" --> bold / italic / underscore
             * 9"" --> normal / strikethru
             * 10"" --> bold / strikethru
             * 11"-" --> italic / strikethru
             * 12"" --> normal / underscore / strikethru
             * 13"" --> bold / underscore / strikethru
             * 14"" --> italic / underscore / strikethru
             * 15"" --> bold / italic / underscore / strikethru
             */

            String fontStyle = mappObject[mappingsList.indexOf("SystemCode")];

            String[] fontstyles = {"","","","","","","","","","","","-","","","",""};

            List fontstyleslist = new ArrayList();

            for(int i=0; i < fontstyles.length; i++)
	    {
                fontstyleslist.add( fontstyles[i]);
            }

            int index = fontstyleslist.indexOf(fontStyle);

            switch(index)
	    {
             	// 0 "" --> standard (bold, but other symbol)
                case 0 : 
			lag.setFontWeight("Bold");
			break;

             	//* 1"" --> normal
                case 1: 
			break;

             	//* 2"" --> bold
                case 2: 
			lag.setFontWeight("Bold");
			break;

             	//* 3"" --> italic
                case 3: 
			lag.setFontStyle("Italic");
			break;

             	//* 4"" --> bold / italic
                case 4: 
			lag.setFontStyle("Italic"); 
			lag.setFontWeight("Bold"); 
			break;

             	// * 5"" --> normal / underscore
                case 5: 
			lag.setFontStyle("Underscore");
			break;

             	// * 6"" --> bold / underscore
                case 6:
			lag.setFontStyle("Underscore");
			lag.setFontWeight("Bold");
			break;

		//!! GMML doesn't support both italic and underscore !!,
		//italic, underscore
             	// * 7"" --> italic / underscore
                case 7:
			lag.setFontStyle("Underscore");
			break;

		// !! GMML doesn't support both italic and underscore !!
		/*bold/italic/underscore*/
             	// * 8"" --> bold / italic / underscore
                case 8:
			lag.setFontStyle("Underscore");
			lag.setFontWeight("Bold");
			break;
		/*normal/strikethru*/
             	// * 9"" --> normal / strikethru
                case 9:
			lag.setFontStyle("Strikethru");
			break;
		/*bold/strikethru*/
             	// * 10"" --> bold / strikethru
                case 10:
			lag.setFontStyle("Strikethru");
			lag.setFontWeight("Bold");
			break;

		// !! GMML doesn't support both italic and strikethru !!
		/*italic/strikethru*/
             	// * 11"-" --> italic / strikethru
                case 11:
			lag.setFontStyle("Strikethru");
			break; 

		// !! GMML doesn't support both underscore and strikethru !!
		/*normal/underscore/strikethru*/
             	// * 12"" --> normal / underscore / strikethru
                case 12:
		lag.setFontStyle("Strikethru");
		break; 

		// !! GMML doesn't support both underscore and strikethru !!
		/*bold/underscore/strikethru*/
             	// * 13"" --> bold / underscore / strikethru
                case 13:
			lag.setFontStyle("Strikethru");
			break; 

		// !! GMML doesn't support both underscore, strikethru and italic !!
		/*italic/underscore/strikethru*/
             	//* 14"" --> italic / underscore / strikethru
                case 14:
			lag.setFontStyle("Strikethru");
			break; 

		//!! GMML doesn't support both underscore, strikethru and italic !!
		/*bold/italic/underscore/strikethru*/
             	//* 15"" --> bold / italic / underscore / strikethru
                case 15:
			lag.setFontStyle("Strikethru");
			lag.setFontWeight("Bold");
			break; 

            }

            lag.setHeight(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("Height")]));

            lag.setWidth(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("Width")]));

            la.setGraphics(lag);


        } catch (JAXBException ex) {
            System.out.println("-> element 'Label' could not be converted due to JAXBException '"+ex.getMessage()+"\n");
        }
        return la;
    }
    
    public static Shape mapShapeType(String[] mappObject, List mappingsList)
    {
        Shape s = null;

        try
	{
		s = o.createShape();

		ShapeType.GraphicsType sg = o.createShapeTypeGraphicsType();
		s.setType( mappObject[mappingsList.indexOf("Type")]);

		// the kind of shape that we have.
		String shape_type = mappObject[mappingsList.indexOf("Type")];

		s.setType( shape_type);

		// set the center for x,y
		sg.setCenterX( ConvertType.stringToBigInt( 
				mappObject[ mappingsList.indexOf( "CenterX")]));

		sg.setCenterY( ConvertType.stringToBigInt( 
				mappObject[ mappingsList.indexOf( "CenterY") ]));


		// Special Case: Color.

		// set the color to be a color, unless is has been flagged as a
		// -1 type ... possibly special in GenMAPP, and to be tracked
		// here.

		// convert it to hex by default for all other cases.
		sg.setColor( ConvertType.decToHex(
				mappObject[ mappingsList.indexOf( "Color")]));

		String o_color = mappObject[ mappingsList.indexOf( "Color")];

		StringBuffer minusone = new StringBuffer( "-1");
	
		//System.out.println( "-\tOval Color:  " + o_color + "\n");
		if( o_color.contentEquals( minusone))
		{
			// color is flagged as special -1 value, pass it directly
			// without hexadecimal translation
			sg.setColor( mappObject[ mappingsList.indexOf( "Color")]);
			//System.out.println( "-\tMapping Directly.\n");
		}


		// set the Height, Rotation and Width
		sg.setHeight( ConvertType.stringToBigInt(
				mappObject[ mappingsList.indexOf( "Height")]));

		sg.setRotation( ConvertType.stringToBigDec(
				mappObject[ mappingsList.indexOf( "Rotation")]));

		sg.setWidth( ConvertType.stringToBigInt(
				mappObject[ mappingsList.indexOf( "Width")]));

		// collect the graphics attributes.
		s.setGraphics( sg);


    	}
		catch( JAXBException ex)
		{
		    System.out.println("-> element 'Shape' could not be converted "
		    	+ "due to JAXBException '" + ex.getMessage() + "\n");
    	}

   	    return s;
    }
    
    public static CellShape mapCellShapeType( String[] mappObject, List mappingsList)
    {
        CellShape cs = null;

        try
	{
            cs = o.createCellShape();

            CellShape.GraphicsType csg = o.createCellShapeTypeGraphicsType();

            csg.setCenterX(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("CenterX")]));
            csg.setCenterY(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("CenterY")]));
            csg.setHeight(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("Height")]));
            csg.setWidth(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("Width")]));

            cs.setGraphics(csg);

            cs.setNotes(mappObject[mappingsList.indexOf("Notes")]);

        }

	catch(JAXBException ex)
	{
            System.out.println("-> element 'CellShape' could not be converted due to JAXBException '"+ex.getMessage()+"\n");
        }
        return cs;
    }
    
    public static CellComponentShape mapCellComponentShapeType( String[] mappObject, List mappingsList)
    {
        CellComponentShape ccs = null;

        try
	{
            ccs = o.createCellComponentShape();

            CellComponentShape.GraphicsType ccsg = o.createCellComponentShapeTypeGraphicsType();

            ccsg.setCenterX(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("CenterX")]));
            ccsg.setCenterY(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("CenterY")]));
            ccs.setGraphics(ccsg);

	    // I build in this construction because for some reason the
	    // generated gmml classes think "Notes" is a required field!
            String notes = mappObject[mappingsList.indexOf("Notes")];
            if(notes == null) {
                notes = "";
            }

            ccs.setNotes(notes);

            ccs.setType(mappObject[mappingsList.indexOf("Type")]);

        }
	catch(JAXBException ex)
	{
	    System.out.println("-> element 'CellComponentShape' could not "
	    	+ "be converted due to JAXBException '" + ex.getMessage());
        }

        return ccs;
    }
    
    public static ProteinComplexShape mapProteinComplexShapeType(String[] mappObject, List mappingsList) {
        ProteinComplexShape pcs = null;
        try {
            pcs = o.createProteinComplexShape();
            ProteinComplexShape.GraphicsType pcsg = o.createProteinComplexShapeTypeGraphicsType();
            pcsg.setCenterX(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("CenterX")]));
            pcsg.setCenterY(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("CenterY")]));
            pcsg.setWidth(ConvertType.stringToBigInt(mappObject[mappingsList.indexOf("Width")]));
            pcs.setGraphics(pcsg);
            pcs.setNotes(mappObject[mappingsList.indexOf("Notes")]);
        } catch(JAXBException ex) {
            System.out.println("-> element 'ProteinComplexShape' could not be converted due to JAXBException '"+ex.getMessage()+"\n");
        }
        return pcs;
    }
    
}
/*
vim:ts=4:
*/
