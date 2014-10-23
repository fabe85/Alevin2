package tests;

/**
 * This class contains some methods which are used by generators to convert 
 * data types
 * @author Fabian Kokot
 *
 */
public class ConversionHelper {
	/**
	 * This Method tries to parse an object to an integer<br/>
	 * Only doubles and integer class is supported 
	 * 
	 * @param ob Object 
	 * @return Integer
	 */
	public static Integer paramObjectToInteger(Object ob) {
		if(ob.getClass().getName().equals("double") || ob.getClass().getName().equals("java.lang.Double")) {
			Double d = new Double((double)ob);
			return new Integer(d.intValue());
		}
		if(ob.getClass().getName().equals("int") || ob.getClass().getName().equals("java.lang.Integer")) {
			return new Integer((int)ob);
		}
			
		throw new Error("Can't parse other types than Integer or Double to an Integer! "+ob.getClass().getName());
	}
	
	public static Boolean paramObjectToBoolean(Object ob) {
		if(ob.getClass().getName().equals("double") || ob.getClass().getName().equals("java.lang.Double")) {
			Double d = new Double((double)ob);
			if(d == 0)
				return new Boolean(false);
			else if (d == 1) 
				return new Boolean(true);
		}
		
		if(ob.getClass().getName().equals("int") || ob.getClass().getName().equals("java.lang.Integer")) {
			Integer i = new Integer((int)ob);
			if(i == 0)
				return new Boolean(false);
			else if (i == 1) 
				return new Boolean(true);
		}
		throw new Error("Cannot parse object to boolean, only Integer or Double is allowed with values 0 or 1! "+ob.getClass().getName());
		
		
	}
	
	public static Long paramObjectToLong(Object ob) {
		if(ob.getClass().getName().equals("double") || ob.getClass().getName().equals("java.lang.Double")) {
			Double d = new Double((double)ob);
			return new Long(d.longValue());
		}
		if(ob.getClass().getName().equals("int") || ob.getClass().getName().equals("java.lang.Integer")) {
			return new Long((int)ob);
		}
		if(ob.getClass().getName().equals("long") || ob.getClass().getName().equals("java.lang.Long")) {
			return new Long((long)ob);
		}
		
			
		throw new Error("Can't parse other types than Integer or Double to an Integer! "+ob.getClass().getName() );
	}
	
	public static String paramObjectToString(Object ob) {
		if(ob.getClass().getName().equals("java.lang.String")) {
			return (String)ob;
		}
		if(ob.getClass().getName().equals("int") || ob.getClass().getName().equals("java.lang.Integer")) {
			return ((int)ob+"");
		}
		if(ob.getClass().getName().equals("long") || ob.getClass().getName().equals("java.lang.Long")) {
			return ((long)ob+"");
		}
		if(ob.getClass().getName().equals("double") || ob.getClass().getName().equals("java.lang.Double")) {
			return (ob+"");
		}
		
		throw new Error("Can't parse other types than String, Integer, Long or Double to an Integer! "+ob.getClass().getName() );
	}
}
