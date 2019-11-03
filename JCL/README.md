## Contents ##

### PROCS ###

JCL Procedures. The procedures aim to abstract as much of the Java environment as possble from the calling job, and to mimic the compile and go procedures provided with other languages.

3 PROCs are provided:
- JAVAC - Compile a Java program using BPXBATCH
- JAVAG - Run a Java program as a z/OS batch job using the JZOS Batch Launcher
- JAVACG - Combine the JAVAC and JAVAG procs to compile and run a Java program in one job.

The JAVAC and JAVAG procs are designed to use the same parameters to specify the Java class name, classpath etc. so that the parameters only need to be specified once for the JAVACG proc. This causes some complexity because JZOS does not support the same syntax for CLASSPATH as the javac compiler. JZOS requires a shell script to set up the Java environment, so this has been adapted to expand the CLASSPATH as required for JZOS.

#### Parameters

The procs use the following parameters:
- JAVACLS - the full name (including package) of the class to compile and/or run
- SRCPATH - the path to the Java source to be compiled. Default : the directory java/src under the user's home directory 
- TGTPATH - the output path for the compiler, and/or the location of the compiled .class files to run. Default : the directory java/target under the user's home directory
- CLASPATH - the Java CLASSPATH for the compilation and/or execution steps
- CLASPAT2-5 - JCL line length limitations can cause problems when multiple classpath entries are required. These parameters allow classpath entries be specified using multiple lines.
- ARGS - Command line arguments for the Java program

#### Additional DD Statements

- C.ADDENV - Optionally, add entries to the STDENV DD statement in the compile step.
- G.ADDENV - Optionally, add entries to the STDENV DD statement in the Java GO step.

Unfortunately, the STDENV syntax for JZOS is different to the STDENV syntax for BPXBATCH. BPXBATCH uses simple name-value pairs, JZOS contains shell commands - which means that the GO step entries targeting JZOS need to **export** any environment variables.

### Jobs  ###

Sample JCL demonstrating use of the PROCs
