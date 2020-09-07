
package org.aksw.iguana.di.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BadwolfResponse {

    @SerializedName("query")
    @Expose
    private String query;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("table")
    @Expose
    private Table table;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

}
