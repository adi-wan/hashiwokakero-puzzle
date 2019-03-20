package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import common.Coordinates;
import common.Direction;
import controller.IInputListener;
import model.HashiModel.Bridge;
import model.HashiModel.Island;
import model.IPuzzleSituationModel;

/**
 * 
 * JFieldPanel is a JPanel containing the field of a Hashiwokakeru puzzle (its
 * islands and bridges).
 * 
 * <p>
 * The field is horizontally and vertically centered inside the panel keeping a
 * minimum distance of one row to the top and bottom border and a minimum
 * distance of one column to the left and right border of the panel. When the
 * panel is resized, the field scales up or down to fit the space inside the
 * panel while keeping the minimum distance to the borders.
 * </p>
 * 
 * <p>
 * By clicking on an island on the field, an instance of an
 * <code>IInputListener</code> is informed to try to add (left mouse button) or
 * remove (right mouse button) a bridge in the direction indicated by the click.
 * If direction is ambiguous or there is no island at the coordinates of the
 * click, the click is ignored.
 * </p>
 * 
 * @author Adrian Stritzinger
 *
 */
public class JFieldPanel extends JPanel implements IModelQuerier {

	public static final Color COLOR_ISLAND_MISSING_BRIDGE = new Color(192, 192, 192); // GREY
	public static final Color COLOR_ISLAND_WITH_ALL_BRIDGES = new Color(148, 198, 148); // GREEN
	public static final Color COLOR_INVALID_ISLAND = new Color(255, 102, 102); // RED
	public static final Color COLOR_BRIDGE_LAST_INSERTED = new Color(204, 204, 0); // DARK_YELLOW

	private int userCoordinateWidth, userCoordinateHeight;
	private final int DIST_BETW_ADJ_GRID_POINTS = 100; // distance between adjacent grid points
	private final int ISLAND_RADIUS = 50; // DIST_BETW_ADJ_GRID_POINTS / 2
	private final int CLICK_TOLERANCE = 50; // DIST_BETW_ADJ_GRID_POINTS / 2
	private final int DIST_BETWEEN_BRIDGES = 20; // belonging to double bridge

	private IPuzzleSituationModel hashiModel;
	private IInputListener inputListener;

	private double scaleFactor;
	private double translation;

	private boolean showNoOfMissingBridges;

	/**
	 * 
	 * Constructs an instance of a JFieldPanel that has a preferred size of 500x500
	 * containing the field of the Hashiwokakeru puzzle represented by the
	 * <code>hashiModel</code> that informs the <code>inputListener</code> if a left
	 * or right mouse-click on the field occurs.
	 * 
	 * @param hashiModel
	 *            that represents the Hashiwokakeru puzzle to be painted
	 * @param inputListener
	 *            that is informed when a click on the field occurs
	 */
	public JFieldPanel(IPuzzleSituationModel hashiModel, IInputListener inputListener) {
		setPuzzleSituationModel(hashiModel);
		this.inputListener = inputListener;
		setPreferredSize(new Dimension(500, 500));
		addMouseListener(getMouseAdapterNotifyingListener());
	}

	/**
	 * 
	 * Set <code>hashiModel</code> as the <code>IPuzzleSituationModel</code> which
	 * the field is painted of.
	 * 
	 * @param hashiModel
	 *            containing the width and height of the field as well as the
	 *            islands and bridges on the field
	 */
	@Override
	public void setPuzzleSituationModel(IPuzzleSituationModel hashiModel) {
		this.hashiModel = hashiModel;
		this.userCoordinateWidth = (hashiModel.getWidth() + 1) * DIST_BETW_ADJ_GRID_POINTS; // +1 to add left border
		this.userCoordinateHeight = (hashiModel.getHeight() + 1) * DIST_BETW_ADJ_GRID_POINTS; // + 1 to add top border
	}

	private MouseListener getMouseAdapterNotifyingListener() {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) || SwingUtilities.isRightMouseButton(e)) { // exclude middle
					reTransformCoordinatesAndNotifyListener(e.getX(), e.getY(), SwingUtilities.isLeftMouseButton(e));
				}
			}
		};
	}

	/**
	 * 
	 * Sets the number of bridges painted inside each island.
	 * 
	 * @param showNoOfBridgesMissing
	 *            if true, the number of bridges missing, i.e. number of bridges
	 *            that yet need to be added, is shown, otherwise the total of number
	 *            of bridges that the island requires is shown
	 */
	public void setIslandString(boolean showNoOfBridgesMissing) {
		this.showNoOfMissingBridges = showNoOfBridgesMissing;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		transformGraphicsToCenterGridOnPanel(g2);
		// translation before scaling because scaling not considered in translation
		transformGraphicsToScaleToUserCoordinateSystem(g2);
		drawIslandsAndBridges(g2);
	}

	private void transformGraphicsToCenterGridOnPanel(Graphics2D g2) {
		double horDistanceBetweenGridPoints = (double) getWidth() / (double) (hashiModel.getWidth() + 1);
		double verDistanceBetweenGridPoints = (double) getHeight() / (double) (hashiModel.getHeight() + 1);
		double differenceBetweenDistances = horDistanceBetweenGridPoints - verDistanceBetweenGridPoints;
		if (differenceBetweenDistances > 0) { // grid can take up all vertical space but must be centered horizontally
			translation = differenceBetweenDistances * (double) (hashiModel.getWidth() + 1) / 2.0; // / 2.0 to center
			g2.transform(AffineTransform.getTranslateInstance(translation, 0));
		} else { // grid can take up all horizontal space but must be centered vertically
			translation = differenceBetweenDistances * (double) (hashiModel.getHeight() + 1) / 2.0;
			g2.transform(AffineTransform.getTranslateInstance(0, -translation));
		}
	}

	private void transformGraphicsToScaleToUserCoordinateSystem(Graphics2D g2) {
		double scaleX = (double) getWidth() / (double) userCoordinateWidth;
		double scaleY = (double) getHeight() / (double) userCoordinateHeight;
		scaleFactor = Math.min(scaleX, scaleY);
		AffineTransform at = AffineTransform.getScaleInstance(scaleFactor, scaleFactor);
		g2.transform(at);
	}

	private void drawIslandsAndBridges(Graphics2D g2) {
		activateAntialiasingForSmootherLines(g2);
		if (hashiModel.getLastInsertedBridge() != null) { // needs to be drawn first to be in the background
			drawLastInsertedBridge(g2, hashiModel.getLastInsertedBridge());
		}
		for (Island island : hashiModel.getIslands()) {
			drawEastAndSouthBridge(g2, island); // need to be drawn before start and end island to be in the background
			drawIsland(g2, island);
		}
	}

	private void activateAntialiasingForSmootherLines(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	private void drawLastInsertedBridge(Graphics2D g2, Bridge bridge) {
		g2.setStroke(new BasicStroke(10.0f)); // default 1.0f
		g2.setColor(COLOR_BRIDGE_LAST_INSERTED);
		drawBridge(g2, bridge);
	}

	private void drawBridge(Graphics2D g2, Bridge bridge) {
		if (bridge.isDouble()) {
			drawDoubleBridge(g2, bridge.getStart().getCoords(), bridge.getEnd().getCoords(), bridge.isVertical());
		} else {
			drawSimpleBridge(g2, bridge.getStart().getCoords(), bridge.getEnd().getCoords());
		}
	}

	private void drawSimpleBridge(Graphics2D g2, Coordinates start, Coordinates end) {
		g2.drawLine((start.x + 1) * 100, (start.y + 1) * 100, (end.x + 1) * 100, (end.y + 1) * 100);
	}

	private void drawDoubleBridge(Graphics2D g2, Coordinates start, Coordinates end, boolean isVertical) {
		if (isVertical) {
			g2.drawLine((start.x + 1) * 100 - DIST_BETWEEN_BRIDGES / 2, (start.y + 1) * 100,
					(end.x + 1) * 100 - DIST_BETWEEN_BRIDGES / 2, (end.y + 1) * 100);
			g2.drawLine((start.x + 1) * 100 + DIST_BETWEEN_BRIDGES / 2, (start.y + 1) * 100,
					(end.x + 1) * 100 + DIST_BETWEEN_BRIDGES / 2, (end.y + 1) * 100);
		} else {
			g2.drawLine((start.x + 1) * 100, (start.y + 1) * 100 - DIST_BETWEEN_BRIDGES / 2, (end.x + 1) * 100,
					(end.y + 1) * 100 - DIST_BETWEEN_BRIDGES / 2);
			g2.drawLine((start.x + 1) * 100, (start.y + 1) * 100 + DIST_BETWEEN_BRIDGES / 2, (end.x + 1) * 100,
					(end.y + 1) * 100 + DIST_BETWEEN_BRIDGES / 2);
		}
	}

	private final Direction[] EAST_AND_SOUTH = { Direction.EAST, Direction.SOUTH };

	private void drawEastAndSouthBridge(Graphics2D g2, Island island) {
		g2.setStroke(new BasicStroke(2.0f)); // default 1.0f
		g2.setColor(Color.BLACK);
		Bridge bridge;
		for (Direction direction : EAST_AND_SOUTH) {
			if ((bridge = hashiModel.getBridge(island, direction)) != null) {
				drawBridge(g2, bridge);
			}
		}
	}

	private void drawIsland(Graphics2D g2, Island island) {
		int x = (island.getCoords().x + 1) * 100 - ISLAND_RADIUS;
		int y = (island.getCoords().y + 1) * 100 - ISLAND_RADIUS;
		int noOfBridgesMissing = island.getNoOfBridgesMissing();
		setIslandColor(g2, noOfBridgesMissing);
		g2.fillOval(x, y, 2 * ISLAND_RADIUS, 2 * ISLAND_RADIUS);
		drawIslandString(g2, island, x, y, noOfBridgesMissing);
	}

	private void setIslandColor(Graphics2D g2, int noOfBridgesMissing) {
		if (noOfBridgesMissing == 0) {
			g2.setColor(COLOR_ISLAND_WITH_ALL_BRIDGES);
		} else if (noOfBridgesMissing > 0) {
			g2.setColor(COLOR_ISLAND_MISSING_BRIDGE);
		} else { // noOfBridgesMissing < 0
			g2.setColor(COLOR_INVALID_ISLAND);
		}
	}

	private void drawIslandString(Graphics2D g2, Island island, int x, int y, int noOfBridgesMissing) {
		g2.setColor(Color.BLACK);
		g2.setFont(getFont().deriveFont((float) ISLAND_RADIUS)); // resize font to fit island
		FontMetrics fm = g2.getFontMetrics();
		int noOfBridgesToBeDrawn = showNoOfMissingBridges ? noOfBridgesMissing : island.getNoOfBridgesRequired();
		String noString = Integer.toString(noOfBridgesToBeDrawn);
		g2.drawString(noString, x + ISLAND_RADIUS - fm.stringWidth(noString) / 2,
				y + ISLAND_RADIUS - fm.getHeight() / 2 + fm.getAscent());
	}

	private void reTransformCoordinatesAndNotifyListener(int panelX, int panelY, boolean isLeftMouseButton) throws IllegalArgumentException {
		double reTranslatedX = translation > 0.0 ? (double) panelX - translation : (double) panelX;
		double reTranslatedY = translation > 0.0 ? (double) panelY : (double) panelY + translation;
		// retrans. needs to be before resc. since it comes before resc. when painted
		double reScaledX = reTranslatedX / scaleFactor;
		double reScaledY = reTranslatedY / scaleFactor;
		int islandX = Math.toIntExact(Math.round(reScaledX / (double) DIST_BETW_ADJ_GRID_POINTS)) - 1;
		int islandY = Math.toIntExact(Math.round(reScaledY / (double) DIST_BETW_ADJ_GRID_POINTS)) - 1;
		double islandCenterX = (double) (islandX + 1) * 100.0;
		double islandCenterY = (double) (islandY + 1) * 100.0;
		if (islandX >= 0 && islandX < hashiModel.getWidth() && islandY >= 0 && islandY < hashiModel.getHeight()
				&& hashiModel.islandAt(islandX, islandY)) {
			Island island = hashiModel.getIslandAt(islandX, islandY);
			Direction directionOfClick = getDirectionOfClick(reScaledX, reScaledY, islandCenterX, islandCenterY);
			inputListener.makeMove(island, directionOfClick, isLeftMouseButton);
		}
	}

	private Direction getDirectionOfClick(double reScaledX, double reScaledY, double islandCenterX,
			double islandCenterY) throws IllegalArgumentException {
		if (islandCenterY > reScaledY && reScaledY >= islandCenterY - CLICK_TOLERANCE
				&& Math.abs(reScaledX - islandCenterX) <= Math.abs(reScaledY - islandCenterY)) {
			return Direction.NORTH;
		}
		if (islandCenterX > reScaledX && reScaledX >= islandCenterX - CLICK_TOLERANCE
				&& Math.abs(reScaledY - islandCenterY) <= Math.abs(reScaledX - islandCenterX)) {
			return Direction.WEST;
		}
		if (islandCenterY <= reScaledY && reScaledY < islandCenterY + CLICK_TOLERANCE
				&& Math.abs(reScaledX - islandCenterX) < Math.abs(reScaledY - islandCenterY)) {
			return Direction.SOUTH;
		}
		if (islandCenterX <= reScaledX && reScaledX < islandCenterX + CLICK_TOLERANCE
				&& Math.abs(reScaledY - islandCenterY) < Math.abs(reScaledX - islandCenterX)) {
			return Direction.EAST;
		}
		throw new IllegalArgumentException("Direction of click is ambiguous.");
	}
}
