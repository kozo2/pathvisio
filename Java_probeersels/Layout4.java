class Layout4 {

/* This program optimises the 2D layout of a certain pathway (collection of blocks and linking lines), 
based on the coordinates of the blocks as stored in nx2 matrix "coord" (e.g. {{113,100},{124,232},{22,3},{301,122},{42,51}})
and the links between blocks as stored in mx2 matrix "link" (e.g. {{1,5},{5,4},{5,2},{2,3}})
NB matrix link should only contain single links; so for example, if a certain row of link is {3,2}, then
there shouldn't be a row {2,3}
NB2 Block one is defined to have number one; i.e. link cannot contain zeros

Declared methods: 

calculateSpringForce(int[][] coord, int[][] link, int refL, int sC)
Calculates the net force vector for every block caused by the forces exerted by the interconnecting springs.
Returns double[][] springForce.
refL: reference length of the interconnecting springs
sC: spring constant of the interconnecting springs
Equation used: ((refL-distance)^2)/sC

calculateElectricForce(int[][] coord, int[][] link, int alpha)
Calculates the net force vector for every block caused by electrical repulsion forces; all blocks are 
considered to have a charge of the same sign. The magnitude of the charge of a certain block is 1+nLinks; 
nLinks is the number of blocks this certain block is attached to. Blocks with a lot of connections thus
have more space by bigger repulsion.
int alpha: repulsion constant
Equation used: (nLinks[first block]+1)*(nLinks[second block]+1)*(alpha/(distance squared)

calculateDisplacement(double[][] forces, int displacementStepSize, int numFixedBlock)
Displacements of the blocks are taken in the direction of the force vectors on the blocks. The magnitude
of a displacement is in order of magnitude displacementStepSize.
Block numFixedBlock is kept in it's initial position!

This program is based on the assumption every newly declared matrix is by default filled with zeros /*

/********************************************************************************************/

/* Method to calculate spring force vector between 2 blocks */

public static double[][] calculateSpringForce(int[][] coord, int[][] link, int refL, int sC) {
   //refL: reference length; sC: spring constant
	
	//Calculating coordinate difference between linked blocks	
	int[][] dCoord = new int[(link.length)][2];
	for(int nRow=0; nRow<(link.length); nRow++) {
		int[] blocksInvolved = {link[nRow][0],link[nRow][1]};
		int[] coordBlockOne = {coord[(blocksInvolved[0])-1][0],coord[(blocksInvolved[0])-1][1]};
		int[] coordBlockTwo = {coord[(blocksInvolved[1])-1][0],coord[(blocksInvolved[1])-1][1]};
		
		dCoord[nRow][0] = (coordBlockOne[0]-coordBlockTwo[0]);
		dCoord[nRow][1] = (coordBlockOne[1]-coordBlockTwo[1]);
		}
	
	//Calculating distance between linked blocks and using this to normalise dCoord as vectors of length one;
	//multiplying these later by the force magnitude gives the force vectors.	
	double[][] direction = new double[(link.length)][2];
	double[] distance = new double[(link.length)];
		
	for(int nRow=0; nRow<(link.length); nRow++) {
		int dx=dCoord[nRow][0];
		int dy=dCoord[nRow][1];
		distance[nRow] = Math.sqrt(dx*dx+dy*dy);
		direction[nRow][0]= ( dCoord[nRow][0] )/(distance[nRow]);
		direction[nRow][1]= ( dCoord[nRow][1] )/(distance[nRow]);
	}
				
	//Calculating spring force vector, using double[][] magnitude and double[][] direction
	double[] magnitudeSpringForce =  new double[(link.length)];
	for(int nRow=0; nRow<(link.length); nRow++) {
		magnitudeSpringForce[nRow]= (((refL-distance[nRow])*(refL-distance[nRow])) /sC );
	}
	
	// Calculating spring force vector
	int firstBlockInvolved=0;
	int secondBlockInvolved=0;
	double fx=0.0;
	double fy=0.0;
				
	double[][] springForce =  new double[(coord.length)][2];
	for(int nRow=0; nRow<(link.length); nRow++) {
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
	//by giving blocks with a lot of links more space (by bigger repulsion)
	int[] nLinks = new int[(coord.length)];
    
	for(int nRow=0; nRow<(link.length); nRow++) {
  		for(int nBlock=0; nBlock<=(coord.length); nBlock++) {	  
			if (link[nRow][0] == nBlock) {
	  			nLinks[nBlock-1]++;
				}
	  		else if (link[nRow][1] == nBlock) {
	  			nLinks[nBlock-1]++;
				}
			}
		}
	
	//Calculating coordinate difference vectors and storing them in array dCoord
	int[][][] dCoord = new int [(coord.length)][(coord.length)][2];
	 	
	for(int nRow=0; nRow<(coord.length); nRow++) {
		for(int nColumn=0; nColumn<(coord.length); nColumn++) {
			dCoord[nRow][nColumn][0]=(coord[nRow][0]-coord[nColumn][0]);
			dCoord[nRow][nColumn][1]=(coord[nRow][1]-coord[nColumn][1]);
			}
		}
			
	/*Calculating double[][] distanceSqeare and using this to normalise dCoord 
	as vectors of length one (stored in double[][] direction; multiplying these 
	later by the force magnitude gives the force vectors.	
	
	It is chosen to calculate the square of the distance and not the distance itself 
	to prevent unnescessary rounding since the square is needed in the force equation */ 
	
	double[][] distanceSquare = new double [(coord.length)][(coord.length)];
	
	for(int nRow=0; nRow<(coord.length); nRow++) {
		for(int nColumn=0; nColumn<(coord.length); nColumn++) {
			int dx=dCoord[nRow][nColumn][0];
			int dy=dCoord[nRow][nColumn][1];
			distanceSquare[nRow][nColumn]=((dx*dx)+(dy*dy));
			}
		}
	
	double[][][] direction = new double [(coord.length)][(coord.length)][2];
	double distance = 0;
	
	for(int nRow=0; nRow<(coord.length); nRow++) {
		for(int nColumn=0; nColumn<(coord.length); nColumn++) {
			distance=Math.sqrt(distanceSquare[nRow][nColumn]);
			if (distance > 0) {
				direction[nRow][nColumn][0]=((dCoord[nRow][nColumn][0])/distance);
				direction[nRow][nColumn][1]=((dCoord[nRow][nColumn][1])/distance);
			}
		}
	}

	//Calculating electric force magnitude
	double[][] electricForceMagnitude = new double [(coord.length)][(coord.length)];
	
	for(int nRow=0; nRow<(coord.length); nRow++) {
		for(int nColumn=0; nColumn<(coord.length); nColumn++) {
			if (distanceSquare[nRow][nColumn] != 0) {
				//Otherwise, the force magnitude keeps default value zero 
				electricForceMagnitude[nRow][nColumn]=(nLinks[nRow]+1)*(nLinks[nColumn]+1)*(alpha/(distanceSquare[nRow][nColumn]));
				/* The force is weighed with the number of links of the corresponding blocks; 1 is added to these values to prevent
				non-linked blocks of having zero force. */
				}
			}
		}
	
	//Calculating the net electric force on every block
	double[][]electricForce = new double [(coord.length)][2];
	
	for(int nRow=0; nRow<(coord.length); nRow++) {
		for(int nColumn=0; nColumn<(coord.length); nColumn++) {
		
			double fx=(direction[nRow][nColumn][0])*(electricForceMagnitude[nRow][nColumn]);
			double fy=(direction[nRow][nColumn][1])*(electricForceMagnitude[nRow][nColumn]);

			electricForce[nRow][0]+=fx;
			electricForce[nRow][1]+=fy;
			}
		}

	return electricForce;
	
	}

/***********************************************************************************************/

/* Method to calculate block displacements; block numFixedBlock keeps it's initial position*/

public static int[][] calculateDisplacement(double[][] forces, int displacementStepSize, int numFixedBlock ) {
	
	// First, the forces are normalised; the smallest component of the force vectors is normalised to one
	double[][] normalisedForces= new double [forces.length][2];
	
	//Finding scaling factor sF
	double sF = forces[0][0];
		
	for (int nRow=0; nRow<(forces.length); nRow++) {
		for (int nColumn=0; nColumn<2; nColumn++) {
			if((forces[nRow][nColumn])<sF) {
				sF=(forces[nRow][nColumn]);
				}
			}
		}		
	
	for (int nRow=0; nRow<(forces.length); nRow++) {
		for (int nColumn=0; nColumn<2; nColumn++) {
			normalisedForces[nRow][nColumn]= (forces[nRow][nColumn])/sF;
			}
		}
	
	//Displacements are taken proporional to and in the direction of normalisedForces. 
	//The multiplication factor is displacementStepSize
	int[][] displacement= new int [forces.length][2];

	for (int nRow=0; nRow<(forces.length); nRow++) {
		for (int nColumn=0; nColumn<2; nColumn++) {
			displacement[nRow][nColumn]=(int)(displacementStepSize * normalisedForces[nRow][nColumn]);
			}
		}
	
	//Keeping block numFixedBlock on it's initial position
	displacement[numFixedBlock ][0]=0;
	displacement[numFixedBlock ][1]=0;
	
	return displacement;
	
	}
	
/***********************************************************************************************/

/* Method to give blocks with the same initial coordinates random different coordinates */

	public static int[][] coordinateFix(int[][] coord) {
		
	int overlappingPointCheck = 1;	
	
	while(overlappingPointCheck != 0)	{	
		//Checking if blocks have same coordinate
		
		int[] overlappingPoint=new int [coord.length];

		for(int pointChecked=0; pointChecked<(coord.length); pointChecked++) {
			for(int nRow=0; nRow<(coord.length); nRow++) {
				if ((coord[pointChecked][0]==coord[nRow][0]) && (coord[pointChecked][1]==coord[nRow][1]) && (nRow!=pointChecked)) {
				//Statement to check if point has same coordinate as reference point without being the reference point itself
					overlappingPoint[nRow]=1;
					}
				}
			}

		overlappingPointCheck = 0;
		for(int nRow=0; nRow<(coord.length); nRow++) {
			overlappingPointCheck+=overlappingPoint[nRow];
			}
			
		//System.out.println("overlappingPointCheck: "+overlappingPointCheck);	
					
		//As long as blocks overlap: random coordinate reassignment
		if(overlappingPointCheck != 0) {
			for(int nRow=0; nRow<(coord.length); nRow++) {
				if (overlappingPoint[nRow]==1) {
					//Creating number between 0 and 10
					int random=(int)(10*(Math.random()));				
					coord[nRow][0]+=random;
					random=(int)(10*(Math.random()));
					coord[nRow][1]+=random;
					}
				}
			}
		}

		return coord;
	}

	
/***********************************************************************************************/

//Program code: tryout 
public static void main(String[] args) {

	int[][] coord={{300,400},{200,200},{400,200}};
	
	int[][]newCoord = coordinateFix(coord);
	
	System.out.println("newCoord: ");
	for(int nRow=0; nRow<(newCoord.length); nRow++) {
		System.out.println(newCoord[nRow][0]+" "+newCoord[nRow][1]);
		}
	
	}
}
	  
/*  //initialising
  public static void main(String[] args) {
    
  int[][] coord={{3000,4000},{2000,2000},{4000,2000}};
  int[][] link={{1,2},{1,3}};
  
   //System.out.println("Initial coord is:");
	//System.out.println(coord[0][0]+" "+coord[0][1]);
	//System.out.println(coord[1][0]+" "+coord[1][1]);
	//System.out.println(coord[2][0]+" "+coord[2][1]);
	//System.out.println(coord[3][0]+" "+coord[3][1]);
   //System.out.println(coord[4][0]+" "+coord[4][1]);

  
  int[] x=new int[coord.length];
  int[] y=new int[coord.length];
	for (int nRow=0; nRow<(coord.length); nRow++) {
		x[nRow]=coord[nRow][0];
		y[nRow]=coord[nRow][1];
		}

	System.out.println("Initial x is:");
	//System.out.println(x[0]+" "+x[1]+" "+x[2]+" "+x[3]+" "+x[4]);
	System.out.println(x[0]+" "+x[1]+" "+x[2]);

	System.out.println("Initial y is:");
	//System.out.println(y[0]+" "+y[1]+" "+y[2]+" "+y[3]+" "+y[4]);
	System.out.println(y[0]+" "+y[1]+" "+y[2]);


  int sC = 1;			//To calculate spring forces, the following equation is used: ((refL-distance)^2)/sC
  int refL = 300;     //For this coord: attraction
  int alpha = 100;	//Equation used: (nLinks[nRow]+1)*(nLinks[nColumn]+1)*(alpha/(distanceSquare[nRow][nColumn])
  
  //Calculating forces
  double[][] electricalForces = calculateElectricForce(coord, link, alpha);
  double[][] springForces = calculateSpringForce(coord, link, refL, sC);
  
  //Combining both types of forces
  double[][] forces = new double [coord.length][2];
  int nRow=0;
  int nColumn=0;
  
  for (nRow=0; nRow<(forces.length); nRow++) {
		for (nColumn=0; nColumn<2; nColumn++) {
			forces[nRow][nColumn]=(electricalForces[nRow][nColumn])+(springForces[nRow][nColumn]);
			}
		}
			
  //Searching forceMaxInitial which can be used as a criterium to stop optimalisation
  double forceMaxInitial = forces[0][0];
	
	for (nRow=0; nRow<(forces.length); nRow++) {
		for (nColumn=0; nColumn<2; nColumn++) {
			if((forces[nRow][nColumn])>forceMaxInitial) {
				forceMaxInitial = forces[nRow][nColumn];
				}
			}
		}		
	
	//System.out.println("forceMaxInitial is: "+forceMaxInitial);
	//System.out.println();
	
	//The force maximum of the current optimalisation cycle; for the first cycle this is
	//equal to forceMaxInitial
	double forceMaxCycle = forceMaxInitial;
	
	int stepSize = 5;
	//Step size displacements
	
	//Starting optimalisation; counter i counts the number of optimalisation cycles
	int i=0;
	
	while(forceMaxCycle >(.0001*forceMaxInitial)) {
	//forces have to drop below 0,01% of their initial value to stop optimalisation
	
		//Calculating displacements
		int[][] displacement = calculateDisplacement(forces, stepSize, 1);
		//keeping block 1 in place
	
		//Updating coord
		for(nRow=0; nRow<(coord.length); nRow++) {
			for(nColumn=0; nColumn<(2); nColumn++) {
				
				//System.out.println();
				//System.out.println("displacement[0][0] and [0][1]: "+displacement[0][0]+" "+displacement[0][1]);
				//System.out.println();
				
				coord[nRow][nColumn]+=displacement[nRow][nColumn];
				}
			}
		
		//System.out.println("coord is now:");
		//System.out.println(coord[0][0]+" "+coord[0][1]);
		//System.out.println(coord[1][0]+" "+coord[1][1]);
		//System.out.println(coord[2][0]+" "+coord[2][1]);
		//System.out.println(coord[3][0]+" "+coord[3][1]);
      //System.out.println(coord[4][0]+" "+coord[4][1]);

		//Calculating new electrical and spring forces
	   electricalForces = calculateElectricForce(coord, link, alpha);
	   springForces = calculateSpringForce(coord, link, refL, sC);
	   
    	//Updating forces
		for (nRow=0; nRow<(forces.length); nRow++) {
			for (nColumn=0; nColumn<2; nColumn++) {
				forces[nRow][nColumn]=(electricalForces[nRow][nColumn])+(springForces[nRow][nColumn]);
				}
			}
		
		//Searching forceMaxCycle to determine if loop is to be executed again
  		forceMaxCycle = forces[0][0];
	
		for (nRow=0; nRow<(forces.length); nRow++) {
			for (nColumn=0; nColumn<2; nColumn++) {
				if((forces[nRow][nColumn])>forceMaxCycle) {
					forceMaxCycle=(forces[nRow][nColumn]);
					}
				}
			}	
			
			i=i+1;	
			//System.out.println("forceMaxCycle after cycle "+i+" is approximately: "+(int)forceMaxCycle);
			//System.out.println();
			
		}
  	
	System.out.println();	
	System.out.println("Number of optimalisation cycles: "+i);
	System.out.println();
		
   //System.out.println("Optimal coord is:");
	//System.out.println(coord[0][0]+" "+coord[0][1]);
	//System.out.println(coord[1][0]+" "+coord[1][1]);
	//System.out.println(coord[2][0]+" "+coord[2][1]);
	//System.out.println(coord[3][0]+" "+coord[3][1]);
   //System.out.println(coord[4][0]+" "+coord[4][1]);
	
	
	//int[] x=new int[coord.length];
	for (nRow=0; nRow<(coord.length); nRow++) {
		x[nRow]=coord[nRow][0];
		y[nRow]=coord[nRow][1];
		}
		
	System.out.println("Optimal x is:");
	//System.out.println(x[0]+" "+x[1]+" "+x[2]+" "+x[3]+" "+x[4]);
	System.out.println(x[0]+" "+x[1]+" "+x[2]);

	
	System.out.println("Optimal y is:");
	//System.out.println(y[0]+" "+y[1]+" "+y[2]+" "+y[3]+" "+y[4]);
	System.out.println(y[0]+" "+y[1]+" "+y[2]);
	System.out.println();
	
  }
}	

*/






			
	
		
	
	
					

