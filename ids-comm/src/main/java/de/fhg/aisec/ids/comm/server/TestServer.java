package de.fhg.aisec.ids.comm.server;

import de.fhg.aisec.ids.messages.AttestationProtos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;


public class TestServer {
    public static void main(String[] args) {
        final KeyStore ks;
        final Path jssePath = Path.of("/etc/trusted-connector-certs/jsse");
        try {
            ks = KeyStore.getInstance("JKS");
            ks.load(
                    Files.newInputStream(jssePath.resolve("server-keystore.jks")), "password".toCharArray());

            SocketListener listener = new MySocketListener();
            IdscpServer server = new IdscpServer()
                    .config(new ServerConfiguration.Builder()
                            .port(8081)
                            .attestationType(AttestationProtos.IdsAttestationType.BASIC)
                            .setKeyStore(ks)
                            .setKeyStorePassword("password")
                            .build())
                    .setSocketListener(listener)
                    .start();



        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
