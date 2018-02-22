package org.aksw.spab;

import java.util.Map;
import java.util.Map.Entry;

import org.aksw.spab.candidates.Candidate;
import org.aksw.spab.candidates.DummyCandidate;
import org.aksw.spab.candidates.one.SpabOneRootCandidate;
import org.aksw.spab.exceptions.PerfectSolutionException;
import org.aksw.spab.exceptions.SpabException;
import org.aksw.spab.input.Configuration;
import org.aksw.spab.input.Input;
import org.aksw.spab.structures.CandidateGraph;
import org.aksw.spab.structures.CandidateQueue;
import org.aksw.spab.structures.CandidateVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SPAB: SPARQL Benchmark Query Generalization
 * 
 * @author Adrian Wilke
 */
public class SpabAlgorithm {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpabAlgorithm.class);

	/**
	 * Configuration container
	 */
	protected Configuration configuration;

	/**
	 * Candidate graph
	 */
	protected CandidateGraph graph;

	/**
	 * Input container
	 */
	protected Input input;

	/**
	 * Candidate priority queue
	 */
	protected CandidateQueue queue;

	/**
	 * Initializes data structures.
	 */
	public SpabAlgorithm() {
		configuration = new Configuration();
		input = new Input();
		graph = new CandidateGraph();
		queue = new CandidateQueue();
	}

	/**
	 * Executes SPAB algorithm.
	 * 
	 * @return The best candidate found.
	 * 
	 * @throws SpabException
	 *             on errors in SPAB algorithm.
	 */
	public CandidateVertex execute() throws SpabException {
		try {

			// Generate and add first candidate
			CandidateVertex firstCandidate = null;
			switch (configuration.getCandidateImplementation()) {
			case DUMMY:
				firstCandidate = new CandidateVertex(new DummyCandidate());
				break;
			case SPAB_ONE:
				firstCandidate = new CandidateVertex(new SpabOneRootCandidate());
				break;
			default:
				throw new SpabException("No candidate implementation set.");
			}
			graph.addCandidate(firstCandidate);
			firstCandidate.calculateScore(input, configuration, graph.getDepth());
			queue.add(firstCandidate);

			// For specified number of iterations run algorithm
			for (int i = 1; i <= configuration.getMaxIterations(); i++) {

				// Get best candidate, generate children, and add them into graph
				CandidateVertex bestCandidate = queue.getBestCandidate();
				if (bestCandidate == null) {
					LOGGER.info("All candidates visited at iteration " + i);
					break;
				}
				Map<CandidateVertex, Candidate> bestCandidateChildren = bestCandidate.generateChildren();
				graph.addCandidates(bestCandidateChildren.keySet(), bestCandidate);
				for (Entry<CandidateVertex, Candidate> bestCandidateChild : bestCandidateChildren.entrySet()) {
					bestCandidateChild.getValue().setVertex(bestCandidateChild.getKey());
				}

				// Graph depth increases by 1, as new children were generated and added.
				// The graph depth influences score of all candidates.
				// Therefore, the scores of all current candidates have to be re-calculated.
				// A reset of the priority queue is needed to maintain changed
				// priorities, represented by scores.
				for (CandidateVertex queueCandidate : queue.reset()) {
					queueCandidate.calculateScore(input, configuration, graph.getDepth());
					queue.add(queueCandidate);
				}

				// Calculate scores of new children and add them to queue
				for (CandidateVertex bestCandidateChild : bestCandidateChildren.keySet()) {
					bestCandidateChild.calculateScore(input, configuration, graph.getDepth());
					queue.add(bestCandidateChild);
				}
			}

			// Return best candidate
			CandidateVertex bestCandidate = firstCandidate;
			for (CandidateVertex candidate : graph.getAllCandidates()) {
				if (candidate.getScore() > bestCandidate.getScore()) {
					bestCandidate = candidate;
				}
			}
			return bestCandidate;

		} catch (PerfectSolutionException e) {

			// Perfect candidate was found before reaching maximum number of iterations.
			// A perfect candidate has no false positives or false negatives.
			LOGGER.info("Perfect solution found!");
			return e.getCandidate();

		}
	}

	/**
	 * Gets configuration container.
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Gets candidate graph.
	 */
	public CandidateGraph getGraph() {
		return graph;
	}

	/**
	 * Gets input container.
	 */
	public Input getInput() {
		return input;
	}

	/**
	 * Gets candidate priority queue.
	 */
	public CandidateQueue getQueue() {
		return queue;
	}
}