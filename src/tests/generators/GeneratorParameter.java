package tests.generators;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This class is to mark which parameters in which order 
 * are expected for that generator
 * <br/><br/>
 * Some examples:<br/>
 * Seed:Seed will put the seed from the actual seed generator into the list 
 * Networks:Networks will put the NetworksStack into the list
 * TR:SecLevels will put the Value from the TestRun-Parameter in the list <br/>
 * Result:test.generator.resource.MLSGenerator will put the result from generate()-Method from the 
 * into the list <br/>
 * Method:test.generator.resource.ABCGenerator|getFoo will put the result getFooObject() from a 
 * Instantiated generator in the list<br/>
 * SMethod:some.package.LALAGenerator|getBar will put the result from the static Method
 * LALAGenerator.getBar() into the list
 * 
 * 
 * 
 * @author Fabian Kokot
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GeneratorParameter {
	String[] parameters();
}
