package org.dice_research.spab;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	 * Input container
	 */
	protected Input input;

	/**
	 * Graph of generated candidates
	 */
	protected CandidateGraph graph;

	/**
	 * Candidate priority queue
	 */
	protected CandidateQueue queue;

	/**
	 * Candidate stack of visited candidates
	 */
	protected List<CandidateVertex> stack = new LinkedList<CandidateVertex>();

	/**
	 * Collection of existing regular expressions to prevent duplicates.
	 */
	protected Set<String> regExCheckSet = new HashSet<String>();

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
	 * @throws SpabException on errors in SPAB algorithm.
	 */
	public CandidateVertex execute() throws SpabException {
		return execute(null);
	}

	/**
	 * Executes SPAB algorithm.
	 * 
	 * @param matcher The matching algorithm to use. If null, the default
	 *                implementation implemented by
	 *                {@link CandidateVertex#matches(Candidate, String)} is used.
	 * 
	 * @return The best candidate found.
	 * 
	 * @throws SpabException on errors in SPAB algorithm.
	 */
	public CandidateVertex execute(Matcher matcher) throws SpabException {
		Statistics.timeBegin = System.currentTimeMillis();
		LOGGER.info("SPAB run with " + getInput().getPositives().size() + " positives and "
				+ getInput().getNegatives().size() + " negatives. Max iterations: " + configuration.getMaxIterations()
				+ ". Lambda: " + getConfiguration().getLambda() + ". Resource URIs: "
				+ getInput().getResources().size());
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
				CandidateVertex bestCandidate = queue.pollBestCandidate();
				if (bestCandidate == null) {
					LOGGER.info("All candidates visited at iteration " + i);
					break;
				}
				stack.add(bestCandidate);
				Map<CandidateVertex, Candidate> bestCandidateChildren = bestCandidate.generateChildren();
				removeDuplicates(bestCandidateChildren);
				graph.addCandidates(bestCandidateChildren.keySet(), bestCandidate);

				if (i <= 10 || i % 100 == 0) {
					LOGGER.info("Iteration " + i + ". Generated " + bestCandidateChildren.size()
							+ " children. Graph size: " + graph.getAllCandidates().size());
				}

				// Graph depth increases by 1, as new children were generated and added.
				// The graph depth influences score of all candidates.
				// Therefore, the scores of all current candidates have to be re-calculated.
				// A reset of the priority queue is needed to maintain changed
				// priorities, represented by scores.
				for (CandidateVertex queueCandidate : queue.reset()) {
					queueCandidate.calculateScore(configuration, graph.getDepth(), matcher);
					queue.add(queueCandidate);
					Statistics.info();
				}

				// Calculate scores of new children and add them to queue
				for (CandidateVertex bestCandidateChild : bestCandidateChildren.keySet()) {
					bestCandidateChild.calculateScore(configuration, graph.getDepth(), matcher);
					queue.add(bestCandidateChild);
					Statistics.info();
				}

				if (i <= 10 || i % 100 == 0) {
					LOGGER.info("Iteration " + i + ". Queue size: " + queue.getQueue().size());
				}
			}

			// Update final scores
			for (CandidateVertex candidate : graph.getAllCandidates()) {
				candidate.calculateScore(configuration, graph.getDepth(), matcher);
			}

			// Return best candidate
			CandidateVertex bestCandidate = firstCandidate;
			for (CandidateVertex candidate : graph.getAllCandidates()) {
				if (candidate.getScore() > bestCandidate.getScore()) {
					bestCandidate = candidate;
				}
			}

			LOGGER.info("Runtime: " + Statistics.getRuntime() + " seconds");
			return bestCandidate;

		} catch (PerfectSolutionException e) {

			// Update final scores
			for (CandidateVertex candidate : graph.getAllCandidates()) {
				try {
					candidate.calculateScore(configuration, graph.getDepth(), matcher);
				} catch (PerfectSolutionException pse) {
					// This will happen, as already in catch clause.
				}
			}

			// Perfect candidate was found before reaching maximum number of iterations.
			// A perfect candidate has no false positives or false negatives.
			LOGGER.info("Perfect solution found!");
			LOGGER.info("Runtime: " + Statistics.getRuntime() + " seconds");

			return e.getCandidate();
		}
	}

	/**
	 * Checks, if regular expression of candidate is already known. If so, the
	 * candidate is removed from map.
	 */
	protected void removeDuplicates(Map<CandidateVertex, Candidate> candidateMap) {
		Set<CandidateVertex> candidatesToRemove = new HashSet<CandidateVertex>();

		// Check regular expression of all candidates
		for (Entry<CandidateVertex, Candidate> candidateEntry : candidateMap.entrySet()) {
			if (regExCheckSet.contains(candidateEntry.getValue().getRegEx())) {
				// If regular expression already is used, the candidate can be removed
				candidatesToRemove.add(candidateEntry.getKey());
			} else {
				// The regular expression was unknown, but is known now.
				regExCheckSet.add(candidateEntry.getValue().getRegEx());
			}
		}

		// Remove
		for (CandidateVertex candidateVertexToRemove : candidatesToRemove) {
			candidateMap.remove(candidateVertexToRemove);
		}
	}

	/**
	 * Gets configuration container.
	 */
	public Configuration getConfiguration() {
		return configuration;
	}

	/**
	 * Gets input container.
	 */
	public Input getInput() {
		return input;
	}

	/**
	 * Gets graph of generated candidates.
	 */
	public CandidateGraph getGraph() {
		return graph;
	}

	/**
	 * Gets candidate priority queue.
	 */
	public CandidateQueue getQueue() {
		return queue;
	}

	/**
	 * Gets stack of visited candidates. Candidates are sorted by insertion time.
	 */
	public List<CandidateVertex> getStack() {
		return stack;
	}

	/**
	 * Gets visited candidates from stack sorted by score.
	 */
	public List<CandidateVertex> getBestCandidates() {
		List<CandidateVertex> bestCandidates = new LinkedList<CandidateVertex>(stack);
		bestCandidates.sort(new Comparator<CandidateVertex>() {
			@Override
			public int compare(CandidateVertex v1, CandidateVertex v2) {
				return Float.compare(v2.getScore(), v1.getScore());
			}
		});
		return bestCandidates;
	}
}