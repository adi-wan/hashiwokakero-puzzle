package controller;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import model.PuzzleState;
import common.Direction;
import model.HashiModel.Bridge;
import model.HashiModel.Island;
import model.IPuzzleSituationModel;

/**
 * 
 * Instances of the <code>PuzzleStateChecker</code> class are used to determine
 * the current <code>PuzzleState</code> of an instance of a Hashiwokakeru puzzle
 * and updating it.
 * 
 * @author Adrian Stritzinger
 *
 */
public class PuzzleStateChecker {

	private IPuzzleSituationModel hashiModel;
	private Island[] allIslands;
	private boolean[] checkedForIsolation;
	// checkedForIsolation[i] true if it has been checked that island islands[i]
	// belongs to isolated component, i. e. component to which no bridge can be
	// built (islands of component have all bridges)

	/**
	 * Constructs a <code>PuzzleStateChecker</code> for determining and setting the
	 * state of the <code>hashiModel</code>.
	 * 
	 * @param hashiModel
	 *            whose state is to be determined and set
	 */
	public PuzzleStateChecker(IPuzzleSituationModel hashiModel) {
		this.hashiModel = hashiModel;
		this.allIslands = new Island[hashiModel.getNoOfIslands()];
		hashiModel.getIslands().toArray(allIslands);
		this.checkedForIsolation = new boolean[allIslands.length];
	}

	/**
	 * Sets the state of the model of which <code>this</code> instance holds a
	 * reference to.
	 */
	public void setPuzzleState() {
		hashiModel.setPuzzleState(getPuzzleState());
	}

	private PuzzleState getPuzzleState() {
		resetIsolationCheck();
		for (int islandIndex = 0; islandIndex < allIslands.length; islandIndex++) {
			if (allIslands[islandIndex].getNoOfBridgesMissing() < 0) {
				return PuzzleState.CONTAINS_ERROR;
			}
			if (allIslands[islandIndex].getNoOfBridgesMissing() == 0) {
				int noOfIslandsOfIsolatedGraph = checkForIsolationAndReturnNoOfIslandsOfIsolatedComponent(islandIndex);
				if (noOfIslandsOfIsolatedGraph > 0) {
					if (noOfIslandsOfIsolatedGraph == hashiModel.getNoOfIslands()) {
						return PuzzleState.SOLVED;
					} else { // not all islands can be connected since there is isolation
						return PuzzleState.UNSOLVABLE;
					}
				}
			} else if (!reqiredBridgesCanBeBuilt(allIslands[islandIndex])) {
				return PuzzleState.UNSOLVABLE;
			}
		}
		return PuzzleState.NOT_YET_SOLVED;
	}

	private void resetIsolationCheck() {
		for (int i = 0; i < checkedForIsolation.length; i++) {
			checkedForIsolation[i] = false;
		}
	}

	/**
	 * Checks if it was already checked if island in islands array at islandIndex
	 * belongs to isolated component and returns the number of islands belonging to
	 * this isolated component. If the island at islandIndex does not belong to an
	 * isolated component, 0 is returned, but nevertheless all islands of component
	 * are marked as checked for isolation so that method returns quicker in the
	 * future.
	 * 
	 * 
	 * @param islandIndex
	 *            of island in islands array to be checked
	 * @return number of islands belonging to isolated component
	 */
	private int checkForIsolationAndReturnNoOfIslandsOfIsolatedComponent(int islandIndex) {
		if (checkedForIsolation[islandIndex])
			return 0; // else breadthFirstSearch (BFS) starting from island islands[islandIndex]
		LinkedList<Island> queue = new LinkedList<>();
		queue.add(allIslands[islandIndex]);
		boolean islandMissingBridgesFound = false;
		int noOfIslandsOfIsolatedComponent = 0;
		while (!queue.isEmpty()) {
			Island front = queue.poll();
			int frontIndex = Arrays.binarySearch(allIslands, front);
			if (!checkedForIsolation[frontIndex]) { // termination condition
				if (front.getNoOfBridgesMissing() == 0) {
					noOfIslandsOfIsolatedComponent++;
				} else { // component, that island at islandIndex belongs to, is not isolated
					islandMissingBridgesFound = true;
				}
				checkedForIsolation[frontIndex] = true; // necessary for termination and quick returns in future
				addConnectedNeighborsToQueue(queue, front); // get rest of component
			}
		}
		return islandMissingBridgesFound ? 0 : noOfIslandsOfIsolatedComponent;
	}

	private void addConnectedNeighborsToQueue(LinkedList<Island> queue, Island island) {
		for (Direction direction : Direction.values()) {
			Bridge bridge = hashiModel.getBridge(island, direction);
			if (bridge != null) {
				queue.add(bridge.getOtherEnd(island));
			}
		}
	}

	/**
	 * Returns true if it is (still) possible to build all the bridges the
	 * <code>island</code> requires, otherwise false. The <code>island</code> is
	 * missing at least one bridge.
	 * 
	 * @param island
	 *            that is to be checked.
	 * @return true if it is (still) possible to build all the bridges the
	 *         <code>island</code> requires, otherwise false.
	 */
	private boolean reqiredBridgesCanBeBuilt(Island island) {
		int noOfBuildableBridges = 0;
		for (Direction direction : Direction.values()) {
			Island neighbor = hashiModel.getNeighborIsland(island, direction);
			if (neighbor != null && neighbor.getNoOfBridgesMissing() > 0) {
				Bridge bridge = hashiModel.getBridge(island, direction);
				if (neighbor.getNoOfBridgesMissing() > 1 && bridge == null) { 
					noOfBuildableBridges += 2;
				} else if (bridge == null || !bridge.isDouble()) { // on bridge can still be built in direction
					noOfBuildableBridges++;
				}
			}
		}
		return island.getNoOfBridgesMissing() <= noOfBuildableBridges;
	}

}
