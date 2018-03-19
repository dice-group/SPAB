package org.dice_research.spab;

import java.util.Map;
import java.util.Map.Entry;

import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.candidates.CandidateFactory;
import org.dice_research.spab.exceptions.PerfectSolutionException;
import org.dice_research.spab.exceptions.SpabException;
import org.dice_research.spab.input.Configuration;
import org.dice_research.spab.input.Input;
import org.dice_research.spab.structures.CandidateGraph;
import org.dice_research.spab.structures.CandidateQueue;
import org.dice_research.spab.structures.CandidateVertex;
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
		return execute(null);
	}

	/**
	 * Executes SPAB algorithm.
	 * 
	 * @param matcher
	 *            The matching algorithm to use. If null, the default implementation
	 *            implemented by {@link CandidateVertex#matches(Candidate, String)}
	 *            is used.
	 * 
	 * @return The best candidate found.
	 * 
	 * @throws SpabException
	 *             on errors in SPAB algorithm.
	 */
	public CandidateVertex execute(Matcher matcher) throws SpabException {
		try {

			// Generate first candidate
			Candidate rootCandidate = CandidateFactory.createCandidate(configuration.getCandidateImplementation(),
					matcher);
			CandidateVertex firstCandidate = new CandidateVertex(rootCandidate, getInput());

			// Set matcher
			if (matcher == null) {
				matcher = firstCandidate;
			}

			// Add first candidate
			graph.addCandidate(firstCandidate);
			firstCandidate.calculateScore(configuration, graph.getDepth(), matcher);
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
					queueCandidate.calculateScore(configuration, graph.getDepth(), matcher);
					queue.add(queueCandidate);
				}

				// Calculate scores of new children and add them to queue
				for (CandidateVertex bestCandidateChild : bestCandidateChildren.keySet()) {
					bestCandidateChild.calculateScore(configuration, graph.getDepth(), matcher);
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