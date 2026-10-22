# HttpsTest

Program to test Java to see whether it trusts compromised eMudhra Intermediate CA certificates.

Based on information from [https://emudhra.we-have-a-problem.com/](https://emudhra.we-have-a-problem.com/)

## z/OS Instructions

Copy the java file to z/OS. Assuming the file is in EBCDIC, tag it so that recent releases of Java will compile it correctly:

```
> chtag -t -c IBM-1047 HttpsTest.java
```

Recent Java versions can run directly from source:

```
> java HttpTest.java
```

### Older Java

The program should be usable by Java 8, but needs to be compiled first:

```
> javac HttpTest.java
```

Then run the resulting file:

```
> java HttpTest
```

## Results

### Failure

If the connection is trusted, results should look like:

```
> java HttpsTest.java
Testing HTTPS connection to: https://dv-g3b-emudhra.we-have-a-problem.com/

Connection Successful!
HTTP Response Code: 200

--- Response Body ---
Your client successfully connected to the site. That's bad!

--- Server Certificates ---
Subject: CN=dv-g3b-emudhra.we-have-a-problem.com
Issuer:  CN=EM DV TLS CA - G3B, O=eMudhra BV, C=NL
----------------------------------------
Subject: CN=EM DV TLS CA - G3B, O=eMudhra BV, C=NL
Issuer:  CN=emSign Root TLS CA - G3, O=eMudhra Technologies Limited, C=IN
----------------------------------------
Subject: CN=emSign Root TLS CA - G3, O=eMudhra Technologies Limited, C=IN
Issuer:  CN=emSign ECC Root CA - G3, O=eMudhra Technologies Limited, OU=emSign PKI, C=IN
----------------------------------------
```

### Success

The program should reject the certificate and throw an exception similar to:

```
> java HttpsTest.java https://untrusted-root.badssl.com/
Testing HTTPS connection to: https://untrusted-root.badssl.com/

Connection Failed!
Error Type: javax.net.ssl.SSLHandshakeException
Message:    (certificate_unknown) PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
javax.net.ssl.SSLHandshakeException: (certificate_unknown) PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
        at java.base/sun.security.ssl.Alert.createSSLException(Alert.java:130)
        at java.base/sun.security.ssl.TransportContext.fatal(TransportContext.java:376)
        at java.base/sun.security.ssl.TransportContext.fatal(TransportContext.java:319)
        at java.base/sun.security.ssl.TransportContext.fatal(TransportContext.java:314)
        at java.base/sun.security.ssl.CertificateMessage$T12CertificateConsumer.checkServerCerts(CertificateMessage.java:651)
        at java.base/sun.security.ssl.CertificateMessage$T12CertificateConsumer.onCertificate(CertificateMessage.java:471)
        at java.base/sun.security.ssl.CertificateMessage$T12CertificateConsumer.consume(CertificateMessage.java:367)
        ...
```