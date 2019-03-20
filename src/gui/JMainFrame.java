package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import controller.IInputListener;
import controller.PuzzleSolver.SolverState;
import model.IPuzzleSituationModel;

/**
 * 
 * A <code>JFrame</code> that contains and manages all the elements of the view
 * of the Hashiwokakeru application (except dialogs).
 * 
 * <p>
 * The elements: A menuBar for (re-)starting, loading, saving a Hashiwokakeru
 * puzzle as well as quitting the application, a panel which contains a
 * graphical representation of the Hashiwokakeru puzzle and serves as an input
 * device for adding and removing bridges, a checkbox to change the numbers (of
 * bridges) painted inside the islands of the (representation of the)
 * Hashiwokakeru puzzle, a control panel to automatically set one bridge or
 * solve the whole puzzle and a state label showing the current state of the
 * puzzle.
 * </p>
 * 
 * <p>
 * <code>JMainFrame</code> implements <code>IPuzzleSituationView</code>, thereby
 * acting as the main interface for the controller to refresh the view and/or
 * change the model. Other elements of the view, e.g. the panel containing (the
 * graphical representation of) the Hashiwokakeru puzzle, are updated by the
 * <code>JMainFrame</code>.
 * </p>
 * 
 * @author Adrian Stritzinger
 *
 */
public class JMainFrame extends JFrame implements IPuzzleSituationView {

	private static final Border COMPONENT_PADDING = BorderFactory.createEmptyBorder(5, 5, 5, 5);

	private IInputListener inputListener;
	private IPuzzleSituationModel hashiModel;
	private JFieldPanel fieldPanel;
	private JControlPanel controlPanel;
	private JLabel puzzleStateLabel;

	/**
	 * Constructs an instance of a <code>JMainFrame</code> and shows it in the
	 * center of the screen.
	 * 
	 * @param inputListener of view.
	 * @param hashiModel to load data from.
	 */
	public JMainFrame(IInputListener inputListener, IPuzzleSituationModel hashiModel) {
		super();
		this.inputListener = inputListener;
		this.hashiModel = hashiModel;
		fieldPanel = new JFieldPanel(hashiModel, inputListener);
		controlPanel = new JControlPanel(this, inputListener, hashiModel);
		puzzleStateLabel = new JLabel(getStateText());
		setPuzzleStateLabel();
		setTitle("Adrian Stritzinger / M-Nr. 8268720");
		setJMenuBar(new JPuzzleMenuBar(this, inputListener, hashiModel));
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		populateContentPane();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void populateContentPane() {
		addFieldPanelWithBorder();
		addIslandStringCheckBox();
		addToContentPane(controlPanel);
		addToContentPane(puzzleStateLabel);
		pack();
	}

	private void addFieldPanelWithBorder() {
		JPanel surroundingPanel = new JPanel(); // to add border to fieldPanel
		surroundingPanel.setLayout(new BorderLayout()); // necessary so that fieldPanel resizes correctly
		Border blackLineBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
		Border outsideBorder = BorderFactory.createCompoundBorder(COMPONENT_PADDING, blackLineBorder);
		Border border = BorderFactory.createCompoundBorder(outsideBorder, COMPONENT_PADDING);
		surroundingPanel.setBorder(border);
		surroundingPanel.add(fieldPanel);
		surroundingPanel.setAlignmentX(LEFT_ALIGNMENT);
		getContentPane().add(surroundingPanel);
	}

	private void addIslandStringCheckBox() {
		JCheckBox islandStringCheckBox = new JCheckBox("Anzahl fehlender Brücken anzeigen");
		islandStringCheckBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					fieldPanel.setIslandString(true);
				} else {
					fieldPanel.setIslandString(false);
				}
				refresh();
			}

		});
		addToContentPane(islandStringCheckBox);
	}

	private void setPuzzleStateLabel() {
		puzzleStateLabel.setForeground(getStateTextColor());
		puzzleStateLabel.setText(getStateText());
	}

	private Color getStateTextColor() {
		switch (hashiModel.getPuzzleState()) {
		case CONTAINS_ERROR:
			return JFieldPanel.COLOR_INVALID_ISLAND;
		case SOLVED:
			return JFieldPanel.COLOR_ISLAND_WITH_ALL_BRIDGES;
		case UNSOLVABLE:
			return JFieldPanel.COLOR_INVALID_ISLAND;
		default:
			return Color.BLACK;
		}
	}

	private String getStateText() {
		switch (hashiModel.getPuzzleState()) {
		case NOT_YET_SOLVED:
			return "noch nicht gelöst";
		case CONTAINS_ERROR:
			return "enthält einen Fehler";
		case UNSOLVABLE:
			return "nicht mehr lösbar";
		case SOLVED:
			return "gelöst";
		default:
			return "unbekannter Status";
		}
	}

	private void addToContentPane(JComponent component) {
		component.setBorder(COMPONENT_PADDING);
		component.setAlignmentX(LEFT_ALIGNMENT);
		getContentPane().add(component);
	}

	@Override
	public void refresh() {
		setPuzzleStateLabel();
		SwingUtilities.invokeLater(new Runnable() { // not always edt

			@Override
			public void run() {
				repaint();

			}
		});
	}

	@Override
	public void setPuzzleSituationModelAndRefresh(IPuzzleSituationModel hashiModel) {
		this.hashiModel = hashiModel;
		((JPuzzleMenuBar) getJMenuBar()).setPuzzleSituationModel(hashiModel);
		fieldPanel.setPuzzleSituationModel(hashiModel);
		controlPanel.setPuzzleSituationModel(hashiModel);
		refresh();
	}

	@Override
	public void setSolverStateAndRefresh(SolverState solverState) {
		controlPanel.setSolverState(solverState);
		refresh();
	}

}
