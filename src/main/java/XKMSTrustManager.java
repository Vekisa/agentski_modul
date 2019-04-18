import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.X509TrustManager;
import java.net.MalformedURLException;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.cxf.xkms.client.XKMSInvoker;
import org.w3._2002._03.xkms_wsdl.XKMSService;


public class XKMSTrustManager implements X509TrustManager {

    private static final Logger LOG = LoggerFactory.getLogger(XKMSTrustManager.class);

    private XKMSInvoker xkms;

    public XKMSTrustManager() throws MalformedURLException {
        XKMSService xkmsService = new XKMSService(
                URI.create(System.getProperty("xkms.wsdl.location", "http://localhost:8040/services/XKMS/?wsdl"))
                        .toURL());
        xkms = new XKMSInvoker(xkmsService.getXKMSPort());
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        LOG.debug("Check client trust for: {}", chain);
        validateTrust(chain);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        LOG.debug("Check server trust for: {}", chain);
        validateTrust(chain);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[] {};
    }

    protected void validateTrust(X509Certificate[] chain) throws CertificateException {
        if (chain == null) {
            throw new CertificateException("Certificate chain is null");
        }

        for(X509Certificate c: chain){
            if (!xkms.validateCertificate(c)) {
                LOG.error("Certificate chain is not trusted: {}", chain);
                throw new CertificateException("Certificate chain is not trusted");

            }
        }

    }
}
