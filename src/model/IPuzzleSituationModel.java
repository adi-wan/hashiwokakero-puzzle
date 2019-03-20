package model;

import java.util.List;

import common.Direction;
import model.HashiModel.FieldElement;
import model.HashiModel.Bridge;
import model.HashiModel.Island;

/**
 * 
 * The <code>IPuzzleSituationModel</code> interface should be implemented by a
 * class modeling (the current state of) a Hashiwokakeru puzzle.
 * 
 * <p>
 * A lot of methods of the <code>IPuzzleSituationModel<code> use coordinates (x,
 * y). x represents the column number and y the row number, therefore, x needs
 * to be greater than or equal to 0 and less than width of the (field of the)
 * puzzle and y needs to be greater than or equal to 0 and less than height of
 * the (field of the) puzzle. <strong>Important:</strong> Rows are numbered
 * starting at the top like similar to coordinates on a screen.
 * </p>
 * 
 * @author Adrian Stritzinger
 *
 */
public interface IPuzzleSituationModel {

	/**
	 *
	 * Gets the width, i.e. the number of columns, of the puzzle.
	 * 
	 * @return width the puzzle
	 */
	int getWidth();

	/**
	 *
	 * Gets the heigth, i.e. the number of rows, of the puzzle.
	 * 
	 * @return height of the puzzle
	 */
	int getHeight();

	/**
	 * 
	 * Gets the number of islands that the puzzle has.
	 * 
	 * @return number of islands of the puzzle
	 */
	int getNoOfIslands();

	/**
	 * 
	 * Gets the state of the puzzle.
	 * 
	 * @return state of the puzzle
	 */
	PuzzleState getPuzzleState();

	/**
	 * Returns <code>true</code> if state of the puzzle equals
	 * {@link PuzzleState#SOLVED PuzzleState.SOLVED}, otherwise <code>false</code>.
	 * 
	 * @return true if the puzzle is solved, otherwise false
	 */
	boolean isSolved();

	/**
	 * 
	 * Returns <code>true</code> if state of the puzzle equals
	 * {@link PuzzleState#NOT_YET_SOLVED PuzzleState.NOT_YET_SOLVED}, otherwise
	 * <code>false</code>.
	 * 
	 * @return true if puzzle is yet to be solved, otherwise false
	 */
	boolean isNotYetSolved();

	/**
	 * 
	 * Returns <code>true</code> if state of the puzzle equals
	 * {@link PuzzleState#UNSOLVABLE PuzzleState.UNSOLVABLE}, otherwise
	 * <code>false</code>.
	 * 
	 * @return true if puzzle is unsolvable, otherwise false
	 */
	boolean isUnsolvable();

	/**
	 * 
	 * Returns <code>true</code> if state of the puzzle equals
	 * {@link PuzzleState#CONTAINS_ERROR PuzzleState.CONTAINS_ERROR}, otherwise
	 * <code>false</code>.
	 * 
	 * @return true if puzzle contains error, otherwise false
	 */
	boolean containsError();

	/**
	 * 
	 * Sets the state of the puzzle.
	 * 
	 * @param state
	 *            of the puzzle
	 */
	void setPuzzleState(PuzzleState state);

	/**
	 * 
	 * Gets <code>FieldElement</code> at coordinates (x, y).
	 * <code>FieldElement</code> can be an <code>Island</code>, a
	 * <code>Bridge</code> or <code>null</code> if there is no
	 * <code>FieldElement</code> at coordinates (x, y).
	 * 
	 * <p>
	 * <strong>Important:</strong> Returns null if there is no <code>Island</code>
	 * or <code>Bridge</code> at (x, y).
	 * </p>
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @return <code>FieldElement</code> at coordinates (x, y); can be an
	 *         <code>Island</code>, a <code>Bridge</code> or <code>null</code>
	 * 
	 * @throws IllegalArgumentException
	 *             if (x, y) are not valid coordinates
	 *             ({@link #isValidFieldPosition(int, int) isValidFieldPosition}
	 *             method returns <code>false</code>)
	 */
	FieldElement getFieldElementAt(int x, int y) throws IllegalArgumentException;

	/**
	 * 
	 * Gets <code>Island</code> at coordinates (x, y).
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @return <code>Island</code> at coordinates (x, y)
	 * 
	 * @throws IllegalArgumentException
	 *             if (x, y) are not valid coordinates
	 *             ({@link #isValidFieldPosition(int, int) isValidFieldPosition}
	 *             method returns <code>false</code>) or there is no
	 *             <code>Island</code> at coordinates (x, y)
	 */
	Island getIslandAt(int x, int y) throws IllegalArgumentException;

	/**
	 * 
	 * Gets <code>Bridge</code> at coordinates (x, y).
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @return <code>Bridge</code> at coordinates (x, y)
	 * 
	 * @throws IllegalArgumentException
	 *             if (x, y) are not valid coordinates
	 *             ({@link #isValidFieldPosition(int, int) isValidFieldPosition}
	 *             method returns <code>false</code>) or there is no
	 *             <code>Bridge</code> at coordinates (x, y)
	 */
	Bridge getBridgeAt(int x, int y) throws IllegalArgumentException;

	/**
	 * 
	 * Returns <code>true</code> if there is an <code>Island</code> at coordinates
	 * (x, y), otherwise <code>false</code>.
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @return true if there is an Island at coordinates (x, y), otherwise false
	 * @throws IllegalArgumentException
	 *             if (x, y) are not valid coordinates
	 *             ({@link #isValidFieldPosition(int, int) isValidFieldPosition}
	 *             method returns <code>false</code>)
	 */
	boolean islandAt(int x, int y) throws IllegalArgumentException;

	/**
	 * 
	 * Returns <code>true</code> if there is a <code>Bridge</code> at coordinates
	 * (x, y), otherwise <code>false</code>.
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @return true if there is a Bridge at coordinates (x, y), otherwise false
	 * @throws IllegalArgumentException
	 *             if (x, y) are not valid coordinates
	 *             ({@link #isValidFieldPosition(int, int) isValidFieldPosition}
	 *             method returns <code>false</code>)
	 */
	boolean bridgeAt(int x, int y) throws IllegalArgumentException;

	/**
	 * 
	 * Returns <code>true</code> if there is neither a <code>Bridge</code> nor an
	 * <code>Island</code> at coordinates (x, y), otherwise <code>false</code>.
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @return true if there is nothing (<code>null</code>) at coordinates (x, y),
	 *         otherwise false
	 * @throws IllegalArgumentException
	 *             if (x, y) are not valid coordinates
	 *             ({@link #isValidFieldPosition(int, int) isValidFieldPosition}
	 *             method returns <code>false</code>)
	 */
	boolean isEmpty(int x, int y) throws IllegalArgumentException;

	/**
	 * 
	 * Returns <code>true</code> if coordinates (x, y) are valid, i.e. if x is be
	 * greater than or equal to 0 and less than width of the puzzle and y is greater
	 * than or equal to 0 and less than height of the puzzle.
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @return true if coordinates (x, y) are valid, otherwise false
	 */
	boolean isValidFieldPosition(int x, int y);

	/**
	 * 
	 * Returns <code>true</code> if <code>Island</code> can be added to puzzle at
	 * coordinates (x, y), i.e. {@link #isValidFieldPosition(int, int)
	 * isValidFieldPosition} method returns <code>true</code> and the distance to
	 * already existing islands is greater than 1.
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @return true if Island can be added to puzzle at coordinates (x, y)
	 */
	boolean isValidIslandPosition(int x, int y);

	/**
	 * 
	 * Adds an <code>Island</code> to the puzzle at the coordinates (x, y) and
	 * thereby increments number of islands the puzzle has if (x, y) is a valid
	 * <code>Island</code> position ({@link #isValidIslandPosition(int, int)
	 * isValidIslandPosition} method returns <code>true</code>) and there is no
	 * <code>Bridge</code> at (x, y), otherwise an
	 * <code>IllegalArgumentException</code> is thrown.
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @throws IllegalArgumentException
	 *             if (x, y) is not a valid <code>Island</code> position or there is
	 *             a <code>Bridge</code> at (x, y)
	 */
	void addIslandAt(int x, int y) throws IllegalArgumentException;

	/**
	 * 
	 * Adds an <code>Island</code> to the puzzle at the coordinates (x, y) that
	 * requires <code>noOfBridge</code> bridges and thereby increments number of
	 * islands the puzzle has if (x, y) is a valid <code>Island</code> position
	 * ({@link #isValidIslandPosition(int, int) isValidIslandPosition} method
	 * returns <code>true</code>) and there is no <code>Bridge</code> at (x, y),
	 * otherwise an <code>IllegalArgumentException</code> is thrown.
	 * 
	 * @param x
	 *            coordinate
	 * @param y
	 *            coordinate
	 * @param noOfBridges
	 *            that island requires
	 * @throws IllegalArgumentException
	 *             if (x, y) is not a valid <code>Island</code> position or there is
	 *             a <code>Bridge</code> at (x, y)
	 */
	void addIslandAt(int x, int y, int noOfBridges) throws IllegalArgumentException;

	/**
	 * 
	 * Gets an instance a <code>List&lt=Island></code> containing all the islands of
	 * the puzzle (at the point in time the method is used) ordered naturally, i.e.
	 * by column first and row second. If there are no islands, the list is going to
	 * be empty.
	 * 
	 * @return a list of all the islands of the puzzle
	 */
	List<Island> getIslands();

	/**
	 * 
	 * Gets the neighbor island of <code>island</code> in the <code>direction</code>
	 * given, i.e. the island that is first encountered when stepping into the
	 * <code>direction</code> given starting at the coordinates right next to the
	 * <code>island</code> in the <code>direction</code> given. The
	 * <code>island</code> is possibly connected to the neighbor island by a
	 * <code>Bridge</code> but this is not necessarily the case.
	 * 
	 * <p>
	 * <strong>Important:</strong> If a crossing <code>Bridge</code>, i.e. a
	 * <code>Bridge</code> that does not connect the <code>island</code> with a
	 * neighbor island, is encountered while stepping into the
	 * <code>direction</code> given, the method returns <code>null</code> since the
	 * <code>Bridge</code> acts as a border hiding a possibly existing neighbor
	 * island. In case a neighbor island is encountered, it is returned even if it
	 * does not miss any bridges and therefore cannot be connected to the
	 * <code>island</code> without removing a bridge. <code>null</code> is also
	 * returned if no neighbor island is found, i.e. the coordinates become invalid
	 * ({@link #isValidFieldPosition(int, int) isValidFieldPosition} method returns
	 * <code>false</code>) by stepping into the <code>direction</code> given.
	 * </p>
	 * 
	 * @param island
	 *            the neighbor island to this island is to be found
	 * @param direction
	 *            the direction in which the neighbor island should be searched for
	 * @return neighbor island if existing, otherwise null
	 * @throws IllegalArgumentException
	 *             if island or direction is null
	 */
	Island getNeighborIsland(Island island, Direction direction) throws IllegalArgumentException;

	/**
	 * 
	 * Gets a <code>List&lt=Island></code> the neighbor islands of the
	 * <code>island</code> in every possible direction (north, east, south and
	 * west).
	 * 
	 * <p>
	 * A neighbor island of the <code>island</code> is an island first encountered
	 * when stepping into a direction D starting at the coordinates right next to
	 * the <code>island</code> in the direction D. The <code>island</code> is
	 * possibly connected to the neighbor island by a <code>Bridge</code> but this
	 * is not necessarily the case.
	 * </p>
	 * 
	 * <p>
	 * <strong>Important:</strong> If a crossing <code>Bridge</code>, i.e. a
	 * <code>Bridge</code> that does not connect the <code>island</code> with a
	 * neighbor island, is encountered while stepping into a direction, no neighbor
	 * island is added to the <code>List&lt=Island></code> of neighbor islands since
	 * the <code>Bridge</code> acts as a border hiding the possibly existing
	 * neighbor island. This is also true if no neighbor island is found, i.e. the
	 * coordinates become invalid ({@link #isValidFieldPosition(int, int)
	 * isValidFieldPosition} method returns <code>false</code>) by stepping into the
	 * direction. In case a neighbor island is encountered, it is added to the
	 * <code>List&lt=Island></code> of neighbor islands that is returned by the
	 * method even if it does not miss any bridges and therefore cannot be connected
	 * to the <code>island</code> without removing a bridge. If no neighbor islands
	 * are found, an empty <code>List&lt=Island></code> is returned.
	 * </p>
	 * 
	 * @param island
	 *            the neighbor islands to this island are to be found
	 * @return list of neighbor islands
	 * @throws IllegalArgumentException
	 *             if island is null
	 */
	List<Island> getNeighbourIslands(Island island) throws IllegalArgumentException; // TODO: Can be used in BridgeAdder

	/**
	 * 
	 * Gets the bridge of <code>island</code> in the <code>direction</code>.
	 * <strong>Important:</strong> Returns <code>null</code> if there is no such
	 * bridge.
	 * 
	 * @param island
	 *            connected by the bridge
	 * @param direction
	 *            of the bridge
	 * @return bridge of island in the direction if existing, otherwise null
	 * @throws IllegalArgumentException
	 *             if island or direction is null
	 */
	Bridge getBridge(Island island, Direction direction) throws IllegalArgumentException;

	/**
	 * 
	 * Gets the bridge between the <code>island</code> and the
	 * <code>otherIsland</code>. <strong>Important:</strong> Returns
	 * <code>null</code> if there is no such bridge.
	 * 
	 * @param island
	 *            connected by the bridge
	 * @param otherIsland
	 *            connected by the bridge
	 * @return bridge between islands if existing, otherwise null
	 * @throws IllegalArgumentException
	 *             if one of the islands is null or the islands are not neighbors to
	 *             each other (this can also mean that there is a bridge crossing
	 *             between them connecting two other islands)
	 */
	Bridge getBridgeBetween(Island island, Island otherIsland) throws IllegalArgumentException;

	/**
	 * 
	 * Gets the bridge last inserted into the puzzle. <strong>Important:</strong>
	 * Returns <code>null</code> if there is no such bridge.
	 * 
	 * @return bridge last inserted, otherwise null
	 */
	Bridge getLastInsertedBridge();

	/**
	 * 
	 * Adds a (single) bridge to the <code>island</code> in the
	 * <code>direction</code> if possible, i.e. the <code>island</code> is missing a
	 * bridge, there is a neighbor island in the <code>direction</code> also missing
	 * a bridge and if there is a bridge between the two islands, it is not a double
	 * bridge.
	 * 
	 * @param island
	 *            to which the bridge should be added
	 * @param direction
	 *            in which the bridge should be added
	 * @return true if the bridge was added
	 * @throws IllegalArgumentException
	 *             if island or direction is null or there is no neighbor island in
	 *             the direction
	 */
	boolean addBridge(Island island, Direction direction) throws IllegalArgumentException;

	/**
	 * 
	 * Adds a (single) bridge between the <code>island</code> and the
	 * <code>otherIsland</code> if possible, i.e. the islands are neighbors, both
	 * are missing a bridge and there is no double <code>Bridge</code> connecting
	 * them.
	 * 
	 * @param island
	 *            to connect by the bridge
	 * @param otherIsland
	 *            to connect by the bridge
	 * @return true if the bridge was added
	 * @throws IllegalArgumentException
	 *             if one of the islands is null or the islands are not neighbors to
	 *             each other (this can also mean that there is a bridge crossing
	 *             between them connecting two other islands)
	 */
	boolean addBridgeBetween(Island island, Island otherIsland) throws IllegalArgumentException;

	/**
	 * 
	 * Adds a bridge between the <code>island</code> and the
	 * <code>otherIsland</code> if possible, i.e. the islands are neighbors, both
	 * are missing a bridge and there is no <code>Bridge</code> already connecting
	 * them. If <code>doubleBridge</code> is true, a double <code>Bridge</code> is
	 * added, otherwise a single <code>Bridge</code>. <strong>Important:</strong>
	 * There cannot be a <code>Bridge</code> already connecting the two islands.
	 * 
	 * @param island
	 *            to connect by the bridge
	 * @param otherIsland
	 *            to connect by the bridge
	 * @param doubleBridge
	 *            true if the bridge to be added is a double bridge, otherwise a
	 *            single bridge is going to be added
	 * @return true if the bridge was added
	 * @throws IllegalArgumentException
	 *             if one of the islands is null, the islands are not neighbors to
	 *             each other (this can also mean that there is a bridge crossing
	 *             between them connecting two other islands) or a bridge between
	 *             the two islands already exists
	 */
	boolean addBridgeBetween(Island island, Island otherIsland, boolean doubleBridge) throws IllegalArgumentException;

	/**
	 * 
	 * Adds a bridge between the <code>island</code> and the
	 * <code>otherIsland</code> if possible, i.e. the islands are neighbors, both
	 * are missing a bridge and there is no <code>Bridge</code> already connecting
	 * them and resets the number of bridges the 2 islands require accordingly. If
	 * <code>doubleBridge</code> is true, a double <code>Bridge</code> is added,
	 * otherwise a single <code>Bridge</code>. <strong>Important:</strong> There
	 * cannot be a <code>Bridge</code> already connecting the two islands.
	 * 
	 * @param island
	 *            to connect by the bridge
	 * @param otherIsland
	 *            to connect by the bridge
	 * @param doubleBridge
	 *            true if the bridge to be added is a double bridge, otherwise a
	 *            single bridge is going to be added
	 * @return true if the bridge was added
	 * @throws IllegalArgumentException
	 *             if one of the islands is null, the islands are not neighbors to
	 *             each other (this can also mean that there is a bridge crossing
	 *             between them connecting two other islands) or a bridge between
	 *             the two islands already exists
	 */
	void addBridgeBetweenIslandsAndResetBridgesRequired(Island existingIsland, Island newIsland, boolean isDouble)
			throws IllegalArgumentException;

	/**
	 * 
	 * Removes a (single) bridge of the <code>island</code> in the
	 * <code>direction</code> if there is a bridge.
	 * 
	 * @param island
	 *            of which the bridge should be removed
	 * @param direction
	 *            in which the bridge should be removed
	 * @return true if the bridge was removed
	 * @throws IllegalArgumentException
	 *             if island or direction is null or there is no neighbor island in
	 *             the direction
	 */
	boolean removeBridge(Island island, Direction direction) throws IllegalArgumentException;

	/**
	 * 
	 * Removes a (single) bridge between the <code>island</code> and the
	 * <code>otherIsland</code> if there is a bridge.
	 * 
	 * @param island
	 *            one end of the bridge that is to be removed
	 * @param otherIsland
	 *            other end of the bridge that is to be removed
	 * @return true if the bridge was removed
	 * @throws IllegalArgumentException
	 *             if one of the islands is null or the islands are not neighbors to
	 *             each other
	 */
	boolean removeBridgeBetween(Island island, Island otherIsland) throws IllegalArgumentException;

	/**
	 * 
	 * Removes a <code>Bridge</code> between the <code>island</code> and the
	 * <code>otherIsland</code> if there is a bridge. If <code>doubleBridge</code>
	 * is true, a double <code>Bridge</code> is removed, otherwise a single
	 * <code>Bridge</code>. <strong>Important:</strong> If there is only a single
	 * <code>Bridge</code> but a double <code>Bridge</code> should be removed, i.e.
	 * <code>doubleBridge</code> is true, no <code>IllegalArgumentException</code>
	 * is thrown. Instead the single <code>Bridge</code> is removed as if
	 * <code>doubleBridge</code> was false.
	 * 
	 * @param island
	 *            one end of the bridge that is to be removed
	 * @param otherIsland
	 *            other end of the bridge that is to be removed
	 * @param doubleBridge
	 *            true if the bridge to be removed is a double bridge, otherwise a
	 *            single bridge is going to be removed
	 * @return true if the bridge was removed
	 * @throws IllegalArgumentException
	 *             if one of the islands is null, the islands are not neighbors to
	 *             each other (this can also mean that there is a bridge crossing
	 *             between them connecting two other islands)
	 */
	boolean removeBridgeBetween(Island island, Island otherIsland, boolean doubleBridge)
			throws IllegalArgumentException;

	/**
	 * 
	 * Removes a <code>bridge</code> if there is a bridge. If
	 * <code>doubleBridge</code> is true, a double <code>Bridge</code> is removed,
	 * otherwise a single <code>Bridge</code>. <strong>Important:</strong> If there
	 * is only a single <code>Bridge</code> but a double <code>Bridge</code> should
	 * be removed, i.e. <code>doubleBridge</code> is true, no
	 * <code>IllegalArgumentException</code> is thrown. Instead the single
	 * <code>Bridge</code> is removed as if <code>doubleBridge</code> was false.
	 * 
	 * @param bridge
	 *            to be removed
	 * @param doubleBridge
	 *            true if the bridge to be removed is a double bridge, otherwise a
	 *            single bridge is going to be removed
	 * @return true if the bridge was removed
	 * @throws IllegalArgumentException
	 *             if one of the islands is null, the islands are not neighbors to
	 *             each other (this can also mean that there is a bridge crossing
	 *             between them connecting two other islands)
	 */
	boolean removeBridge(Bridge bridge, boolean doubleBridge) throws IllegalArgumentException;

	/**
	 * 
	 * Removes a <code>bridge</code> if there is a bridge and resets number of
	 * bridges required by island at both ends accordingly, i.e., minus number of
	 * bridges removed. If <code>doubleBridge</code> is true, a double
	 * <code>Bridge</code> is removed, otherwise a single <code>Bridge</code>.
	 * <strong>Important:</strong> If there is only a single <code>Bridge</code> but
	 * a double <code>Bridge</code> should be removed, i.e.
	 * <code>doubleBridge</code> is true, no <code>IllegalArgumentException</code>
	 * is thrown. Instead the single <code>Bridge</code> is removed as if
	 * <code>doubleBridge</code> was false.
	 * 
	 * @param bridge
	 *            to be removed
	 * @param doubleBridge
	 *            true if the bridge to be removed is a double bridge, otherwise a
	 *            single bridge is going to be removed
	 * @return true if the bridge was removed
	 * @throws IllegalArgumentException
	 *             if one of the islands is null, the islands are not neighbors to
	 *             each other (this can also mean that there is a bridge crossing
	 *             between them connecting two other islands)
	 */
	boolean removeBridgeBetweenIslandsAndResetBridgesRequired(Bridge oldBridge, boolean doubleBridge) throws IllegalArgumentException;

	/**
	 * Removes all bridges from the puzzle.
	 */
	void removeAllBridges();

}
