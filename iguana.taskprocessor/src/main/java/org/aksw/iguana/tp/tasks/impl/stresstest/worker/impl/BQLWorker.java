package org.aksw.iguana.tp.tasks.impl.stresstest.worker.impl;

import org.aksw.iguana.tp.tasks.impl.stresstest.worker.AbstractWorker;

import java.io.IOException;

public class BQLWorker extends AbstractWorker {

    public BQLWorker(String workerType) {
        super(workerType);
    }

    public BQLWorker(String[] args, String workerType) {
        super(args, workerType);
    }

    @Override
    public Long[] getTimeForQueryMs(String query, String queryID) {
        return new Long[0];
    }

    @Override
    public void getNextQuery(StringBuilder queryStr, StringBuilder queryID) throws IOException {

    }
}
