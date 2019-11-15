## Compress ##

This sample provides a simple program for compressing and uncompressing z/OS datasets using gzip format. This program is designed to operate on z/OS datasets using JCL, rather than files stored in the Unix filesystem.

The gzip format data is in stream of bytes format and so can be transferred to and from other systems using binary transfer without data loss.

Gzip format does not provide a simple way to store z/OS dataset attributes. This program does not attempt to store that information, instead the end user needs to know the original dataset attributes and allocate a suitable output dataset for the uncompress step. Attempts to uncompress using the incorrect RECFM or LRECL (LRECL too short, for variable length records) is likely to result in wrong length record errors.

Dataset attributes have no meaning on non-z/OS platforms so the data can simply be uncompressed to a file.

Fixed and variable length records are supported. Variable length records have the RDW with the record length stored as part of the data when compressing.

Data is stored in binary format i.e. no translation between EBCDIC and ASCII/UTF8 etc. is attempted.

The compressed data on z/OS can be stored in a FB dataset with any convenient LRECL. The last record will be padded as required, the extra data is ignored by gzip. 

Data can be uncompressed on non-z/OS systems, and processed by programs that understand the z/OS data records.

Likewise, data can be written in z/OS format on a non-z/OS system i.e. fixed length records (without delimiters) or variable length records including RDW, compressed, transferred and uncompressed into a z/OS sequential dataset.

### Performance ###

Compression and CPU performance was compared to Gzip and TERSE compressing a file containing SMF data. Java was very similar to Gzip with the advantage that most of the CPU time was on the zIIP. This system did not have zEDC available - however both gzip (ported by Rocket Software) and Java should take advantage of zEDC for further CPU reduction.

|Program       | CP Time | zIIP Time | Total CPU | Compressed Size|
|--------------|---------|-----------|-----------|----------------|
|Java          |      1s |       72s |       73s |    21%         |
|gzip          |     70s |        0  |       70s |    21%         |
|TERSE (PACK)  |     53s |        0  |       53s |    45%         |
|TERSE (SPACK) |    124s |        0  |      124s |    23%         |

## JCL ##

This JCL uses the [JAVAC and JAVAG PROCs included with this repository](../../JCL)

### Run Jar File (Compress) ###

```
//JOBNAME  JOB CLASS=A,
//             MSGCLASS=H,
//             NOTIFY=&SYSUID 
//*               
//JAVAG   EXEC JAVAG,          
// JAVACLS='-jar java/compress-1.0.0.jar',
//INPUT    DD  DISP=SHR,DSN=HLQ.SMF.RECORDS(0)
//OUTPUT   DD DISP=(NEW,CATLG), 
//     DSN=HLQ.SMF.DATA.GZ,  
//     SPACE=(TRK,(1000,1000),RLSE),
//     LRECL=80,BLKSIZE=27920,RECFM=FB, 
//     UNIT=SYSDA
```
### Run Jar File (Uncompress) ###

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

### Compile Class File ###

```
//JOBNAME  JOB CLASS=A, 
//             MSGCLASS=H,
//             NOTIFY=&SYSUID    
//*
//JAVAC    EXEC JAVAC,                          
// JAVACLS='com/blackhillsoftware/zos/Compress',
// SRCPATH='z-java/java/compress/src/main/java',
// TGTPATH='java/target'
```
### Run Class File (Compress) ###

```
//JOBNAME  JOB CLASS=A,
//             MSGCLASS=H,
//             NOTIFY=&SYSUID 
//*               
//JAVAG   EXEC JAVAG,          
// JAVACLS='com/blackhillsoftware/zos/Compress',
// TGTPATH='java/target'
//INPUT    DD  DISP=SHR,DSN=HLQ.SMF.RECORDS(0)
//OUTPUT   DD DISP=(NEW,CATLG), 
//     DSN=HLQ.SMF.DATA.GZ,  
//     SPACE=(TRK,(1000,1000),RLSE),
//     LRECL=80,BLKSIZE=27920,RECFM=FB, 
//     UNIT=SYSDA
```
### Run Class File (Uncompress) ###

```
//JOBNAME  JOB CLASS=A, 
//             MSGCLASS=H,
//             NOTIFY=&SYSUID 
//*
//JAVAG   EXEC JAVAG,
// JAVACLS='com/blackhillsoftware/zos/Compress',
// TGTPATH='java/target',
// ARGS='-d'
//INPUT    DD  DISP=SHR,DSN=HLQ.SMF.DATA.GZ
//OUTPUT   DD DISP=(NEW,CATLG),
//     DSN=HLQ.SMF.DATA,
//     SPACE=(TRK,(1000,1000),RLSE),
//     LRECL=32760,BLKSIZE=0,RECFM=VBS, 
//     UNIT=SYSDA 
```