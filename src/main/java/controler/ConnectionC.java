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
    private static String KSFile;

    @Value("${server.ssl.trust-store-password}")
    private static String KSPassword;

    private static String alias;

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


    public String sendRequest(String urlS) throws IOException {

        URL url = new URL(urlS);

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        int stanje = httpURLConnection.getResponseCode();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(httpURLConnection.getInputStream()));
        String inputL;
        StringBuffer content = new StringBuffer();
        while ((inputL = in.readLine()) != null) {
            content.append(inputL);
            System.out.println(inputL);
        }
        in.close();


        httpURLConnection.disconnect();

        return content.toString();

    }

    private static boolean existsInTrustStore(Certificate[] certificates) {

        Certificate fromTrustStore;
        try {
            fromTrustStore = readCer(alias, KSFile, KSPassword);


            try {
                fromTrustStore.verify(certificates[0].getPublicKey());

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

    public static Certificate readCer(String a, String keyStoreFile, String keyStorePass ) throws NoSuchProviderException {
        try {
            KeyStore ks = KeyStore.getInstance("JKS", "SUN");
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(keyStoreFile));
            ks.load(in, keyStorePass.toCharArray());

            if(ks.isKeyEntry(a)) {
                Certificate cert = ks.getCertificate(a);
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
