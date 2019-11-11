//JAVAG  PROC JAVACLS=,
//  ARGS=,
//  LIBRARY='VENDOR.LINKLIBE',
//  LOGLVL='',
//  REGSIZE='0M',
//  LEPARM='',
//  SRCPATH='java/src',
//  TGTPATH='java/target',
//  CLASPATH='',
//* Additional CLASPAT* entries allow CLASSPATH to
//* be extended with less JCL continuation issues.
//  CLASPAT2='',
//  CLASPAT3='',
//  CLASPAT4='',
//  CLASPAT5=''
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
//         SET QT=''''
//         SET JAVACLS=&QT&JAVACLS&QT
//         SET SRCPATH=&QT&SRCPATH&QT
//         SET TGTPATH=&QT&TGTPATH&QT
//         SET CLASPATH=&QT&CLASPATH&QT
//         SET CLASPAT2=&QT&CLASPAT2&QT
//         SET CLASPAT3=&QT&CLASPAT3&QT
//         SET CLASPAT4=&QT&CLASPAT4&QT
//         SET CLASPAT5=&QT&CLASPAT5&QT
//*
//G        EXEC PGM=JVMLDM80,REGION=&REGSIZE,
//   PARM='&LEPARM/&LOGLVL &JAVACLS &ARGS'
//*
//STEPLIB  DD DSN=&LIBRARY,DISP=SHR
//SYSPRINT DD SYSOUT=*
//SYSOUT   DD SYSOUT=*
//STDOUT   DD SYSOUT=*
//STDERR   DD SYSOUT=*
//CEEDUMP  DD SYSOUT=*
//ABNLIGNR DD DUMMY
//STDENV   DD DDNAME=ENV
//         DD DDNAME=ADDENV
//ADDENV   DD DISP=SHR,DSN=NULLFILE
//ENV      DD *,SYMBOLS=JCLONLY
. /etc/profile
export JAVA_HOME=/usr/lpp/java/J8.0
export PATH=/bin:"${JAVA_HOME}"/bin

LIBPATH=/lib:/usr/lib:"${JAVA_HOME}"/bin
LIBPATH="$LIBPATH":"${JAVA_HOME}"/lib/s390
LIBPATH="$LIBPATH":"${JAVA_HOME}"/lib/s390/j9vm
LIBPATH="$LIBPATH":"${JAVA_HOME}"/bin/classic
export LIBPATH="$LIBPATH":

APP_HOME=&TGTPATH
CLASSPATH="${APP_HOME}"
IFS=':'
for i in &CLASPATH; do
    for j in ${i}; do
        CLASSPATH="${CLASSPATH}":"${j}"
        done
    done
for i in &CLASPAT2; do
    for j in ${i}; do
        CLASSPATH="${CLASSPATH}":"${j}"
        done
    done
for i in &CLASPAT3; do
    for j in ${i}; do
        CLASSPATH="${CLASSPATH}":"${j}"
        done
    done
for i in &CLASPAT4; do
    for j in ${i}; do
        CLASSPATH="${CLASSPATH}":"${j}"
        done
    done
for i in &CLASPAT5; do
    for j in ${i}; do
        CLASSPATH="${CLASSPATH}":"${j}"
        done
    done
export CLASSPATH="${CLASSPATH}"

IJO="-Xms16m -Xmx128m"
export IBM_JAVA_OPTIONS="$IJO "

//*export HJV_JZOS_JVM_SMF_LOGGING=true
//*export HJV_JZOS_JVM_SMF_LOGGING_INTERVAL=10
//*export HJV_JZOS_JVM_SMF_THREADS=true
// PEND