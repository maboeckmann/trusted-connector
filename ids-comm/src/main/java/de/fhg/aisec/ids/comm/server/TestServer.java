package de.fhg.aisec.ids.comm.server;

import de.fhg.aisec.ids.comm.server.persistence.RepositoryFacade;
import de.fhg.aisec.ids.messages.AttestationProtos;
import org.apache.jena.fuseki.main.FusekiServer;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.util.FileUtils;
import org.topbraid.spin.util.JenaUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;


public class TestServer {
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

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
        final String sparqlEndpointUrl = "http://localhost:3332/patient/sparql";
        final Path jssePath = Path.of("/etc/trusted-connector-certs/jsse");

        try {
            ks = KeyStore.getInstance("JKS");
            ks.load(
                    Files.newInputStream(jssePath.resolve("iais1-keystore.jks")), "password".toCharArray());
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
            Path datasetPath = Paths.get(new URI("file:/home/mboeckmann/Dataset/med2icin-triplesPatient43.ttl"));
            Model dataModel = JenaUtil.createMemoryModel();
            dataModel.read(datasetPath.toString(), FileUtils.langTurtle);
/*            StmtIterator iterator = dataModel.listStatements();
            while(iterator.hasNext())
            {
                Statement current = iterator.nextStatement();
                System.out.println(current.getSubject().toString() + " " + current.getPredicate().toString() + " " + current.getObject().toString());
            }
*/            Dataset ds = DatasetFactory.create(dataModel);
            FusekiServer fusekiServer = FusekiServer.create()
                    .add("/patient", ds)
                    .port(3332)
                    .build();
            fusekiServer.start();
/*
            TupleQuery q = testServer.repositoryFacade.getRepositoryConnection().prepareTupleQuery(QueryLanguage.SPARQL,"SELECT ?s ?p ?o WHERE { ?s ?p ?o . }");
            TupleQueryResult result = q.evaluate();
            while(result.hasNext())
            {
                BindingSet next = result.next();
                System.out.println(next.getValue("s").toString());
            }
*/
        } catch (KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
