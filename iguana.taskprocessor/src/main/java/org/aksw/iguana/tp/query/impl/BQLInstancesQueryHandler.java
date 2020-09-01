package org.aksw.iguana.tp.query.impl;

import org.aksw.iguana.tp.tasks.impl.stresstest.worker.Worker;

import java.io.File;
import java.util.LinkedList;

public class BQLInstancesQueryHandler extends InstancesQueryHandler {

    /**
     * Default Constructor
     *
     * @param workers Workers to consider queryFiles/updatePaths of
     */
    public BQLInstancesQueryHandler(LinkedList<Worker> workers) {
        super(workers);
    }

    @Override
    protected File[] generateSPARQL(String queryFileName) {
        File[] queries = generateQueryPerLine(queryFileName, "sparql");
        this.queryFiles = queries;
        return queries;
    }

    @Override
    protected File[] generateQueryPerLine(String queryFileName, String idPrefix) {
        return super.generateQueryPerLine(queryFileName, idPrefix);
    }
}
