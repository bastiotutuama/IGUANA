
package org.aksw.iguana.di.model.statement_parts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PredicateNode {

    @SerializedName("pred")
    @Expose
    private String pred;

    public String getPred() {
        return pred;
    }

    public void setPred(String pred) {
        this.pred = pred;
    }

}
