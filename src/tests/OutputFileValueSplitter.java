package tests;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class to extract values from a scenario-output-file to be GNUplot-readable.
 *  
 * @author Julian Willerding
 */
public class OutputFileValueSplitter {

    public static void main(String[] args) throws IOException {

	String metricToAttend = "MaxSecSpreadProvDem";
	String fileNameInput = "SecuritySubgraphTest_out_ProvDem.txt";
	String fileNameOutput = "out.txt";

	FileReader fr = new FileReader(fileNameInput);
	BufferedReader br = new BufferedReader(fr);

	FileWriter fstream = new FileWriter(fileNameOutput);
	BufferedWriter out = new BufferedWriter(fstream);

	String s;
	int numberOfVNs = 0;

	// Iterate over alle lines in the file
	while ((s = br.readLine()) != null) {
	    
	    String[] values = s.split("\t");
	    String[] runParams = values[1].split("_");

	    // If the requested metric is in the current line
	    if (values[0].equals(metricToAttend)) {

		values[2] = values[2].replace('.', ',');
		System.out.println(runParams[2] + "\t" + values[2]);

		// Adds an empty line between different blocks (of numberOfVNs)
		if (numberOfVNs != 0
			&& numberOfVNs != Integer.parseInt(runParams[2])) {
		    out.write("\n");
		}
		numberOfVNs = Integer.parseInt(runParams[2]);
		
		out.write(runParams[2] + "\t" + values[2] + "\n");
	    }
	}
	
	fr.close();
	out.close();
    }
}
