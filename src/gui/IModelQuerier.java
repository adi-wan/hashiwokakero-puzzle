package gui;

import model.IPuzzleSituationModel;

/**
 * 
 * The <code>IModelQuerier</code> interface should be implemented by any class whose
 * instances' models need to be updated if new puzzle is created or loaded.
 */
public interface IModelQuerier {
	/**
	 * 
	 * Updates the model queried to get the current state of the puzzle.
	 * 
	 * @param model
	 *            the new model
	 */
	public void setPuzzleSituationModel(IPuzzleSituationModel model);
}
