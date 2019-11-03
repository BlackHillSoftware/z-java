package com.blackhillsoftware.zos;

import java.io.*;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

import com.ibm.jzos.ZFile;
import com.ibm.jzos.ZFileException;

/**
 * This class shows how Java can be used to send email from 
 * a z/OS batch job using the javax.mail classes.
 * <p>
 * It sends email through Gmail, using TLS for the connection and
 * logging in with a userid and password. Other mail systems are
 * likely to have similar requirements.
 * <p>
 * The program needs to run under the JZOS Batch Launcher so that
 * it has access to DDs from the JCL.
 * <p>
 * The program uses the following DDNAMEs:
 * <ul>
 * <li> MESSAGE The text of the message to be sent
 * <li> SUBJECT The first line read is used as the message subject
 * <li> TO The list of recipients
 * <li> FROM The first line read is used as the From address
 * <li> AUTH The userid and password to connect to the mail server.
 * The first line is the userid, the second line the password.
 * This can be a RACF protected dataset.  
 * </ul>
 * <p>
 * Other DD names could be used to add CC, BCC functionality etc.
 * <p>
 * There are a number of ways this could be made more generic, e.g.
 * read mail server information from environment variables, read
 * the session properties from a DD, parse CC: and BCC: lines from 
 * the TO DD etc.
 * <p>
 * This example is kept relatively simple so that the basic 
 * mechanism is as clear as possible.
 * <h3>Dependencies
 * <p>
 * The software is dependent on the following jars:
 * <ul>
 * <li>javax.mail-api-1.6.1.jar
 * <li>activation-1.1.jar
 * </ul>
 * The Maven build copies them to the target/lib directory. The 
 * executable jar built from this sample expects to find them in 
 * the ./lib subdirectory relative to the jar location.  
 * 
 * @author Andrew Rowley
 *
 */

public class ZMail 
{
    public static void main(String[] args) 
            throws IOException, MessagingException 
    {	
        String subject = readLinesFromDD("SUBJECT").get(0);
        String from = readLinesFromDD("FROM").get(0);
        List<String> recipients = readLinesFromDD("TO");		

        List<String> messagelines = readLinesFromDD("MESSAGE");
        StringBuilder messagetext = new StringBuilder();
        for (String line : messagelines)
        {
            messagetext.append(line);
            messagetext.append(System.lineSeparator());
        }

        List<String> auth = readLinesFromDD("AUTH");
        String username = auth.get(0);
        String password = auth.get(1);

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.ssl.checkserveridentity", "true");

        Session session = Session.getInstance(props);
        
        // session.setDebug(true); // if you need to debug the connection
        
        Message message = new MimeMessage(session);
        
        for (String recipient : recipients)
        {
            message.addRecipients(Message.RecipientType.TO, 
                    InternetAddress.parse(recipient));
        }
        message.setFrom(new InternetAddress(from));       
        message.setSubject(subject);
        message.setText(messagetext.toString());

        Transport.send(message, username, password);		
    }

    /**
     * Read input from an allocated DD. Input is read in text mode
     * and returned as a list of strings.
     * @param dd The DDNAME to read from
     * @return List<String> containing the records read from the DD
     * @throws ZFileException
     * @throws IOException
     */
    private static List<String> readLinesFromDD(String dd) 
            throws ZFileException, IOException 
    {
        List<String> lines = new ArrayList<String>();
        ZFile input = null;
        try
        {
            input = new ZFile("//DD:" + dd, "rt");
            BufferedReader reader = 
                    new BufferedReader(
                            new InputStreamReader(input.getInputStream()));

            String line;     
            while ((line = reader.readLine()) != null) 
            {	
                lines.add(line);
            }

        }
        finally
        {
            if (input != null)
            {
                input.close();
            }
        }
        return lines;
    }
}
