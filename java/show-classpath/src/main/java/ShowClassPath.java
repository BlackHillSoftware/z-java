import java.net.URL;
import java.net.URLClassLoader;

/**
 * This class displays the CLASSPATH environment variable and the 
 * resolved CLASSPATH entries from the system class loader.
 * It is intended to help diagnose problems setting the CLASSPATH.
 * <p>
 * This class should be executable by specifying the absolute path of 
 * the class or jar file, without any dependency on the actual 
 * CLASSPATH.
 * <p>
 * Therefore it is important that this class does not depend on any 
 * classes other than the JRE itself, including inner or anonymous 
 * classes.  
 * 
 */

public class ShowClassPath 
{
   public static void main (String args[]) 
   {
	   System.out.format("CLASSPATH environment variable: %s%n%n", 
			   System.getenv("CLASSPATH"));
	   
       ClassLoader cl = ClassLoader.getSystemClassLoader();
       URL[] urls = ((URLClassLoader)cl).getURLs();
	   System.out.println("Resolved CLASSPATH entries:");
       for(URL url: urls)
       {
        	System.out.format("%s%n", url.getFile());
       }
       
       String libPathProperty = System.getProperty("java.library.path");
	   System.out.println("java.library.path value:");
       System.out.println(libPathProperty);
       
   }
}
