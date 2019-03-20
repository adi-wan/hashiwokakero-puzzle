package gui;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import controller.IInputListener;
import controller.PuzzleSolver.SolverState;
import model.IPuzzleSituationModel;

/**
 * 
 * A <code>JPanel</code> containing two buttons, one for adding a bridge and one
 * for solving the whole puzzle automatically. If no (further) bridge can be
 * added a dialog is shown that informs the user of the cause.
 */
public class JControlPanel extends JPanel implements IModelQuerier {

	private Container owner;
	private IInputListener inputListener; // to add bridge automatically and start and stop solving the puzzle
											// automatically
	private IPuzzleSituationModel model; // to get PuzzleState
	private JButton nextBridgeButton = new JButton("Nächste Brücke");
	private JButton solvePuzzleButton = new JButton("Automatisch lösen");

	/**
	 * 
	 * Constructs an instance of a <code>JControlPanel</code>.
	 * 
	 * @param owner
	 *            of the panel, i.e. the <code>Container</code> which contains the
	 *            <code>JControlPanel</code>
	 * @param inputListener
	 *            which is informed when clicking a button
	 * @param model
	 *            of the puzzle to query to get the current state of the puzzle to
	 *            determine the cause of why no (further) bridge could be added
	 * 
	 */
	public JControlPanel(Container owner, IInputListener inputListener, IPuzzleSituationModel model) {
		this.owner = owner;
		this.inputListener = inputListener;
		this.model = model;
		GridLayout gridLayout = new GridLayout(1, 2);
		gridLayout.setHgap(20);
		setLayout(gridLayout);
		nextBridgeButton.addActionListener(getNextBridgeActionListener());
		solvePuzzleButton.addActionListener(getSolvePuzzleActionListener());
		add(nextBridgeButton);
		add(solvePuzzleButton);
	}

	private ActionListener getNextBridgeActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean bridgeCouldBeSet = inputListener.addNextBridge();
				if (!bridgeCouldBeSet) {
					JOptionPane.showMessageDialog(owner, getDialogMessage(), getDialogTitle(),
							JOptionPane.PLAIN_MESSAGE);
				}
			}

		};
	}

	private ActionListener getSolvePuzzleActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				inputListener.startAndStopSolving();
			}

		};
	}

	/**
	 * 
	 * @return message (string) to be displayed in dialog if no more bridges can be
	 *         added.
	 */
	private Object getDialogMessage() {
		String message;
		switch (model.getPuzzleState()) {
		case SOLVED:
			message = "Herzlichen Glückwunsch, das Rätsel ist gelöst! Es kann keine Brücke mehr hinzugefügt werden.";
			break;
		case NOT_YET_SOLVED:
			message = "Es konnte keine Brücke gefunden werden, die hinzugefügt werden kann, "
					+ "ohne einen Regelverstoß zu provozieren oder gar zu riskieren. Es liegt nun an Ihnen, eine solche Brücke zu finden.";
			break;
		case CONTAINS_ERROR:
			message = "Min. eine Insel (rot) hat zu viele Brücken. Es kann solange keine Brücke hinzugefügt werden, wie eine Insel zu viele Brücken hat. Entfernen Sie die Brücke(n) oder starten Sie das Rätsel neu, um das Rätsel lösen zu können.";
			break;
		default: // UNSOLVABLE
			message = "Das Rätsel ist nicht mehr lösbar. Es kann keine Brücke hinzugefügt werden, ohne einen Regelverstoß zu provozieren oder gar zu riskieren. Entfernen Sie Brücken oder starten Sie das Rätsel neu, um das Rätsel lösen zu können.";
			break;
		}
		return StringConverter.convertToMultilineToFitPanel(this, message);
	}

	/**
	 * 
	 * @return title of dialog to be displayed if no more bridges can be added.
	 */
	private String getDialogTitle() {
		String title;
		switch (model.getPuzzleState()) {
		case SOLVED:
			title = "Das Rätsel ist gelöst!";
			break;
		case NOT_YET_SOLVED:
			title = "Keine konfliktfreie Brücke gefunden!";
			break;
		case CONTAINS_ERROR:
			title = "Das Rätsel enthält einen Fehler!";
			break;
		default: // UNSOLVABLE
			title = "Das Rätsel ist nicht mehr lösbar!";
		}
		return title;
	}

	/**
	 * 
	 * Sets the states of the buttons depending on if the puzzle is currently solved
	 * automatically or not. If the puzzle is currently solved, the button to add a
	 * bridge is disabled and the button to solve the puzzle becomes the button to
	 * stop solving the puzzle, otherwise the buttons are reset to their default
	 * state.
	 * 
	 * @param isSolving
	 *            if true, puzzle is currently solved, otherwise solving has
	 *            stopped or not yet started
	 */
	public void setSolverButtonSolvingState(boolean isSolving) {
		if (isSolving) {
			solvePuzzleButton.setText("Stop");
			nextBridgeButton.setEnabled(false);
		} else {
			solvePuzzleButton.setText("Automatisch lösen");
			nextBridgeButton.setEnabled(true);
		}
	}

	@Override
	public void setPuzzleSituationModel(IPuzzleSituationModel model) {
		this.model = model;
	}

	/**
	 * Sets state of solver to change state of buttons of panel and show dialog if
	 * useful.
	 * 
	 * @param solverState.
	 */
	public void setSolverState(SolverState solverState) {
		switch (solverState) {
		case FINISHED:
			JOptionPane.showMessageDialog(owner, getDialogMessage(), getDialogTitle(), JOptionPane.PLAIN_MESSAGE);
			setSolverButtonSolvingState(false);
			break;
		case RUNNING:
			setSolverButtonSolvingState(true);
			break;
		case INTERRUPTED:
			setSolverButtonSolvingState(false);
			break;
		default:
			; // UNCHANGED
		}
	}
}
