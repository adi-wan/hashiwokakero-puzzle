package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

import controller.IInputListener;
import model.IPuzzleSituationModel;

/**
 * An extension of <code>JMenuBar</code> representing the menu and dialogs of
 * the Hashiwokakeru puzzle application for restarting and saving the currently
 * shown puzzle, creating and loading a new puzzle as well as quitting the
 * application.
 */
public class JPuzzleMenuBar extends JMenuBar implements IModelQuerier {

	private JFrame owner;
	private IInputListener inputListener;
	private IPuzzleSituationModel model;
	private File currentDirectory;
	private String filePath;
	private ActionListener saveAsActionListener;

	/**
	 * Constructs an instance of a <code>JPuzzleMenuBar</code> including all the
	 * dialogs that are (possibly) opened by selecting a menu item.
	 * 
	 * @param owner         the <code>Frame</code> instance the menu belongs to
	 * @param inputListener that is informed if a menu item is selected
	 * @param hashiModel    that this menu uses to name the .bgs file that the
	 *                      instance of a Hashiwokakeru puzzle currently shown is
	 *                      saved to (if not loaded from a file or saved before)
	 */
	public JPuzzleMenuBar(JFrame owner, IInputListener inputListener, IPuzzleSituationModel hashiModel) {
		super();
		this.owner = owner;
		this.inputListener = inputListener;
		this.model = hashiModel;
		JMenu menu = new JMenu("Datei");
		add(menu);
		menu.add(getNewPuzzleMenuItem());
		menu.add(getRestartPuzzleMenuItem());
		menu.add(getLoadPuzzleMenuItem());
		setSaveAsActionListener();
		menu.add(getSavePuzzleMenuItem());
		menu.add(getSavePuzzleAsMenuItem());
		menu.add(getExitMenuItem());
	}

	private JMenuItem getNewPuzzleMenuItem() {
		JMenuItem menuItem = new JMenuItem("Neues Rätsel");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean newPuzzleWasCreated = new JNewPuzzleDialog(owner, inputListener).newPuzzleWasCreated();
				filePath = newPuzzleWasCreated ? null : filePath;
			}

		});
		return menuItem;
	}

	private JMenuItem getRestartPuzzleMenuItem() {
		JMenuItem menuItem = new JMenuItem("Rätsel neu starten");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				inputListener.restartPuzzle();
			}

		});
		return menuItem;
	}
	
	private JMenuItem getLoadPuzzleMenuItem() {
		JMenuItem menuItem = new JMenuItem("Rätsel laden");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(currentDirectory);
				fc.setFileFilter(new FileNameExtensionFilter("BGS-Dateien (*.bgs)", "bgs"));
				int state = fc.showOpenDialog(owner);
				if (state == JFileChooser.APPROVE_OPTION) {
					currentDirectory = fc.getCurrentDirectory();
					File file = fc.getSelectedFile();
					filePath = file.getPath();
					inputListener.loadPuzzle(filePath);
				}
			}

		});
		return menuItem;
	}
	
	private JMenuItem getSavePuzzleMenuItem() {
		JMenuItem menuItem = new JMenuItem("Rätsel speichern");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (filePath != null) {
					inputListener.savePuzzle(filePath);
				} else {
					saveAsActionListener.actionPerformed(e);
				}
			}

		});
		return menuItem;
	}

	private JMenuItem getSavePuzzleAsMenuItem() {
		JMenuItem menuItem = new JMenuItem("Rätsel speichern unter");
		menuItem.addActionListener(saveAsActionListener);
		return menuItem;
	}

	private void setSaveAsActionListener() {
		saveAsActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(currentDirectory);
				setDefaultFilePath(fc);
				fc.setFileFilter(new FileNameExtensionFilter("BGS-Dateien (*.bgs)", "bgs"));
				int state = fc.showSaveDialog(owner);
				if (state == JFileChooser.APPROVE_OPTION) {
					currentDirectory = fc.getCurrentDirectory();
					File file = fc.getSelectedFile();
					filePath = file.getPath();
					inputListener.savePuzzle(filePath);
				}
			}

			private void setDefaultFilePath(JFileChooser fc) {
				if (filePath == null) {
					String fileName = "HashiPuzzle_" + model.getWidth() + "x" + model.getHeight() + ".bgs";
					fc.setSelectedFile(new File(currentDirectory, fileName));
				} else {
					fc.setSelectedFile(new File(filePath));
				}
			}

		};
	}

	private JMenuItem getExitMenuItem() {
		JMenuItem menuItem = new JMenuItem("Beenden");
		menuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}

		});
		return menuItem;
	}

	@Override
	public void setPuzzleSituationModel(IPuzzleSituationModel hashiModel) {
		this.model = hashiModel;
	}

}
