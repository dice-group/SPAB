package org.dice_research.spab;

import java.util.ArrayList;
import java.util.List;

import org.dice_research.spab.exceptions.SpabException;
import org.dice_research.spab.structures.CandidateVertex;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class SimpleScenarioBasedTest extends AbstractTestCase {

    public static final double DELTA = 0.0000001;

    @Parameters
    public static List<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<>();

        // 0
        // Simple example where a perfect solution is possible
        testConfigs.add(new Object[] { new String[] {
                "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . }",
                "SELECT ?a ?b WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . }",
                "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . }" },
                new String[] {
                        "INSERT { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . } WHERE { ?b <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?a . }" },
                1.0, new String("Scenario-" + testConfigs.size()) });

        // 1
        // Two positive examples that are sharing the same WHERE clause
        testConfigs.add(new Object[] { new String[] {
                "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b .  ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . }",
                "INSERT { ?a <http://example.org#type> <http://example.org#Typed> . } WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b .  ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . }", },
                new String[] { "SELECT ?a ?b WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . }" },
                1.0, new String("Scenario-" + testConfigs.size()) });

        // 2
        // Two positive examples are exactly the same but with different
        // variable names and a different order of their triples
        testConfigs.add(new Object[] { new String[] {
                "SELECT ?a ?b ?l WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . ?b <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . ?b <http://www.w3.org/2000/01/rdf-schema#label> ?l . }",
                "SELECT ?v1 ?v3 ?v2 WHERE { ?v1 <http://www.w3.org/2000/01/rdf-schema#label> ?v2 . ?v1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . ?v3 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?v1 . }", },
                new String[] {
                        "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . ?b <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . }" },
                1.0, new String("Scenario-" + testConfigs.size()) });

        // 3
        // Two positive examples that have two triples in their WHERE clause
        // while the negative example has only one (although it is the same as
        // one of the positive example triples)
        testConfigs.add(new Object[] { new String[] {
                "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b .  ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . }",
                "SELECT ?v1 WHERE { ?v1 <http://example.org#type> <http://example.org#Typed> . ?v1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?v2 . }", },
                new String[] { "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . }" }, 1.0,
                new String("Scenario-" + testConfigs.size()) });

        // 4
        // An example where the two positive examples can not be selected
        // without selecting the first negative example (it shares the first
        // triple of the WHERE clause with the first positive example and the
        // second triple with the second positive example. However, the second
        // negative example should be excluded which should lead to a Recall of 1.0 and
        // a precision of 0.666.
        testConfigs.add(new Object[] { new String[] {
                "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . ?a <http://www.w3.org/2000/01/rdf-schema#label> \"Literal\" . }",
                "SELECT ?v1 WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . ?a <http://www.w3.org/2000/01/rdf-schema#comment> \"Literal\" . }", },
                new String[] {
                        "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . ?a <http://www.w3.org/2000/01/rdf-schema#comment> \"Literal\" . }",
                        "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . }" },
                0.8, new String("Scenario-" + testConfigs.size()) });

        // 5
        // An example where the two positive examples are equal but have a different
        // order of their triple patterns while the negative example have nearly the
        // same triple patterns in the same order as at least on of the positive
        // examples. (The positive case have the rdfs:comment added to the class while
        // the negative example have the rdfs:comment triple with the instance)
        testConfigs.add(new Object[] { new String[] {
                "SELECT ?a ?b WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . ?b <http://www.w3.org/2000/01/rdf-schema#label> \"Literal\" . }",
                "SELECT ?v1 ?v2 WHERE { ?v1 <http://www.w3.org/2000/01/rdf-schema#comment> \"Literal\" . ?v2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?v1 . }", },
                new String[] {
                        "SELECT ?a ?b WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . ?a <http://www.w3.org/2000/01/rdf-schema#label> \"Literal\" .  }",
                        "SELECT ?v1 ?v2 WHERE { ?v2 <http://www.w3.org/2000/01/rdf-schema#comment> \"Literal\" . ?v2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?v1 . }" },
                1.0, new String("Scenario-" + testConfigs.size()) });

        // 6
        // an example of two queries which are exactly the same but have swapped sub
        // WHERE clauses connected by a UNION.
        testConfigs.add(new Object[] { new String[] {
                "SELECT ?a WHERE { { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/Class> . } UNION { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . } }",
                "SELECT ?a WHERE { { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . } UNION { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/Class> . } }" },
                new String[] {
                        "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/Class> . }",
                        "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . }" },
                1.0, new String("Scenario-" + testConfigs.size()) });

        // 7
        // an example of queries which have UNION statements with different triple
        // patterns.
        testConfigs.add(new Object[] { new String[] {
                "SELECT ?a WHERE { { ?a <http://example.org/prop1> <http://example.org/Entity1> . } UNION { ?a <http://example.org/prop2> <http://example.org/Entity2> . } }",
                "SELECT ?a WHERE { { ?a <http://example.org/prop3> <http://example.org/Entity3> . } UNION { ?a <http://example.org/prop4> <http://example.org/Entity4> . ?a <http://example.org/prop5> <http://example.org/Entity5> . } }",
                "SELECT ?a WHERE { { ?a <http://example.org/prop6> <http://example.org/Entity6> . } UNION { ?a <http://example.org/prop7> <http://example.org/Entity7> . } UNION { ?a <http://example.org/prop8> <http://example.org/Entity8> . } }" },
                new String[] { "SELECT ?a WHERE { ?a <http://example.org/prop1> <http://example.org/Entity1> . }",
                        "SELECT ?a WHERE { ?a <http://example.org/prop2> <http://example.org/Entity2> . }",
                        "SELECT ?a WHERE { ?a <http://example.org/prop3> <http://example.org/Entity3> . }",
                        "SELECT ?a WHERE { ?a <http://example.org/prop4> <http://example.org/Entity4> . }",
                        "SELECT ?a WHERE { ?a <http://example.org/prop5> <http://example.org/Entity5> . }",
                        "SELECT ?a WHERE { ?a <http://example.org/prop6> <http://example.org/Entity6> . }",
                        "SELECT ?a WHERE { ?a <http://example.org/prop7> <http://example.org/Entity7> . }",
                        "SELECT ?a WHERE { ?a <http://example.org/prop8> <http://example.org/Entity8> . }" },
                1.0, new String("Scenario-" + testConfigs.size()) });

        // 8
        // Two queries that have one pattern in common but one has a UNION statement
        // while the negative example has a UNION as well.
        testConfigs.add(new Object[] { new String[] {
                "SELECT ?a WHERE { { ?a <http://example.org/prop1> <http://example.org/Entity1> . } UNION { ?a <http://example.org/prop2> <http://example.org/Entity2> . } }",
                "SELECT ?a WHERE { ?a <http://example.org/prop1> <http://example.org/Entity1> . }" },
                new String[] {
                        "SELECT ?a WHERE { { ?a <http://example.org/prop3> <http://example.org/Entity3> . } UNION { ?a <http://example.org/prop4> <http://example.org/Entity4> . ?a <http://example.org/prop5> <http://example.org/Entity5> . } }",
                        "SELECT ?a WHERE { ?a <http://example.org/prop2> <http://example.org/Entity2> . }" },
                1.0, new String("Scenario-" + testConfigs.size()) });

        // 9
        // Six queries which are equal but have different orders of variables in the
        // projection. The negative examples have the same where clause but a different
        // projection
        testConfigs.add(new Object[] {
                new String[] { "SELECT ?s ?p ?o WHERE { ?s ?p ?o . }", "SELECT ?s ?o ?p WHERE { ?s ?p ?o . }",
                        "SELECT ?p ?s ?o WHERE { ?s ?p ?o . }", "SELECT ?p ?o ?s WHERE { ?s ?p ?o . }",
                        "SELECT ?o ?s ?p WHERE { ?s ?p ?o . }", "SELECT ?o ?p ?s WHERE { ?s ?p ?o . }" },
                new String[] { "SELECT ?s WHERE { ?s ?p ?o . }", "SELECT ?p WHERE { ?s ?p ?o . }",
                        "SELECT ?o WHERE { ?s ?p ?o . }", "SELECT ?s ?p WHERE { ?s ?p ?o . }",
                        "SELECT ?s ?o WHERE { ?s ?p ?o . }", "SELECT ?p ?s WHERE { ?s ?p ?o . }",
                        "SELECT ?p ?o WHERE { ?s ?p ?o . }", "SELECT ?o ?s WHERE { ?s ?p ?o . }",
                        "SELECT ?o ?p WHERE { ?s ?p ?o . }" },
                1.0, new String("Scenario-" + testConfigs.size()) });

        // 10
        // positive queries with filters and negative queries without filters.
        testConfigs.add(new Object[] {
                new String[] { "SELECT  ?x ?p {  ?x <http://example.org/ns#price> ?p . FILTER(?p < 20) }",
                        "SELECT  ?x ?d {  ?x <http://example.org/ns#discount> ?d . FILTER(?d > 0.1) }" },
                new String[] { "SELECT  ?x ?p {  ?x <http://example.org/ns#price> ?p . }",
                        "SELECT  ?x ?d {  ?x <http://example.org/ns#discount> ?d . }" },
                1.0, new String("Scenario-" + testConfigs.size()) });

        // 11
        // positive queries with regex filters and negative queries with simpler
        // filters.
        testConfigs.add(new Object[] { new String[] {
                "SELECT ?n ?m WHERE { ?x <http://xmlns.com/foaf/0.1/name> ?n . ?x <http://xmlns.com/foaf/0.1/mbox>  ?m . FILTER regex(str(?m), \"@example\") }",
                "SELECT ?n ?m WHERE { ?x <http://xmlns.com/foaf/0.1/name> ?n . ?x <http://xmlns.com/foaf/0.1/mbox>  ?m . FILTER regex(str(?n), \"peter.*\") }" },
                new String[] {
                        "SELECT ?n ?m WHERE { ?x <http://xmlns.com/foaf/0.1/name> ?n . ?x <http://xmlns.com/foaf/0.1/mbox>  ?m . FILTER (str(?m) > 10) }",
                        "SELECT ?n ?m WHERE { ?x <http://xmlns.com/foaf/0.1/name> ?n . ?x <http://xmlns.com/foaf/0.1/mbox>  ?m . FILTER (lang(?n) = \"es\") }" },
                1.0, new String("Scenario-" + testConfigs.size()) });

        // 12
        // positive queries group by and a negative query without group by
        testConfigs.add(new Object[] { new String[] {
                "SELECT (SUM(?lprice) AS ?totalPrice) WHERE { ?org <http://books.example/affiliates> ?auth . ?auth <http://books.example/writesBook> ?book . ?book <http://books.example/price> ?lprice . } GROUP BY ?org HAVING (SUM(?lprice) > 10) ",
                "SELECT (AVG(?lprice) AS ?avg) WHERE { ?book <http://books.example/price> ?lprice . } GROUP BY ?book"},
                new String[] {
                        "SELECT ?book WHERE { ?org <http://books.example/affiliates> ?auth . ?auth <http://books.example/writesBook> ?book . ?book <http://books.example/price> ?lprice . }" },
                1.0, new String("Scenario-" + testConfigs.size()) });

        return testConfigs;
    }

    private String[] posExamples;
    private String[] negExamples;
    private double expectedF1Measure;
    private String title;

    public SimpleScenarioBasedTest(String[] posExamples, String[] negExamples, double expectedF1Measure, String title) {
        this.posExamples = posExamples;
        this.negExamples = negExamples;
        this.expectedF1Measure = expectedF1Measure;
        this.title = title;
    }

    @Test
    public void test() throws SpabException {
        SpabApi spab = new SpabApi();
        spab.setMaxIterations(100);
        for (int i = 0; i < posExamples.length; ++i) {
            spab.addPositive(posExamples[i]);
        }
        for (int i = 0; i < negExamples.length; ++i) {
            spab.addNegative(negExamples[i]);
        }
        CandidateVertex result = spab.run();

        // Human debugging
        int indexForDebbung = -1;
        if (title.equals("Scenario-" + indexForDebbung)) {
            printInput(spab, true, "Positive inputs", true);
            printInput(spab, false, "Negative inputs", true);
            printResult(result, spab, title, true);
            printGeneratedCandidates(spab, "Generated candidates:", true);
        }

        String resultString = result.getInfoLine();
        Assert.assertEquals(resultString, expectedF1Measure, result.getfMeasure(), DELTA);
    }

}
