package com.blackhillsoftware.zos;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.ibm.jzos.RDWInputRecordStream;
import com.ibm.jzos.RDWOutputRecordStream;
import com.ibm.jzos.RecordReader;
import com.ibm.jzos.RecordWriter;
import com.ibm.jzos.ZFile;
import com.ibm.jzos.ZFileException;
import com.ibm.jzos.fields.daa.BinaryUnsignedIntL2Field;

/**
 * Compress : Compress and uncompress z/OS sequential datasets
 * to and from gzip format.
 * <p>
 * The gzip format data is in stream of bytes format and so can be
 * transferred to and from other systems using binary transfer without
 * data loss.
 * <p>
 * Fixed and variable length records are supported. Variable length 
 * records have the RDW with the record length stored as part of the 	
 * data when compressing. Record format information is not stored in
 * the gzip data, instead the decompression routine uses the RECFM and
 * LRECL of the output dataset. Therefore, the user needs to know the
 * correct RECFM and LRECL. Attempts to uncompress using the incorrect  
 * RECFM or LRECL (LRECL too short, for variable length records) is 
 * likely to result in wrong length record errors.
 * <p>
 * Data is stored in binary format i.e. no translation between EBCDIC
 * and ASCII/UTF8 etc. is attempted.
 * <p>
 * The compressed data on z/OS can be stored in a FB dataset
 * with any convenient LRECL. The last record will be padded as required,
 * the extra data is ignored by gzip. 
 * <p>
 * Data can be uncompressed on non-z/OS systems, and processed by programs 
 * if they understand the z/OS data records.
 * <p>
 * Likewise, data can be written in z/OS format i.e. fixed length 
 * records (without delimiters) or variable length records with RDW
 * on a non-z/OS system, compressed, transferred and uncompressed into 
 * a z/OS sequential dataset. 
 * 
 * @author Andrew Rowley
 *
 */
public class Compress 
{
    public static void main(String[] args) throws IOException 
    {
    	if (args.length == 0)
    	{
    		doCompress();
    	}
    	else if (args[0].equals("-d"))
		{
			doUncompress();
		}
		else
		{
			System.out.println("Usage:");
			System.out.println("Compress");
			System.out.println(" - Read data from DD INPUT and write gzip format");
			System.out.println("   compressed data to DD OUTPUT");
			System.out.println("Compress -d");
			System.out.println(" - Read gzip format compressed data from DD INPUT");
			System.out.println("   and write uncompressed data to DD OUTPUT");
		}
    }
    
	static final int RDWLENGTH = 4; 
	static final BinaryUnsignedIntL2Field recordlength = new BinaryUnsignedIntL2Field(0);

	private static void doCompress() throws ZFileException, IOException 
	{
		long recordCount = 0;
        long bytesIn = 0;
        long bytesOut = 0;
        
        RecordReader input = null;
        ZFile zFileOut = null;
        OutputStream outstream = null;
    	RDWOutputRecordStream rdwoutstream = null;
               
        try 
        {
        	// Read records from input file
        	input = RecordReader.newReaderForDD("INPUT");
        	
        	// Treat the output file as a stream, but it is likely to be
        	// e.g. FB, LRECL = anything.
        	zFileOut = new ZFile("//DD:OUTPUT", "wb,noseek");
        	
            // If the input is RECFM V then we also need to write RDWs with the 
            // record length into the output stream
        	boolean recfmV = 
        		(input.getRecfmBits() & RecordReader.RECFM_V) == RecordReader.RECFM_V;       	      	
    
        	byte[] buffer = new byte[input.getLrecl()];         
        	outstream = new BufferedOutputStream(
        			new GZIPOutputStream(zFileOut.getOutputStream()));
        	
        	if (recfmV)
        	{	
        		rdwoutstream = new RDWOutputRecordStream(outstream);
        	}
        		       	
    		int bytesRead; 
            while ((bytesRead = input.read(buffer)) >= 0) 
            {
                recordCount++;
                bytesIn += recfmV ? bytesRead + RDWLENGTH : bytesRead;
                if (recfmV)
                {
                    rdwoutstream.write(buffer, 0, bytesRead);
                }
                else
                {
                	outstream.write(buffer, 0, bytesRead);
                }
            }
            // flush stream to get best estimate of output bytes
            // from zFileOut. There may still be some bytes in the
            // compressor because we didn't use syncFlush = true 
            // when creating the GZIPOutputStream.
            outstream.flush();
            bytesOut = zFileOut.getByteCount();      	
        }
        finally 
        {
        	if (input != null) 
        	{
	        	try 
	        	{
	    	       input.close(); 
	    	    } 
	        	catch (ZFileException zfe) 
	        	{
	    	       zfe.printStackTrace();
	    	    }    
        	}
        	if (rdwoutstream != null) 
        	{
	        	try 
	        	{
	        		rdwoutstream.close(); 
	    	    } 
	        	catch (IOException ioe) 
	        	{
	    	       ioe.printStackTrace();
	    	    }    
        	}
        	else if (outstream != null) 
        	{
	        	try 
	        	{
	        		outstream.close(); 
	    	    } 
	        	catch (IOException ioe) 
	        	{
	    	       ioe.printStackTrace();
	    	    }    
        	}
        }
        
        System.out.format("Records     : %,15d%n", recordCount);
        System.out.format("Bytes In    : %,15d%n", bytesIn);
        System.out.format("Bytes Out   : %,15d (approximately)%n", bytesOut);
        System.out.format("Compression : %,15.1f%%%n",
                ((double) bytesIn - bytesOut) / bytesIn * 100);
	}
	
	private static void doUncompress() throws ZFileException, IOException 
	{
		long recordCount = 0;
        long bytesIn = 0;
        long bytesOut = 0;
        
        ZFile zFileIn = null;
        InputStream inputstream = null;
        RDWInputRecordStream rdwinputstream = null;
        RecordWriter output = null;            
        		
        try 
        {
        	// Treat the input file as a stream, but it is likely to be
        	// e.g. FB, LRECL = anything.
        	zFileIn = new ZFile("//DD:INPUT", "rb,noseek");

        	// Output file
        	output = RecordWriter.newWriterForDD("OUTPUT");  	
        	        	
            // GZIP format does not include information about the RECFM,
        	// LRECL etc.
        	// We rely on the creator of the JCL to know the correct 
        	// output dataset characteristics, and use those to work out 
        	// how to treat the input data. If the output dataset is incorrect
        	// e.g. RECFM V(B) when the original data was F(B) we can expect 
        	// errors due to treating record data as RDWs.
        	
         	boolean recfmV = (output.getRecfmBits() & RecordWriter.RECFM_V) == RecordWriter.RECFM_V;
         	int lrecl = output.getLrecl();
        	byte[] buffer = new byte[lrecl];         
            
			// GZIPInputStream can return less than the requested
			// number of bytes even if more data will be returned 
			// by another read. BufferedInputStream performs multiple
			// reads on the inner stream and returns the number of bytes
			// requested if available
        	
            inputstream = new BufferedInputStream(
            	new GZIPInputStream(zFileIn.getInputStream()));
            
        	if (recfmV)
        	{	
        		rdwinputstream = new RDWInputRecordStream(inputstream);
        	}
            
        	int bytesRead;
        	while ((bytesRead = 
            			(recfmV ?
            					rdwinputstream.read(buffer) : 
            					inputstream.read(buffer, 0, lrecl))) 
        			!= -1)
        	{
        		if (!recfmV && bytesRead != lrecl)
        		{
        			throw new IOException("Wrong length record: Tried to read " 
        					+ Integer.toString(lrecl) 
        					+ "bytes, read " 
        					+ Integer.toString(bytesRead));
        		}
        		recordCount++;
        		bytesOut += recfmV ? bytesRead + RDWLENGTH : bytesRead;
        		output.write(buffer, 0, bytesRead);
        	}
    		bytesIn = zFileIn.getByteCount();
                   
        }
        finally 
        {
        	if (rdwinputstream != null) 
        	{
	        	try 
	        	{
	        		rdwinputstream.close(); 
	    	    } 
	        	catch (IOException ioe) 
	        	{
	    	       ioe.printStackTrace();
	    	    }    
        	}
        	if (inputstream != null) 
        	{
	        	try 
	        	{
	        		inputstream.close(); 
	    	    } 
	        	catch (IOException ioe) 
	        	{
	    	       ioe.printStackTrace();
	    	    }    
        	}
        	if (output != null) 
        	{
	        	try 
	        	{
	        		output.close(); 
	    	    } 
	        	catch (ZFileException zfe) 
	        	{
	    	       zfe.printStackTrace();
	    	    }    
        	}
        }
        
        System.out.format("Records     : %,15d%n", recordCount);
        System.out.format("Bytes In    : %,15d%n", bytesIn);
        System.out.format("Bytes Out   : %,15d%n", bytesOut);
	}
}
