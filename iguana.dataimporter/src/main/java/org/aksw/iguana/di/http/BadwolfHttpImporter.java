package org.aksw.iguana.di.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.reactivex.rxjava3.core.Observer;
import okhttp3.*;
import org.aksw.iguana.di.model.BadwolfResponse;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class BadwolfHttpImporter {


    private static ConnectionPool pool = new ConnectionPool(5, 10000, TimeUnit.MILLISECONDS);


    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(pool)
            .build();

    public static void sendRequestToBadwolfEndpoint(String endpointAddress, Observer<String> responseObserver, String bqlQuery, ArrayList<String> bqlQueryElements) {
        RequestBody formBody = new FormBody.Builder()
                .addEncoded("bqlQuery", bqlQuery)
                .build();

        Request request = new Request.Builder()
                .url(endpointAddress)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                //retry failed once
                try {
                    call.execute();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                    /*Headers responseHeaders = response.headers();
                    for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                        System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                    }*/

                    Gson gson = new Gson();

                    String responseBodyString = responseBody.string();
                    Type badwolfResponseArrayListType = new TypeToken<ArrayList<BadwolfResponse>>(){}.getType();
                    ArrayList<BadwolfResponse> badwolfResponses = gson.fromJson(responseBodyString, badwolfResponseArrayListType);

                    //Evaluate if Query was successful and notify Observer with the successful queryElement
                    int i = 0;
                    for (BadwolfResponse badwolfResponse:badwolfResponses) {
                        if (badwolfResponse.getMsg().equals("[OK]")) {
                            responseObserver.onNext(bqlQueryElements.get(i));
                        }
                        i++;
                    }
                    responseObserver.onComplete();

                    System.out.println(responseBodyString);
                    System.out.println();
                }
            }
        });
    }

    public static void main(String[] args) {
        //sendRequestToBadwolfEndpoint("http://131.234.29.241:1234/bql", "SELECT ?s, ?p, ?o FROM ?swdf WHERE {?s ?p ?o};");
    }

    public static void shutdownNetworkClient(){
        client.dispatcher().executorService().shutdown();
    }



}
