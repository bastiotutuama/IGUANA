package org.aksw.iguana.tp.tasks.impl.stresstest.worker.impl;

import org.aksw.iguana.commons.constants.COMMON;
import org.aksw.iguana.tp.tasks.impl.stresstest.worker.AbstractWorker;
import org.aksw.iguana.tp.utils.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class BQLWorker extends AbstractWorker {

    private int currentQueryID = 0;

    private Random queryPatternChooser;

    public BQLWorker() {
        super("BQL");
    }

    public BQLWorker(String[] args) {
        super(args, "BQL");
        queryPatternChooser = new Random(this.workerID);
    }

    @Override
    public void init(String[] args) {
        super.init(args);
        queryPatternChooser = new Random(this.workerID);
    }


    @Override
    public Long[] getTimeForQueryMs(String query, String queryID) {
        // QueryExecution exec = QueryExecutionFactory.sparqlService(service, query);
        // exec.setTimeout(this.timeOut);
        // Set query timeout
        // exec.setTimeout(this.timeOut, TimeUnit.MILLISECONDS, this.timeOut,
        // TimeUnit.MILLISECONDS);
        long start = System.currentTimeMillis();
        final AtomicReference<String> res = new AtomicReference<String>("");

        try {
            // Execute Query
            String url = service;
            HttpPost request = new HttpPost(url);

            // Add headers
            //request.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            // -> Done automatically by UrlEncodedFormEntity

            // Add body
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("bqlQuery", query));
            request.setEntity(new UrlEncodedFormEntity(nvps, HTTP.DEF_CONTENT_CHARSET));


            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeOut.intValue())
                    .setConnectTimeout(timeOut.intValue()).build();

            request.setConfig(requestConfig);
            try (CloseableHttpClient client = HttpClients.createDefault();
                 CloseableHttpResponse response = client.execute(request);) {

                HttpEntity entity = response.getEntity();
                int responseCode = response.getStatusLine().getStatusCode();
                if (responseCode != 200) {
                    return new Long[] { COMMON.WRONG_RESPONSE_CODE_VALUE, System.currentTimeMillis() - start };

                }
                Header[] contentType = response.getHeaders("Content-Type");
                String cType = getContentTypeVal(contentType[0]);

                executeAndTerminate(entity, res, cType);

                long end = System.currentTimeMillis();
                if (this.timeOut < end - start) {
                    return new Long[] { 0L, System.currentTimeMillis() - start };
                }
                long size=0L;
                if("application/json".equals(cType)) {
                    size = parseJson(res.get());
                }
                else {
                    size = StringUtils.countMatches(res.get(), "\n");
                }
                return new Long[] { 1L, end - start, size };
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
                // check ResultSet.
            } catch (java.net.SocketTimeoutException | ConnectTimeoutException e) {
                System.out.println("Timeout occured for " + service + " - " + queryID);

                return new Long[] { COMMON.SOCKET_TIMEOUT_VALUE, System.currentTimeMillis() - start };

            } catch (Exception e) {
                System.out.println("Query could not be exceuted: " + e);
                return new Long[] { COMMON.UNKNOWN_EXCEPTION_VALUE, System.currentTimeMillis() - start };
            }

        } catch (Exception e) {
            LOGGER.warn("Worker[{{}} : {{}}]: Could not execute the following query\n{{}}\n due to", this.workerType,
                    this.workerID, query, e);
        }
        // Exception was thrown, return error
        // return -1L;
        return new Long[] { COMMON.UNKNOWN_EXCEPTION_VALUE, System.currentTimeMillis() - start };

    }

    private String getContentTypeVal(Header header) {
        System.out.println("[DEBUG] HEADER: " + header);
        for (HeaderElement el : header.getElements()) {
            NameValuePair cTypePair = el.getParameterByName("Content-Type");
//			System.out.println("[DEBUG] Pair: " + cTypePair);

            if (cTypePair != null && !cTypePair.getValue().isEmpty()) {
//				System.out.println("[DEBUG] VAL: " + cTypePair.getValue());
                return cTypePair.getValue();
            }
        }
        int index = header.toString().indexOf("Content-Type");
        if (index >= 0) {
            String ret = header.toString().substring(index + "Content-Type".length() + 1);
            if (ret.contains(";")) {
//				System.out.println("[DEBUG] VAL: " + ret.substring(0, ret.indexOf(";")).trim());
                return ret.substring(0, ret.indexOf(";")).trim();
            }
//			System.out.println("[DEBUG] VAL: " + ret.trim());
            return ret.trim();
        }
        return "application/sparql-results+json";
    }

    private void executeAndTerminate(HttpEntity entity, AtomicReference<String> res, String cType)
            throws InterruptedException {
        ExecutorService service2 = Executors.newSingleThreadExecutor();
        // ResultSet res;
        service2.execute(new Runnable() {
            @Override
            public void run() {
                try (InputStream inputStream = entity.getContent();
                     BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))) {
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        result.append(line);
                    }
                    System.out.println("[DEBUG]: byte size: "+result.length());
                    res.set(result.toString());
                    result = null;
                } catch (Exception e) {
                    System.out.println("Query could not be exceuted: " + e);
                }
            }
        });

        service2.shutdown();
//
        service2.awaitTermination(this.timeOut + 100, TimeUnit.MILLISECONDS);
    }

    private long parseJson(String response) throws ParseException {
        JSONParser parser = new JSONParser();
        JSONArray rootArray = (JSONArray) parser.parse(response.trim());
        JSONObject responseObject = (JSONObject) rootArray.get(0);
        JSONObject tableObject = (JSONObject) responseObject.get("table");
        long size = 0;
        try {
            if(!tableObject.isEmpty())
            size = ((JSONArray) tableObject.get("rows")).size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        response = "";
        return size;
    }

    @Override
    public void getNextQuery(StringBuilder queryStr, StringBuilder queryID) throws IOException {
        // get next Query File and next random Query out of it.
        File currentQueryFile = this.queryFileList[this.currentQueryID++];
        queryID.append(currentQueryFile.getName());

        int queriesInFile = FileUtils.countLines(currentQueryFile);
        int queryLine = queryPatternChooser.nextInt(queriesInFile);
        queryStr.append(FileUtils.readLineAt(queryLine, currentQueryFile));

        // If there is no more query(Pattern) start from beginning.
        if (this.currentQueryID >= this.queryFileList.length) {
            this.currentQueryID = 0;
        }

    }

    @Override
    public void setQueriesList(File[] queries) {
        super.setQueriesList(queries);
        this.currentQueryID = queryPatternChooser.nextInt(this.queryFileList.length);
    }

}
