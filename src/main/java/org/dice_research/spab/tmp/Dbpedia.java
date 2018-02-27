package org.dice_research.spab.tmp;

import java.io.File;

import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.vocabulary.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dbpedia {

	static protected final boolean CONFIG_IMPORT_ONTOLOGY = false;
	static protected final boolean EXECUTE = false;

	static protected final Logger LOGGER = LoggerFactory.getLogger(Dbpedia.class);
	static protected String ONTOLOGY_FILE;
	static protected String TDB_DIRECTORY;

	public static void main(String[] args) {

		// Configure
		mainConfiguration(args);

		// Instantiate
		Dbpedia dbpedia = new Dbpedia();

		// Import DBpedia ontology
		if (CONFIG_IMPORT_ONTOLOGY) {
			Model model = ModelFactory.createDefaultModel();
			model.read(ONTOLOGY_FILE);
			dbpedia.addModel(model);
		}

		// S/P/O overview
		if (EXECUTE) {
			dbpedia.printSubjects();
		}
		if (EXECUTE) {
			dbpedia.printAllPredicates();
		}
		if (EXECUTE) {
			dbpedia.printObjects();
		}

		// Development
		if (EXECUTE) {
			dbpedia.printSubClassOf();
		}
	}

	public static void mainConfiguration(String[] args) {
		if (args.length != 2) {
			LOGGER.error("Please set correct arguments");
		} else {
			File tdbDirectory = new File(args[0]);
			if (!tdbDirectory.exists()) {
				tdbDirectory.getParentFile().mkdirs();
			}
			TDB_DIRECTORY = tdbDirectory.getPath();
			LOGGER.info("TDB directory: " + TDB_DIRECTORY);

			File ontology = new File(args[1]);
			if (!ontology.exists()) {
				LOGGER.error("Ontology file not found.");
				System.exit(1);
			}
			ONTOLOGY_FILE = ontology.getPath();
			LOGGER.info("Ontology file: " + ONTOLOGY_FILE);
		}
	}

	protected Dataset TDB;

	public Dbpedia() {
		// Create/connect to TDB
		TDB = TDBFactory.createDataset(TDB_DIRECTORY);
	}

	public void addModel(Model model) {

		LOGGER.info("Adding model");

		TDB.begin(ReadWrite.READ);
		Model datasetModel = TDB.getDefaultModel();
		TDB.end();

		TDB.begin(ReadWrite.WRITE);
		try {
			datasetModel.add(model);
			datasetModel.commit();
		} finally {
			TDB.end();
		}
	}

	public void printAllPredicates() {

		System.out.println("All predicates: ");
		SelectBuilder sb = new SelectBuilder();
		sb.setDistinct(true).addVar("p").addWhere("?s", "?p", "?o").addOrderBy("p");
		ResultSet results = QueryExecutionFactory.create(sb.build(), TDB).execSelect();
		while (results.hasNext()) {
			System.out.println(results.next().get("p"));
		}
		System.out.println();

		System.out.println("Number of predicates: ");
		Query q = QueryFactory.create("SELECT (count(distinct ?p) as ?count) WHERE {?s ?p ?o} GROUP BY ?count");
		results = QueryExecutionFactory.create(q, TDB).execSelect();
		while (results.hasNext()) {
			System.out.println(results.next().get("count").asLiteral().getLexicalForm());
		}
		System.out.println();
	}

	public void printObjects() {
		SelectBuilder sb;
		ResultSet results;
		Query q;

		System.out.println("Example objects: ");
		sb = new SelectBuilder();
		sb.setDistinct(true).addVar("o").addWhere("?s", "?p", "?o").addOrderBy("o").setLimit(10);
		results = QueryExecutionFactory.create(sb.build(), TDB).execSelect();
		while (results.hasNext()) {
			System.out.println(results.next());
		}
		System.out.println();

		System.out.println("Number of objects: ");
		q = QueryFactory.create("SELECT  (count(distinct ?o) as ?count) WHERE {?s ?p ?o} GROUP BY ?count");
		results = QueryExecutionFactory.create(q, TDB).execSelect();
		while (results.hasNext()) {
			System.out.println(results.next().get("count").asLiteral().getLexicalForm());
		}
		System.out.println();
	}

	public void printSubClassOf() {
		SelectBuilder sb;
		ResultSet results;

		// <http://dbpedia.org/datatype/fuelType>
		// <http://dbpedia.org/ontology/Restaurant>
		String resourceString = "<http://dbpedia.org/ontology/Restaurant>";

		System.out.println("Predicates for " + resourceString + " as object" + " (Limit:10)");
		sb = new SelectBuilder();
		sb.setDistinct(true).addVar("p").addWhere("?s", "?p", resourceString).addOrderBy("p").setLimit(10);
		results = QueryExecutionFactory.create(sb.build(), TDB).execSelect();
		while (results.hasNext()) {
			System.out.println(results.next());
		}
		System.out.println();

		System.out.println("Predicates for " + resourceString + " as subject" + " (Limit:10)");
		sb = new SelectBuilder();
		sb.setDistinct(true).addVar("p").addWhere(resourceString, "?p", "?o").addOrderBy("p").setLimit(10);
		results = QueryExecutionFactory.create(sb.build(), TDB).execSelect();
		while (results.hasNext()) {
			System.out.println(results.next());
		}
		System.out.println();

		System.out.println(resourceString + "as Subject" + " (Limit:10)");
		sb = new SelectBuilder();
		sb.addWhere(resourceString, "?p", "?o").setLimit(10);
		results = QueryExecutionFactory.create(sb.build(), TDB).execSelect();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			System.out.println("<>  " + solution.get("p") + "  " + solution.get("o"));
		}
		System.out.println();

		System.out.println(resourceString + "as Object" + " (Limit:10)");
		sb = new SelectBuilder();
		sb.addWhere("?s", "?p", resourceString).setLimit(10);
		results = QueryExecutionFactory.create(sb.build(), TDB).execSelect();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			System.out.println(solution.get("s") + "  " + solution.get("p") + "  <>");
		}
		System.out.println();

		System.out.println("<> subClassOf ?o" + " (Limit:10)");
		sb = new SelectBuilder();
		sb.addWhere(resourceString, RDFS.subClassOf, "?o").setLimit(10);
		results = QueryExecutionFactory.create(sb.build(), TDB).execSelect();
		while (results.hasNext()) {
			System.out.println(results.next());
		}
		System.out.println();
	}

	public void printSubjects() {
		SelectBuilder sb;
		ResultSet results;
		Query q;

		System.out.println("Example subjects: ");
		sb = new SelectBuilder();
		sb.setDistinct(true).addVar("s").addWhere("?s", "?p", "?o").addOrderBy("s").setLimit(10);
		results = QueryExecutionFactory.create(sb.build(), TDB).execSelect();
		while (results.hasNext()) {
			System.out.println(results.next());
		}
		System.out.println();

		System.out.println("Number of subjects: ");
		q = QueryFactory.create("SELECT (count(distinct ?s) as ?count) WHERE {?s ?p ?o} GROUP BY ?count");
		results = QueryExecutionFactory.create(q, TDB).execSelect();
		while (results.hasNext()) {
			System.out.println(results.next().get("count").asLiteral().getLexicalForm());
		}
		System.out.println();
	}

}