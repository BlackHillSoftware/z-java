## ShowClassPath ## 

This class displays the CLASSPATH environment variable and the 
resolved CLASSPATH entries from the system class loader.
It is intended to help diagnose problems setting the CLASSPATH.
 
This class should be executable by specifying the absolute path of 
the class or jar file, without any dependency on the actual 
CLASSPATH.
 
Therefore it is important that this class does not depend on any 
classes other than the JRE itself, including inner or anonymous 
classes.  