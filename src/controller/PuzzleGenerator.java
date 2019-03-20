package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import common.Coordinates;
import common.Direction;
import model.HashiModel;
import model.HashiModel.Bridge;
import model.HashiModel.Island;
import model.IPuzzleSituationModel;

/**
 * Class providing methods for generating a random Hashiwokakero puzzle that can
 * be solved.
 * 
 * @author Adrian Stritzinger
 *
 */
public class PuzzleGenerator {

	private static final int MIN_WIDTH = 4, MIN_HEIGHT = 4, MAX_WIDTH = 25, MAX_HEIGHT = 25, MIN_NO_OF_ISLANDS = 2;
	private IPuzzleSituationModel hashiModel; // model created

	private static Random random = new Random();
	// to generate width, height, noOfIslands, coords of islands and type of bridges

	private List<Island> bridgeableIslands;
	// islands from or to which a bridge can still be built, from these islands
	// islands are picked to build a bridge to a new island

	/**
	 * Generates a random, solvable Hashiwokakero puzzle with random width and
	 * height in interval [4, 25], respectively and random number of islands in
	 * interval [min(width, height), max(4, width*height / 5)].
	 * 
	 * @return A Hashiwokakero puzzle that can be solved
	 */
	public IPuzzleSituationModel getPuzzleSituationModel() {
		int width = random.nextInt(MAX_WIDTH - MIN_WIDTH + 1) + MIN_WIDTH;
		int height = random.nextInt(MAX_HEIGHT - MIN_HEIGHT + 1) + MIN_HEIGHT;
		int noOfIslands = getRandNoOfIslands(width, height);
		return getPuzzleSituationModel(width, height, noOfIslands);
	}

	private static int getRandNoOfIslands(int width, int height) {
		int minNoOfIslands = width < height ? width : height;
		int maxNoOfIslands = getMaxNoOfIslands(width, height);
		// if width = height = 4 then minNoOfIslands = 4 and maxNoOfIslands = 3
		minNoOfIslands = minNoOfIslands > maxNoOfIslands ? maxNoOfIslands : minNoOfIslands;
		return random.nextInt(maxNoOfIslands - minNoOfIslands + 1) + minNoOfIslands;
	};

	private static int getMaxNoOfIslands(int width, int height) {
		return width * height / 5;
	}

	/**
	 * Generates a random, solvable <code>width</code> x <code>height</code>
	 * Hashiwokakero puzzle with random number of islands in interval [min(width,
	 * height), max(4, width*height / 5)].
	 * 
	 * @param width
	 *            of puzzle to be generated
	 * @param height
	 *            of puzzle to be generated
	 * @return A Hashiwokakero puzzle that can be solved
	 * @throws IllegalArgumentException
	 *             if <code>width</code> or <code>height</code> is not in [4, 25]
	 */
	public IPuzzleSituationModel getPuzzleSituationModel(int width, int height) throws IllegalArgumentException {
		int noOfIslands = getRandNoOfIslands(width, height);
		return getPuzzleSituationModel(width, height, noOfIslands);
	};

	private static boolean isPuzzleConfigurationValid(int width, int height) {
		return MIN_WIDTH <= width && width <= MAX_WIDTH && MIN_HEIGHT <= height && height <= MAX_HEIGHT;
	};

	/**
	 * Generates a random, solvable <code>width</code> x <code>height</code>
	 * Hashiwokakero puzzle with <code>noOfIslands</code> number of islands.
	 * 
	 * @param width
	 *            of puzzle to be generated
	 * @param height
	 *            of puzzle to be generated
	 * @param noOfIslands
	 *            The number of islands of the puzzle to be generated
	 * @return A Hashiwokakero puzzle that can be solved
	 * @throws IllegalArgumentException
	 *             if <code>width</code> or <code>height</code> is not in [4, 25] or
	 *             <code>noOfIslands</code> is not in [2, max(4,
	 *             <code>width</code>*<code>height</code> / 5)]
	 */
	public IPuzzleSituationModel getPuzzleSituationModel(int width, int height, int noOfIslands)
			throws IllegalArgumentException {
		// check if width and height are valid
		if (!isPuzzleConfigurationValid(width, height))
			throw new IllegalArgumentException(
					"Board configuration is not valid for generating a puzzle. Width needs to be between " + MIN_WIDTH
							+ " and " + MAX_WIDTH + ". Height needs to be between " + MIN_HEIGHT + " and " + MAX_HEIGHT
							+ ".");
		// check if noOfIslands is valid
		if (MIN_NO_OF_ISLANDS > noOfIslands || noOfIslands > getMaxNoOfIslands(width, height))
			throw new IllegalArgumentException(
					"Board configuration is not valid for generating a puzzle. Number of islands needs to be between "
							+ MIN_NO_OF_ISLANDS + " and " + getMaxNoOfIslands(width, height) + ".");
		// create model and populate it with islands and bridges until requirements are
		// met
		while (hashiModel == null || hashiModel.getNoOfIslands() != noOfIslands) {
			hashiModel = new HashiModel(width, height);
			addSolvedHashiPuzzleToModel(noOfIslands);
		}
		hashiModel.removeAllBridges();
		return hashiModel;
	}

	// populate model with islands and bridges until requirements are met
	private void addSolvedHashiPuzzleToModel(int noOfIslands) {
		bridgeableIslands = new ArrayList<>();
		// put first island on field
		addIslandAtRandomPositionToModel();
		// put other islands on field including a bridge to an island already existing
		// to make sure that puzzle is going be solvable
		while (hashiModel.getNoOfIslands() < noOfIslands && !bridgeableIslands.isEmpty()) {
			addIslandWithBridgeToExistingIsland();
		}
	}

	private void addIslandAtRandomPositionToModel() {
		int x = random.nextInt(hashiModel.getWidth());
		int y = random.nextInt(hashiModel.getHeight());
		addIslandToModelAndBridgeableIslands(x, y);
	}

	private void addIslandToModelAndBridgeableIslands(int x, int y) {
		hashiModel.addIslandAt(x, y);
		bridgeableIslands.add(hashiModel.getIslandAt(x, y));
	}

	private void addIslandWithBridgeToExistingIsland() {
		// pick island from bridgeableIslands
		int randIslandIndex = random.nextInt(bridgeableIslands.size());
		Island existingIsland = bridgeableIslands.get(randIslandIndex);
		List<Coordinates> validNeighborIslandCoords = getValidNeighborIslandCoords(existingIsland);
		if (validNeighborIslandCoords.isEmpty()) {
			bridgeableIslands.remove(randIslandIndex);
		} else { // new island including bridge to existing one can be added
			Coordinates coords = validNeighborIslandCoords.get(random.nextInt(validNeighborIslandCoords.size()));
			// check if bridge  must be split to add island 
			if (hashiModel.bridgeAt(coords.x, coords.y)) {
				addIslandBySplittingBridgeWithBridgeToExIslands(existingIsland, coords);
			} else {
				addIslandWithBridgeToExIsland(existingIsland, coords);
			}

		}
	}

	private void addIslandWithBridgeToExIsland(Island existingIsland, Coordinates coords) {
		addIslandToModelAndBridgeableIslands(coords.x, coords.y);
		Island newIsland = hashiModel.getIslandAt(coords.x, coords.y);
		boolean isDouble = random.nextBoolean();
		hashiModel.addBridgeBetweenIslandsAndResetBridgesRequired(existingIsland, newIsland, isDouble);
	}

	private void addIslandBySplittingBridgeWithBridgeToExIslands(Island existingIsland, Coordinates coords) {
		Bridge oldBridge = hashiModel.getBridgeAt(coords.x, coords.y);
		hashiModel.removeBridgeBetweenIslandsAndResetBridgesRequired(oldBridge, true);
		addIslandWithBridgeToExIsland(existingIsland, coords);
		Island newIsland = hashiModel.getIslandAt(coords.x, coords.y);
		hashiModel.addBridgeBetweenIslandsAndResetBridgesRequired(oldBridge.getStart(), newIsland, oldBridge.isDouble());
		hashiModel.addBridgeBetweenIslandsAndResetBridgesRequired(oldBridge.getEnd(), newIsland, oldBridge.isDouble());
	}

	/**
	 * Generates a list of coordinates in a random direction from the
	 * <code>existingIsland</code> where a neighbor island to
	 * <code>existingIsland</code> can be placed.
	 * 
	 * <p>
	 * Direction is chosen randomly out of all possible directions but when no
	 * suitable coordinates can be found, direction is removed from possible
	 * directions and another direction is chosen out of all possible directions.
	 * This continues until either suitable coordinates are found or no possible
	 * direction remains in which case an empty list of coordinates is returned.
	 * </p>
	 * 
	 * <p>
	 * A neighbor island to the <code>existingIsland</code> can be placed at a pair
	 * of coordinates (x, y) if there would be no bridge crossing between the
	 * neighbor island and the <code>existingIsland</code> and if the (vertical and
	 * horizontal) distance of the neighbor island to other islands is greater 1.
	 * <b>Note that there could be a bridge crossing at (x, y) since the "between"
	 * excludes the coordinates of the islands themselves.</b>
	 * </p>
	 * 
	 * @param existingIsland
	 *            that is used for generating coordinates
	 * @return A list of coordinates on which a neighbor island to the existing
	 *         island can be placed
	 */
	private List<Coordinates> getValidNeighborIslandCoords(Island existingIsland) {
		List<Coordinates> validNeighborIslandCoords = Collections.EMPTY_LIST;
		Iterator<Direction> neighborDirectionsIter = getDirectsWithoutBridgesRandOrd(existingIsland).iterator();
		// check directions from existingIsland to find coords in which bridge to new
		// island can be built until no direction left or possible direction found
		while (neighborDirectionsIter.hasNext() && validNeighborIslandCoords.isEmpty()) {
			validNeighborIslandCoords = getValidNeighborIslandCoords(existingIsland, neighborDirectionsIter.next());
		}
		return validNeighborIslandCoords;
	}

	// returns a shuffled list of directions in which no bridge has been built from
	// the island
	private List<Direction> getDirectsWithoutBridgesRandOrd(Island island) {
		List<Direction> directionsWithoutBridges = new ArrayList<>();
		for (Direction direction : Direction.values()) {
			if (hashiModel.getBridge(island, direction) == null)
				directionsWithoutBridges.add(direction);
		}
		Collections.shuffle(directionsWithoutBridges);
		return directionsWithoutBridges;
	}

	/**
	 * Generates a list of coordinates in the <code>direction</code> from the
	 * <code>existingIsland</code> where a neighbor island to
	 * <code>existingIsland</code> can be placed.
	 * 
	 * A neighbor island to the <code>existingIsland</code> can be placed at a pair
	 * of coordinates (x, y) if there would be no bridge crossing between the
	 * neighbor island and the <code>existingIsland</code> and if the (vertical and
	 * horizontal) distance of the neighbor island to other islands is greater 1.
	 * <b>Note that there could be a bridge crossing at (x, y) since the "between"
	 * excludes the coordinates of the islands themselves.</b>
	 * 
	 * @param existingIsland
	 *            that is used for generating coordinates
	 * @param direction
	 *            in which coordinates are searched for
	 * @return A list of coordinates in <code>direction</code> on which a neighbor
	 *         island to the existing island can be placed
	 */
	private List<Coordinates> getValidNeighborIslandCoords(Island existingIsland, Direction direction) {
		List<Coordinates> validNeighborIslandCoords = new ArrayList<>();
		// add all coordinates that a neighbor island could be built on to list
		Coordinates coords = existingIsland.getCoords().getNextCoordsIn(direction);
		while (hashiModel.isValidFieldPosition(coords.x, coords.y)
				&& !hashiModel.islandAt(coords.x, coords.y)) {
			if (hashiModel.isValidIslandPosition(coords.x, coords.y)) {
				validNeighborIslandCoords.add(coords);
			}
			if (hashiModel.bridgeAt(coords.x, coords.y)) {
				break;
			}
			coords = coords.getNextCoordsIn(direction);
		}
		return validNeighborIslandCoords;
	}

}
