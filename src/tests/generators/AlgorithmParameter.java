package tests.generators;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



/**
 * This class connecting the parameters which are needed to init the Algorithms
 * <br/><br/>
 * type: Which type of algorithm<br/>
 *  "combined" -> for algorithms which are doing node and link mapping in one eg. subgraph (use IAlgorithmClass field)<br/>
 *  "alone" -> when node and link algorithms are chosen for it self<br/><br/>
 *  
 *  IAlgorithmClass: Class with full path which is implementing IAlgorithm <br/>
 *  nodemapping: Which is the node mapping algorithm (complete class path) <br/>
 *  nodemapping_params: which TestRun parameters will be used as parameters<br/>
 *  linkmapping: Which is the link mapping algorithm (complete class path) <br/>
 *  linkmapping_params: which TestRun parameters will be used as parameters  
 * 
 * @author Fabian Kokot
 *
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface AlgorithmParameter {
	String type();
	
	String IAlgorithmClass();
	String nodemapping();
	String linkmapping();
	
	String[] nodemapping_params();
	String[] linkmapping_params();
}
