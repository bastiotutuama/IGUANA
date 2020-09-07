
package org.aksw.iguana.di.model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Row {

    @SerializedName("?s")
    @Expose
    private JsonObject subjectNode;

    @SerializedName("?p")
    @Expose
    private JsonObject predicateNode;

    @SerializedName("?o")
    @Expose
    private JsonObject objectNode;

    public JsonObject getSubjectNode() {
        return subjectNode;
    }

    public void setSubjectNode(JsonObject subjectNode) {
        this.subjectNode = subjectNode;
    }

    public JsonObject getPredicateNode() {
        return predicateNode;
    }

    public void setPredicateNode(JsonObject predicateNode) {
        this.predicateNode = predicateNode;
    }

    public JsonObject getObjectNode() {
        return objectNode;
    }

    public void setObjectNode(JsonObject objectNode) {
        this.objectNode = objectNode;
    }
}
