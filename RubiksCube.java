import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class RubiksCube {
	/* Map is read from a file. Faces are mapped as follows:
	 * 			---------
	 * 			|	0	|
	 * 			|	w	|
	 * 			|	U	|
	 * --------------------------
	 * |	4	|	1	|	5	|
	 * |	r	|	b	|	o	|
	 * |	L	|	F	|	R	|
	 * --------------------------
	 * 			|	2	|
	 * 			|	y	|
	 * 			|	D	|
	 * 			---------
	 * 			|	3	|
	 * 			|	g	|
	 * 			|	B	|
	 * 			---------
	 * NOTE: the BACK face is read in backwards and upside-down to how it looks in this representation.
	 * This is because it was easier for me to think about and manipulate the back face this way,
	 * since when the cube is folded up into its 3D shape it turns out to be right-side-up and oriented
	 * with x = 0 y = 0 in the upper left just like all the other faces.
	 */
	private Face[] faces;
	
	//constructor
	public RubiksCube(String fileName) throws FileNotFoundException{
		Scanner in = new Scanner(new File(fileName));
		
		faces = new Face[6];
		for(int i = 0; i < 6; i++){
			faces[i] = new Face(in.nextLine(), in.nextLine(), in.nextLine());
		}
		
		setSideReferences();
		setFaceReferences();
		
		in.close();
	}
	
	//constructor that copies another cube
	public RubiksCube(RubiksCube otherCube){
		faces = new Face[6];
		for(int i = 0; i < 6; i++){
			this.faces[i] = new Face(otherCube.faces[i]);
		}
		setSideReferences();
		setFaceReferences();
	}
	
	//this ugly monster sets the side references for each face (the rows/columns that contain the other colors for that face's cubies)
	private void setSideReferences(){
		faces[0].setReferences(reverse(faces[3].getRow(0)), faces[1].getRow(0), faces[4].getRow(0), reverse(faces[5].getRow(0)));
		faces[1].setReferences(faces[0].getRow(2), faces[2].getRow(0), faces[4].getCol(2), faces[5].getCol(0));
		faces[2].setReferences(faces[1].getRow(2), reverse(faces[3].getRow(2)), reverse(faces[4].getRow(2)), faces[5].getRow(2));
		faces[3].setReferences(reverse(faces[0].getRow(0)), reverse(faces[2].getRow(2)), faces[5].getCol(2), faces[4].getCol(0));
		faces[4].setReferences(faces[0].getCol(0), reverse(faces[2].getCol(0)), faces[3].getCol(2), faces[1].getCol(0));
		faces[5].setReferences(reverse(faces[0].getCol(2)), faces[2].getCol(2), faces[1].getCol(2), faces[3].getCol(0));
	}
	
	//this slightly less ugly monster sets the string references for each face
	private void setFaceReferences(){
		faces[0].setFaceRef("B", "F", "L", "R");
		faces[1].setFaceRef("U", "D", "L", "R");
		faces[2].setFaceRef("F", "B", "L", "R");
		faces[3].setFaceRef("U", "D", "R", "L");
		faces[4].setFaceRef("U", "D", "B", "F");
		faces[5].setFaceRef("U", "D", "F", "B");
	}
	
	/* this function is for testing purposes only
	 * the idea is to take a solved cube and randomly perform a given number of moves on it to mix it up
	 * this results in a very simple test case
	 */
	public void mixCube(int moves){
		Random rand = new Random();
		List<Integer> methods = new ArrayList<Integer>();
		for(int i = 0; i < moves; i++){
			methods.add(rand.nextInt(12));
		}
		
		for(Integer index : methods){
			switch(index){
			case 0: rotateUpCW();
					System.out.print("U ");
					break;
			case 1: rotateUpCCW(); 
					System.out.print("U' ");
					break;
			case 2: rotateFrontCW(); 
					System.out.print("F ");
					break;
			case 3: rotateFrontCCW(); 
					System.out.print("F' ");
					break;
			case 4: rotateDownCW(); 
					System.out.print("D ");
					break;
			case 5: rotateDownCCW(); 
					System.out.print("D' ");
					break;
			case 6: rotateBackCW(); 
					System.out.print("B ");
					break;
			case 7: rotateBackCCW(); 
					System.out.print("B' ");
					break;
			case 8: rotateLeftCW(); 
					System.out.print("L ");
					break;
			case 9: rotateLeftCCW(); 
					System.out.print("L' ");
					break;
			case 10: rotateRightCW(); 
					System.out.print("R ");
					break;
			case 11: rotateRightCCW(); 
					System.out.print("R' ");
					break;
			}
		}
		System.out.println();
	}
	
	//returns individual face by index
	public Face getFace(int index){
		return faces[index];
	}
	
	//returns face by short single letter string name
	public Face getFace(String face){
		if(face.equals("U")){
			return faces[0];
		} else if(face.equals("F")){
			return faces[1];
		} else if(face.equals("D")){
			return faces[2];
		} else if(face.equals("B")){
			return faces[3];
		} else if(face.equals("L")){
			return faces[4];
		} else {
			return faces[5];
		}
	}
	
	//Search cube for a specific edge piece
	public Vector<Integer> searchEdge(char color1, char color2){
		Vector<Integer> foundAt = new Vector<Integer>();
		for(int i = 0; i < 6; i++){
			//System.out.println("Searching on face " + i);
			foundAt = faces[i].searchEdge(color1, color2);
			if(!foundAt.isEmpty()){
				foundAt.add(i);
				//System.out.println(foundAt);
				return foundAt;
			}
		}
		System.out.println("Couldn't find cubie. Vector is empty.");
		return foundAt;
	}
	
	//Search the cube for a specific corner piece
	public Vector<Integer> searchCorner(char color1, char color2, char color3){
		Vector<Integer> foundAt = new Vector<Integer>();
		for(int i = 0; i < 6; i++){
			foundAt = faces[i].searchCorner(color1, color2, color3);
			if(!foundAt.isEmpty()){
				foundAt.add(i);
				return foundAt;
			}
		}
		System.out.println("Couldn't find cubie. Vector is empty.");
		return foundAt;
	}
	
	//checks a single corner for correctness
	//function assumes that this piece is in fact a corner piece
	public boolean cornerCorrect(int x, int y, int face){
		char mainColor = faces[face].getChar(x, y);
		char mainCenter = faces[face].getChar(1, 1);
		char color1 = faces[face].getOtherCornerChar1(x, y);
		char color2 = faces[face].getOtherCornerChar2(x, y);
		char center1;
		char center2;
		
		//get the top or bottom center color
		if(x == 0){
			center1 = getFace(faces[face].getTopFace()).getChar(1, 1);
		} else {
			center1 = getFace(faces[face].getBottomFace()).getChar(1, 1);
		}
		
		//get the left or right center color
		if(y == 0){
			center2 = getFace(faces[face].getLeftFace()).getChar(1, 1);
		} else {
			center2 = getFace(faces[face].getRightFace()).getChar(1, 1);
		}
		
		if(color1 != center1 || color2 != center2 || mainColor != mainCenter){
			return false; //if the side colors don't match their center colors, return false
		} else {
			return true;
		}
	}
	
	//checks a single edge piece for correctness
	//function assumes that this piece is in face an edge piece
	public boolean edgeCorrect(int x, int y, int face){
		char mainColor = faces[face].getChar(x, y);
		char otherColor = faces[face].getOtherEdgeChar(x, y);
		char mainCenter = faces[face].getChar(1, 1);
		char otherCenter;
		//other center could be any of the surrounding faces
		if(x == 0 && y == 1) {
			otherCenter = getFace(faces[face].getTopFace()).getChar(1, 1);
		} else if(x == 1 && y == 0){
			otherCenter = getFace(faces[face].getLeftFace()).getChar(1, 1);
		} else if(x == 1 && y == 2){
			otherCenter = getFace(faces[face].getRightFace()).getChar(1, 1);
		} else {
			otherCenter = getFace(faces[face].getBottomFace()).getChar(1, 1);
		}
		//if the colors don't match the centers then return false
		if(mainColor != mainCenter || otherColor != otherCenter){
			return false;
		} else { //otherwise they must be correct--return true
			return true;
		}
	}
	
	//helper function to reverse the order of an input array
	private char[] reverse(char[] input){
		char[] result = new char[input.length];
		for(int i = 0, j = input.length - 1; i < input.length; i++, j--){
			result[i] = input[j];
		}
		return result;
	}
	
	//rotation function for whole cube counterclockwise
	//re-sets side references and face references for all sides
	public void rotateCubeCCW(){
		RubiksCube temp = new RubiksCube(this);
		//rotate top and bottom faces
		faces[0].rotate90CCW();
		faces[2].rotate90CW();
		
		//rotate side faces counterclockwise
		faces[1] = temp.getFace(4);
		faces[5] = temp.getFace(1);
		faces[3] = temp.getFace(5);
		faces[4] = temp.getFace(3);
		
		//re-set side and face references
		setFaceReferences();
		setSideReferences();
		//System.out.println("Rotated Cube: ");
		//printRubiks();
	}
	
	public void rotateCubeUpsideDown(){
		RubiksCube temp = new RubiksCube(this);
		//flip all the faces upside-down
		for(int i = 0; i < 6; i++){
			temp.getFace(i).rotate90CW();
			temp.getFace(i).rotate90CW();
		}
		
		//swap top and bottom faces
		faces[0] = temp.getFace(2);
		faces[2] = temp.getFace(0);
		
		//swap left and right faces
		faces[4] = temp.getFace(5);
		faces[5] = temp.getFace(4);
		
		//re-set side and face references
		setFaceReferences();
		setSideReferences();
	}
	
	//rotation functions: rotates a given face clockwise or counterclockwise
	//rotates row/column faces of affected cubies
	//re-sets the side references for each side
	
	//master rotate function: takes a string and rotates appropriate face
	public void rotate(String face){
		if(face.equals("U")){
			rotateUpCW();
		} else if(face.equals("U'")){
			rotateUpCCW();
		} else if(face.equals("F")){
			rotateFrontCW();
		} else if(face.equals("F'")){
			rotateFrontCCW();
		} else if(face.equals("D")){
			rotateDownCW();
		} else if(face.equals("D'")){
			rotateDownCCW();
		} else if(face.equals("B")){
			rotateBackCW();
		} else if(face.equals("B'")){
			rotateBackCCW();
		} else if(face.equals("L")){
			rotateLeftCW();
		} else if(face.equals("L'")){
			rotateLeftCCW();
		} else if(face.equals("R")){
			rotateRightCW();
		} else if(face.equals("R'")){
			rotateRightCCW();
		}
	}
	//master inverse function: takes a string and does the inverse rotation of that face
	public void rotateInverse(String face){
		if(face.equals("U")){
			rotateUpCCW();
		} else if(face.equals("U'")){
			rotateUpCW();
		} else if(face.equals("F")){
			rotateFrontCCW();
		} else if(face.equals("F'")){
			rotateFrontCW();
		} else if(face.equals("D")){
			rotateDownCCW();
		} else if(face.equals("D'")){
			rotateDownCW();
		} else if(face.equals("B")){
			rotateBackCCW();
		} else if(face.equals("B'")){
			rotateBackCW();
		} else if(face.equals("L")){
			rotateLeftCCW();
		} else if(face.equals("L'")){
			rotateLeftCW();
		} else if(face.equals("R")){
			rotateRightCCW();
		} else if(face.equals("R'")){
			rotateRightCW();
		}
	}
	//top face clockwise
	public void rotateUpCW(){
		faces[0].rotate90CW();
		char[] blue = faces[1].getRow(0);
		char[] red = faces[4].getRow(0);
		char[] green = faces[3].getRow(0);
		char[] orange = faces[5].getRow(0);
		
		faces[4].setRow(blue, 0);
		faces[3].setRow(red, 0);
		faces[5].setRow(green, 0);
		faces[1].setRow(orange, 0);
		
		setSideReferences();
	}
	
	//top face counterclockwise
	public void rotateUpCCW(){
		faces[0].rotate90CCW();
		char[] blue = faces[1].getRow(0);
		char[] red = faces[4].getRow(0);
		char[] green = faces[3].getRow(0);
		char[] orange = faces[5].getRow(0);
		
		faces[4].setRow(green, 0);
		faces[3].setRow(orange, 0);
		faces[5].setRow(blue, 0);
		faces[1].setRow(red, 0);
		
		setSideReferences();
	}
	
	//front face clockwise
	public void rotateFrontCW(){
		faces[1].rotate90CW();
		char[] white = faces[0].getRow(2);
		char[] orange = faces[5].getCol(0);
		char[] yellow = faces[2].getRow(0);
		char[] red = faces[4].getCol(2);
		
		faces[0].setRow(reverse(red), 2);
		faces[5].setCol(white, 0);
		faces[2].setRow(reverse(orange), 0);
		faces[4].setCol(yellow, 2);
	
		setSideReferences();
	}
	
	//front face counterclockwise
	public void rotateFrontCCW(){
		faces[1].rotate90CCW();
		char[] white = faces[0].getRow(2);
		char[] orange = faces[5].getCol(0);
		char[] yellow = faces[2].getRow(0);
		char[] red = faces[4].getCol(2);
		
		faces[0].setRow(orange, 2);
		faces[5].setCol(reverse(yellow), 0);
		faces[2].setRow(red, 0);
		faces[4].setCol(reverse(white), 2);
	
		setSideReferences();
	}
	
	//bottom face clockwise
	public void rotateDownCW(){
		faces[2].rotate90CW();
		char[] blue = faces[1].getRow(2);
		char[] orange = faces[5].getRow(2);
		char[] green = faces[3].getRow(2);
		char[] red = faces[4].getRow(2);
		
		faces[1].setRow(red, 2);
		faces[5].setRow(blue, 2);
		faces[3].setRow(orange, 2);
		faces[4].setRow(green, 2);
	
		setSideReferences();
	}
	
	//bottom face counterclockwise
	public void rotateDownCCW(){
		faces[2].rotate90CCW();
		char[] blue = faces[1].getRow(2);
		char[] orange = faces[5].getRow(2);
		char[] green = faces[3].getRow(2);
		char[] red = faces[4].getRow(2);
		
		faces[1].setRow(orange, 2);
		faces[5].setRow(green, 2);
		faces[3].setRow(red, 2);
		faces[4].setRow(blue, 2);
	
		setSideReferences();
	}
	
	//back face clockwise
	public void rotateBackCW(){
		faces[3].rotate90CW();
		char[] white = faces[0].getRow(0);
		char[] red = faces[4].getCol(0);
		char[] yellow = faces[2].getRow(2);
		char[] orange = faces[5].getCol(2);
		
		faces[0].setRow(orange, 0);
		faces[4].setCol(reverse(white), 0);
		faces[2].setRow(red, 2);
		faces[5].setCol(reverse(yellow), 2);
		
		setSideReferences();
	}
	
	//back face counterclockwise
	public void rotateBackCCW(){
		faces[3].rotate90CCW();
		char[] white = faces[0].getRow(0);
		char[] red = faces[4].getCol(0);
		char[] yellow = faces[2].getRow(2);
		char[] orange = faces[5].getCol(2);
		
		faces[0].setRow(reverse(red), 0);
		faces[4].setCol(yellow, 0);
		faces[2].setRow(reverse(orange), 2);
		faces[5].setCol(white, 2);
		
		setSideReferences();
	}
	
	//left face clockwise
	public void rotateLeftCW(){
		faces[4].rotate90CW();
		char[] white = faces[0].getCol(0);
		char[] blue = faces[1].getCol(0);
		char[] yellow = faces[2].getCol(0);
		char[] green = faces[3].getCol(2);
		
		faces[0].setCol(reverse(green), 0);
		faces[1].setCol(white, 0);
		faces[2].setCol(blue, 0);
		faces[3].setCol(reverse(yellow), 2);
	
		setSideReferences();
	}

	//left face counterclockwise
	public void rotateLeftCCW(){
		faces[4].rotate90CCW();
		char[] white = faces[0].getCol(0);
		char[] blue = faces[1].getCol(0);
		char[] yellow = faces[2].getCol(0);
		char[] green = faces[3].getCol(2);
		
		faces[0].setCol(blue, 0);
		faces[1].setCol(yellow, 0);
		faces[2].setCol(reverse(green), 0);
		faces[3].setCol(reverse(white), 2);
	
		setSideReferences();
	}
	
	//right face clockwise
	public void rotateRightCW(){
		faces[5].rotate90CW();
		char[] white = faces[0].getCol(2);
		char[] green = faces[3].getCol(0);
		char[] yellow = faces[2].getCol(2);
		char[] blue = faces[1].getCol(2);
		
		faces[0].setCol(blue, 2);
		faces[3].setCol(reverse(white), 0);
		faces[2].setCol(reverse(green), 2);
		faces[1].setCol(yellow, 2);
	
		setSideReferences();
	}
	
	//right face counterclockwise
	public void rotateRightCCW(){
		faces[5].rotate90CCW();
		char[] white = faces[0].getCol(2);
		char[] green = faces[3].getCol(0);
		char[] yellow = faces[2].getCol(2);
		char[] blue = faces[1].getCol(2);
		
		faces[0].setCol(reverse(green), 2);
		faces[3].setCol(reverse(yellow), 0);
		faces[2].setCol(blue, 2);
		faces[1].setCol(white, 2);
	
		setSideReferences();
	}
	
	//Print the current status of the cube
	public void printRubiks(){
		
		System.out.println("\t Up");
		for(int i = 0; i < 3; i++){
			System.out.print("\t");
			for(int j = 0; j < 3; j++){
				System.out.print(faces[0].getChar(i, j) + " ");
			}
			System.out.println();
		}
		
		System.out.println(" Left \tFront \tRight");
		for(int i = 0; i < 3; i++){
			System.out.print(" ");
			for(int j = 0; j < 3; j++){
				System.out.print(faces[4].getChar(i, j) + " ");
			}
			System.out.print(" ");
			for(int k = 0; k < 3; k++){
				System.out.print(faces[1].getChar(i, k) + " ");
			}
			System.out.print(" ");
			for(int l = 0; l < 3; l++){
				System.out.print(faces[5].getChar(i, l) + " ");
			}
			System.out.println();
		}
		
		System.out.println("\tDown");
		for(int i = 0; i < 3; i++){
			System.out.print("\t");
			for(int j = 0; j < 3; j++){
				System.out.print(faces[2].getChar(i, j) + " ");
			}
			System.out.println();
		}
		
		System.out.println("\tBack");
		for(int i = 0; i < 3; i++){
			System.out.print("\t");
			for(int j = 0; j < 3; j++){
				System.out.print(faces[3].getChar(i, j) + " ");
			}
			System.out.println();
		}
	}
	
	//used for generating valid cube configurations to export to a file
	//then read in and solve
	public void outputCube(){
		for(int face = 0; face < 6; face++){
			for(int x = 0; x < 3; x++){
				for(int y = 0; y < 3; y++){
					System.out.print(faces[face].getChar(x, y));
				}
				System.out.println();
			}
		}
	}
}
