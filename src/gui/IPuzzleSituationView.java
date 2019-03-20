package gui;

import controller.PuzzleSolver.SolverState;
import model.IPuzzleSituationModel;

/**
 * Classes that implement the <code>IPuzzleSituationView</code> interface
 * represent the parts of the view of the Hashiwokakeru puzzle application that
 * need to be refreshed or that are responsible for refreshing the components of
 * the view that need to be refreshed.
 */
public interface IPuzzleSituationView {
	/**
	 * Refreshes, i.e. repaints, <code>this</code> component or components managed
	 * by <code>this</code> component that need to be refreshed.
	 */
	public void refresh();

	/**
	 * Sets the <code>hashiModel</code> as the new model <code>this</code>
	 * components or components managed by <code>this</code> component query to
	 * paint themselves and refreshes, i.e. repaints, the component or components
	 * managed by <code>this</code> component that need to be refreshed.
	 * 
	 * @param hashiModel
	 *            new <code>IPuzzleSituationModel</code> representing a new
	 *            (situation of the current) Hashiwokakeru puzzle
	 */
	public void setPuzzleSituationModelAndRefresh(IPuzzleSituationModel hashiModel);

	/**
	 * Informs the instance of the implementing class of the current state of the
	 * <code>PuzzleSolver</code> instance to open dialogs, enabling or disabling
	 * certain elements of the view if necessary. Refreshes, i.e. repaints, the component or
	 * components managed by <code>this</code> component that need to be refreshed.
	 * 
	 * @param solverState of the current <code>PuzzleSolver</code> instance of the controller
	 */
	public void setSolverStateAndRefresh(SolverState solverState);
}
