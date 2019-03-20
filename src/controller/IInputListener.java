package controller;

import common.Direction;

import model.HashiModel.Island;

/**
 * The <code>IInputListener</code> interface forms the main interface of the
 * controller to the view. A class that implements the interface is responsible
 * for receiving inputs from components of the view and processing them.
 * 
 * After processing an input, the state of the model of the Hashiwokakeru puzzle
 * is updated and relevant components of the view are refreshed if model changed
 * as a result of the processing.
 */
public interface IInputListener {
	/**
	 * Makes a move, i.e. adds or removes a (single) bridge from the field of the
	 * puzzle, if possible, meaning that there is a neighbor to the
	 * <code>island</code> in the <code>directionOfClick</code> to which a bridge
	 * can be built or to which a bridge is existing that can be removed. If
	 * <code>addBridge</code> is true, a bridge is to be added, otherwise a bridge
	 * is to be removed.
	 * 
	 * <p>
	 * <strong>Important:</strong> If the puzzle is currently being solved
	 * automatically, no move can be made by calling this method.
	 * </p>
	 * 
	 * @param island
	 *            to which to add a bridge or from which to remove a bridge
	 * @param directionOfClick
	 *            direction in which to add or to remove bridge
	 * @param addBridge
	 *            true if bridge is to be added, false if a bridge is to be
	 *            removed
	 */
	void makeMove(Island island, Direction directionOfClick, boolean addBridge);

	/**
	 * Adds a bridge to the puzzle if a bridge can be found that must certainly be
	 * built based <strong>on the current state</strong> of the puzzle.
	 * 
	 * <p>
	 * <strong>Important:</strong> If the user has already added bridges by himself
	 * or herself that render the puzzle unsolvable without the program recognizing
	 * it, the bridge that is added by the method may not lead to a solution of the
	 * puzzle and may need to be removed in order to solve the puzzle.
	 * </p>
	 * 
	 * <p>
	 * <strong>Important:</strong> No bridge is added if
	 * <ul>
	 * <li>no bridge can be found,</li>
	 * <li>the puzzle is currently being solved automatically,</li>
	 * <li>the puzzle is unsolvable and this was recognized by the application,</li>
	 * <li>the puzzle is invalid or already solved.
	 * </ul>
	 * </p>
	 * 
	 * @return true if a bridge was added, false otherwise
	 */
	boolean addNextBridge();

	/**
	 * Starts automatic solving of the puzzle if the puzzle is currently not being
	 * solved automatically. Otherwise the automatic solving of the puzzle is
	 * stopped. Each time after a bridge was added, a short pause is made and the
	 * view refreshed.
	 * 
	 * <p>
	 * The automatic solving of a puzzle stops automatically if
	 * <ul>
	 * <li>no bridge can be found that must certainly be built based <strong>on the
	 * current state</strong> of the puzzle (see {@link #addNextBridge()
	 * addNextBridge} method),</li>
	 * <li>the puzzle is restarted, i.e. all bridges are removed from the
	 * puzzle,</li>
	 * <li>a new puzzle is loaded from a file,</li>
	 * <li>the puzzle is saved to a file,</li>
	 * <li>a new puzzle is generated or</li>
	 * <li>the application is quit.</li>
	 * </p>
	 * 
	 * <p>
	 * <strong>Important:</strong> While the puzzle is being solved automatically,
	 * the user can neither add nor remove any bridges "manually" or add bridges by
	 * using the {@link #addNextBridge() addNextBridge} method.
	 * </p>
	 * 
	 */
	void startAndStopSolving();

	/**
	 * Restarts the puzzle, i.e. removes all bridges from the puzzle and resets the
	 * puzzle state to {@link model.PuzzleState#NOT_YET_SOLVED}.
	 * 
	 * <p>
	 * <strong>Important:</strong> If the puzzle is currently being solved
	 * automatically, this is stopped by calling this method.
	 * </p>
	 * 
	 */
	void restartPuzzle();

	/**
	 * Loads a new puzzle from the file at <code>filePath</code>. If the puzzle
	 * cannot be loaded because, e.g. there is no file at <code>filePath</code> or
	 * the file does contain bridges but no islands, this method has no effect with
	 * the exception of the view being refreshed and the puzzle being stopped from
	 * being solved automatically if this is currently the case.
	 * 
	 * <p>
	 * <strong>Important:</strong> If the puzzle is currently being solved
	 * automatically, this is stopped by calling this method.
	 * </p>
	 * 
	 * @param filePath of the .bgs file that contains the Hashiwokakeru puzzle to be loaded
	 */
	void loadPuzzle(String filePath);

	/**
	 * Saves puzzle to file at <code>filePath</code>. If file at
	 * <code>filePath</code> already exists it is overwritten.
	 * 
	 * <p>
	 * <strong>Important:</strong> If the puzzle is currently being solved
	 * automatically, this is stopped by calling this method.
	 * </p>
	 * 
	 * @param filePath of the .bgs file in which the Hashiwokakeru puzzle is to be saved
	 */
	void savePuzzle(String filePath);

	/**
	 * Generates a new puzzle that replaces the existing one. If the existing one
	 * was not saved before, it is lost.
	 * 
	 * <p>
	 * <strong>Important:</strong> If the puzzle is currently being solved
	 * automatically, this is stopped by calling this method.
	 * </p>
	 */
	void generatePuzzle();

	/**
	 * Generates a new puzzle of size <code>width</code> x <code>height</code> that
	 * replaces the existing one. If the existing one was not saved before, it is
	 * lost.
	 * 
	 * <p>
	 * <strong>Important:</strong> If the puzzle is currently being solved
	 * automatically, this is stopped by calling this method.
	 * </p>
	 * 
	 * @param width
	 *            of new puzzle
	 * @param height
	 *            of new puzzle
	 */
	void generatePuzzle(int width, int height);

	/**
	 * Generates a new puzzle of size <code>width</code> x <code>height</code> that
	 * has <code>noOfIslands</code> number of islands and replaces the existing one.
	 * If the existing one was not saved before, it is lost.
	 * 
	 * <p>
	 * <strong>Important:</strong> If the puzzle is currently being solved
	 * automatically, this is stopped by calling this method.
	 * </p>
	 * 
	 * @param width
	 *            of new puzzle
	 * @param height
	 *            of new puzzle
	 * @param noOfIslands
	 *            of new puzzle
	 */
	void generatePuzzle(int width, int height, int noOfIslands);

}
