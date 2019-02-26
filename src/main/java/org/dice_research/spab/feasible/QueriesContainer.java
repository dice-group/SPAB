package org.dice_research.spab.feasible;

import java.util.List;

import org.dice_research.spab.benchmark.Query;
import org.dice_research.spab.feasible.enumerations.Dataset;
import org.dice_research.spab.feasible.enumerations.QueryType;
import org.dice_research.spab.feasible.enumerations.Triplestore;

/**
 * Container for query sets and properties.
 * 
 * @author Adrian Wilke
 */
public class QueriesContainer implements Comparable<QueriesContainer> {

	public QueryType queryType;
	public Dataset dataset;
	public Triplestore triplestore;
	public List<Query> queriesPositive;
	public List<Query> queriesNegative;

	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(queryType.getShortHand());
		stringBuffer.append(" ");
		stringBuffer.append(dataset.getShortHand());
		stringBuffer.append(" ");
		stringBuffer.append(triplestore.getShortHand());
		for (int i = 0; i < 3 - new String("" + queriesPositive.size()).length(); i++) {
			stringBuffer.append(" ");
		}
		stringBuffer.append(queriesPositive.size());
		stringBuffer.append(" ");
		for (int i = 0; i < 3 - new String("" + queriesNegative.size()).length(); i++) {
			stringBuffer.append(" ");
		}
		stringBuffer.append(queriesNegative.size());
		return stringBuffer.toString();
	}

	public QueriesContainer setQueryType(QueryType queryType) {
		this.queryType = queryType;
		return this;
	}

	public QueriesContainer setDataset(Dataset dataset) {
		this.dataset = dataset;
		return this;
	}

	public QueriesContainer setTriplestore(Triplestore triplestore) {
		this.triplestore = triplestore;
		return this;
	}

	public QueriesContainer setQueriesPositive(List<Query> queriesPositive) {
		this.queriesPositive = queriesPositive;
		return this;
	}

	public QueriesContainer setQueriesNegative(List<Query> queriesNegative) {
		this.queriesNegative = queriesNegative;
		return this;
	}

	@Override
	public int compareTo(QueriesContainer o) {
		int comp;
		comp = queryType.compareTo(o.queryType);
		if (comp != 0) {
			return comp;
		}
		comp = dataset.compareTo(o.dataset);
		if (comp != 0) {
			return comp;
		}
		comp = triplestore.compareTo(o.triplestore);
		if (comp != 0) {
			return comp;
		}
		comp = Integer.compare(queriesPositive.size(), o.queriesPositive.size());
		if (comp != 0) {
			return comp;
		}
		return Integer.compare(queriesNegative.size(), o.queriesNegative.size());
	}
}
