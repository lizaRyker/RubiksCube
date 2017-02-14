import java.io.*;

public class Main {
	public static void main(String[] args) throws FileNotFoundException{
		//System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("output.txt")),true));
		RubiksCube cube = new RubiksCube("simpleTestChars");
		cube.printRubiks();
		Solve.solveCube(cube);
	}
}
