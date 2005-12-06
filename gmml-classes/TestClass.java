//This class is just to test the other classes
public class TestClass {
	//Main should run
	public static void main (String[] args) {
		GmmlColor kleur = new GmmlColor();
		kleur.storeColor("80FF0G");
		
		double[] color = kleur.getColor();
		
		System.out.println("Red: "+color[0]);
		System.out.println("Green: "+color[1]);
		System.out.println("Blue: "+color[2]);
	}
}