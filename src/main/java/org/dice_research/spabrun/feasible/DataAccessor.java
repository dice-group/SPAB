package org.dice_research.spabrun.feasible;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.dice_research.spab.io.FileReader;

/**
 * Reads files and extracts values.
 * 
 * @author Adrian Wilke
 */
public class DataAccessor {

	public final static String BENCHMARK_DBPEDIA = "dbpedia";
	public final static String BENCHMARK_SWDF = "swdf";

	public final static String QUERYTYPE_ASK = "ask-100";
	public final static String QUERYTYPE_CONSTRUCT = "construct-100";
	public final static String QUERYTYPE_DESCRIBE = "describe-";
	public final static String QUERYTYPE_SELECT = "select-100";
	public final static String QUERYTYPE_MIX = "";

	public final static String DATATYPE_CSV = "csv";
	public final static String DATATYPE_QUERIES = "txt";

	public final static String DATABASE_FUSEKI = "Fuseki";
	public final static String DATABASE_OWLIMSE = "OWLIM-SE";
	public final static String DATABASE_SESAME = "Sesame";
	public final static String DATABASE_VIRTUOSE = "Virtuoso";

	protected final static String SUBDIR_INDIVIDUAL = "individual-benchmarks";
	protected final static String SUBDIR_MIX = "mix-benchmarks";
	protected final static String SUBDIR_QUERIES = "queries";

	protected final static String SUBDIR_DBPEDIA = "dbpedia351";
	protected final static String SUBDIR_SWDF = "swdf";

	protected final static String FILENAME_CSV = "results";
	protected final static String FILENAME_QUERIES = "queries";
	protected final static String FILENAME_MIXED = "-175";
	protected final static String FILENAME_DESCIBE_DBPEDIA = "25";
	protected final static String FILENAME_DESCIBE_SWDF = "100";

	protected Configuration configuration;

	public DataAccessor(Configuration configuration) {
		this.configuration = configuration;
	}

	public Data getData(String benchmark, String queryType) {
		Data data = new Data();

		File csvFile = getFile(benchmark, queryType, DataAccessor.DATATYPE_CSV);
		data.setSourceOfValues(csvFile);

		List<String> csvLines = FileReader.readFileToList(csvFile.getPath(), true, StandardCharsets.UTF_8.name());
		float[][] csvValues = new float[csvLines.size() - 1][];
		for (int l = 0; l < csvLines.size(); l++) {
			if (l == 0) {
				checkHeadings(csvLines.get(l));
			} else {
				csvValues[l - 1] = getNumericValues(csvLines.get(l));
			}
		}
		data.setValues(csvValues);

		File queryFile = getFile(benchmark, queryType, DataAccessor.DATATYPE_QUERIES);
		data.setSourceOfQueries(queryFile);

		List<String> queryLines = FileReader.readFileToList(queryFile.getPath(), true, StandardCharsets.UTF_8.name());
		String[] queries = new String[csvLines.size() - 1];
		int index = 0;
		StringBuilder stringBuilder = new StringBuilder();
		for (int l = 0; l < queryLines.size(); l++) {
			if (queryLines.get(l).startsWith("#")) {
				if (stringBuilder.length() == 0) {
					continue;
				} else {
					queries[index] = stringBuilder.toString();
					index++;
					stringBuilder = new StringBuilder();
				}
			} else {
				stringBuilder.append(queryLines.get(l));
				stringBuilder.append(System.lineSeparator());
			}
		}
		if (stringBuilder.length() != 0) {
			queries[index] = stringBuilder.toString();
		}
		data.setQueries(queries);

		return data;
	}

	protected File getFile(String benchmark, String queryType, String dataType) {

		boolean isIndividualQuery;
		switch (queryType) {
		case QUERYTYPE_ASK:
		case QUERYTYPE_CONSTRUCT:
		case QUERYTYPE_DESCRIBE:
		case QUERYTYPE_SELECT:
			isIndividualQuery = true;
			break;
		case QUERYTYPE_MIX:
			isIndividualQuery = false;
			break;
		default:
			throw new RuntimeException("Unknown query type: " + queryType);
		}

		// Directory

		StringBuilder directoryName = new StringBuilder();

		switch (dataType) {
		case DATATYPE_CSV:
			directoryName.append(configuration.getDirectoryInputCsv());
			break;
		case DATATYPE_QUERIES:
			directoryName.append(configuration.getDirectoryInputQueries());
			break;
		default:
			throw new RuntimeException("Unknown data type: " + dataType);
		}
		directoryName.append("/");

		if (isIndividualQuery) {
			directoryName.append(SUBDIR_INDIVIDUAL);
		} else {
			directoryName.append(SUBDIR_MIX);
		}
		directoryName.append("/");

		switch (benchmark) {
		case BENCHMARK_DBPEDIA:
			directoryName.append(SUBDIR_DBPEDIA);
			break;
		case BENCHMARK_SWDF:
			directoryName.append(SUBDIR_SWDF);
			break;
		default:
			throw new RuntimeException("Unknown benchmark: " + benchmark);
		}
		directoryName.append("/");

		if (isIndividualQuery) {

			switch (benchmark) {
			case BENCHMARK_DBPEDIA:
				directoryName.append(BENCHMARK_DBPEDIA);
				break;
			case BENCHMARK_SWDF:
				directoryName.append(BENCHMARK_SWDF);
				break;
			default:
				break;
			}

			directoryName.append("-");

			switch (queryType) {
			case QUERYTYPE_ASK:
				directoryName.append(QUERYTYPE_ASK);
				break;
			case QUERYTYPE_CONSTRUCT:
				directoryName.append(QUERYTYPE_CONSTRUCT);
				break;
			case QUERYTYPE_DESCRIBE:
				directoryName.append(QUERYTYPE_DESCRIBE);
				if (benchmark.equals(BENCHMARK_DBPEDIA)) {
					directoryName.append(FILENAME_DESCIBE_DBPEDIA);
				} else if (benchmark.equals(BENCHMARK_SWDF)) {
					directoryName.append(FILENAME_DESCIBE_SWDF);
				} else {
					throw new RuntimeException("Unknown benchmark: " + benchmark);
				}
				break;
			case QUERYTYPE_SELECT:
				directoryName.append(QUERYTYPE_SELECT);
				break;
			default:
				break;
			}

			directoryName.append("/");

			if (dataType.equals(DATATYPE_QUERIES)) {
				directoryName.append(SUBDIR_QUERIES);
				directoryName.append("/");
			}
		}

		// File

		StringBuilder fileName = new StringBuilder();

		switch (dataType) {
		case DATATYPE_CSV:
			fileName.append(FILENAME_CSV);
			break;
		case DATATYPE_QUERIES:
			fileName.append(FILENAME_QUERIES);
			break;
		default:
			break;
		}

		if (!isIndividualQuery) {
			fileName.append(FILENAME_MIXED);
		}

		fileName.append(".");

		switch (dataType) {
		case DATATYPE_CSV:
			fileName.append(DATATYPE_CSV);
			break;
		case DATATYPE_QUERIES:
			fileName.append(DATATYPE_QUERIES);
			break;
		default:
			break;
		}

		// Path

		File file = new File(directoryName.toString(), fileName.toString());
		if (!file.canRead()) {
			throw new RuntimeException("Can not read file: " + file.getPath());
		}
		return file;
	}

	protected void checkHeadings(String line) {
		String[] parts = line.split(",");
		if (parts.length != 5) {
			throw new RuntimeException("Line does not consist of 5 parts: " + line);
		}
		if (!parts[0].isEmpty()) {
			throw new RuntimeException("Incorrect format: " + parts[0]);
		}
		if (parts[1].equals(DATABASE_SESAME)) {
			throw new RuntimeException("Incorrect format: " + parts[1]);
		}
		if (parts[2].equals(DATABASE_VIRTUOSE)) {
			throw new RuntimeException("Incorrect format: " + parts[2]);
		}
		if (parts[3].equals(DATABASE_OWLIMSE)) {
			throw new RuntimeException("Incorrect format: " + parts[3]);
		}
		if (parts[4].equals(DATABASE_FUSEKI)) {
			throw new RuntimeException("Incorrect format: " + parts[4]);
		}
	}

	protected float[] getNumericValues(String line) {
		String[] parts = line.split(",");
		if (parts.length != 5) {
			throw new RuntimeException("Line does not consist of 5 parts: " + line);
		}

		float[] values = new float[4];
		for (int p = 1; p < parts.length; p++) {
			values[p - 1] = Float.parseFloat(parts[p]);
		}
		return values;
	}
}