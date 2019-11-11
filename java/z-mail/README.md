## ZMail ##

This class shows how Java can be used to send email from a z/OS batch job using the javax.mail classes.

It sends email through Gmail, using TLS for the connection and logging in with a userid and password. Other mail systems are likely to have similar requirements.

The program needs to run under the JZOS Batch Launcher so that it has access to DDs from the JCL.

The program uses the following DDNAMEs:
- MESSAGE The text of the message to be sent
- SUBJECT The first line read is used as the message subject
- TO The list of recipients
- FROM The first line read is used as the From address
- AUTH The userid and password to connect to the mail server.
The first line is the userid, the second line the password. This can be a RACF protected dataset.  

Other DD names could be used to add CC, BCC functionality etc.

There are a number of ways this could be made more generic, e.g. read mail server information from environment variables, read the session properties from a DD, parse CC: and BCC: lines from the TO DD etc.

This example is kept relatively simple so that the basic mechanism is as clear as possible.

### Dependencies ###

The software is dependent on the following jars:
- javax.mail-1.6.2.jar
- activation-1.1.jar

The Maven build copies them to the target/lib directory. The executable jar built from this sample expects to find them in the ./lib subdirectory relative to the jar location.

### Run Jar File ###

```
//JOBNAME  JOB CLASS=A,
//             MSGCLASS=H, 
//             NOTIFY=&SYSUID
//*
//JAVAG    EXEC PROC=JAVAG,
// JAVACLS='-jar java/zmail-1.0.0.jar'
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


### Compile Class File ###

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

### Run Class File ###

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