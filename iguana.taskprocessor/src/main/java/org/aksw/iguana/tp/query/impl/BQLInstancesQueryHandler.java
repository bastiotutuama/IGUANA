package org.aksw.iguana.tp.query.impl;

import org.aksw.iguana.tp.query.AbstractWorkerQueryHandler;
import org.aksw.iguana.tp.tasks.impl.stresstest.worker.Worker;

import java.io.File;
import java.util.LinkedList;

public class BQLInstancesQueryHandler extends AbstractWorkerQueryHandler {

    /**
     * Default Constructor
     *
     * @param workers Workers to consider queryFiles/updatePaths of
     */
    public BQLInstancesQueryHandler(LinkedList<Worker> workers) {
        super(workers);
    }

    @Override
    protected File[] generateSingleQueries(String queryFileName) {
        File[] queries = generateQueryPerLine(queryFileName, "bql");
        this.queryFiles = queries;
        return queries;
    }

    @Override
    protected File[] generateUPDATE(String updatePath) {
        return new File[0];
    }

    protected File[] generateQueryPerLine(String queryFileName, String idPrefix) {
        return new File[0];
    }

    @Override
    public String generateTripleStats(String taskID, String resource, String property) {
        return null;
    }
}
