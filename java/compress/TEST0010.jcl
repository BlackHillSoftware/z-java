//JOBNAME  JOB CLASS=A,
//             MSGCLASS=H,
//             NOTIFY=&SYSUID
//*
//* Test java compression between z/OS datasets and gzip format.
//* This job has 4 steps:
//* 1) Compress a z/OS dataset into gzip format
//* 2) Uncompress the result using gzip, and recompress
//*    using gzip (done in 1 step using pipe)
//* 3) Uncompress the result again using the java program
//* 4) Compare the result with the original dataset
//*
//* All steps should end RC 0.
//*
//* Step 2 verifies that the output of the original compression
//* is readable using gzip, and the decompression routine can
//* read data compressed using gzip.
//*
//* Prerequisites for this specific job:
//* - Co:Z Dataset Pipes from Dovetailed Technologies
//* - gzip e.g. from Rocket Software
//*
// EXPORT SYMLIST=(GZIP,COZDIR)
//*
// SET TESTDATA=YOUR.FB.OR.VB.PS.DATASET
//*
// SET COMPRESS='''-jar java/compress-1.0.0.jar'''
// SET GZIP='/usr/local/gzip/bin/gzip'
// SET COZDIR='/usr/local/coz/bin'
// SET COZLLIB=SYS1.COZ.LINKLIB
//*
//COMPRESS EXEC PROC=JAVAG,
// JAVACLS=&COMPRESS
//INPUT    DD  DISP=SHR,DSN=&TESTDATA
//OUTPUT   DD DISP=(,PASS),
//     SPACE=(CYL,(10,10),RLSE),
//     LRECL=80,BLKSIZE=0,RECFM=FB,
//     UNIT=SYSDA
//*
//GZIP      EXEC PGM=COZBATCH
//STEPLIB   DD  DISP=SHR,DSN=&COZLLIB
//IN       DD  DISP=(OLD,PASS),DSN=*.COMPRESS.G.OUTPUT
//OUT      DD DISP=(,PASS),
//     SPACE=(CYL,(10,10),RLSE),
//     LRECL=80,BLKSIZE=0,RECFM=FB,
//     UNIT=SYSDA
//SYSOUT    DD SYSOUT=*
//STDOUT    DD SYSOUT=*
//STDERR    DD SYSOUT=*
//STDIN     DD *,SYMBOLS=JCLONLY
&COZDIR/fromdsn -b DD:IN | \
  &GZIP -d | \
  &GZIP | \
  &COZDIR/todsn -b DD:OUT
//*
//UNCOMPR  EXEC PROC=JAVAG,
// JAVACLS=&COMPRESS,
// ARGS='-d'
//INPUT    DD  DISP=(OLD,PASS),DSN=*.GZIP.OUT
//OUTPUT   DD DISP=(,PASS),
//     SPACE=(CYL,(10,10),RLSE),
//     DCB=&TESTDATA,
//     UNIT=SYSDA
//*
//COMPARE EXEC PGM=ISRSUPC,PARM=(FILECMP)
//OLDDD    DD DSN=&TESTDATA,
//           DISP=SHR
//NEWDD    DD DSN=*.UNCOMPR.G.OUTPUT,
//           DISP=(OLD,PASS)
//OUTDD    DD SYSOUT=*
//*