# z-java
Tools and samples to help use Java on z/OS.

## Contents ##

### [java](./java) ###

Sample z/OS Java programs.

- **[compress](./java/compress)**
Compress and decompress z/OS sequential datasets using gzip compatible compression. Data can be decompressed on other systems using gzip and processed by programs that understand the z/OS record formats. Likewise, programs on other systems can write z/OS compatible records, compress the data using gzip and it can be uncompressed to a z/OS dataset.
- **[catalog](./java/catalog)**
Samples demonstrating use of the **com.ibm.jzos.CatalogSearch** class to use the z/OS Catalog Search Interface from Java. 
- **[show-classpath](./java/show-classpath)**
A simple program to help diagnose classpath programs. It displays the CLASSPATH environment variable, and the resolved CLASSPATH entries from the class loader.
- **[z-mail](./java/z-mail)**
A program to send email from a z/OS batch job. The sample demonstrates using Gmail, using a TLS connection and a userid and password for authentication.

### [JCL](./JCL) ###

JCL Procedures and jobs to compile and run Java on z/OS.

3 PROCs are provided:
- JAVAC - Compile a Java program using BPXBATCH
- JAVAG - Run a Java program as a z/OS batch job using the JZOS Batch Launcher
- JAVACG - Combine the JAVAC and JAVAG procs to compile and run a Java program in one job.

Sample jobs are provided to demonstrate use of the PROCs.

