## Compress ##

Compress and uncompress z/OS sequential datasets to and from gzip format.

This sample provides a simple program for compressing and uncompressing z/OS datasets using gzip format. This program is designed to operate on z/OS datasets using JCL, rather than files stored in the Unix filesystem.

The gzip format data is in stream of bytes format and so can be transferred to and from other systems using binary transfer without data loss.

Gzip format does not provide a simple way to store z/OS dataset attributes. This program does not attempt to store that information, instead the end user needs to know the original dataset attributes and allocate a suitable output dataset for the uncompress step. Attempts to uncompress using the incorrect RECFM or LRECL (LRECL too short, for variable length records) is likely to result in wrong length record errors.

Dataset attributes have no meaning on non-z/OS platforms so the data can simply be uncompressed to a file.

Fixed and variable length records are supported. Variable length records have the RDW with the record length stored as part of the data when compressing.

Data is stored in binary format i.e. no translation between EBCDIC and ASCII/UTF8 etc. is attempted.

The compressed data on z/OS will typically be stored in a FB dataset with any convenient LRECL. The last record will be padded as required, the extra data is ignored by gzip. 

Data can be uncompressed on non-z/OS systems, and processed if they understand the z/OS data records.

Likewise, data can be written in z/OS format i.e. fixed length records (without delimiters) or variable length records with RDW on a non-z/OS system, compressed, transferred and uncompressed into a z/OS sequential dataset.

### Performance ###

Compression and CPU performance was compared to Gzip and TERSE compressing a file containing SMF data. Java was very similar to Gzip with the advantage that most of the CPU time was on the zIIP. This system did not have zEDC available - however both gzip (ported by Rocket Software) and Java should take advantage of zEDC for further CPU reduction.

|Program       | CP Time | zIIP Time | Total CPU | Compressed Size|
|--------------|---------|-----------|-----------|----------------|
|Java          |      1s |       72s |       73s |    21%         |
|gzip          |     70s |        0  |       70s |    21%         |
|TERSE (PACK)  |     53s |        0  |       53s |    45%         |
|TERSE (SPACK) |    124s |        0  |      124s |    23%         |


