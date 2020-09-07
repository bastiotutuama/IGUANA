
package org.aksw.iguana.di.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Table {

    @SerializedName("bindings")
    @Expose
    private List<String> bindings = null;
    @SerializedName("rows")
    @Expose
    private List<Row> rows = null;

    public List<String> getBindings() {
        return bindings;
    }

    public void setBindings(List<String> bindings) {
        this.bindings = bindings;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }

}
