## JCL Procedures ##

The procedures aim to abstract as much of the Java environment as possble from the calling job, and to mimic the compile and go procedures provided with other languages.

3 PROCs are provided:
- JAVAC - Compile a Java program using BPXBATCH
- JAVAG - Run a Java program as a z/OS batch job using the JZOS Batch Launcher
- JAVACG - Combine the JAVAC and JAVAG procs to compile and run a Java program in one job.

The JAVAC and JAVAG procs are designed to use the same parameters to specify the Java class name, classpath etc. so that the parameters only need to be specified once for the JAVACG proc. This causes some complexity because JZOS does not support the same syntax for CLASSPATH as the javac compiler. JZOS requires a shell script to set up the Java environment, so this has been adapted to expand the CLASSPATH as required for JZOS.

## Parameters ##

The procs use the following parameters:
- JAVACLS - the full name (including package) of the class to compile and/or run
- SRCPATH - the path to the Java source to be compiled. Default : the directory java/src under the user's home directory 
- TGTPATH - the output path for the compiler, and/or the location of the compiled .class files to run. Default : the directory java/target under the user's home directory
- CLASPATH - the Java CLASSPATH for the compilation and/or execution steps
- CLASPAT2-5 - JCL line length limitations can cause problems when multiple classpath entries are required. These parameters allow classpath entries be specified using multiple lines.
- ARGS - Command line arguments for the Java program

## Additional DD Statements ##

- C.ADDENV - Optionally, add entries to the STDENV DD statement in the compile step.
- G.ADDENV - Optionally, add entries to the STDENV DD statement in the Java GO step.

Unfortunately, the STDENV syntax for JZOS is different to the STDENV syntax for BPXBATCH. BPXBATCH uses simple name-value pairs, JZOS contains shell commands - which means that the GO step entries targeting JZOS need to **export** any environment variables.

## Examples ##

### Run a Jar File ###

```
//JOBNAME  JOB CLASS=A, 
//             MSGCLASS=H,
//             NOTIFY=&SYSUID 
//*
//JAVAG   EXEC JAVAG,
// JAVACLS='-jar java/compress-1.0.0.jar',
// ARGS='-d'
//INPUT    DD  DISP=SHR,DSN=HLQ.SMF.DATA.GZ
//OUTPUT   DD DISP=(NEW,CATLG),
//     DSN=HLQ.SMF.DATA,
//     SPACE=(TRK,(1000,1000),RLSE),
//     LRECL=32760,BLKSIZE=0,RECFM=VBS, 
//     UNIT=SYSDA 
```

This JCL runs a jar file located in the **java** directory under the user's home directory. **-d** is passed as a command line argument to the Java program.

The Java program runs under the JZOS Batch Launcher so the **INPUT** and **OUTPUT** DDs in the JCL are available to the program.


### Compile a Java File ###

```
//JOBNAME JOB CLASS=A,
//             MSGCLASS=H,
//             NOTIFY=&SYSUID 
//*
//JAVAC    EXEC JAVAC,
// JAVACLS='com/blackhillsoftware/zos/ZMail',
// SRCPATH='z-java/java/z-mail/src/main/java',
// TGTPATH='java/target',
// CLASPATH='java/lib/javax.mail-1.6.2.jar',
// CLASPAT2='java/lib/activation-1.1.jar'
```     
This JCL compiles a Java file. The path names do not begin with a "/" which means the paths are relative to the user's home directory. If the user's home directory is /u/userid, the file to be compiled is:
```
/u/userid/z-java/java/z-mail/src/main/java/com/blackhillsoftware/zos/ZMail.java
```
relative to the user's home directory. (This is the file location you end up with when the git repository is cloned to /u/userid/z-java.)

The class file is output to:
```
java/target/com/blackhillsoftware/zos/ZMail.class
```
again relative to the home directory.

When running this class, the classpath must be set to "java/target" and the class specified as **com.blackhillsoftware.zos.ZMail** or **com/blackhillsoftware/zos/ZMail**.


### Run a Java Class File ###

This JCL runs the compiled Java class file created by the previous sample. DD statements define the input for the program.

```
//JOBNAME  JOB CLASS=A,
//             MSGCLASS=H, 
//             NOTIFY=&SYSUID
//*
//JAVAG    EXEC PROC=JAVAG,
// JAVACLS='com/blackhillsoftware/zos/ZMail',
// CLASPATH='java/lib/javax.mail-1.6.2.jar',
// CLASPAT2='java/lib/activation-1.1.jar'
//FROM     DD *
user1@example.com
//TO       DD *
user2@example.com
//AUTH     DD *
userid
password
//SUBJECT  DD *
Test Message from batch job
//MESSAGE  DD *
Message line 1
Message line 2
...
Last Message Line
//
```

### Compile and Run a Java Class File ###

This JCL combines the previous 2 examples. As a variation, a wildcard is used in the CLASSPATH instead of multiple CLASPATH entries.  

```
//JOBNAME  JOB CLASS=A,
//             MSGCLASS=H, 
//             NOTIFY=&SYSUID
//*
//JAVACG    EXEC PROC=JAVACG,
// JAVACLS='com/blackhillsoftware/zos/ZMail',
// SRCPATH='z-java/java/z-mail/src/main/java',
// TGTPATH='java/target',
// CLASPATH='java/lib/*'
//G.FROM     DD *
user1@example.com
//G.TO       DD *
user2@example.com
//G.AUTH     DD *
userid
password
//G.SUBJECT  DD *
Test Message from batch job
//MESSAGE  DD *
Message line 1
Message line 2
...
Last Message Line
//
```