package org.aksw.iguana.tp.query;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.aksw.iguana.tp.tasks.impl.stresstest.worker.AbstractWorker;
import org.aksw.iguana.tp.tasks.impl.stresstest.worker.Worker;
import org.aksw.iguana.tp.tasks.impl.stresstest.worker.impl.CLIInputWorker;
import org.aksw.iguana.tp.tasks.impl.stresstest.worker.impl.CLIWorker;
import org.aksw.iguana.tp.tasks.impl.stresstest.worker.impl.SPARQLWorker;
import org.aksw.iguana.tp.tasks.impl.stresstest.worker.impl.UPDATEWorker;
import org.aksw.iguana.tp.utils.QueryStatistics;

/**
 * 
 * An abstract class to use if the QueryHandler should work with Workers. (e.g. in the Stresstest Task)
 * 
 * @author f.conrads
 *
 */
public abstract class AbstractWorkerQueryHandler implements QueryHandler{

	/**
	 * Will contain the path of the worker specified query files to 
	 * the Files where the final querys will be saved
	 */
	private Map<String, File[]> mapping = new HashMap<String, File[]>();
	private HashSet<String> sparqlKeys = new HashSet<String>();
	private HashSet<String> updateKeys = new HashSet<String>();
	private Collection<Worker> workers;

	protected HashMap<String, Integer> type2IDcounter = new HashMap<String, Integer>();
	protected QueryStatistics qs = new QueryStatistics();
	protected File[] queryFiles;

	/**
	 * 
	 * @param workers
	 */
	public AbstractWorkerQueryHandler(Collection<Worker> workers) {
		this.workers = workers;
		for(Worker worker : workers) {
			if(worker instanceof SPARQLWorker) {
				sparqlKeys.add(((SPARQLWorker)worker).getQueriesFileName());
			}
			else if(worker instanceof CLIWorker) {
				sparqlKeys.add(((CLIWorker)worker).getQueriesFileName());
			}
			else if(worker instanceof CLIInputWorker){
				sparqlKeys.add(((CLIInputWorker)worker).getQueriesFileName());

			}
			else if(worker instanceof UPDATEWorker) {
				updateKeys.add(((UPDATEWorker)worker).getQueriesFileName());
			}
		}
	}
	
	@Override
	public void generateQueries() {
		for(String sparqlKey : sparqlKeys) {
			mapping.put(sparqlKey, generateSingleQueries(sparqlKey));
		}
		for(String updateKey : updateKeys) {
			mapping.put(updateKey, generateUPDATE(updateKey));
		}
		for(Worker worker : workers) {
			if(worker instanceof AbstractWorker) {
				((AbstractWorker)worker).setQueriesList(
						mapping.get(((AbstractWorker)worker).getQueriesFileName()));
			}
		}
	}

	protected File createFileWithID(File rootFolder, String idPrefix) throws IOException {
		// create a File with an ID
		int id = 0;
		if (type2IDcounter.containsKey(idPrefix)) {
			id = type2IDcounter.get(idPrefix);
		}
		File out = new File(rootFolder.getAbsolutePath() + File.separator + idPrefix + id);
		out.createNewFile();
		id++;
		type2IDcounter.put(idPrefix, id);
		return out;
	}

	/**
	 * This method will generate Language-Specific Queries given a file with queries.
	 * 
	 * @param queryFileName The queries file
	 * @return for each query in the file, a File representing the query
	 */
	protected abstract File[] generateSingleQueries(String queryFileName) ;

	/**
	 * This method will generate UPDATE Queries given a folder with files in which updates are stated.
	 * 
	 * @param updatePath The path to the updates
	 * @return for each update, a File representing it.
	 */
	protected abstract File[] generateUPDATE(String updatePath) ;
	
}
