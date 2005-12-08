import java.awt.*;

public class RectangleConnecter2 {

	public static void main(String[] args) {
	ConnectionCheck connectioncheck= new ConnectionCheck();
	connectioncheck.checkconnection();
	for (int i=0 ; i<connectioncheck.connection.length ; i++) {
		System.out.println("lijn " + connectioncheck.connection[i][0]+ " verbindt blok " + connectioncheck.connection[i][1] + " met blok " + connectioncheck.connection[i][2]);
		}
	}
}

class ConnectionCheck{

	// rectangles contains the constructors of a graphics object rechtangle
	int[][] rectangles={{0,0,25,25},{30,40,15,30},{30,80,20,20},{60,60,30,15},{100,0,40,50}};
	// linepoints contains the coordinates of the lines
	int[][][] linepoints={{{13,13},{38,55}},{{13,13},{40,90}},{{38,55},{40,90}},{{40,90},{75,68}},{{120,25},{38,55}},{{75,68},{13,13}},{{120,25},{160,25}}};
	// connection will store which line connects which rectangles, a 0 means no rectangle
	int[][] connection=new int[linepoints.length][3];
	
	
	public void checkconnection(){
	/* for each point of each line the corresponding rectangle is searched
	 * these rectangles are saved in connection
	 * the first point of connection is the line index
	 * the second point is the first rectangle
	 * the third point is the second rectangle
	 */
	 
		for (int i=0; i < linepoints.length; i++){
			for (int j=0; j < rectangles.length; j++){
				connection[i][0]=i+1;
				Rectangle temprectj=new Rectangle(rectangles[j][0],rectangles[j][1],rectangles[j][2],rectangles[j][3]);
				if (temprectj.contains(linepoints[i][0][0], linepoints[i][0][1])) {
					connection[i][1]=j+1;
					}
				if (temprectj.contains(linepoints[i][1][0], linepoints[i][1][1])) {
					connection[i][2]=j+1;
					}
				}
			}
		}
}


	