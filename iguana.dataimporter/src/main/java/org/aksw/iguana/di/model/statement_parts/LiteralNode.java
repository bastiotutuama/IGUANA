
package org.aksw.iguana.di.model.statement_parts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LiteralNode {

    @SerializedName("lit")
    @Expose
    private String lit;

    public String getLit() {
        return lit;
    }

    public void setLit(String lit) {
        this.lit = lit;
    }

}
