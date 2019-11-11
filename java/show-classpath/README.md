## ShowClassPath ## 

This class displays the CLASSPATH environment variable and the resolved CLASSPATH entries from the system class loader. It is intended to help diagnose problems setting the CLASSPATH.
 
This class should be executable by specifying the absolute path of the class or jar file, without any dependency on the actual CLASSPATH.
 
Therefore it is important that this class does not depend on any classes other than the JRE itself, including inner or anonymous classes.

## JCL ##

This JCL uses the [JAVAC and JAVAG PROCs included with this repository](../../JCL)

### Run Jar File ###

```
//JOBNAME  JOB CLASS=A,
//             MSGCLASS=H,
//             NOTIFY=&SYSUID
//*
//JAVAG   EXEC JAVAG,
// JAVACLS='-jar java/show-classpath-1.0.0.jar',
// CLASPATH='java/lib/*' 
```

### Compile Class File ###

```
//JOBNAME  JOB CLASS=A,
//             MSGCLASS=H,
//             NOTIFY=&SYSUID 
//*
//JAVAC    EXEC JAVAC,
// JAVACLS='ShowClassPath',
// SRCPATH='z-java/java/show-classpath/src/main/java',
// TGTPATH='java/target'
```

### Run Class File ###

```
//JOBNAME  JOB CLASS=A,
//             MSGCLASS=H,
//             NOTIFY=&SYSUID
//*
//JAVAG   EXEC JAVAG,
// JAVACLS='ShowClassPath',
// TGTPATH='/u/userid/java/target',
// CLASPATH='java/lib/*' 
```