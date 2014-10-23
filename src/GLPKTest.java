import org.gnu.glpk.GLPK;

public class GLPKTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("If you get any error, set -Djava.library.path=/usr/lib/x86_64-linux-gnu/jni/");
		System.out.println(GLPK.glp_version());
	}

}
