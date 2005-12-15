class Layout {

/*This program optimises the 2D layout of a certain pathway (collection of blocks and linking lines), 
based on the coordinates of the blocks as stored in nx2 matrix "coord" (e.g. {{1,1},{1,2},{2,3},{3,1},{4,5}})
and the links between blocks as stored in mx2 matrix "link" (e.g. {{1,5},{5,4},{5,2},{2,3}})
NB matrix link should only contain single links; so for example, if a certain row of link is {3,2}, then
there shouldn't be a row {2,3}

This program is based on the assumption every newly declared matrix is by default filled with zeros /*

/********************************************************************************************/

/* Method to calculate spring force vector between 2 blocks */

public static double[][] calculateSpringForce(int[][] coord, int[][] link, int refL, int sC) {
   //refL: reference length; sC: spring constant
	
	//Calculating coordinate difference between linked blocks
	int[][] dCoord = new int[(link[0].length)][2];
	int nRow=0;
	for(nRow=0; nRow<(link.length); nRow++) {
		int[] blocksInvolved = {link[nRow][0],link[nRow][1]};
		int[] coordBlockOne = {coord[(blocksInvolved[0])-1][0],coord[(blocksInvolved[0])-1][1]};
		int[] coordBlockTwo = {coord[(blocksInvolved[1])-1][0],coord[(blocksInvolved[1])-1][1]};
		dCoord[nRow][0] = (coordBlockOne[0]-coordBlockTwo[0]);
		dCoord[nRow][1] = (coordBlockOne[1]-coordBlockTwo[1]);
	}
	
	//Calculating distance between linked blocks and and using this to normalise dCoord as vectors of length one. 	
	double[][] direction = new double[(link.length)][2];
	double[] distance = new double[(link.length)];
		
	for(nRow=0; nRow<(link.length); nRow++) {
		int dx=dCoord[nRow][0];
		int dy=dCoord[nRow][1];
		distance[nRow] = Math.sqrt(dx*dx+dy*dy);
		direction[nRow][0]= ( dCoord[nRow][0] )/(distance[nRow]);
		direction[nRow][1]= ( dCoord[nRow][1] )/(distance[nRow]);
	}
				
	//Calculating the magnitude of the spring force		
	double[] magnitudeSpringForce =  new double[(link.length)];
	for(nRow=0; nRow<(link.length); nRow++) {
		magnitudeSpringForce[nRow]= (((refL-distance[nRow])*(refL-distance[nRow])) /sC );
	}
	
	// Calculating spring force vector
	int firstBlockInvolved=0;
	int secondBlockInvolved=0;
	double fx=0.0;
	double fy=0.0;
				
	double[][] springForce =  new double[(coord.length)][2];
	for(nRow=0; nRow<(link.length); nRow++) {
		firstBlockInvolved=(link[nRow][0]-1);
		secondBlockInvolved=(link[nRow][1]-1);

		if (distance[nRow]<=refL) {
			//repulsion
			fx=magnitudeSpringForce[nRow]*(direction[nRow][0]);
			fy=magnitudeSpringForce[nRow]*(direction[nRow][1]);

			springForce[firstBlockInvolved][0]+=fx;
			springForce[firstBlockInvolved][1]+=fy;
			springForce[secondBlockInvolved][0]-=fx;
			springForce[secondBlockInvolved][1]-=fy;
			}
						
						
		else if (distance[nRow] > refL) {
			//attraction: opposite signs
			fx=magnitudeSpringForce[nRow]*(direction[nRow][0]);
			fy=magnitudeSpringForce[nRow]*(direction[nRow][1]);

			springForce[firstBlockInvolved][0]-=fx;
			springForce[firstBlockInvolved][1]-=fy;
			springForce[secondBlockInvolved][0]+=fx;
			springForce[secondBlockInvolved][1]+=fy;


			}
		}
	
	return springForce;
  }
  
/***********************************************************************************************/

/* Method to calculate electrical force between 2 blocks */

public static double[][] calculateElectricForce(int[][] coord, int[][] link, int alpha) {
	//alpha: electrical force constant
	
  //The number of links to a block is chosen as the charge of the block; this prevents crowding
  int[] nLinks = new int[(coord.length)];
  
  int nRow=0;
  int nBlock=0;
  
  for(nRow=0; nRow<(link.length); nRow++) {
  	for(nBlock=0; nBlock<=(coord.length); nBlock++) {	  
		if (link[nRow][0] == nBlock) {
	  		nLinks[nBlock-1]++;
			}
	  	else if (link[nRow][1] == nBlock) {
	  		nLinks[nBlock-1]++;
			}
		}
	}
	
	/*Calculating coordinate difference vectors and storing them in array dCoord */ 
	int[][][] dCoord = new int [(coord.length)][(coord.length)][2];
	
 	int nColumn=0;
 	
	for(nRow=0; nRow<(coord.length); nRow++) {
		for(nColumn=0; nColumn<(coord.length); nColumn++) {
			dCoord[nRow][nColumn][0]=(coord[nRow][0]-coord[nColumn][0]);
			dCoord[nRow][nColumn][1]=(coord[nRow][1]-coord[nColumn][1]);
			}
		}
			
	/*Calculating squared distance distanceSqeare between linked blocks and and using 
	this to normalise dCoord as vectors of length one. It is chosen to calculate the square
	of the distance and not the distance itself since the square is needed in the force
	equation */ 
	
	double[][] distanceSquare = new double [(coord.length)][(coord.length)];
	
	int dx=0;
	int dy=0;
	
	for(nRow=0; nRow<(coord.length); nRow++) {
		for(nColumn=0; nColumn<(coord.length); nColumn++) {
			dx=dCoord[nRow][nColumn][0];
			dy=dCoord[nRow][nColumn][1];
			distanceSquare[nRow][nColumn]=((dx*dx)+(dy*dy));
			}
		}
	
	//Using distanceSquare to normalise dCoord to calculate direction vectors
	double[][][] direction = new double [(coord.length)][(coord.length)][2];
	double distance = 0;
	
	for(nRow=0; nRow<(coord.length); nRow++) {
		for(nColumn=0; nColumn<(coord.length); nColumn++) {
			distance=Math.sqrt(distanceSquare[nRow][nColumn]);
			if (distance > 0) {
				direction[nRow][nColumn][0]=((dCoord[nRow][nColumn][0])/distance);
				direction[nRow][nColumn][1]=((dCoord[nRow][nColumn][1])/distance);
			}
			}
		}

	//Calculating electric force magnitude
	
	double[][] electricForceMagnitude = new double [(coord.length)][(coord.length)];
	
	for(nRow=0; nRow<(coord.length); nRow++) {
		for(nColumn=0; nColumn<(coord.length); nColumn++) {
			if (distanceSquare[nRow][nColumn] != 0) {
				//Otherwise, the force magnitude keeps default value zero 
				electricForceMagnitude[nRow][nColumn]=(nLinks[nRow]+1)*(nLinks[nColumn]+1)*(alpha/(distanceSquare[nRow][nColumn]));
				/* The force is weighed with the number of links of the corresponding blocks; 1 is added to these values to prevent
				non-linked of having zero force. */
				
				
				}
			}
		}
	
	//Calculating the net electric force on every block
	double[][]electricForce = new double [(coord.length)][2];
	double fx=0.0;
	double fy=0.0;
	
	for(nRow=0; nRow<(coord.length); nRow++) {
		for(nColumn=0; nColumn<(coord.length); nColumn++) {
		
			fx=(direction[nRow][nColumn][0])*(electricForceMagnitude[nRow][nColumn]);
			fy=(direction[nRow][nColumn][1])*(electricForceMagnitude[nRow][nColumn]);

			electricForce[nRow][0]+=fx;
			electricForce[nRow][1]+=fy;
			}
		}

	return electricForce;

	}

/***********************************************************************************************/

//Program code: tryout 
  
  public static void main(String[] args) {
    
  int[][] coord={{100,100},{200,400},{300,200}};
  int[][] link={{1,3},{1,2}};
  
  int sC = 1;
  int refL = 300;  //repulsion
  int alpha = 1;
  
  double[][] elektrischeKrachten = calculateElectricForce(coord, link, alpha);
  double[][] veerkrachten = calculateSpringForce(coord, link, refL, sC);
  
  System.out.println("veerkrachten ("+veerkrachten.length+"x"+veerkrachten[0].length+")");
  System.out.println(veerkrachten[0][0]+"  "+veerkrachten[0][1]);
  System.out.println(veerkrachten[1][0]+"  "+veerkrachten[1][1]);
  System.out.println(veerkrachten[2][0]+"  "+veerkrachten[2][1]);
  
  System.out.println();

  System.out.println("elektrische krachten ("+elektrischeKrachten.length+"x"+elektrischeKrachten[0].length+")");
  System.out.println(elektrischeKrachten[0][0]+"  "+elektrischeKrachten[0][1]);
  System.out.println(elektrischeKrachten[1][0]+"  "+elektrischeKrachten[1][1]);
  System.out.println(elektrischeKrachten[2][0]+"  "+elektrischeKrachten[2][1]);
  System.out.println();

  
  }
}	


			
	
		
	
	
					

