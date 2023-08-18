import java.io.File;
import java.util.Scanner;

/**
 * @author Mehrdad Sabetzadeh, University of Ottawa
 */
public class ParkingLot {
	/**
	 * The delimiter that separates values
	 */
	private static final String SEPARATOR = ",";

	/**
	 * Instance variable for storing the number of rows in a parking lot
	 */
	private int numRows;

	/**
	 * Instance variable for storing the number of spaces per row in a parking lot
	 */
	private int numSpotsPerRow;

	/**
	 * Instance variable (two-dimensional array) for storing the lot design
	 */
	private CarType[][] lotDesign;

	/**
	 * Instance variable (two-dimensional array) for storing occupancy information
	 * for the spots in the lot
	 */
	private Spot[][] occupancy;

	/**
	 * Constructs a parking lot by loading a file
	 * 
	 * @param strFilename is the name of the file
	 */
	public ParkingLot(String strFilename) throws Exception {
		if (strFilename == null) {
			System.out.println("File name cannot be null.");
			return;
		}
		calculateLotDimensions(strFilename);
		lotDesign = new CarType[numRows][numSpotsPerRow];
		occupancy = new Spot[numRows][numSpotsPerRow];
		populateDesignFromFile(strFilename);
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumSpotsPerRow() {
		return numSpotsPerRow;
	}

	/**
	 * Parks a car (c) at a give location (i, j) within the parking lot.
	 * 
	 * @param i         is the parking row index
	 * @param j         is the index of the spot within row i
	 * @param c         is the car to be parked
	 * @param timestamp is the (simulated) time when the car gets parked in the lot
	 */
	public void park(int i, int j, Car c, int timestamp) {
		if (canParkAt(i, j, c) == true) {
			occupancy[i][j] = new Spot(c, timestamp);
		}
	}

	/**
	 * Removes the car parked at a given location (i, j) in the parking lot
	 * 
	 * @param i is the parking row index
	 * @param j is the index of the spot within row i
	 * @return the spot removed; the method returns null when either i or j are out
	 *         of range, or when there is no car parked at (i, j)
	 */
	public Spot remove(int i, int j) {
		if (occupancy[i][j] == null) {
			System.out.println("There is no car parked at this spot.");
			return null;
		} else if (i >= numRows || j >= numSpotsPerRow) {
			System.out.println("No such parking index exists.");
			return null;
		}
		Spot holder = occupancy[i][j];
		occupancy[i][j] = null;
		return holder;
	}

	/**
	 * Returns the spot instance at a given position (i, j)
	 * 
	 * @param i is the parking row index
	 * @param j is the index of the spot within row i
	 * @return the spot instance at position (i, j)
	 */
	public Spot getSpotAt(int i, int j) {
		return occupancy[i][j];
	}

	/**
	 * Checks whether a car (which has a certain type) is allowed to park at
	 * location (i, j)
	 *
	 * NOTE: This method is complete; you do not need to change it.
	 * 
	 * @param i is the parking row index
	 * @param j is the index of the spot within row i
	 * @return true if car c can park at (i, j) and false otherwise
	 */
	public boolean canParkAt(int i, int j, Car c) {
		CarType check = c.getType();
        if (i < 0 || i >= numRows || j < 0 || j >= numSpotsPerRow){ //checks to make sure parking spot is valid and not out of bounds
            return false;
        } else {
			if (occupancy[i][j] == null) {
				if (lotDesign[i][j].equals(CarType.NA)) { //checks if parking space is NA
					return false;
				} else if (check.equals(CarType.ELECTRIC)) { //checks if car is electric. since it is not NA, it will fit in any spot.
					return true;
				} else if (check.equals(CarType.SMALL)) { //checks if the car is small, then checking whether the parking space is electric or not to see if it fits.
					if (lotDesign[i][j].equals(CarType.ELECTRIC)) {
						return false;
					} else {
						return true;
					}
				} else if (check.equals(CarType.REGULAR)) { //checks if the car is regular. It then checks if the car is able to go into the position in the lot.
					if (lotDesign[i][j].equals(CarType.ELECTRIC) || lotDesign[i][j].equals(CarType.SMALL)) {
						return false;
					} else {
						return true;
					}
				} else if (check.equals(CarType.LARGE)) { //checks if the both the car and the parking spot are large.
					if (lotDesign[i][j].equals(CarType.LARGE)) {
						return true;
					} else {
						return false;
					}
				}
			}
		}
        return false;
	}

	/**
	 * Attempts to park a car in the lot. Parking is successful if a suitable parking spot
	 * is available in the lot. If some suitable spot is found (anywhere in the lot), the car
	 * is parked at that spot with the indicated timestamp and the method returns "true".
	 * If no suitable spot is found, no parking action is taken and the method simply returns
	 * "false"
	 * 
	 * @param c is the car to be parked
	 * @param timestamp is the simulation time at which parking is attempted for car c 
	 * @return true if c is successfully parked somwhere in the lot, and false otherwise
	 */
	public boolean attemptParking(Car c, int timestamp) {
		int row = 0;
		int column = 0;
		boolean found = false;
		CarType holder = CarType.LARGE;
		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numSpotsPerRow; j++) {
				if (canParkAt(i, j, c)) {
					found = true;
					if (c.getType().equals(CarType.LARGE)) {
						row = i;
						column = j;
					} else if (c.getType().equals(CarType.REGULAR)) {
						if (lotDesign[i][j].equals(CarType.REGULAR)) {
							row = i;
							column = j;
							holder = CarType.REGULAR;
						} else if (holder.equals(CarType.LARGE)) {
							row = i;
							column = j;
						}
					} else if (c.getType().equals(CarType.SMALL)) {
						if (lotDesign[i][j].equals(CarType.SMALL)) {
							row = i;
							column = j;
							holder = CarType.SMALL;
						} else if (lotDesign[i][j].equals(CarType.REGULAR) && holder.equals(CarType.SMALL) == false) {
							row = i;
							column = j;
							holder = CarType.REGULAR;
						} else if (holder.equals(CarType.LARGE)) {
							row = i;
							column = j;
						}
					} else if (c.getType().equals(CarType.ELECTRIC)) {
						if (lotDesign[i][j].equals(CarType.ELECTRIC)) {
							row = i;
							column = j;
							holder = CarType.ELECTRIC;
						} else if (lotDesign[i][j].equals(CarType.SMALL) && holder.equals(CarType.ELECTRIC) == false) {
							row = i;
							column = j;
							holder = CarType.SMALL;
						} else if (lotDesign[i][j].equals(CarType.REGULAR) && holder.equals(CarType.SMALL) == false) {
							row = i;
							column = j;
							holder = CarType.REGULAR;
						} else if (holder.equals(CarType.LARGE)) {
							row = i;
							column = j;
						}
					}
				}
			}
			if (found == true) {
				park(row, column, c, timestamp);
			}
			return found;
		}
		
		return false; // Remove this statement when your implementation is complete.

	}

	/**
	 * @return the total capacity of the parking lot excluding spots that cannot be
	 *         used for parking (i.e., excluding spots that point to CarType.NA)
	 */
	public int getTotalCapacity() {
		int counter = 0;
		for (int i = 0; i < numRows; i++) {
			for (int x = 0; x < numSpotsPerRow; x++) { //nested for loop to run through the lotDesign array
				if (!lotDesign[i][x].equals(CarType.NA)) { //checks if the spot is NA or not
					counter++;
				}
			}
		}
		return counter;
	}

	/**
	 * @return the total occupancy of the parking lot
	 */
	public int getTotalOccupancy() {
		int counter = 0;
		for (int i = 0; i < numRows; i++) {
			for (int x = 0; x < numSpotsPerRow; x++) { //a nested for loop is used to run through every element in the occupancy array
				if (occupancy[i][x] != null) { //checks if the spot is empty
					counter++;
				}
			}
		}
		return counter;
	}

	private void calculateLotDimensions(String strFilename) throws Exception {
		String str = "";
		String hold = "";
		Scanner scanner = new Scanner(new File(strFilename));
		while (scanner.hasNext()) {
			str = scanner.nextLine();
			if (str.length() != 0) {
				numRows++;
				hold = str;
			} else {
				break;
			}
		}
		String[] holder = hold.split(",");
		numSpotsPerRow = holder.length; //since every row is the same length, it determines the number of spots per row by counting the length of the holder string, which holds one row
		scanner.close();
	}

	private void populateDesignFromFile(String strFilename) throws Exception {
		Scanner scanner = new Scanner(new File(strFilename));
		String holder[] = new String[numSpotsPerRow];
		int counter = 0;
		while (scanner.hasNext()) {
			String str = scanner.nextLine();
			if (str.length() != 0) {
				holder = str.split(",");
				for (int i = 0; i < numSpotsPerRow; i++) {
					holder[i] = holder[i].trim();
					lotDesign[counter][i] = Util.getCarTypeByLabel(holder[i]);
				}
				counter++;
			} else {
				break;
			}
		}
	}

	/**
	 * NOTE: This method is complete; you do not need to change it.
	 * @return String containing the parking lot information
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("==== Lot Design ====").append(System.lineSeparator());

		for (int i = 0; i < lotDesign.length; i++) {
			for (int j = 0; j < lotDesign[0].length; j++) {
				buffer.append((lotDesign[i][j] != null) ? Util.getLabelByCarType(lotDesign[i][j])
						: Util.getLabelByCarType(CarType.NA));
				if (j < numSpotsPerRow - 1) {
					buffer.append(", ");
				}
			}
			buffer.append(System.lineSeparator());
		}

		buffer.append(System.lineSeparator()).append("==== Parking Occupancy ====").append(System.lineSeparator());

		for (int i = 0; i < occupancy.length; i++) {
			for (int j = 0; j < occupancy[0].length; j++) {
				buffer.append(
						"(" + i + ", " + j + "): " + ((occupancy[i][j] != null) ? occupancy[i][j] : "Unoccupied"));
				buffer.append(System.lineSeparator());
			}

		}
		return buffer.toString();
	}
}