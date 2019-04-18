package controler;

import org.springframework.beans.factory.annotation.Value;

import javax.net.ssl.SSLSession;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

public class ConnectionC {
    @Value("${server.ssl.trust-store}")
    private static String trustStoreFile;

    @Value("${server.ssl.trust-store-password}")
    private static String trustStorePassword;

    private static String trustedAlias = "mba";

    static {
        //for localhost testing only
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier(){

                    public boolean verify(String hostname, SSLSession sslSession) {

                        if (hostname.equals("localhost")) {
                            return true;
                        } else
                            return false;
                    }
                });
    }


    public String getMethod(String urlString) throws IOException {

        URL url = new URL(urlString);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
            System.out.println(inputLine);
        }
        in.close();


        con.disconnect();

        return content.toString();

    }

    private static boolean existsInTrustStore(Certificate[] certificates) {

        Certificate certFromTrustSture;
        try {
            certFromTrustSture = readCertificate(trustStoreFile, trustStorePassword, trustedAlias);


            try {
                certFromTrustSture.verify(certificates[0].getPublicKey());

            } catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException
                    | SignatureException e) {
                e.printStackTrace();
                return false;
            }

        } catch (NoSuchProviderException e1) {
            e1.printStackTrace();
        }

        return true;


    }

    public static Certificate readCertificate(String keyStoreFile, String keyStorePass, String alias) throws NoSuchProviderException {
        try {
            KeyStore ks = KeyStore.getInstance("JKS", "SUN");
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            ks.load(in, keyStorePass.toCharArray());

            if(ks.isKeyEntry(alias)) {
                Certificate cert = ks.getCertificate(alias);
                return cert;

            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
