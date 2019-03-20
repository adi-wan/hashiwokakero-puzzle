package controller;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.HashiModel;
import model.HashiModel.Island;
import model.IPuzzleSituationModel;

/**
 * Class providing method for loading a Hashiwokakero puzzle from a file.
 * <b>Note that the puzzle loaded can be unsolvable.</b>
 * 
 * @author Adrian Stritzinger
 *
 */
public class PuzzleLoader {

	private String remainingFileString;
	private static String FILE_STRING_REG = "(.*)";
	private static String FILE_STRING_REG_NOT_EMPTY = "(.+)";

	private int noOfIslandsToAdd; // to model in regard to FIELD section
	private List<Island> islands; // to be added to model

	/**
	 * Loads a Hashiwokakero puzzle from .bgs file at <code>filePath</code>. If file
	 * cannot be found, file is of incorrect type or there are syntax or semantic
	 * errors in file, then an IllegalArgumentException is thrown.
	 * 
	 * <p>
	 * List of possible syntax errors:
	 * <ul>
	 * <li>FIELD, ISLANDS or BRIDGES (if file contains bridges) keywords are missing
	 * or in the wrong order</li>
	 * <li>FIELD section does not match the syntax Width x Height | Number of
	 * islands (white spaces and comments are ignored)</li>
	 * <li>ISLANDS section does not match the syntax { ( Column, Row | Number of
	 * bridges ) } (white spaces and comments are ignored)</li>
	 * <li>BRIDGES section does not match the syntax { ( Start Index, End Index |
	 * Double Bridge ) } (white spaces and comments are ignored)</li>
	 * </ul>
	 * </p>
	 * 
	 * <p>
	 * List of possible semantic errors:
	 * <ul>
	 * <li>Negative field width, height or number of islands</li>
	 * <li>More or less islands in ISLANDS section than number of islands declared
	 * in FIELD section</li>
	 * <li>Island not on field</li>
	 * <li>Vertical or horizontal distance between two islands less (more than one
	 * island at the same coordinates) or equal to 1</li>
	 * <li>Number of required bridges of island not in interval [1, number of
	 * neighbors of island * 2 or 8]</li>
	 * <li>No island with start or end index of bridge existing</li>
	 * <li>Bridge is diagonal</li>
	 * <li>Bridge is crossing another bridge or island</li>
	 * </ul>
	 * </p>
	 * 
	 * @param filePath
	 *            of file which puzzle is to be loaded from
	 * @return A Hashiwokakero puzzle
	 * @throws IllegalArgumentException
	 *             if file cannot be found at <code>filePath</code>, file is not of
	 *             type .bgs or file contains syntax or semantic errors
	 */
	public IPuzzleSituationModel loadPuzzle(String filePath) throws IllegalArgumentException {
		checkFileType(filePath);
		remainingFileString = convertFileToString(filePath);
		IPuzzleSituationModel hashiModel = loadEmptyPuzzleSituationModel();
		loadIslands(hashiModel);
		loadBridges(hashiModel);
		return hashiModel;
	}

	private void checkFileType(String filePath) throws IllegalArgumentException {
		if (!filePath.endsWith(".bgs")) {
			throw new IllegalArgumentException(
					"Puzzle could not be loaded from " + filePath + ". Type of file needs to be .bgs.");
		}
	}

	/**
	 * Reads in file at <code>filePath</code> and converts it to a string without
	 * whitespaces and comments and returns it.
	 * 
	 * @param filePath
	 *            at which file to be read is to be found.
	 * @return string representing file without whitespaces and comments.
	 * @throws IllegalArgumentException
	 *             if file cannot be found at filePath.
	 */
	private static String convertFileToString(String filePath) throws IllegalArgumentException {
		StringBuffer sb = new StringBuffer();
		try (BufferedReader in = new BufferedReader(new FileReader(filePath))) {
			String line = in.readLine();
			while (line != null) {
				if (!line.startsWith("#")) { // comments are ignored
					sb.append(line);
				}
				line = in.readLine();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("File at " + filePath + " could not be found.");
		} catch (IOException e) {
			System.out.println("IOException reading " + filePath + ".");
			e.printStackTrace();
		}
		String result = sb.toString();
		result = result.replaceAll("\\s+", ""); // remove whitespaces
		return result;
	}

	/**
	 * Checks if section with <code>sectionName</code> can found and removes name of
	 * section from remainingFileString.
	 * 
	 * @param sectionName
	 *            of section to be found.
	 * @throws IllegalArgumentException
	 *             if section cannot be found.
	 */
	private void checkSection(String sectionName) throws IllegalArgumentException {
		if (!remainingFileString.startsWith(sectionName)) {
			throw new IllegalArgumentException("Section " + sectionName
					+ " could not be found. Keyword is missing or sections are not in the right order (FIELD, ISLANDS, BRIDGES).");
		}
		remainingFileString = remainingFileString.substring(sectionName.length());
	}

	private Matcher getMatcherForRemainingFileString(Pattern pattern) throws IllegalArgumentException {
		Matcher m = pattern.matcher(remainingFileString);
		if (!m.find()) {
			throw new IllegalArgumentException(
					"Syntax of section is not valid (see StackTrace). RemainingFileString:\n" + remainingFileString);
		}
		return m;
	}

	private static final String FIELD_STRING = "(\\d+)x(\\d+)\\|(\\d+)";
	private static final Pattern FIELD_PATTERN = Pattern.compile(FIELD_STRING + FILE_STRING_REG_NOT_EMPTY);

	private IPuzzleSituationModel loadEmptyPuzzleSituationModel() throws IllegalArgumentException {
		checkSection("FIELD");
		Matcher m = getMatcherForRemainingFileString(FIELD_PATTERN);
		int width = Integer.parseInt(m.group(1));
		int height = Integer.parseInt(m.group(2));
		noOfIslandsToAdd = Integer.parseInt(m.group(3));
		remainingFileString = m.group(4);
		checkFieldSemantics(width, height);
		return new HashiModel(width, height);
	}

	private void checkFieldSemantics(int width, int height) throws IllegalArgumentException {
		if (width < 0 || height < 0 || noOfIslandsToAdd < 0) {
			throw new IllegalArgumentException("Invalid puzzle configuration (" + width + " x " + height + " | "
					+ noOfIslandsToAdd + "). Width, height and number of islands need to be positive.");
		}
	}

	private void loadIslands(IPuzzleSituationModel hashiModel) {
		checkSection("ISLANDS");
		while (!remainingFileString.equals("") && !remainingFileString.startsWith("BRIDGES")) {
			loadNextIsland(hashiModel);
		}
		checkIslandToFieldSectionConsistency(hashiModel);
		islands = hashiModel.getIslands();
		checkIfNoOfRequiredBridgesIsValid(hashiModel);
	}

	private static final String ISLAND_STRING = "\\((\\d+),(\\d+)\\|(\\d+)\\)";
	private static final Pattern ISLAND_PATTERN = Pattern.compile(ISLAND_STRING + FILE_STRING_REG);

	private void loadNextIsland(IPuzzleSituationModel hashiModel) throws IllegalArgumentException {
		Matcher m = getMatcherForRemainingFileString(ISLAND_PATTERN);
		int col = Integer.parseInt(m.group(1));
		int row = Integer.parseInt(m.group(2));
		int noOfBridges = Integer.parseInt(m.group(3));
		checkIslandSemantics(noOfBridges);
		remainingFileString = m.group(4);
		hashiModel.addIslandAt(col, row, noOfBridges);
	}

	private void checkIslandSemantics(int noOfBridges) throws IllegalArgumentException {
		if (noOfBridges < 1 || noOfBridges > 8) // must be >= 1 to be able to connect island to others
			throw new IllegalArgumentException("File contains island requiring " + noOfBridges + " bridges.");
	}

	private void checkIslandToFieldSectionConsistency(IPuzzleSituationModel hashiModel)
			throws IllegalArgumentException {
		if (noOfIslandsToAdd - hashiModel.getNoOfIslands() != 0) {
			throw new IllegalArgumentException("Number of islands in the ISLANDS section ("
					+ hashiModel.getNoOfIslands() + ") does not match the number of islands in the FIELD section ("
					+ noOfIslandsToAdd + ").");
		}
	}

	/**
	 * Roughly checks if number of bridges required by islands in
	 * <code>hashiModel</code> can be built. Rough check basically consists of check
	 * if number of bridges required by an island is less than or equal to the
	 * number of bridges required by its neighbors.
	 * 
	 * @param hashiModel
	 *            that is to be checked.
	 * @throws IllegalArgumentException
	 *             if number of bridges required by island cannot be built.
	 */
	private void checkIfNoOfRequiredBridgesIsValid(IPuzzleSituationModel hashiModel) throws IllegalArgumentException {
		for (Island island : hashiModel.getIslands()) {
			List<Island> neighbors = hashiModel.getNeighbourIslands(island);
			int noOfBridgesRequiredByNeighbors = 0;
			for (Island neighbor : neighbors) {
				noOfBridgesRequiredByNeighbors = noOfBridgesRequiredByNeighbors + neighbor.getNoOfBridgesRequired();
			}
			if (island.getNoOfBridgesRequired() > noOfBridgesRequiredByNeighbors) {
				throw new IllegalArgumentException(
						"Island " + island + " requires too many bridges. The neighbors of the island only require "
								+ noOfBridgesRequiredByNeighbors + " bridges.");
			}
		}
	}

	private void loadBridges(IPuzzleSituationModel hashiModel) {
		if (!remainingFileString.equals("")) { // BRIDGES section is optional
			checkSection("BRIDGES");
		}
		while (!remainingFileString.equals("")) {
			loadNextBridge(hashiModel);
		}
	}

	private static final String BRIDGE_STRING = "\\((\\d+),(\\d+)\\|(true|false)\\)";
	private static final Pattern BRIDGE_PATTERN = Pattern.compile(BRIDGE_STRING + FILE_STRING_REG);

	private void loadNextBridge(IPuzzleSituationModel hashiModel) throws IllegalArgumentException {
		Matcher m = getMatcherForRemainingFileString(BRIDGE_PATTERN);
		Island island = islands.get(Integer.parseInt(m.group(1)));
		Island otherIsland = islands.get(Integer.parseInt(m.group(2)));
		boolean isDouble = Boolean.parseBoolean(m.group(3));
		remainingFileString = m.group(4);
		hashiModel.addBridgeBetween(island, otherIsland, isDouble);
	}

}
