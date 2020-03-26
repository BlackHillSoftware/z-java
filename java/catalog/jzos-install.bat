rem  Install ibmjzos.jar in the local Maven repository. This allows the Maven dependency in the 
rem  POM to be resolved.
rem  ibmjzos.jar is obtained from the z/OS Java installation.

call mvn install:install-file -Dfile=./JZOS/ibmjzos.jar -DgroupId=com.ibm.jzos -DartifactId=ibmjzos -Dversion=2.4.8 -Dpackaging=jar

pause