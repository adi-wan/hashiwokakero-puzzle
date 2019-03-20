package main;

import java.io.File;	

import controller.BridgeAdder;
import controller.MainPuzzleController;
import controller.PuzzleGenerator;
import controller.PuzzleLoader;
import controller.PuzzleSaver;
import controller.PuzzleStateChecker;
import model.IPuzzleSituationModel;

/**
 * Class containing the main-method as a starting point for the Bridges / Hashiwokakeru application.
 * 
 * @author Adrian Stritzinger
 *
 */
public class Main_Stritzinger_Adrian {
	
	/*
	 * Starts the application.
	 */
	public static void main(String[] args) {
		new MainPuzzleController();
//		for (int i = 0; i < 1000; i++) {
//			testSolvePuzzle("/Users/Hightown/Desktop/BGS/");
//		}
	}
	
	// TODO: Optimize method, so it is possible to check the puzzles that can't be solved or take a long time to solve manually
//	public static void testSolvePuzzle(String solutionPath) {
//		IPuzzleSituationModel hashiModel = new PuzzleGenerator().getPuzzleSituationModel();
//		PuzzleStateChecker stateChecker = new PuzzleStateChecker(hashiModel);
//		BridgeAdder bridgeAdder = new BridgeAdder(hashiModel, stateChecker);
//		while (bridgeAdder.makeSureMove()) {}
//		String stateString = hashiModel.isSolved() ? "SOLVED" : "UNSOLVED";
//		int no = 0;
//		String fileName = solutionPath + "HashiPuzzle_" + hashiModel.getWidth() + "x" + hashiModel.getHeight() + "_" + stateString + "_" + no + ".bgs";
//		File file = new File(fileName);
//		while (file.exists()) {
//			final int decNo = getDecNo(no);
//			no++;
//			file = new File(fileName.substring(0, fileName.length() - ".bgs".length() - decNo) + no + ".bgs");
//		}
//		new PuzzleSaver().savePuzzle(file.getPath(), hashiModel);
//	}

//	private static int getDecNo(int no) {
//		int decNo = 1;
//		while ((no /= 10) > 0) {
//			decNo++;
//		}
//		return decNo;
//	}

}