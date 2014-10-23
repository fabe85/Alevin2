package vnreal;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This class marks the needed constructor parameters for necessary for creating a object from xml file
 * 
 * @author Fabian Kokot
 * @date 25.12.2012
 * 
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface AdditionalConstructParameter {
	String[] parameterNames();
//	@SuppressWarnings("rawtypes")
//	Class[] parameterTypes();
	String[] parameterGetters();
}
