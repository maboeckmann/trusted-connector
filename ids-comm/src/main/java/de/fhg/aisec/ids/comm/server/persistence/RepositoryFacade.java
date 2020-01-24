package de.fhg.aisec.ids.comm.server.persistence;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.eclipse.rdf4j.repository.util.Repositories;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;

public class RepositoryFacade {

    final private Logger logger = LoggerFactory.getLogger(RepositoryFacade.class);

    private Repository repository;

    public RepositoryFacade() {
        this("");
    }

    public RepositoryFacade(String sparqlUrl) {
        if (sparqlUrl == null || sparqlUrl.isEmpty()) {
            logger.info("Preparing memory repository");
            repository = new SailRepository(new MemoryStore());
        } else {
            logger.info("Setting SPARQL repository to be used: '" + sparqlUrl + "'");
            repository = new SPARQLRepository(sparqlUrl);
        }

        repository.initialize();
    }

    public RepositoryConnection getRepositoryConnection()
    {
        return repository.getConnection();
    }

    public Collection<Resource> getContextIds() {
        Collection<Resource> ret = new HashSet<>();
        try (RepositoryConnection repCon = repository.getConnection()) {
            RepositoryResult<Resource> result = repCon.getContextIDs();
            while (result.hasNext()) {
                ret.add(result.next());
            }
        }
        return ret;
    }

    public void addStatements(Iterable<? extends Statement> statements, Resource context) {
        try (RepositoryConnection repCon = repository.getConnection()) {
            repCon.add(statements, context);
        }
    }

    public void removeAllFromContext(Resource context) {
        try (RepositoryConnection repCon = repository.getConnection()) {
            repCon.remove(repCon.getStatements(null, null, null, context), context);
        }
    }

    public void replaceStatements(Iterable<? extends Statement> newStatements, Resource context) {
        try (RepositoryConnection repCon = repository.getConnection()) {
            repCon.begin();
            repCon.remove(repCon.getStatements(null, null, null, context), context);
            repCon.add(newStatements, context);
            repCon.commit();
        }
    }

    public <T> T query(String query, Function<TupleQueryResult, T> processFunction) {
        return Repositories.tupleQuery(repository, query, processFunction);
    }

    Model getAllStatements() {
        try (RepositoryConnection repCon = repository.getConnection()) {
            Model model = new LinkedHashModel();
            RepositoryResult<Statement> statements = repCon.getStatements(null, null, null);
            while (statements.hasNext()) {
                model.add(statements.next());
            }
            return model;
        }
    }

    public long getSize() {
        try (RepositoryConnection repCon = repository.getConnection()) {
            return repCon.size();
        }
    }

}
