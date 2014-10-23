package vnreal.algorithms.utils.mls;

import java.util.ArrayList;

/**
 * This class is the Class for the MLS Lattice
 * @author Fabian Kokot
 * @version 0.1
 */
public class MLSLattice {
	//Max Security Level
	private final int mNumberOfLevels;
	
	//Categories
	private final ArrayList<String> mCategories;

	/**
	 * Create a "Lattice" with the number of Levels an the categories that are needed
	 * @param numberOfLevels Sets the Number of Levels, eg. 5 gives you 0-4
	 * @param categories ArrayList of Strings with Categories
	 */
	public MLSLattice(int numberOfLevels, ArrayList<String> categories) {
		super();
		if(numberOfLevels == 0 || categories.size() == 0)
			throw new Error("Error while initalize the MLSLattice; Levels:"+numberOfLevels
					+" Categories:"+categories.size());
		this.mNumberOfLevels = numberOfLevels;
		this.mCategories = categories;
	}

	public int getNumberOfLevels() {
		return mNumberOfLevels;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<String> getCategories() {
		return (ArrayList<String>) mCategories.clone();
	}
	
	
	
	
	
}
