import java.util.Arrays;
import java.util.*;

public class Solve {
	
	private static RubiksCube cube;
	private static List<String> moves;
	
	//this method calls all the steps to solve the cube
	public static void solveCube(RubiksCube otherCube){
		cube = new RubiksCube(otherCube);
		moves = new LinkedList<String>();
		
		whiteCross();
		whiteCorners();
		middleLayer();
		yellowCross();
		yellowTopLayer();
		yellowCorners();
		yellowEdges();
		
		System.out.println(moves);
		int faceTurns = 0;
		String currentMove = "";
		
		for(int i = 0; i < moves.size(); i++){
			currentMove = moves.get(i);
			if(!currentMove.equals("CC") && !currentMove.equals("FC")){
				faceTurns++;
			}
		}
		
		System.out.println(faceTurns + " moves made");
	}
	
	private static void whiteCross(){
		System.out.println("Solving the white cross");
		//first, because of how I read in the cube I need to rotate the cube until the blue face is on the right
		rotateCube(); //this rotates the cube counterclockwise, which puts blue on the right
		phase1('b'); //solve the white/blue edge piece
		rotateCube(); //rotate the cube so red is on the right
		phase1('r'); //solve the white/red edge piece
		rotateCube(); //rotate so green is on the right
		phase1('g'); //solve the white/green edge piece
		rotateCube(); //rotate so orange is on the right
		phase1('o'); //solve the white/orange edge piece
		cube.printRubiks(); //print the cube
	}
		
	private static void phase1(char otherCubieColor){
		//find the edge piece
		Vector<Integer> foundEdge = cube.searchEdge('w', otherCubieColor);
		
		//this is where the edge piece should go
		Vector<Integer> intendedTarget = new Vector<Integer>(Arrays.asList(1,2,0));
		
		//if it isn't in the right spot, move it to the down face and rotate until it's below its intended spot
		if(!foundEdge.equals(intendedTarget)){
			//tempMoves is for remembering what face you turn so you can put it back where it was
			List<String> tempMoves = new LinkedList<String>();
			//if it's already on the top face, but in the wrong position, rotate until its on the bottom row/face
			if(foundEdge.get(2) == 0){
				//in this section I realize it would be much better for me to go back and write all the 180 degree
				//rotations, but for now this is me being lazy, which looks uglier.
				//System.out.println("On top face");
				if(foundEdge.get(0) == 2 && foundEdge.get(1) == 1){
					rotate("F");
					rotate("F");
					tempMoves.add("F");
					tempMoves.add("F");
				} else if(foundEdge.get(0) == 1 && foundEdge.get(1) == 0){
					rotate("L");
					rotate("L");
					tempMoves.add("L");
					tempMoves.add("L");
				} else if(foundEdge.get(0) == 1 && foundEdge.get(1) == 2){
					rotate("R");
					rotate("R");
					tempMoves.add("R");
					tempMoves.add("R");
				} else {
					rotate("B");
					rotate("B");
					tempMoves.add("B");
					tempMoves.add("B");
				}
			} else {
				//if it's on a side, rotate until it's on the bottom row/bottom face
				if(foundEdge.get(2) != 2){
					//System.out.println("On " + foundEdge.get(2) + " side");
					boolean onDFace = (foundEdge.get(2) == 2 || foundEdge.get(0) == 2);
					while(!onDFace){
						if(foundEdge.get(2) == 1){
							rotate("F");
							tempMoves.add("F");
						} else if(foundEdge.get(2) == 4){
							rotate("L");
							tempMoves.add("L");
						} else if(foundEdge.get(2) == 5){
							rotate("R");
							//if it's already on R, don't add the R rotation to tempMoves
						} else if(foundEdge.get(2) == 3){
							rotate("B");
							tempMoves.add("B");
						}
						foundEdge = cube.searchEdge('w', otherCubieColor);
						if(foundEdge.get(2) == 2 || foundEdge.get(0) == 2){
							onDFace = true;
							//System.out.println("On D face");
						}
					}
				}
			}
				//rotate bottom face until it's below the target position
				boolean belowTarget = (foundEdge.get(0) == 1 && foundEdge.get(1) == 2 && foundEdge.get(2) == 2) ||
						  			  (foundEdge.get(0) == 2 && foundEdge.get(1) == 1 && foundEdge.get(2) == 5);
				while(!belowTarget){
					rotate("D");
					foundEdge = cube.searchEdge('w', otherCubieColor);
					//System.out.println(foundEdge);
					belowTarget = (foundEdge.get(0) == 1 && foundEdge.get(1) == 2 && foundEdge.get(2) == 2) ||
								  (foundEdge.get(0) == 2 && foundEdge.get(1) == 1 && foundEdge.get(2) == 5);
				}
				//rotate the face that the cubie was found on back into the position it was found in
				while(!tempMoves.isEmpty()){
					rotateInverse(tempMoves.remove(0));
					//tempMoves will only have up to three elements, all turns of the same face
					//so it doesn't matter which order you reverse tempMoves because all elements are the same
				}
				//rotate right face until piece is in target position
				rotate("R");
				rotate("R");
				//if piece is oriented incorrectly, do Fi, U, Li, Ui
				foundEdge = cube.searchEdge('w', otherCubieColor);
				boolean orientationCorrect = foundEdge.get(2) == 0;
				if(!orientationCorrect){
					phase1Orientate();
				}
		}
	}
	
	
	private static void rotate(String face){
		cube.rotate(face);
		moves.add(face);
		//System.out.println(face);
		//cube.printRubiks();
	}
	
	private static void rotateInverse(String face){
		cube.rotateInverse(face);
		moves.add(face + "'");
		//System.out.println(face + "'");
		//cube.printRubiks();
	}
	
	private static void rotateCube(){
		//System.out.println("Rotate Cube");
		cube.rotateCubeCCW();
		moves.add("CC");
		//cube.printRubiks();
	}
	
	private static void phase1Orientate(){
		rotate("R'");
		rotate("U");
		rotate("F'");
		rotate("U'");
	}
	
	private static void whiteCorners(){
		System.out.println("Solving the white corners");
		//at the end of phase 1 the blue face is the F face, so we start with the blue/orange/white corner piece
		phase2('b','o');
		rotateCube(); //rotate so that red is the front
		phase2('r','b'); //solve the red/blue/white corner piece
		rotateCube(); //rotate so that green is the front
		phase2('g','r'); //solve the green/red/white corner piece
		rotateCube(); //rotate so that orange is the front
		phase2('o','g'); //solve the orange/green/white corner piece
		cube.printRubiks(); //print the cube
	}
	
	private static void phase2(char otherColor1, char otherColor2){
		//find the current location of the corner
		Vector<Integer> foundCorner = cube.searchCorner('w', otherColor1, otherColor2);
		//this is where the corner belongs
		Vector<Integer> intendedTarget = new Vector<Integer>(Arrays.asList(2,2,0));
		//these two vectors are used to check if it's in the right spot but wrong orientation
		Vector<Integer> wrongOrientation1 = new Vector<Integer>(Arrays.asList(0,2,1));
		Vector<Integer> wrongOrientation2 = new Vector<Integer>(Arrays.asList(0,0,5));
		//if the edge piece isn't where it's supposed to go
		if(!foundCorner.equals(intendedTarget)){
			//tempMoves is to store the rotations you use to get it to the D face so you can put that face back after
			List<String> tempMoves = new LinkedList<String>();
			//if it's on the top face, rotate it down to the bottom face
			if(foundCorner.get(2) == 0){
				if(foundCorner.get(0) == 0 && foundCorner.get(1) == 0){
					rotate("B");
					tempMoves.add("B");
				} else if(foundCorner.get(0) == 2 && foundCorner.get(1) == 0){
					rotate("L");
					tempMoves.add("L");
				} else if(foundCorner.get(0) == 0 && foundCorner.get(1) == 2){
					rotate("R");
					tempMoves.add("R");
				}
			//else if it's in the right spot but wrong orientation
			} else if(foundCorner.equals(wrongOrientation1) || (foundCorner.equals(wrongOrientation2))) {
				//do R' D' R
				rotate("R'");
				rotate("D'");
				rotate("R"); //no need to add to tempMoves since this already puts the R face back into place
			//else if it's on a side and one of the top two corners, rotate it down to the bottom face	
			} else if(!(foundCorner.get(2) == 2)){
				if(foundCorner.get(0) == 0 && foundCorner.get(1) == 0){
					String face = cube.getFace(foundCorner.get(2)).getLeftFace();
					rotate(face);
					tempMoves.add(face);
				} else if(foundCorner.get(0) == 0 && foundCorner.get(1) == 2){
					String face = cube.getFace(foundCorner.get(2)).getRightFace() + "'";
					rotate(face);
					tempMoves.add(face);
				}
			}
			//cubie can be rotated so that the white square is on F, D or R
			boolean belowTarget = (foundCorner.get(0) == 0 && foundCorner.get(1) == 2 && foundCorner.get(2) == 2) ||
		  			  			  (foundCorner.get(0) == 2 && foundCorner.get(1) == 2 && foundCorner.get(2) == 1) ||
		  			  			  (foundCorner.get(0) == 2 && foundCorner.get(1) == 0 && foundCorner.get(2) == 5);
			//rotate D face until cubie is below where it should be
			while(!belowTarget){
				rotate("D");
				foundCorner = cube.searchCorner('w', otherColor1, otherColor2);
				//System.out.println(foundCorner);
				belowTarget = (foundCorner.get(0) == 0 && foundCorner.get(1) == 2 && foundCorner.get(2) == 2) ||
			  			      (foundCorner.get(0) == 2 && foundCorner.get(1) == 2 && foundCorner.get(2) == 1) ||
			  			      (foundCorner.get(0) == 2 && foundCorner.get(1) == 0 && foundCorner.get(2) == 5);
			}
			//rotate the face that the cubie was found on back into the position it was found in
			while(!tempMoves.isEmpty()){
				rotateInverse(tempMoves.remove(0));
				//tempMoves should only have one element
			}
			//rotate piece into place by doing R' D' R D
			//which is also the same sequence of moves you use to re-orient the cubie
			phase2Orientate();
			
			//if piece is oriented incorrectly, do R' D' R D until it's correct
			foundCorner = cube.searchCorner('w', otherColor1, otherColor2);
			//it's oriented correctly if the white square is on the white face and its side color matches the center
			//on the face it's on - I'm only checking one side square because if that one is correct, so is the other one
			
			//debugging breakout of foundCorner for readability
			int x = foundCorner.get(0);
			int y = foundCorner.get(1);
			int face = foundCorner.get(2);
			
			boolean orientationCorrect = (face == 0) && 
				(cube.getFace(face).getOtherCornerChar1(x, y) == 
				cube.getFace(cube.getFace(face).getBottomFace()).getChar(1, 1));
			while(!orientationCorrect){
				phase2Orientate();
				foundCorner = cube.searchCorner('w', otherColor1, otherColor2);
				x = foundCorner.get(0);
				y = foundCorner.get(1);
				face = foundCorner.get(2);
				orientationCorrect = (face == 0) && 
						(cube.getFace(face).getOtherCornerChar1(x, y) == 
						cube.getFace(cube.getFace(face).getBottomFace()).getChar(1, 1));
				//System.out.print(foundCorner + " ");
				//System.out.print(face + " ");
				//System.out.print(cube.getFace(face).getOtherCornerChar1(x, y) + " ");
				//System.out.println(cube.getFace(cube.getFace(face).getBottomFace()).getChar(1, 1));
			}
		}
	}
	
	private static void phase2Orientate(){
		rotate("R'");
		rotate("D'");
		rotate("R");
		rotate("D");
	}
	
	private static void middleLayer(){
		System.out.println("Solving the middle layer");
		cube.rotateCubeUpsideDown();
		moves.add("FC");
		//until the middle layer is complete
		//should run no more than four times
		while(!middleLayerComplete()){
			verticalLine();
		}
		//print the cube
		cube.printRubiks();
	}
	
	private static boolean middleLayerComplete(){
		//check all the sides: if any side is wrong, then complete is false
		for(int i = 1; i < 6; i++){
			if(i != 2){
				char color1 = cube.getFace(i).getChar(1, 0);
				char color2 = cube.getFace(i).getChar(1, 2);
				char centerColor = cube.getFace(i).getChar(1, 1);
				
				if(color1 != centerColor || color2 != centerColor){
					return false;
				}
			}
		}
		return true;
	}
	
	private static void verticalLine(){
		//until you have a piece you can work with:
		boolean done = false;
		Vector<Integer> foundAt = new Vector<Integer>();
		int startX = 0;
		int startY = 0;
		char color1;
		char color2;
		
		while(!done){
			//find a non-yellow square on the yellow (top) face
			foundAt = cube.getFace(0).searchAntiCubie('y',startX,startY);
			//System.out.println(foundAt);
			//if you find a non-yellow cubie
			if(!foundAt.isEmpty()){
				//check if it's an edge piece and if the color on the other side is also not yellow
				if(cube.getFace(0).isEdge(foundAt.get(0), foundAt.get(1))){
					color1 = cube.getFace(0).getChar(foundAt.get(0), foundAt.get(1));
					color2 = cube.getFace(0).getOtherEdgeChar(foundAt.get(0), foundAt.get(1));
					if(color2 != 'y'){
						//if it is, find the face whose center matches the second color
						//rotate the cube until that face is at the front
						char centerColor = cube.getFace(1).getChar(1, 1);
						while(centerColor != color2){
							//System.out.println("Rotating so that " + color2 + " face is in front");
							rotateCube();
							centerColor = cube.getFace(1).getChar(1, 1);
						}
						//rotate top layer until the cubie you found creates the vertical line
						foundAt = cube.searchEdge(color1, color2);
						Vector<Integer> intendedTarget = new Vector<Integer>(Arrays.asList(2,1,0));
						while(!foundAt.equals(intendedTarget)){
							//System.out.println("Rotating U");
							rotate("U");
							foundAt = cube.searchEdge(color1, color2);
						}
						//if the top color matches the left face, call middleLeft()
						if(color1 == cube.getFace(cube.getFace(0).getLeftFace()).getChar(1, 1)){
							middleLeft();
							done = true;
						} else if(color1 == cube.getFace(cube.getFace(0).getRightFace()).getChar(1, 1)){
							//if the top color matches the right face, call middleRight()
							middleRight();
							done = true;
						}		
					}
				}
			}
			//if it isn't, increment searching until you've searched the whole top face
			startY++;
			if(startY > 3){
				startY = 0;
				startX++;
			}
			//if there's no piece on the top face that will work, search the cube for a piece in the middle layer
			if(startX > 3){
				for(int i = 1; i < 6; i++){
					if(i != 2){
						boolean correct = checkMiddleLayer(i);
						if(!correct){
							break; //break out of loop -- checkMiddleLayer() has moved a piece to the top
								   //no need to continue searching the middle layer
						}
					}
				}	
				startY = 0;
				startX = 0;
			}
		//search again
		}			
	}
	
	private static void middleLeft(){
		rotate("U'");
		rotate("L'");
		rotate("U");
		rotate("L");
		rotate("U");
		rotate("F");
		rotate("U'");
		rotate("F'");
	}
	
	private static void middleRight(){
		rotate("U");
		rotate("R");
		rotate("U'");
		rotate("R'");
		rotate("U'");
		rotate("F'");
		rotate("U");
		rotate("F");
	}
	
	private static boolean checkMiddleLayer(int face){
		boolean correct = true;
		for(int j = 0; j < 2; j+=2){ //for loop to check both the right and left cubies
			Vector<Integer> foundAt = new Vector<Integer>(Arrays.asList(1,2,face));
			char color1 = cube.getFace(face).getChar(1, j);
			char color2 = cube.getFace(face).getOtherEdgeChar(1, j);

			correct = (color1 == cube.getFace(face).getChar(1, 1)); //compare this cubie's main face with the center of this face
			if(j == 0){ //if the cubie is on the left, compare its other color to the center of the left face
				correct &= (color2 == cube.getFace(cube.getFace(face).getLeftFace()).getChar(1, 1));
			} else if(j == 2){ //if the cubie is on the right, compare its other color to the center of the right face
				correct &= (color2 == cube.getFace(cube.getFace(face).getRightFace()).getChar(1, 1));
			}
			//if the piece you found is not correct, rotate the cube so it's on the front face
			if(!correct){
				while(color1 != cube.getFace(1).getChar(1, 1)){
					//System.out.println("No piece on top found: rotating cube so that the " + color1 + " face is in front");
					rotateCube();
				}
				//move it to the top layer
				foundAt = cube.searchEdge(color1, color2);
				if(foundAt.get(1) == 0){
					middleRight();
				} else if(foundAt.get(1) == 2){
					middleLeft();
				}
				//get out of this loop
				return correct;
			}
		}
		return correct;
	}
	
	private static void yellowCross(){
		System.out.println("Solving the yellow cross");
		
		//until you have the yellow cross
		while(!crossCheck()){
			//if you have the yellow line
			if(lineCheck()){
				//orient the cube
				orientLine();
				//do the yellow line rotations
				stage5Line();
			} else if(lCheck()){
				//if you have the yellow L
				//orient the cube
				orientCornerL();
				//do the yellow L rotations
				stage5L();
			} else {
				//otherwise you only have the yellow dot
				//so do the yellow dot rotations
				stage5Dot();
			}
		}
		
		cube.printRubiks();
	}
	
	//checks the cube to see if the yellow cross has been achieved
	//should be checked first
	private static boolean crossCheck(){
		boolean cross = (cube.getFace(0).getChar(0, 1) == 'y') &&
					    (cube.getFace(0).getChar(1, 0) == 'y') &&
					    (cube.getFace(0).getChar(1, 2) == 'y') &&
					    (cube.getFace(0).getChar(2, 1) == 'y');
		
		return cross;		
	}
	
	//checks if the cube has the yellow line
	//must be run after the cross check or you may get the wrong result
	//line may be vertical or horizontal
	private static boolean lineCheck(){
		boolean line = (cube.getFace(0).getChar(0, 1) == 'y' && cube.getFace(0).getChar(2, 1) == 'y') ||
					   (cube.getFace(0).getChar(1, 0) == 'y' && cube.getFace(0).getChar(1, 2) == 'y');
		
		return line;
	}
	
	//checks if the cube has the yellow corner L
	//must be run after the cross check or you may get the wrong result
	//L may be oriented on any of the four top corners
	private static boolean lCheck(){
		boolean cornerL = (cube.getFace(0).getChar(1, 0) == 'y' && cube.getFace(0).getChar(0, 1) == 'y') ||
						  (cube.getFace(0).getChar(0, 1) == 'y' && cube.getFace(0).getChar(1, 2) == 'y') ||
						  (cube.getFace(0).getChar(1, 2) == 'y' && cube.getFace(0).getChar(2, 1) == 'y') ||
						  (cube.getFace(0).getChar(2, 1) == 'y' && cube.getFace(0).getChar(1, 0) == 'y');
		
		return cornerL;
	}
	
	//to be run after verifying that the cube has the yellow L on the top
	//orients the cube so that the L is in the top left corner
	private static void orientCornerL(){
		while(!lTopLeft()){
			rotateCube();
		}
	}
	
	//checks if the L is in the top left corner
	private static boolean lTopLeft(){
		boolean inTopLeft = (cube.getFace(0).getChar(1, 0) == 'y' && cube.getFace(0).getChar(0, 1) == 'y');
		return inTopLeft;
	}
	
	//to be run after verifying that the cube has the yellow line on the top
	//orients the cube so that the line is horizontal
	private static void orientLine(){
		if(!horizontalLine()){
			rotateCube(); //only need to rotate once because the line can only be horizontal or vertical
		}
	}
	
	//checks if the line is horizontal
	private static boolean horizontalLine(){
		boolean horizontal = (cube.getFace(0).getChar(1, 0) == 'y' && cube.getFace(0).getChar(1, 2) == 'y');
		return horizontal;
	}
	
	//if the cube is in the dot state, do these rotations
	private static void stage5Dot(){
		rotate("F");
		rotate("U");
		rotate("R");
		rotate("U'");
		rotate("R'");
		rotate("F'");
	}
	
	//if the cube is in the L state, do these rotations
	//this function assumes the cube has already been checked and oriented
	private static void stage5L(){
		//since this requires the same rotations as the stage5Dot function
		//just call that function
		stage5Dot();
	}
	
	//if the cube is in the line state, do these rotations
	//this function assumes the cube has already been checked and oriented
	private static void stage5Line(){
		rotate("F");
		rotate("R");
		rotate("U");
		rotate("R'");
		rotate("U'");
		rotate("F'");
	}

	private static void yellowTopLayer(){
		System.out.println("Getting all yellow on top");
		//while the top is not all yellow
		while(!cube.getFace(0).isSolved()){
			//if two corners are yellow
			if(twoCorners()){
				//orient for the two corner state
				orientTwoCorners();
			} else if(oneCorner()){
				//else if only one corner is yellow
				//orient for the one corner state
				orientOneCorner();
			} else {
				//otherwise no corners are yellow
				//orient for the no corners state
				orientNoCorners();
			}
			//do the rotations
			rotateYellowCorners();
		}
		
		cube.printRubiks();
	}
	
	//should be checked first before checking for only one corner
	private static boolean twoCorners(){
		int numCorners = 0; //for counting the number of yellow corners
		for(int i = 0; i <= 2; i += 2){
			for(int j = 0; j <= 2; j += 2){
				if(cube.getFace(0).getChar(i, j) == 'y'){
					numCorners++; //if the corner is yellow, increment numCorners
				}
			}
		}
		
		if(numCorners >= 2){ //if there's two or more yellow corners
			return true; //return true
		} else {
			return false; //otherwise return false
		}
	}
	
	//should be checked after checking for two yellow corners
	private static boolean oneCorner(){
		//loop through all four corners
		for(int i = 0; i <= 2; i += 2){
			for(int j = 0; j <= 2; j += 2){
				if(cube.getFace(0).getChar(i, j) == 'y'){
					return true; //if you find a yellow corner return true
				}
			}
		}
		return false; //otherwise there isn't one, return false
	}
	
	//assumes cube has already been matched to the two-corners state
	//rotates the cube until a yellow square appears in the top left square of the front face
	private static void orientTwoCorners(){
		while(cube.getFace(1).getChar(0, 0) != 'y'){
			rotateCube();
		}
	}
	
	//assumes cube has already been matched to the one-corner state
	//rotates the cube until a yellow square appears on the bottom left square on the top face
	private static void orientOneCorner(){
		while(cube.getFace(0).getChar(2, 0) != 'y'){
			rotateCube();
		}
	}
	
	//assumes cube has already been matched to the no-corners state
	//rotates the cube until a yellow square appears on the top right corner of the left face
	private static void orientNoCorners(){
		while(cube.getFace(4).getChar(0, 2) != 'y'){
			rotateCube();
		}
	}
	
	//rotations for getting all the corners yellow
	private static void rotateYellowCorners(){
		rotate("R");
		rotate("U");
		rotate("R'");
		rotate("U");
		rotate("R");
		rotate("U");
		rotate("U");
		rotate("R'");
	}
	
	private static void yellowCorners(){
		System.out.println("Solving the yellow corners");
		//until all corners are correct
		while(!allCornersCorrect()){
			//twist U face until two corners are in the right location
			orientTwoRightCorners();
			//if two adjacent corners are correct
			if(abCornersCorrect()){
				//rotate the cube so that the AB corners are at the back
				orientCube();
			}
			//do the rotations for this step
			rotateCorners();
			//if the two corners that were correct were diagonal from each other
			//this loop will run again
		}
		cube.printRubiks();
	}
	
	//checks all four corners for correctness
	private static boolean allCornersCorrect(){
		//loop through all four corners
		for(int x = 0; x <= 2; x += 2){
			for(int y = 0; y <= 2; y += 2){
				if(!cube.cornerCorrect(x, y, 0)){
					return false;
				}
			}
		}
		//otherwise you've checked them all and they're all correct, return true
		return true;
	}
	
	//checks to see if two diagonal corners are correct
	private static boolean diagonalCornersCorrect(){
		boolean diagonals = (cube.cornerCorrect(0, 0, 0) && cube.cornerCorrect(2, 2, 0)) ||
						    (cube.cornerCorrect(2, 0, 0) && cube.cornerCorrect(0, 2, 0));
		
		return diagonals;
	}
	
	//checks to see if two adjacent corners are correct
	private static boolean abCornersCorrect(){
		boolean ab = (cube.cornerCorrect(0, 0, 0) && cube.cornerCorrect(0, 2, 0)) ||
					 (cube.cornerCorrect(0, 2, 0) && cube.cornerCorrect(2, 2, 0)) ||
					 (cube.cornerCorrect(2, 0, 0) && cube.cornerCorrect(2, 2, 0)) ||
					 (cube.cornerCorrect(0, 0, 0) && cube.cornerCorrect(2, 0, 0));
		
		return ab;
	}
	
	//turns the U face until two corners are correct
	private static void orientTwoRightCorners(){
		while(!(abCornersCorrect() || diagonalCornersCorrect())){
			rotate("U");
		}
	}
	
	//rotates the cube until the A and B correct corners are at the back
	//assumes that A and B are already checked/correct
	private static void orientCube(){
		while(!(cube.cornerCorrect(0, 0, 0) && cube.cornerCorrect(0, 2, 0))){
			rotateCube();
		}
	}
	
	//stage 6 corner rotations
	private static void rotateCorners(){
		rotate("R'");
		rotate("F");
		rotate("R'");
		rotate("B");
		rotate("B");
		rotate("R");
		rotate("F'");
		rotate("R'");
		rotate("B");
		rotate("B");
		rotate("R");
		rotate("R");
		rotate("U'");
	}
	
	private static void yellowEdges(){
		System.out.println("Solving the yellow edges");
		//until all edges are correct
		while(!allEdgesCorrect()){
			//if there's one edge correct
			if(oneEdgeCorrect()){
				//orient the cube so that that edge is at the back
				orientOneEdge();
				//determine which direction you need to rotate the remaining edges
				//and rotate them
				char middleColor = cube.getFace(0).getOtherEdgeChar(2, 1);
				char leftCenter = cube.getFace(cube.getFace(0).getLeftFace()).getChar(1, 1);
				char rightCenter = cube.getFace(cube.getFace(0).getRightFace()).getChar(1, 1);
				
				if(middleColor == leftCenter){
					rotateClockwise();
				} else if(middleColor == rightCenter){
					rotateCounterClockwise();
				}
			} else {
				//do one of the rotations
				rotateClockwise();
				//and let the loop run again
			}
		}
		
		cube.printRubiks();
	}
	
	private static boolean allEdgesCorrect(){
		//loop through all the edges
		for(int i = 0; i <= 2; i += 2){
			if(!cube.edgeCorrect(i, 1, 0) || !cube.edgeCorrect(1, i, 0)){
				return false; //if you find any incorrect edges return false
			}
		}
		//otherwise you've been over them all and they're all correct
		return true;
	}
	
	//checks for a correct edge piece
	private static boolean oneEdgeCorrect(){
		//loop through all the edges
		for(int i = 0; i <= 2; i += 2){
			if(cube.edgeCorrect(i, 1, 0) || cube.edgeCorrect(1, i, 0)){
				return true; //if you find a correct edge return true
			}
		}
		//otherwise you've been over them all and they're all incorrect
		return false;
	}
	
	//rotates the cube until the one correct edge piece is in back
	//assumes that there is a correct edge piece
	private static void orientOneEdge(){
		while(!cube.edgeCorrect(0, 1, 0)){
			rotateCube();
		}
	}
	
	private static void rotateClockwise(){
		rotate("F");
		rotate("F");
		rotate("U");
		rotate("L");
		rotate("R'");
		rotate("F");
		rotate("F");
		rotate("L'");
		rotate("R");
		rotate("U");
		rotate("F");
		rotate("F");
	}
	
	private static void rotateCounterClockwise(){
		rotate("F");
		rotate("F");
		rotate("U'");
		rotate("L");
		rotate("R'");
		rotate("F");
		rotate("F");
		rotate("L'");
		rotate("R");
		rotate("U'");
		rotate("F");
		rotate("F");
	}
}
