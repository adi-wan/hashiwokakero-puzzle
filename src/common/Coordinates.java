package common;

/**
 * An instance of <code>Coordinates</code> represents the location in (x,y)
 * coordinate space, specified in integer precision.
 */
public class Coordinates implements Comparable<Coordinates> {

	public final int x;
	public final int y;

	/**
	 * Constructs an instance of <code>Coordinates</code>.
	 * 
	 * @param x coordinate
	 * @param y coordinate
	 */
	public Coordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Compares <code>this</code> instance to <code>otherCoords</code> based on
	 * first the x coordinate and second the y coordinate.
	 * <strong>Important:</strong> The {@link Object#hashCode() hashcode} method is
	 * not overriden so that two coordinates with the same x and the same y
	 * coordinate have different hashcodes.
	 * 
	 * @param otherCoords to be compared
	 * @result a negative integer, zero, or a positive integer as these coordinates
	 *         are less than, equal to, or greater than the specified coordinates
	 */
	@Override
	public int compareTo(Coordinates otherCoords) {
		if (x == otherCoords.x) {
			if (y == otherCoords.y) {
				return 0;
			} else if (y < otherCoords.y) {
				return -1;
			} else {
				return 1;
			}
		} else if (x < otherCoords.x) {
			return -1;
		} else {
			return 1;
		}
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * <strong>Important:</strong> The {@link Object#hashCode() hashcode} method is
	 * not overriden so that two coordinates with the same x and the same y
	 * coordinate have different hashcodes.
	 * 
	 * @param obj the reference object with which to compare.
	 * @result true if this object is the same as the obj argument; false otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Coordinates && compareTo((Coordinates) obj) == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the coordinates next to the coordinates represented by
	 * <code>this Coordinates</code> instance in the specified
	 * <code>direction</code>. Figuratively, the method takes one step in the
	 * <code>direction</code>.
	 * 
	 * @param direction the direction in which the next coordinates are to be
	 *                  determined.
	 * @return next coordinates in direction.
	 */
	public Coordinates getNextCoordsIn(Direction direction) {
		return getCoordsForNoStepsInDirection(direction, 1);
	}

	private Coordinates getCoordsForNoStepsInDirection(Direction direction, int steps) {
		int newX = x;
		int newY = y;
		switch (direction) {
		case NORTH:
			newY -= steps;
			break;
		case EAST:
			newX += steps;
			break;
		case SOUTH:
			newY += steps;
			break;
		case WEST:
			newX -= steps;
		}
		return new Coordinates(newX, newY);
	}

	/**
	 * Returns the direction of the specified coordinates relative to the
	 * coordinates represented by <code>this Coordinates</code> instance.
	 * 
	 * @param coords the coordinates of which the direction is to be determined.
	 * @return direction of <code>coords</code>
	 * @throws IllegalArgumentException if the direction of the specified
	 *                                  coordinates relative to the coordinates
	 *                                  represented by <code>this Coordinates</code>
	 *                                  instance is ambiguous.
	 */
	public Direction getDirectionOfCoord(Coordinates coords) throws IllegalArgumentException {
		if (this.x == coords.x) {
			if (this.y < coords.y) {
				return Direction.SOUTH;
			} else if (this.y > coords.y) {
				return Direction.NORTH;
			}
		} else if (this.y == coords.y) {
			if (this.x < coords.x) {
				return Direction.EAST;
			} else if (this.x > coords.x) {
				return Direction.WEST;
			}
		}
		throw new IllegalArgumentException("No valid direction.");
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

}
