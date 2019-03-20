package controller;

import java.io.BufferedWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.List;

import common.Direction;
import model.HashiModel.Bridge;
import model.HashiModel.Island;
import model.IPuzzleSituationModel;

/**
 * Class for saving the current state of a Hashiwokakero puzzle into a .bgs
 * file.
 * 
 * @author Adrian Stritzinger
 *
 */
public class PuzzleSaver {

	private String filePath;
	private StringBuilder fileStringBuilder = new StringBuilder();
	private IPuzzleSituationModel hashiModel;
	private List<Island> allIslands;

	/**
	 * Saves the current state of the puzzle contained in the
	 * <code>hashiModel</code> as .bgs file at <code>filePath</code>.
	 * 
	 * @param filePath
	 *            which .bgs file containing current state of puzzle is saved at
	 * @param hashiModel
	 *            containing the current state of the puzzle
	 */
	public void savePuzzle(String filePath, IPuzzleSituationModel hashiModel) {
		this.filePath = filePath;
		this.hashiModel = hashiModel;
		writeToFile(getFileString());
	}

	private void writeToFile(String fileString) {
		try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "utf-8"))) {
			out.write(fileString);
			out.flush();
			out.close();
		} catch (IOException e) {
			System.out.println("IOException writing " + filePath);
			e.printStackTrace();
		}
	}

	private String getFileString() {
		allIslands = hashiModel.getIslands();
		addFieldString();
		addIslandString();
		addBridgesString();
		return fileStringBuilder.toString();
	}

	private void addFieldString() {
		fileStringBuilder.append("FIELD\n");
		fileStringBuilder.append(
				hashiModel.getWidth() + " x " + hashiModel.getHeight() + " | " + hashiModel.getNoOfIslands() + "\n");
	}

	private void addIslandString() {
		fileStringBuilder.append("\nISLANDS\n");
		for (int index = 0; index < hashiModel.getNoOfIslands(); index++) {
			Island island = allIslands.get(index);
			fileStringBuilder.append(island + "\n");
		}
	}
	
	private static Direction[] SOUTH_AND_EAST = {Direction.SOUTH, Direction.EAST};

	private void addBridgesString() {
		fileStringBuilder.append("\nBRIDGES\n");
		for (int islandIdx = 0; islandIdx < hashiModel.getNoOfIslands(); islandIdx++) {
			addBridgesString(islandIdx);
		}
	}

	private void addBridgesString(int startIndex) {
		Bridge bridgeToAdd;
		final Island startIsland = allIslands.get(startIndex);
		for (Direction direction : SOUTH_AND_EAST) {
			if ((bridgeToAdd = hashiModel.getBridge(startIsland, direction)) != null) {
				int endIndex = Collections.binarySearch(allIslands, bridgeToAdd.getOtherEnd(startIsland));
				fileStringBuilder.append("( " + startIndex + ", " + endIndex + " | " + bridgeToAdd.isDouble() + " )\n");
			}
		}
	}

}
