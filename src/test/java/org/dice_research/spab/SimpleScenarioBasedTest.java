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
public class SimpleScenarioBasedTest {

    public static final double DELTA = 0.0000001;

    @Parameters
    public static List<Object[]> data() {
        List<Object[]> testConfigs = new ArrayList<>();
        // Simple example where a perfect solution is possible
        testConfigs.add(new Object[] {
                new String[] { "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . }",
                        "SELECT ?a ?b WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . }",
                        "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . }" },
                new String[] {
                        "INSERT { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . } WHERE { ?b <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?a . }" },
                1.0 });
        // Two positive examples that are sharing the same WHERE clause
        testConfigs.add(new Object[] {
                new String[] {
                        "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b .  ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . }",
                        "INSERT { ?a <http://example.org#type> <http://example.org#Typed> . } WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b .  ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . }", },
                new String[] { "SELECT ?a ?b WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . }" },
                1.0 });
        // Two positive examples are exactly the same but with different
        // variable names and a different order of their triples
        testConfigs.add(new Object[] {
                new String[] {
                        "SELECT ?a ?b ?l WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . ?b <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . ?b <http://www.w3.org/2000/01/rdf-schema#label> ?l . }",
                        "SELECT ?v1 ?v3 ?v2 WHERE { ?v1 <http://www.w3.org/2000/01/rdf-schema#label> ?v2 . ?v1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . ?v3 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?v1 . }", },
                new String[] { "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . ?b <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . }" },
                1.0 });
        // Two positive examples that have two triples in their WHERE clause
        // while the negative example has only one (although it is the same as
        // one of the positive example triples)
        testConfigs.add(new Object[] {
                new String[] {
                        "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b .  ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . }",
                        "SELECT ?v1 WHERE { ?v1 <http://example.org#type> <http://example.org#Typed> . ?v1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?v2 . }", },
                new String[] { "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . }" },
                1.0 });

        // An example where the two positive examples can not be selected
        // without selecting the first negative example (it shares the first
        // triple of the WHERE clause with the first positive example and the
        // second triple with the second positive example. However, the second
        // negative example should be excluded which should lead to a Recall of 1.0 and a precision of 0.666.
        testConfigs.add(new Object[] {
                new String[] {
                        "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . ?a <http://www.w3.org/2000/01/rdf-schema#label> \"Literal\" . }",
                        "SELECT ?v1 WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . ?a <http://www.w3.org/2000/01/rdf-schema#comment> \"Literal\" . }", },
                new String[] {
                        "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . ?a <http://www.w3.org/2000/01/rdf-schema#comment> \"Literal\" . }",
                        "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . }" },
                0.8 });

        // An example where the two positive examples can not be selected
        // without selecting the first negative example (it shares the first
        // triple of the WHERE clause with the first positive example and the
        // second triple with the second positive example. However, the second
        // negative example should be excluded which should lead to a Recall of 1.0 and a precision of 0.666.
        testConfigs.add(new Object[] {
                new String[] {
                        "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . ?a <http://www.w3.org/2000/01/rdf-schema#label> \"Literal\" . }",
                        "SELECT ?v1 WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . ?a <http://www.w3.org/2000/01/rdf-schema#comment> \"Literal\" . }", },
                new String[] {
                        "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . ?a <http://www.w3.org/2000/01/rdf-schema#comment> \"Literal\" . }",
                        "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?b . }" },
                0.8 });

        // an example of two queries which are exactly the same but have swapped sub WHERE clauses connected by a UNION. 
        testConfigs.add(new Object[] {
                new String[] {
                        "SELECT ?a WHERE { { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/Class> . } UNION { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . } }",
                        "SELECT ?a WHERE { { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . } UNION { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/Class> . } }"},
                new String[] { "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://example.org/Class> . }",
                        "SELECT ?a WHERE { ?a <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2000/01/rdf-schema#Class> . }"},
                1.0 });

        // an example of queries which have UNION statements with different triple patterns. 
        testConfigs.add(new Object[] {
                new String[] {
                        "SELECT ?a WHERE { { ?a <http://example.org/prop1> <http://example.org/Entity1> . } UNION { ?a <http://example.org/prop2> <http://example.org/Entity2> . } }",
                        "SELECT ?a WHERE { { ?a <http://example.org/prop3> <http://example.org/Entity3> . } UNION { ?a <http://example.org/prop4> <http://example.org/Entity4> . ?a <http://example.org/prop5> <http://example.org/Entity5> . } }",
                        "SELECT ?a WHERE { { ?a <http://example.org/prop6> <http://example.org/Entity6> . } UNION { ?a <http://example.org/prop7> <http://example.org/Entity7> . } UNION { ?a <http://example.org/prop8> <http://example.org/Entity8> . } }"},
                new String[] { "SELECT ?a WHERE { ?a <http://example.org/prop1> <http://example.org/Entity1> . }", 
                        "SELECT ?a WHERE { ?a <http://example.org/prop2> <http://example.org/Entity2> . }", 
                        "SELECT ?a WHERE { ?a <http://example.org/prop3> <http://example.org/Entity3> . }", 
                        "SELECT ?a WHERE { ?a <http://example.org/prop4> <http://example.org/Entity4> . }", 
                        "SELECT ?a WHERE { ?a <http://example.org/prop5> <http://example.org/Entity5> . }", 
                        "SELECT ?a WHERE { ?a <http://example.org/prop6> <http://example.org/Entity6> . }", 
                        "SELECT ?a WHERE { ?a <http://example.org/prop7> <http://example.org/Entity7> . }", 
                        "SELECT ?a WHERE { ?a <http://example.org/prop8> <http://example.org/Entity8> . }" },
                1.0 });

        // Two queries that have one pattern in common but one has a UNION statement while the negative example has a UNION as well. 
        testConfigs.add(new Object[] {
                new String[] {
                        "SELECT ?a WHERE { { ?a <http://example.org/prop1> <http://example.org/Entity1> . } UNION { ?a <http://example.org/prop2> <http://example.org/Entity2> . } }",
                        "SELECT ?a WHERE { ?a <http://example.org/prop1> <http://example.org/Entity1> . }"},
                new String[] {
                        "SELECT ?a WHERE { { ?a <http://example.org/prop3> <http://example.org/Entity3> . } UNION { ?a <http://example.org/prop4> <http://example.org/Entity4> . ?a <http://example.org/prop5> <http://example.org/Entity5> . } }",
                        "SELECT ?a WHERE { ?a <http://example.org/prop2> <http://example.org/Entity2> . }"},
                1.0 });

        return testConfigs;
    }

    private String[] posExamples;
    private String[] negExamples;
    private double expectedF1Measure;

    public SimpleScenarioBasedTest(String[] posExamples, String[] negExamples, double expectedF1Measure) {
        this.posExamples = posExamples;
        this.negExamples = negExamples;
        this.expectedF1Measure = expectedF1Measure;
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

        String resultString = result.getInfoLine();
        Assert.assertEquals(resultString, expectedF1Measure, result.getfMeasure(), DELTA);
    }

}
