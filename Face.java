import java.util.Vector;

public class Face {
	private char[][] face;
	private char[] top;
	private char[] bottom;
	private char[] left;
	private char[] right;
	private String topFace;
	private String bottomFace;
	private String leftFace;
	private String rightFace;
	
	//expects strings that look like this: wrb
	//converts those strings directly to char arrays and stores them in xy matrix
	/*	face is stored as follows (in [x,y] pairs):
	 * 		   y
	 * 	[0,0][0,1][0,2]
	 * x[1,0][1,1][1,2]
	 * 	[2,0][2,1][2,2]
	 */
	public Face(String row1, String row2, String row3){
		face = new char[3][3];
		
		face[0] = row1.toCharArray();
		face[1] = row2.toCharArray();
		face[2] = row3.toCharArray();
	}
	
	//overloaded constructor to construct a copy of a given face
	public Face(Face otherFace){
		this.face = otherFace.face.clone();
		this.top = otherFace.top.clone();
		this.bottom = otherFace.bottom.clone();
		this.left = otherFace.left.clone();
		this.right = otherFace.right.clone();
	}
	
	//sets side references (the arrays containing the other sides of the cubies)
	public void setReferences(char[] up, char[] down, char[] leftside, char[] rightside){
		this.top = up;
		this.bottom = down;
		this.left = leftside;
		this.right = rightside;
	}
	
	//sets integer references to the adjacent sides of the cube
	public void setFaceRef(String up, String down, String leftside, String rightside){
		this.topFace = up;
		this.bottomFace = down;
		this.leftFace = leftside;
		this.rightFace = rightside;
	}
	
	//checks if a cubie is an edge piece
	public boolean isEdge(int x, int y){
		boolean isEdgePiece = (x == 0 && y == 1) || (x == 1 && y == 0) ||
							  (x == 1 && y == 2) || (x == 2 && y == 1);
		return isEdgePiece;
	}
	
	//checks if a cubie is a corner piece
	public boolean isCorner(int x, int y){
		boolean isCornerPiece = (x == 0 && y == 0) || (x == 0 && y == 2) ||
							    (x == 2 && y == 0) || (x == 2 && y == 2);
		return isCornerPiece;
	}
	
	//search for any cubie of this color
	public Vector<Integer> searchCubie(char color){
		Vector<Integer> foundAt = new Vector<Integer>();
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				if(face[x][y] == color){
					foundAt.add(x);
					foundAt.add(y);
					return foundAt;
				}
			}
		}
		//if there's no cubie of that color, this vector will be empty
		return foundAt;
	}
	
	//search for any cubie that does not match this color
	//starts the search at a given x/y
		public Vector<Integer> searchAntiCubie(char notThisColor, int x, int y){
			Vector<Integer> foundAt = new Vector<Integer>();
			for(int i = x; i < 3; i++){
				for(int j = y; j < 3; j++){
					if(face[i][j] != notThisColor){
						foundAt.add(i);
						foundAt.add(j);
						return foundAt;
					}
				}
			}
			//if all cubies are this color, this vector will be empty
			return foundAt;
		}
	
	//search for a specific edge cubie
	public Vector<Integer> searchEdge(char color1, char color2){
		Vector<Integer> foundAt = new Vector<Integer>();
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				if(face[x][y] == color1){
					//System.out.println("Found " + color1 + " cubie, checking other side");
					if(getOtherEdgeChar(x,y) == color2){
						//System.out.println("Found cubie at " + x + " " + y);
						//System.out.println("This face is the " + face[1][1] + " face");
						foundAt.add(x);
						foundAt.add(y);
						return foundAt;
					}
				}
			}
		}
		//If the cubie is not found, this vector will be empty
		return foundAt; 
	}
	
	//search for a specific corner cubie
	public Vector<Integer> searchCorner(char color1, char color2, char color3){
		Vector<Integer> foundAt = new Vector<Integer>();
		for(int x = 0; x < 3; x++){
			for(int y = 0; y < 3; y++){
				if(face[x][y] == color1){
					//Cubie could be rotated
					boolean isRightCubie = (getOtherCornerChar1(x,y) == color2 && getOtherCornerChar2(x,y) == color3) ||
											(getOtherCornerChar2(x,y) == color2 && getOtherCornerChar1(x,y) == color3);
					if(isRightCubie){
						foundAt.add(x);
						foundAt.add(y);
					}
				}
			}
		}
		//If the cubie is not found, this vector will be empty
		return foundAt; 
	}
	
	//Get one specific cubie color
	public char getChar(int x, int y){
		return face[x][y];
	}
	
	//if the cubie is an edge, get the other face color
	public char getOtherEdgeChar(int x, int y){
		if(x == 0 && y == 1){
			//System.out.println(top[1]);
			return top[1];
		} else if(x == 1 && y == 0){
			//System.out.println(left[1]);
			return left[1];
		} else if(x == 1 && y == 2){
			//System.out.println(right[1]);
			return right[1];
		} else if(x == 2 && y == 1){
			//System.out.println(bottom[1]);
			return bottom[1];
		} else {
			//System.out.println("Not an edge piece");
			return 'a';
		}
	}
	
	//if the cubie is a corner, get the first side (either the top or bottom face)
	public char getOtherCornerChar1(int x, int y){
		if(x == 0 && y == 0){
			return top[0];
		} else if(x == 0 && y == 2){
			return top[2];
		} else if(x == 2 && y == 0){
			return bottom[0];
		} else if(x == 2 && y == 2){
			return bottom[2];
		} else {
			//System.out.println("Not a corner piece");
			return 'a';
		}
	}
	
	//if the cubie is a corner, get the second side (either the left or right face)
	public char getOtherCornerChar2(int x, int y){
		if(x == 0 && y == 0){
			return left[0];
		} else if(x == 0 && y == 2){
			return right[0];
		} else if(x == 2 && y == 0){
			return left[2];
		} else if(x == 2 && y == 2){
			return right[2];
		} else {
			//System.out.println("Not a corner piece");
			return 'a';
		}
	}
	
	//"Column" means y is fixed, so return all x values for that fixed y position
	//col must be between 0 and 2
	public char[] getCol(int col){
		char[] result = new char[3];
		
		for(int i = 0; i < 3; i++){
			result[i] = face[i][col];
		}
		
		return result;
	}
	
	//"Row" means x is fixed and you can simply return the array at that x position
	//row must be between 0 and 2
	public char[] getRow(int row){
		return face[row];
	}
	
	//y is fixed: set each x in the y column to the input values
	public void setCol(char[] input, int col){
		for(int i = 0; i < 3; i++){
			face[i][col] = input[i];
		}
	}
	
	//x is fixed: set the array in x to the input array
	public void setRow(char[] input, int row){
		face[row] = input;
	}
	
	public String getTopFace(){
		return topFace;
	}
	
	public String getBottomFace(){
		return bottomFace;
	}
	
	public String getLeftFace(){
		return leftFace;
	}
	
	public String getRightFace(){
		return rightFace;
	}
	
	//does not check if the other sides are solved
	//only checks squares on this face
	//so it's not really "solved" per se as much as it is just all the same color
	public boolean isSolved(){
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++)
				if(face[i][j] != face[1][1]){
					return false;
				}
		}
		return true;
	}
	
	/*
	 * Code for face rotation credit goes to u/JavaDeveloper and u/Joop Eggen
	 * on codereview.stackexchange.com (transpose to JavaDeveloper, others to Joop)
	 */
	//mirrors face across the x = y line
	private void transpose(){
		for(int i = 0; i < face.length; i++){
			for(int j = i; j < face[0].length; j++){
				char saved = face[i][j];
				face[i][j] = face[j][i];
				face[j][i] = saved;
			}
		}
	}
	//swaps rows for rotation
	private void swapRows(){
		for(int i = 0, k = face.length - 1; i < k; i++, k--){
			char[] saved = face[i];
			face[i] = face[k];
			face[k] = saved;
		}
	}
	//swaps rows, then transposes
	public void rotate90CW(){
		swapRows();
		transpose();
	}
	//transposes, then swaps rows
	public void rotate90CCW(){
		transpose();
		swapRows();
	}
}
