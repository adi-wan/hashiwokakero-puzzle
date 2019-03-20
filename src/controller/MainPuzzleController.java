package controller;

import common.Direction;
import gui.IPuzzleSituationView;
import gui.JMainFrame;
import model.HashiModel.Island;
import model.IPuzzleSituationModel;

/**
 * Main controller receiving and processing inputs from view.
 */
public class MainPuzzleController implements IInputListener {

	private IPuzzleSituationModel hashiModel;

	// Controller components
	private PuzzleStateChecker stateChecker;
	private BridgeAdder bridgeAdder;
	private PuzzleSolver solver;

	// View components
	private IPuzzleSituationView view;

	/**
	 * Constructs an instance of <code>MainPuzzleController</code> and, thereby,
	 * initializes the model by generating a new Hashiwokakeru puzzle randomly and
	 * all other controller components that need to be initialized, i.e. the
	 * <code>BridgeAdder</code>, the <code>PuzzleStateChecker</code>, the
	 * <code>PuzzleLoader</code>, the <code>PuzzleSaver</code>, the
	 * <code>PuzzleSolver</code>, as well as the view of the application.
	 */
	public MainPuzzleController() {
		hashiModel = new PuzzleGenerator().getPuzzleSituationModel(); // singleton design pattern could be used
		view = new JMainFrame(this, hashiModel);
		initControllerComponentsAndSetPuzzleState();
	}

	private void initControllerComponentsAndSetPuzzleState() {
		stateChecker = new PuzzleStateChecker(hashiModel);
		stateChecker.setPuzzleState();
		bridgeAdder = new BridgeAdder(hashiModel, stateChecker);
		solver = new PuzzleSolver(bridgeAdder, view);
	}

	@Override
	public void makeMove(Island island, Direction directionOfClick, boolean addBridge) {
		if (!solver.isAlive()) { // state design pattern could be used
			Island otherIsland = hashiModel.getNeighborIsland(island, directionOfClick); // design by contract: island != null
			if (island != null && otherIsland != null) { 
				if (addBridge) {
					hashiModel.addBridgeBetween(island, otherIsland);
				} else { // removeBridge
					hashiModel.removeBridgeBetween(island, otherIsland);
				}
			}
			stateChecker.setPuzzleState();
			view.refresh();
		}
	}

	@Override
	public boolean addNextBridge() {
		if (!solver.isAlive()) {
			boolean moveCouldBeMade = bridgeAdder.makeSureMove();
			stateChecker.setPuzzleState();
			view.refresh();
			return moveCouldBeMade;
		}
		return false; 
	}

	@Override
	public void startAndStopSolving() {
		if (!solver.isAlive()) {
			solver = new PuzzleSolver(bridgeAdder, view);
			solver.solvePuzzleWithPauses();
		} else {
			solver.interrupt();
		}
	}

	@Override
	public void restartPuzzle() {
		solver.interrupt();
		hashiModel.removeAllBridges();
		stateChecker.setPuzzleState();
		view.refresh();
	}

	@Override
	public void loadPuzzle(String filePath) {
		solver.interrupt();
		hashiModel = new PuzzleLoader().loadPuzzle(filePath);
		updateViewAndControllerComponents();
	}

	private void updateViewAndControllerComponents() {
		initControllerComponentsAndSetPuzzleState();
		view.setPuzzleSituationModelAndRefresh(hashiModel);
	}

	@Override
	public void savePuzzle(String fileName) {
		solver.interrupt();
		new PuzzleSaver().savePuzzle(fileName, hashiModel);
	}

	@Override
	public void generatePuzzle() {
		solver.interrupt();
		hashiModel = new PuzzleGenerator().getPuzzleSituationModel();
		updateViewAndControllerComponents();
	}

	@Override
	public void generatePuzzle(int width, int height) {
		solver.interrupt();
		hashiModel = new PuzzleGenerator().getPuzzleSituationModel(width, height);
		updateViewAndControllerComponents();
	}

	@Override
	public void generatePuzzle(int width, int height, int noOfIslands) {
		solver.interrupt();
		hashiModel = new PuzzleGenerator().getPuzzleSituationModel(width, height, noOfIslands);
		updateViewAndControllerComponents();
	}

}
