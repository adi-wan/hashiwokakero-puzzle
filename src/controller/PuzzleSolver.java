package controller;

import gui.IPuzzleSituationView;

/**
 * This class is an extension of <code>Thread</code> of which an instance (when
 * the {@link #run() run} method is called) automatically solves a Hashiwokakeru
 * puzzle by adding bridges until it is either interrupted or no more bridge can
 * be added. For a bridge to be added, it certainly must be added based on the
 * current state of the puzzle (see {@link BridgeAdder#makeSureMove()
 * makeSureMove()} method).
 */
public class PuzzleSolver extends Thread {

	private BridgeAdder bridgeAdder;
	private IPuzzleSituationView view;

	/**
	 * Constructs an instance of a <code>PuzzleSolver</code>.
	 * 
	 * @param bridgeAdder
	 *            used to add bridges to the puzzle
	 * @param view
	 *            that is refreshed if bridge has been added or state of the
	 *            <code>this</code> solver changes
	 */
	public PuzzleSolver(BridgeAdder bridgeAdder, IPuzzleSituationView view) {
		this.bridgeAdder = bridgeAdder;
		this.view = view;
	}

	/**
	 * Runs the <code>this</code> thread. Bridges are added until no more bridges
	 * can be added (see {@link BridgeAdder#makeSureMove() makeSureMove()} method)
	 * or <code>this</code> thread is interrupted. Each time after a bridge is added
	 * a pause is made and the view is refreshed. Each time the state of
	 * <code>this</code> thread changes the view is refreshed.
	 */
	@Override
	public void run() {
		view.setSolverStateAndRefresh(SolverState.RUNNING);
		try {
			while (bridgeAdder.makeSureMove()) {
				view.refresh();
				Thread.sleep(2000);
			}
			view.setSolverStateAndRefresh(SolverState.FINISHED);
		} catch (InterruptedException e) {
			this.interrupt(); // Necessary to get correct state of solver
			view.setSolverStateAndRefresh(SolverState.INTERRUPTED);
		}
	}

	/**
	 * Starts <code>this</code> thread.
	 */
	public void solvePuzzleWithPauses() {
		this.start();
	}

	/**
	 * <code>Enum</code> representing the states of <code>this</code> thread.
	 * 
	 
	 *
	 */
	public static enum SolverState {
		/**
		 * <code>PuzzleSolver.this</code> is currently solving the puzzle
		 */
		RUNNING,

		/**
		 * <code>PuzzleSolver.this</code> was interrupted
		 */
		INTERRUPTED,

		/**
		 * <code>PuzzleSolver.this</code> has finished because no more brige can be added
		 */
		FINISHED
	}
}
