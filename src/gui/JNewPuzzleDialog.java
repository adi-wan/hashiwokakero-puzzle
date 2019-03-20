package gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import controller.IInputListener;

/**
 * 
 * A class extending <code>JDialog</code> representing the dialog to create a
 * new Hashiwokakeru puzzle.
 */
public class JNewPuzzleDialog extends JDialog {

	private IInputListener inputListener;
	private JPanel contentPane = new JPanel();
	private final int BORDER = 10;
	private JRadioButton automaticPuzzleButton;
	private JTextField widthTextField, heightTextField, islandsTextField;
	private final String DEFAULT_WIDTH = "10", DEFAULT_HEIGHT = "10", DEFAULT_ISLANDS = "25";
	private JCheckBox islandCheckBox = new JCheckBox("Inselzahl festlegen");
	private JLabel exceptionLabel = new JLabel();
	private boolean newPuzzleWasCreated;
	private static final Color EXCEPTION_LABEL_COLOR = new Color(255, 102, 102); // RED

	/**
	 * Constructs an instance of a <code>JNewPuzzleDialog</code>.
	 * 
	 * @param owner
	 *            the <code>Frame</code> instance the dialog belongs to
	 * @param inputListener
	 *            that is informed if a new puzzle is to be created
	 */
	public JNewPuzzleDialog(JFrame owner, IInputListener inputListener) {
		super(owner, true);
		this.inputListener = inputListener;
		setTitle("Neues Rätsel");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));
		addRadioButtonsToContentPane();
		addCustomOptionsToContentPane();
		addBottomButtonsToContentPane();
		addExceptionLabelToContentPane();
		setContentPane(contentPane);
		pack();
		setLocationRelativeTo(owner);
		setResizable(false);
		setVisible(true);
	}

	private void addExceptionLabelToContentPane() {
		exceptionLabel.setForeground(EXCEPTION_LABEL_COLOR);
		addComponentToContentPaneLeftAligned(exceptionLabel);
	}

	private void addBottomButtonsToContentPane() {
		JPanel buttonPane = new JPanel(new GridLayout(1, 2));
		JButton cancelButton = new JButton("Abbrechen");
		JButton okButton = new JButton("OK");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}

		});
		okButton.addActionListener(getOKButtonActionListener());
		buttonPane.add(cancelButton);
		buttonPane.add(okButton);
		addComponentToContentPaneLeftAligned(buttonPane);
	}

	private void addComponentToContentPaneLeftAligned(JComponent component) {
		component.setAlignmentX(LEFT_ALIGNMENT);
		contentPane.add(component);
	}

	private ActionListener getOKButtonActionListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (automaticPuzzleButton.isSelected()) {
						inputListener.generatePuzzle();
					} else {
						int width = Integer.parseInt(widthTextField.getText());
						int height = Integer.parseInt(heightTextField.getText());
						if (!islandCheckBox.isSelected()) {
							inputListener.generatePuzzle(width, height);
						} else {
							int noOfIslands = Integer.parseInt(islandsTextField.getText());
							inputListener.generatePuzzle(width, height, noOfIslands);
						}
					}
					newPuzzleWasCreated = true;
					dispose();
				} catch (IllegalArgumentException exception) {
					exceptionLabel
							.setText(StringConverter.convertToMultilineToFitPanel(contentPane, exception.getMessage()));
					pack();
				}
			}

		};
	}

	private void addCustomOptionsToContentPane() {
		JPanel customOptionsPane = new JPanel(new GridBagLayout()); // 2x2 Grid
		initializeTextFields();
		addActionListenerToIslandCheckBox();
		enableCustomOptions(false);
		JLabel widthLabel = new JLabel("Breite:");
		JLabel heightLabel = new JLabel("Höhe:");
		JLabel islandsLabel = new JLabel("Inseln:");
		GridBagConstraints c = new GridBagConstraints();
		addLabelsAndCheckboxToContentPane(customOptionsPane, c, widthLabel, heightLabel, islandsLabel);
		addTextFieldsToContentPane(customOptionsPane, c);
		addComponentToContentPaneLeftAligned(customOptionsPane);
	}

	private void addTextFieldsToContentPane(JPanel customOptionsPane, GridBagConstraints c) {
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		customOptionsPane.add(widthTextField, c);
		c.gridy = 1;
		customOptionsPane.add(heightTextField, c);
		c.gridy = 3;
		customOptionsPane.add(islandsTextField, c);
	}

	private void addLabelsAndCheckboxToContentPane(JPanel customOptionsPane, GridBagConstraints c, JLabel widthLabel,
			JLabel heightLabel, JLabel islandsLabel) {
		c.anchor = GridBagConstraints.LINE_START;
		c.gridx = 0;
		c.gridy = 0;
		customOptionsPane.add(widthLabel, c);
		c.gridy = 1;
		customOptionsPane.add(heightLabel, c);
		c.gridy = 2;
		c.gridwidth = 2;
		customOptionsPane.add(islandCheckBox, c);
		c.gridwidth = 1;
		c.gridy = 3;
		customOptionsPane.add(islandsLabel, c);
	}

	private void addActionListenerToIslandCheckBox() {
		islandCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				islandsTextField.setEnabled(islandCheckBox.isSelected());
			}

		});
	}

	private void initializeTextFields() {
		widthTextField = getNumberInputField(DEFAULT_WIDTH);
		heightTextField = getNumberInputField(DEFAULT_HEIGHT);
		islandsTextField = getNumberInputField(DEFAULT_ISLANDS);
	}

	private JTextField getNumberInputField(String defaultValue) {
		JTextField textField = new JFormattedTextField(new DecimalFormat("###"));
		textField.setText(defaultValue);
		textField.addFocusListener(getDefaultTextFocusAdapter(defaultValue));
		return textField;
	}

	/**
	 * Returns a focus adapter that sets the content of a JTextField to be "" if
	 * focus is gained and text field contains <code>defaultValue</code>. If focus
	 * is lost and content is "" the content is set to the
	 * <code>defaultValue</code>.
	 * 
	 * @param defaultValue
	 *            of text field.
	 * @return focus adapter for JTextField.
	 */
	private FocusAdapter getDefaultTextFocusAdapter(String defaultValue) {
		return new FocusAdapter() {
			private final String DEFAULT_TEXT = defaultValue;

			@Override
			public void focusGained(FocusEvent e) {
				JTextField source = (JTextField) e.getComponent();
				if (source.getText().equals(DEFAULT_TEXT)) {
					source.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				JTextField source = (JTextField) e.getComponent();
				if (source.getText().equals("")) {
					source.setText(DEFAULT_TEXT);
				}
			}
		};
	}

	private void addRadioButtonsToContentPane() {
		automaticPuzzleButton = new JRadioButton("Automatische Größe und Inselanzahl");
		automaticPuzzleButton.setSelected(true);
		JRadioButton customPuzzleButton = new JRadioButton("Größe und / oder Inselzahl selbst festlegen");
		ButtonGroup group = new ButtonGroup();
		group.add(automaticPuzzleButton);
		group.add(customPuzzleButton);
		setRadioButtonActionListener(automaticPuzzleButton, customPuzzleButton);
		addComponentToContentPaneLeftAligned(automaticPuzzleButton);
		addComponentToContentPaneLeftAligned(customPuzzleButton);
	}

	private void setRadioButtonActionListener(JRadioButton automaticPuzzleButton2, JRadioButton customPuzzleButton) {
		ActionListener radioButtonActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				enableCustomOptions(customPuzzleButton.isSelected());
			}
		};
		automaticPuzzleButton.addActionListener(radioButtonActionListener);
		customPuzzleButton.addActionListener(radioButtonActionListener);
	}

	private void enableCustomOptions(boolean isEnabled) {
		widthTextField.setEnabled(isEnabled);
		heightTextField.setEnabled(isEnabled);
		islandCheckBox.setEnabled(isEnabled);
		islandsTextField.setEnabled(islandCheckBox.isSelected() && isEnabled);
	}

	/**
	 * Return true if new Hashiwokakeru puzzle was created, i.e. the dialog was not
	 * cancelled.
	 * 
	 * @return true if new Hashiwokakeru puzzle was created
	 */
	public boolean newPuzzleWasCreated() {
		return newPuzzleWasCreated;
	}

}
