//JAVAC   PROC JAVACLS=,
//  REGSIZE='0M',
//* Parameters for instream data need triple quotes
//* to preserve quotes in the SET statement
//  SRCPATH='''java/src''',
//  TGTPATH='''java/target''',
//  CLASPATH='''''',
//* Additional CLASPAT* entries allow CLASSPATH to
//* be extended with less JCL continuation issues.
//  CLASPAT2='''''',
//  CLASPAT3='''''',
//  CLASPAT4='''''',
//  CLASPAT5='''''',
//* Options to the java compiler
//  JAVACOPT=''''''
//*
//SYMBOLS  EXPORT SYMLIST=(JAVACOPT,
//     SRCPATH,
//     TGTPATH,
//     CLASPATH,
//     CLASPAT2,
//     CLASPAT3,
//     CLASPAT4,
//     CLASPAT5,
//     JAVACLS)
//*
//         SET JAVACLS=&JAVACLS
//         SET SRCPATH=&SRCPATH
//         SET TGTPATH=&TGTPATH
//         SET CLASPATH=&CLASPATH
//         SET CLASPAT2=&CLASPAT2
//         SET CLASPAT3=&CLASPAT3
//         SET CLASPAT4=&CLASPAT4
//         SET CLASPAT5=&CLASPAT5
//         SET JAVACOPT=&JAVACOPT
//*
//C        EXEC PGM=BPXBATCH,REGION=&REGSIZE
//* STDPARM is multiple commands chained into one with
//* semicolons as an argument to "SH"
//* IFS splits CLASSPATH based on ":"
//* Then wildcards are expanded and recombined
//STDPARM  DD *,SYMBOLS=JCLONLY
SH CLASSPATH=&TGTPATH;
 IFS=":";
 for i in &CLASPATH; do
    for j in ${i}; do
       CLASSPATH="${CLASSPATH}":"${i}";
    done;
 done;
 for i in &CLASPAT2; do
    for j in ${i}; do
       CLASSPATH="${CLASSPATH}":"${i}";
    done;
 done;
 for i in &CLASPAT3; do
    for j in ${i}; do
       CLASSPATH="${CLASSPATH}":"${i}";
    done;
 done;
 for i in &CLASPAT4; do
    for j in ${i}; do
       CLASSPATH="${CLASSPATH}":"${i}";
    done;
 done;
 for i in &CLASPAT5; do
    for j in ${i}; do
       CLASSPATH="${CLASSPATH}":"${i}";
    done;
 done;
 export CLASSPATH="${CLASSPATH}";
 SRCPATH=&SRCPATH;
 TGTPATH=&TGTPATH;
 JAVACLS=&JAVACLS;
 /usr/lpp/java/J8.0/bin/javac
 &JAVACOPT
 -sourcepath
 ${SRCPATH}
 -d
 ${TGTPATH}
 ${SRCPATH}/${JAVACLS}.java
//STDENV   DD DDNAME=ENV
//         DD DDNAME=ADDENV
//ADDENV   DD DISP=SHR,DSN=NULLFILE
//ENV      DD *
HJV_JZOS_JVM_SMF_LOGGING=true
HJV_JZOS_JVM_SMF_LOGGING_INTERVAL=10
HJV_JZOS_JVM_SMF_THREADS=true
//STDOUT   DD SYSOUT=*
//STDERR   DD SYSOUT=*
//*
// PEND
