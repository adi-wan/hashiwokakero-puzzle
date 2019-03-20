package model;

/**
 * 
 * Enumeration modeling the state of a Hashiwokakeru puzzle. 
 * Possible states:
 * <ul><li>{@link #NOT_YET_SOLVED}</li>
 * <li>{@link #CONTAINS_ERROR}</li>
 * <li>{@link #UNSOLVABLE}</li>
 * <li>{@link #SOLVED}</li></ul>
 * 
 * @author Adrian Stritzinger
 *
 */
public enum PuzzleState {

	/**
	 * 
	 * Puzzle is yet to be solved. Does not guarantee that puzzle can be solved by
	 * simply adding bridges, i.e. without removing bridges already added. If puzzle
	 * was loaded from a file, there is possibly no solution, otherwise there
	 * certainly is a solution.
	 * 
	 */
	NOT_YET_SOLVED,

	/**
	 * 
	 * Puzzle contains error, i.e. puzzle contains an island that has more bridges
	 * than it requires. Puzzle can not be solved without removing some of these
	 * bridges.
	 * 
	 */
	CONTAINS_ERROR,

	/**
	 * 
	 * Puzzle can certainly not be solved by simply adding bridges, i.e. bridges
	 * need to be removed. If puzzle was loaded from a file, there is possibly no
	 * solution, otherwise there certainly is a solution.
	 * 
	 */
	UNSOLVABLE,

	/**
	 * 
	 * Puzzle is solved, i.e. every island has exactly the number of bridges it
	 * requires and all islands belong to one and the same connected component.
	 * 
	 */
	SOLVED;
}
