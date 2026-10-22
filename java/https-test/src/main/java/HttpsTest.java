import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.cert.*;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;

public class HttpsTest 
{
    public static void main(String[] args) 
    {
        String urlString = (args.length > 0) ? args[0] : "https://dv-g3b-emudhra.we-have-a-problem.com/";
        
        System.out.println("Testing HTTPS connection to: " + urlString);
        
        try {
            URL url = new URL(urlString);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            
            // Set reasonable timeouts
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            // Trigger the TLS handshake and connection
            connection.connect();
            
            // Output connection results
            int responseCode = connection.getResponseCode();
            System.out.println("\nConnection Successful!");
            System.out.println("HTTP Response Code: " + responseCode);

            // Capture certs before reading the body - closing the stream
            // can leave HttpsURLConnection in a state where this fails
            Certificate[] certs = connection.getServerCertificates();

            // Print response body (error stream for 4xx/5xx)
            InputStream stream = (responseCode >= 400)
                    ? connection.getErrorStream()
                    : connection.getInputStream();
            System.out.println("\n--- Response Body ---");
            if (stream != null) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }
            } else {
                System.out.println("(empty)");
            }
            
            // Print Server Certificates
            System.out.println("\n--- Server Certificates ---");
            for (Certificate cert : certs) {
                if (cert instanceof X509Certificate) {
                    X509Certificate x509 = (X509Certificate) cert;
                    System.out.println("Subject: " + x509.getSubjectX500Principal());
                    System.out.println("Issuer:  " + x509.getIssuerX500Principal());
                    System.out.println("----------------------------------------");
                }
            }
            
            connection.disconnect();
            
        } catch (IOException e) 
        {
            System.err.println("\nConnection Failed!");
            System.err.println("Error Type: " + e.getClass().getName());
            System.err.println("Message:    " + e.getMessage());

            e.printStackTrace();
        }
    }
}

