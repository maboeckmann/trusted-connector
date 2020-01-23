package de.fhg.aisec.ids.comm.server;

import de.fhg.aisec.ids.comm.server.persistence.RepositoryFacade;
import de.fhg.aisec.ids.messages.AttestationProtos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;


public class TestServer {
    RepositoryFacade repositoryFacade;
    IdscpServer server;
    public TestServer()
    {

    }

    RepositoryFacade getRepositoryFacade()
    {
        return repositoryFacade;
    }
    public static void main(String[] args) {
        final KeyStore ks;
        final String sparqlEndpointUrl = "";
        final Path jssePath = Path.of("/etc/trusted-connector-certs/jsse");

        try {
            ks = KeyStore.getInstance("JKS");
            ks.load(
                    Files.newInputStream(jssePath.resolve("server-keystore.jks")), "password".toCharArray());
            TestServer testServer = new TestServer();
            SocketListener listener = new MySocketListener(testServer);

            testServer.server = new IdscpServer()
                    .config(new ServerConfiguration.Builder()
                            .port(8081)
                            .attestationType(AttestationProtos.IdsAttestationType.BASIC)
                            .setKeyStore(ks)
                            .setKeyStorePassword("password")
                            .build())
                    .setSocketListener(listener)
                    .start();

            testServer.repositoryFacade = new RepositoryFacade(sparqlEndpointUrl);

        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
