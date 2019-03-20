package model;

import java.util.ArrayList;
import java.util.List;

import common.Coordinates;
import common.Direction;
import model.HashiModel.Bridge;

/**
 * 
 * This class models a Hashiwokakero puzzle and its current state.
 */
public class HashiModel implements IPuzzleSituationModel {
	private int noOfIslands;

	/**
	 * 
	 * Model of field including all current field elements, i.e. islands and
	 * bridges. The <code>field.length</code> equals <code>height</code> of field,
	 * for 0 &lt= i &lt <code>width</code> the <code>field[i].length</code> equals
	 * <code>width</code>. If there is no island or bridge at
	 * <code>field[y][x]</code>, <code>field[y][x]</code> = <code>null</code>.
	 * 
	 */
	private FieldElement[][] field;

	private PuzzleState puzzleState = PuzzleState.NOT_YET_SOLVED;
	private Bridge lastInsertedBridge;
	private static int bridgeCounter; // needed to determine lastInsertedBridge

	/**
	 * 
	 * Constructs an instance of an empty <code>width x height</code> Hashiwokakeru
	 * puzzle, i.e. a Hashiwokakeru puzzle with <code>width</code> columns and
	 * <code>height</code> rows without any islands or bridges. Islands must be
	 * added after construction.
	 * 
	 * @param width
	 *            of the Hashiwokakeru puzzle
	 * @param height
	 *            of the Hashiwokakeru puzzle
	 */
	public HashiModel(int width, int height) {
		field = new FieldElement[height][width];
	}

	@Override
	public int getWidth() {
		return field[0].length;
	}

	@Override
	public int getHeight() {
		return field.length;
	}

	@Override
	public int getNoOfIslands() {
		return noOfIslands;
	}

	@Override
	public PuzzleState getPuzzleState() {
		return puzzleState;
	}

	@Override
	public boolean isSolved() {
		return puzzleState == PuzzleState.SOLVED;
	}

	@Override
	public boolean isNotYetSolved() {
		return puzzleState == PuzzleState.NOT_YET_SOLVED;
	}

	@Override
	public boolean isUnsolvable() {
		return puzzleState == PuzzleState.UNSOLVABLE;
	}

	@Override
	public boolean containsError() {
		return puzzleState == PuzzleState.CONTAINS_ERROR;
	}

	@Override
	public void setPuzzleState(PuzzleState puzzleState) {
		this.puzzleState = puzzleState;
	}

	@Override
	public FieldElement getFieldElementAt(int x, int y) throws IllegalArgumentException {
		if (!isValidFieldPosition(x, y)) { // (x,y) on field
			int maxX = getWidth() - 1;
			int maxY = getHeight() - 1;
			throw new IllegalArgumentException(
					"(" + x + ", " + y + ") are not valid coordinates. x needs to be between 0 and " + maxX
							+ ", y betweeen 0 and " + maxY + ".");
		}
		return field[y][x];
	}
	
	@Override
	public Island getIslandAt(int x, int y) throws IllegalArgumentException {
		if (!islandAt(x, y)) throw new IllegalArgumentException("There is no island at (" + x + ", " + y + ").");
		return (Island) getFieldElementAt(x, y);
	}
	
	@Override
	public Bridge getBridgeAt(int x, int y) throws IllegalArgumentException {
		if (!bridgeAt(x, y)) throw new IllegalArgumentException("There is no bridge at (" + x + ", " + y + ").");
		return (Bridge) getFieldElementAt(x, y);
	}

	@Override
	public boolean islandAt(int x, int y) throws IllegalArgumentException {
		return getFieldElementAt(x, y) instanceof Island;
	}

	@Override
	public boolean bridgeAt(int x, int y) throws IllegalArgumentException {
		return getFieldElementAt(x, y) instanceof Bridge;
	}
	
	@Override
	public boolean isEmpty(int x, int y) throws IllegalArgumentException {
		return getFieldElementAt(x, y) == null;
	}

	@Override
	public boolean isValidFieldPosition(int x, int y) {
		return x >= 0 && y >= 0 && x < getWidth() && y < getHeight();
	}

	@Override
	public boolean isValidIslandPosition(int x, int y) {
		if (!isValidFieldPosition(x, y) || getFieldElementAt(x, y) instanceof Island
				|| isValidFieldPosition(x - 1, y) && getFieldElementAt(x - 1, y) instanceof Island
				|| isValidFieldPosition(x + 1, y) && getFieldElementAt(x + 1, y) instanceof Island
				|| isValidFieldPosition(x, y - 1) && getFieldElementAt(x, y - 1) instanceof Island
				|| isValidFieldPosition(x, y + 1) && getFieldElementAt(x, y + 1) instanceof Island) {
			return false;
		}
		return true;
	}
	
	@Override
	public void addIslandAt(int x, int y) throws IllegalArgumentException {
		addIslandAt(x, y, 0);
	}
	
	@Override
	public void addIslandAt(int x, int y, int noOfBridges) throws IllegalArgumentException {
		if (!isValidFieldPosition(x, y)) {
			int maxX = getWidth() - 1;
			int maxY = getHeight() - 1;
			throw new IllegalArgumentException(
					"(" + x + ", " + y + ") are not valid coordinates. x needs to be between 0 and " + maxX
							+ ". y needs to be betweeen 0 and " + maxY + ".");
		}
		if (!isValidIslandPosition(x, y)) {
			throw new IllegalArgumentException("Island cannot be added at (" + x + ", " + y
					+ ") because distance to existing island needs to be greater than 1.");
		}
		if (getFieldElementAt(x, y) instanceof Bridge) {
			throw new IllegalArgumentException(
					"Island cannot be added at (" + x + ", " + y + ") because at the coordinates there is a bridge.");
		}
		field[y][x] = new Island(x, y);
		noOfIslands++;
		getIslandAt(x, y).setNoOfBridgesRequired(noOfBridges);
	}
	


	@Override
	public List<Island> getIslands() {
		List<Island> islands = new ArrayList<>();
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				if (islandAt(x, y)) {
					islands.add((Island) getFieldElementAt(x, y));
				}
			}
		}
		return islands;
	}

	@Override
	public Island getNeighborIsland(Island island, Direction direction) throws IllegalArgumentException {
		if (island == null || direction == null)
			throw new IllegalArgumentException("Island and direction need not to be null.");
		Bridge bridge = getBridge(island, direction);
		if (bridge != null) { // there is a neighbor to which a bridge has been built
			return bridge.getOtherEnd(island);
		} else { // there either is no neighbor or a neighbor to which no bridge has been built
			return getNeighbourIslandNotConnected(island, direction);
		}
	}

	@Override
	public List<Island> getNeighbourIslands(Island island) throws IllegalArgumentException {
		List<Island> neighbourIslands = new ArrayList<>();
		for (Direction direction : Direction.values()) {
			Island neighbor = getNeighborIsland(island, direction);
			if (neighbor != null) {
				neighbourIslands.add(neighbor);
			}
		}
		return neighbourIslands;
	}

	private Island getNeighbourIslandNotConnected(Island island, Direction direction) {
		Coordinates coords = island.getCoords().getNextCoordsIn(direction);
		while (isValidFieldPosition(coords.x, coords.y) && getFieldElementAt(coords.x, coords.y) == null) {
			// move one more step in direction
			coords = coords.getNextCoordsIn(direction);
		}
		if (isValidFieldPosition(coords.x, coords.y) && islandAt(coords.x, coords.y)) {
			// there is an island at the (coords.x, coords.y)
			return (Island) getFieldElementAt(coords.x, coords.y);
		}
		return null;
	}

	@Override
	public Bridge getBridge(Island island, Direction direction) throws IllegalArgumentException {
		if (island == null || direction == null)
			throw new IllegalArgumentException("Island and direction need not to be null.");
		// check coords right next to island in specified direction
		Coordinates coordsBetweenIslands = island.getCoords().getNextCoordsIn(direction);
		if (isValidFieldPosition(coordsBetweenIslands.x, coordsBetweenIslands.y)
				&& bridgeAt(coordsBetweenIslands.x, coordsBetweenIslands.y)) {
			Bridge bridge = (Bridge) getFieldElementAt(coordsBetweenIslands.x, coordsBetweenIslands.y);
			// check that bridge is not orthogonal to direction, i.e. does connect island
			if (island.equals(bridge.getStart()) || island.equals(bridge.getEnd())) {
				return bridge;
			}
		}
		return null;
	}

	@Override
	public Bridge getBridgeBetween(Island island, Island otherIsland) throws IllegalArgumentException {
		if (island == null || otherIsland == null)
			throw new IllegalArgumentException("One of the islands is null.");
		// check that islands are neighbors
		Direction directionOfOtherIsland = island.getCoords().getDirectionOfCoord(otherIsland.getCoords());
		if (!otherIsland.equals(getNeighborIsland(island, directionOfOtherIsland)))
			throw new IllegalArgumentException("Islands are not neighbors to each other.");
		// get bridge if existing
		Coordinates coordsBetweenIslands = island.getCoords().getNextCoordsIn(directionOfOtherIsland);
		if (bridgeAt(coordsBetweenIslands.x, coordsBetweenIslands.y)) {
			// since islands are neighbors there is no crossing bridge or island between
			// them
			return (Bridge) getFieldElementAt(coordsBetweenIslands.x, coordsBetweenIslands.y);
		}
		return null;
	}

	@Override
	public Bridge getLastInsertedBridge() {
		return lastInsertedBridge;
	}

	@Override
	public boolean addBridge(Island island, Direction direction) throws IllegalArgumentException {
		Island neighbor = getNeighborIsland(island, direction);
		if (neighbor == null)
			throw new IllegalArgumentException(
					"There is no neighbor in the " + direction + " of " + island + " to add a bridge to.");
		return addBridgeBetween(island, neighbor);
	}

	@Override
	public boolean addBridgeBetween(Island island, Island otherIsland) throws IllegalArgumentException {
		Bridge bridge = getBridgeBetween(island, otherIsland);
		if (bridge == null) {
			return addBridgeBetween(island, otherIsland, false);
		} else if (!bridge.isDouble()) { // single bridge already existing
			bridge.setDouble(true);
			lastInsertedBridge = bridge; // update bridge last inserted // TODO: incapsulate in setDouble method
			return true;
		} else { // double bridge already existing
			return false;
		}
	}

	@Override
	public boolean addBridgeBetween(Island island, Island otherIsland, boolean doubleBridge)
			throws IllegalArgumentException {
		if (island == null || otherIsland == null)
			throw new IllegalArgumentException("Islands need not to be null.");
		// check if islands are neighbors
		Direction directionOfOtherIsland = island.getCoords().getDirectionOfCoord(otherIsland.getCoords());
		if (!otherIsland.equals(getNeighborIsland(island, directionOfOtherIsland)))
			throw new IllegalArgumentException("No bridge can be added between " + island + " and " + otherIsland
					+ " because there is an island or bridge inbetween.");
		// check if bridge already exists
		if (getBridge(island, directionOfOtherIsland) != null) {
			throw new IllegalArgumentException(
					"A bridge between " + island + " and " + otherIsland + " already exists.");
		}
		// create bridge
		Bridge bridge = new Bridge(island, otherIsland, doubleBridge);
		// put bridge on field
		Island start = bridge.getStart();
		Island end = bridge.getEnd();
		for (int x = start.getCoords().x + 1; x < end.getCoords().x; x++) {
			field[start.getCoords().y][x] = bridge;
		}
		for (int y = start.getCoords().y + 1; y < end.getCoords().y; y++) {
			field[y][start.getCoords().x] = bridge;
		}
		lastInsertedBridge = bridge; // update last inserted bridge // TODO: incapsulate in bridge creation
		return true;
	}
	
	@Override
	public void addBridgeBetweenIslandsAndResetBridgesRequired(Island existingIsland, Island newIsland, boolean isDouble)
			throws IllegalArgumentException {
		addBridgeBetween(existingIsland, newIsland, isDouble);
		int noOfBridgesAdded = isDouble ? 2 : 1;
		existingIsland.setNoOfBridgesRequired(existingIsland.getNoOfBridgesRequired() + noOfBridgesAdded);
		newIsland.setNoOfBridgesRequired(newIsland.getNoOfBridgesRequired() + noOfBridgesAdded);
	}

	@Override
	public boolean removeBridge(Island island, Direction direction) throws IllegalArgumentException {
		Island otherIsland = getNeighborIsland(island, direction);
		return removeBridgeBetween(island, otherIsland);
	}

	@Override
	public boolean removeBridgeBetween(Island island, Island otherIsland) {
		return removeBridgeBetween(island, otherIsland, false);
	}

	@Override
	public boolean removeBridgeBetween(Island island, Island otherIsland, boolean doubleBridge) {
		Bridge bridge = getBridgeBetween(island, otherIsland);
		// check if bridge exists and what kind of bridge it is
		if (bridge != null) {
			if (bridge.isDouble() && !doubleBridge) {
				// only a single bridge of a double bridge is to be removed
				bridge.setDouble(false);
			} else { // remove whole bridge from field
				Island start = bridge.getStart();
				Island end = bridge.getEnd();
				for (int x = start.getCoords().x + 1; x < end.getCoords().x; x++) {
					field[start.getCoords().y][x] = null;
				}
				for (int y = start.getCoords().y + 1; y < end.getCoords().y; y++) {
					field[y][start.getCoords().x] = null;
				}
			}
			// update bridge last inserted
			if (bridge.equals(lastInsertedBridge)) {
				updateLastInsertedBridge();
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removeBridge(Bridge bridge, boolean doubleBridge) throws IllegalArgumentException {
		return removeBridgeBetween(bridge.getStart(), bridge.getEnd(), doubleBridge);
	}
	
	@Override
	public boolean removeBridgeBetweenIslandsAndResetBridgesRequired(Bridge oldBridge, boolean doubleBridge) throws IllegalArgumentException {
		int noOfBridgesRemoved = oldBridge.isDouble() && doubleBridge ? 2 : 1;
		boolean bridgeWasRemoved = removeBridge(oldBridge, doubleBridge);
		if (bridgeWasRemoved) {
			Island start = oldBridge.getStart();
			Island end = oldBridge.getEnd();
			start.setNoOfBridgesRequired(start.getNoOfBridgesRequired() - noOfBridgesRemoved);
			end.setNoOfBridgesRequired(end.getNoOfBridgesRequired() - noOfBridgesRemoved);
		}
		return bridgeWasRemoved;
	}

	private void updateLastInsertedBridge() {
		lastInsertedBridge = null;
		// determine bridge with highest number (= bridge last inserted)
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				if (bridgeAt(x, y) && (lastInsertedBridge == null
						|| lastInsertedBridge.getBridgeNo() < ((Bridge) getFieldElementAt(x, y)).getBridgeNo())) {
					lastInsertedBridge = (Bridge) getFieldElementAt(x, y);
				}
			}
		}
	}

	@Override
	public void removeAllBridges() {
		for (int x = 0; x < getWidth(); x++) {
			for (int y = 0; y < getHeight(); y++) {
				// check if bridget at (x, y)
				if (bridgeAt(x, y)) { // remove bridge
					Bridge bridge = (Bridge) getFieldElementAt(x, y);
					removeBridgeBetween(bridge.getStart(), bridge.getEnd(), true);
				}
			}
		}
	}

	/**
	 * Returns a <code>String</code> representing the puzzle.
	 */
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				if (getFieldElementAt(x, y) == null) { // nothing at (x, y)
					stringBuilder.append("*");
				} else if (islandAt(x, y)) {
					Island island = (Island) getFieldElementAt(x, y);
					stringBuilder.append(island.getNoOfBridgesRequired());
				} else { // bridge at (x, y)
					Bridge bridge = (Bridge) getFieldElementAt(x, y);
					// check kind of bridge
					if (bridge.isVertical()) {
						if (bridge.isDouble()) {
							stringBuilder.append("||");
						} else {
							stringBuilder.append("|");
						}
					} else {
						if (bridge.isDouble()) {
							stringBuilder.append("=");
						} else {
							stringBuilder.append("-");
						}
					}
				}
				stringBuilder.append("\t"); // new row
			}
			stringBuilder.append("\n");
		}
		return stringBuilder.toString();
	}

	/**
	 * 
	 * The <code>FieldElement</code> interface should be implemented by any class
	 * whose instances can be elements of a field of a Hashiwokakeru puzzle (bridges
	 * and islands).
	 * 
	 
	 *
	 */
	public interface FieldElement {
	}

	/**
	 * 
	 * This inner class models an island on the field of the Hashiwokakeru puzzle.
	 * 
	 
	 *
	 */
	public class Island implements FieldElement, Comparable<Island> {
		private final Coordinates coords;
		private int noOfBridgesRequired;

		private Island(int x, int y) {
			this.coords = new Coordinates(x, y);
		}

		/**
		 * 
		 * Islands are compared by their coordinates. The model guarantees that there
		 * are never two islands with the same coordinates. <strong>Important:</strong>
		 * The equals and hashcode method are not overriden since there are never two
		 * islands with the same coordinates and therefore they are still valid.
		 * 
		 * @param otherIsland
		 *            to compare <code>this</code> island to
		 * @result a negative integer, zero, or a positive integer as this island is
		 *         less than, equal to, or greater than the specified island
		 */
		@Override
		public int compareTo(Island otherIsland) {
			return getCoords().compareTo(otherIsland.getCoords());
		}

		/**
		 * Returns a <code>String</code> representing this instance of an
		 * <code>Island</code>.
		 */
		@Override
		public String toString() {
			return "( " + coords.x + ", " + coords.y + " | " + noOfBridgesRequired + " )";
		}

		/**
		 * Gets the number of bridges required by this instance of an
		 * <code>Island</code>.
		 * 
		 * @return number of bridges required by this instance of an <code>Island</code>
		 */
		public int getNoOfBridgesRequired() {
			return noOfBridgesRequired;
		}

		/**
		 * Sets the number of bridges required by this instance of an
		 * <code>Island</code>.
		 */
		private void setNoOfBridgesRequired(int noOfBridges) {
			this.noOfBridgesRequired = noOfBridges;
		}

		/**
		 * Gets an instance of <code>Coordinates</code> representing this island's
		 * coordinates on the field.
		 * 
		 * @return this island's <code>Coordinates</code>
		 */
		public Coordinates getCoords() {
			return coords;
		}
		
		public int getX() {
			return getCoords().x;
		}
		
		public int getY() {
			return getCoords().y;
		}

		/**
		 * Gets the number of bridges missing, i.e. the number of bridges that yet need
		 * to be added, by this instance of an <code>Island</code>.
		 * 
		 * @return number of bridges missing
		 */
		public int getNoOfBridgesMissing() {
			int noOfBridgesMissing = noOfBridgesRequired;
			for (Direction direction : Direction.values()) {
				Bridge bridge = getBridge(this, direction);
				if (bridge != null) {
					noOfBridgesMissing = bridge.isDouble() ? noOfBridgesMissing - 2 : noOfBridgesMissing - 1;
				}
			}
			return noOfBridgesMissing;
		}

	}

	/**
	 * 
	 * This inner class models a bridge on the field of the Hashiwokakeru puzzle.
	 * 
	 
	 *
	 */
	public class Bridge implements FieldElement {
		private Island start, end;
		private boolean isDouble, isVertical;
		private int singleBridgeNo, bridgeNo;

		private Bridge(Island islandA, Island islandB, boolean isDouble) throws IllegalArgumentException {
			// check orientation of bridge
			if (islandA.getCoords().x != islandB.getCoords().x && islandA.getCoords().y == islandB.getCoords().y) {
				isVertical = false;
			} else if (islandA.getCoords().x == islandB.getCoords().x
					&& islandA.getCoords().y != islandB.getCoords().y) {
				isVertical = true;
			} else {
				throw new IllegalArgumentException("No bridge can be built between " + islandA + " and " + islandB
						+ " because bridge would neither be horizontal nor vertical.");
			}
			// set start and end of bridge
			boolean islandAisStart = islandA.compareTo(islandB) < 0;
			this.start = islandAisStart ? islandA : islandB;
			this.end = islandAisStart ? islandB : islandA;
			this.isDouble = isDouble;
			bridgeNo = bridgeCounter++; // to determine bridge last inserted
		}

		private int getBridgeNo() {
			return bridgeNo;
		}

		/**
		 * Returns true if this <code>Bridge</code> is vertical.
		 * 
		 * @return true if this <code>Bridge</code> is vertical
		 */
		public boolean isVertical() {
			return isVertical;
		}

		/**
		 * Returns true if this <code>Bridge</code> is a double bridge.
		 * 
		 * @return true if this <code>Bridge</code> is a double bridge
		 */
		public boolean isDouble() {
			return isDouble;
		}

		private void setDouble(boolean isDouble) {
			if (isDouble) { // update bridgeNo
				if (!this.isDouble) {
					singleBridgeNo = bridgeNo; // lazy instantiation
					bridgeNo = bridgeCounter++;
				}
			} else {
				bridgeNo = singleBridgeNo;
			}
			this.isDouble = isDouble;
		}

		/**
		 * Gets the <code>Island</code> instance representing the north end of
		 * <code>this</code> bridge if <code>this</code> bridge is vertical or the west
		 * end of <code>this</code> bridge if <code>this</code> bridge is horizontal.
		 * 
		 * @return island connected by this bridge north or west of the island it is
		 *         connected to
		 */
		public Island getStart() {
			return start;
		}

		/**
		 * Gets the <code>Island</code> instance representing the south end of
		 * <code>this</code> bridge if <code>this</code> bridge is vertical or the east
		 * end of <code>this</code> bridge if <code>this</code> bridge is horizontal.
		 * 
		 * @return island connected by this bridge south or east of the island it is
		 *         connected to
		 */
		public Island getEnd() {
			return end;
		}

		/**
		 * 
		 * Gets the <code>Island</code> instance representing the other end of
		 * <code>this</code> bridge. If <code>island</code> is the north or west end of
		 * <code>this</code> bridge, the south or east end is returned, respectively. If
		 * the <code>island</code> is the south or east end of <code>this</code> bridge,
		 * the north or west end is returned, respectively.
		 * 
		 * @param island
		 *            representing one end of this bridge
		 * @return other end of this bridge
		 * @throws IllegalArgumentException
		 *             if <code>island</code> is neither one nor the other end of
		 *             <code>this</code> bridge
		 */
		public Island getOtherEnd(Island island) throws IllegalArgumentException {
			if (island.equals(start))
				return end;
			if (island.equals(end))
				return start;
			throw new IllegalArgumentException(
					"Island " + island + " is neither start nor end of bridge " + this + ".");
		}

		/**
		 * Returns a <code>String</code> representing this instance of a
		 * <code>Bridge</code>.
		 */
		@Override
		public String toString() {
			return "( " + getStart() + ", " + getEnd() + " | " + isDouble + " )";
		}

	}



}
