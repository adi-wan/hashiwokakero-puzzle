package controller;

import java.util.ArrayList;	

import java.util.Iterator;
import java.util.List;
import java.time.*;

import common.Direction;
import model.HashiModel.Bridge;
import model.HashiModel.Island;
import model.IPuzzleSituationModel;

//TODO: Get sure moves, but do not make them here. Instead return move to main controller und make it in the main controller.

/**
 * An instance of the class <code>BridgeAdder</code> can add a bridge to a
 * Hashiwokakeru puzzle if this bridge must certainly be added to the puzzle
 * based on the puzzle's current state (see {@link #makeSureMove() makeSureMove}
 * method).
 * 
 * @author Adrian Stritzinger
 *
 */
public class BridgeAdder {

	private IPuzzleSituationModel hashiModel;
	private PuzzleStateChecker stateChecker;
	private List<Island> neighbors;
	private List<Island> neighborsToBuildBridge;
	private List<Island> neighborsToBuildDoubleBridge;
	private List<Island> neighborsRequiringMoreThanOneBridge;
	private List<Island> neighborsRequiringMoreThanTwoBridges;

	/**
	 * Constructs an instance of a <code>BridgeAdder</code>.
	 * 
	 * @param hashiModel
	 *            which holds the puzzle to which a bridge is to be added as well as
	 *            the puzzles state
	 * @param stateChecker
	 *            to update the state of the puzzle after a bridge has been added
	 */
	public BridgeAdder(IPuzzleSituationModel hashiModel, PuzzleStateChecker stateChecker) {
		this.hashiModel = hashiModel;
		this.stateChecker = stateChecker;
	}

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
	 * <li>the puzzle is unsolvable and this was recognized by the application,</li>
	 * <li>the puzzle is invalid or already solved.
	 * </ul>
	 * </p>
	 * 
	 * @return true if a bridge was added, false otherwise
	 */
	public boolean makeSureMove() {
		if (!hashiModel.isNotYetSolved()) {
			return false;
		}
		return makeSureMoveByUsingRules(); // TODO: || trialAndError(); // here an extension could be added to solve puzzle
																// differently
	}

	/*
	 * Goal: Solving every puzzle, i.e., adding a bridge in every case that a bridge
	 * can be added to get to the solution of the puzzle.
	 * 
	 * Solution idea 1: If no sure move can be made, make an unsure move (here I can
	 * use some heuristics to choose the bridge to add), solve the puzzle with the
	 * solver without refreshing the view until a solution is found or the puzzle
	 * becomes unsolvable. If solution is found, add first bridge added to the
	 * puzzle (unsure bridge). If puzzle becomes unsolvable, remove last unsure
	 * bridge added and make another unsure move that has not been made before. Stop
	 * if a certain time period has passed (all bridges added must be removed).
	 * 
	 * 
	 */
	
	private static int NO_OF_TRIALS = 0;
	
	/**
	 * 
	 * Finds a sure move by trial and error and makes it.
	 * 
	 * @return true if sure move could be made.
	 */
	private boolean trialAndError() {
		NO_OF_TRIALS++;
		if (NO_OF_TRIALS <= 100) {
			Iterator<Island> islands = hashiModel.getIslands().iterator();
			while (islands.hasNext()) { // TODO: Time limit
				Island island = islands.next();
				if (island.getNoOfBridgesMissing() > 0) {
					Iterator<Island> neighbors = hashiModel.getNeighbourIslands(island).iterator();
					while (neighbors.hasNext()) {
						Island neighbor = neighbors.next();
						if (neighbor.getNoOfBridgesMissing() > 0) {
							if (isNextMove(island, neighbor)) {
								hashiModel.addBridgeBetween(island, neighbor);
								stateChecker.setPuzzleState();
								NO_OF_TRIALS--;
								return true;
							}
						}
					}
				}
			}
		}
		NO_OF_TRIALS--;
		return false;
	}

	private boolean isNextMove(Island island, Island neighbor) {
		List<Bridge> unsureBridges = new ArrayList<>();
		addUnsureBridgeAndResetPuzzleState(island, neighbor, unsureBridges);
		while (makeSureMove()) {
			unsureBridges.add(hashiModel.getLastInsertedBridge());
		}
		boolean isNextMove = hashiModel.isSolved();
		removeBridgesFromModel(unsureBridges);
		return isNextMove;
	}

	private void removeBridgesFromModel(List<Bridge> unsureBridges) {
		Iterator<Bridge> unsureBridgesIterator = unsureBridges.iterator();
		while (unsureBridgesIterator.hasNext()) {
			hashiModel.removeBridge(unsureBridgesIterator.next(), false);
		}
	}

	private void addUnsureBridgeAndResetPuzzleState(Island island, Island neighbor, List<Bridge> unsureBridges) {
		hashiModel.addBridgeBetween(island, neighbor);
		stateChecker.setPuzzleState();
		unsureBridges.add(hashiModel.getLastInsertedBridge());
	}

	/**
	 * Goes through all islands of <code>hashiModel</code> iteratively trying to
	 * find a bridge that must be added to an island (and its neighbor island).
	 * 
	 * @return true if sure move could be made, otherwise false.
	 */
	private boolean makeSureMoveByUsingRules() {
		boolean sureMoveWasMade = false;
		Iterator<Island> islandIter = hashiModel.getIslands().iterator();
		while (islandIter.hasNext() && !sureMoveWasMade) {
			sureMoveWasMade = addSureBridgeToIsland(islandIter.next());
		}
		return sureMoveWasMade;
	}

	/**
	 * @param island
	 *            to be checked if (sure) bridge can be added.
	 * @return true if bridge was added, otherwise false.
	 */
	private boolean addSureBridgeToIsland(Island island) {
		if (island.getNoOfBridgesMissing() != 0) {
			initListsOfNeighbors(island);
			Island neighbor = getNeighborToWhichBridgeMustBeBuilt(island);
			if (neighbor != null) {
				hashiModel.addBridgeBetween(island, neighbor);
				stateChecker.setPuzzleState();
				return true;
			}
		}
		return false;
	}

	/**
	 * Initialize lists of neighbor islands of <code>island</code> that are needed
	 * in algorithms to determine if a sure bridge can be built from
	 * <code>island</code>.
	 * 
	 * @param island
	 */
	private void initListsOfNeighbors(Island island) {
		neighbors = new ArrayList<>();
		neighborsToBuildBridge = new ArrayList<>();
		neighborsToBuildDoubleBridge = new ArrayList<>();
		neighborsRequiringMoreThanOneBridge = new ArrayList<>();
		neighborsRequiringMoreThanTwoBridges = new ArrayList<>();
		addNeighborsToLists(island);
	}

	/**
	 * Initialize lists of neighbor islands of <code>island</code> that are needed
	 * in algorithms to determine if a sure bridge can be built from
	 * <code>island</code>.
	 * 
	 * @param island
	 */
	private void addNeighborsToLists(Island island) {
		for (Direction direction : Direction.values()) {
			Island neighbor = hashiModel.getNeighborIsland(island, direction);
			if (neighbor != null) {
				neighbors.add(neighbor);
				int noOfBuildableBridges = getNoOfBuildableBridges(island, neighbor);
				if (noOfBuildableBridges > 0) {
					neighborsToBuildBridge.add(neighbor);
					if (noOfBuildableBridges > 1) {
						neighborsToBuildDoubleBridge.add(neighbor);
					}
					if (neighbor.getNoOfBridgesRequired() > 1) {
						neighborsRequiringMoreThanOneBridge.add(neighbor);
					}
					if (neighbor.getNoOfBridgesRequired() > 2) {
						neighborsRequiringMoreThanTwoBridges.add(neighbor);
					}
				}
			}
		}
	}

	private int getNoOfBuildableBridges(Island island, Island neighbor) {
		Bridge bridgeToNeighbor = hashiModel.getBridgeBetween(island, neighbor);
		if (bridgeToNeighbor == null && neighbor.getNoOfBridgesMissing() > 1) {
			return 2;
		}
		if ((bridgeToNeighbor != null && !bridgeToNeighbor.isDouble() || bridgeToNeighbor == null)
				&& neighbor.getNoOfBridgesMissing() > 0) {
			return 1;
		}
		return 0;
	}

	/**
	 * Checks if there is a neighbor of <code>island</code> to which a bridge must
	 * be built from <code>island</code> by using rules described in assignment.
	 * <strong>Important:</strong> Returns null if no such neighbor can be found.
	 * 
	 * @param island
	 *            to be checked for a neighbor to which a bridge must be built.
	 * @return neighbor (island) to which bridge must be built if existing,
	 *         otherwise null.
	 */
	private Island getNeighborToWhichBridgeMustBeBuilt(Island island) {
		int noOfBuildableBridges = neighborsToBuildBridge.size() + neighborsToBuildDoubleBridge.size();
		if (2 * neighbors.size() <= island.getNoOfBridgesRequired()
				|| island.getNoOfBridgesMissing() == noOfBuildableBridges) {
			return neighborsToBuildBridge.get(0); // contains one since island is missing bridge(s)
		}
		if ((2 * neighbors.size() - 1 <= island.getNoOfBridgesRequired()
				|| island.getNoOfBridgesMissing() == noOfBuildableBridges - 1)
				&& !neighborsToBuildDoubleBridge.isEmpty()) {
			return neighborsToBuildDoubleBridge.get(0);
		}
		if (neighborsRequiringMoreThanOneBridge.size() == 1 && (island.getNoOfBridgesRequired() == 1
				|| island.getNoOfBridgesRequired() == 2 && island.getNoOfBridgesMissing() == 2)) {
			return neighborsRequiringMoreThanOneBridge.get(0);
		}
		if (island.getNoOfBridgesRequired() == 2 && island.getNoOfBridgesMissing() == 2
				&& neighborsToBuildBridge.size() == 2 && neighborsRequiringMoreThanTwoBridges.size() == 1) {
			return neighborsRequiringMoreThanTwoBridges.get(0);
		}
		return null;
	}

}
