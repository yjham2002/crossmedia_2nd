package com.RKclassichaeven.tube.models;

/**
 * Created by HP on 2018-06-27.
 */

public class CategoryBox {
    private int manageNo;
    private int cg_id;
    private String cg_name;
    private String alias;
    private int cg_parent;
    private int cg_depth;
    private int cg_order;
    private String cg_image_url;
    private String cg_subname;

    public int getManageNo() {
        return manageNo;
    }

    public void setManageNo(int manageNo) {
        this.manageNo = manageNo;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getCg_id() {
        return cg_id;
    }

    public void setCg_id(int cg_id) {
        this.cg_id = cg_id;
    }

    public String getCg_name() {
        return cg_name;
    }

    public void setCg_name(String cg_name) {
        this.cg_name = cg_name;
    }

    public int getCg_parent() {
        return cg_parent;
    }

    public void setCg_parent(int cg_parent) {
        this.cg_parent = cg_parent;
    }

    public int getCg_depth() {
        return cg_depth;
    }

    public void setCg_depth(int cg_depth) {
        this.cg_depth = cg_depth;
    }

    public int getCg_order() {
        return cg_order;
    }

    public void setCg_order(int cg_order) {
        this.cg_order = cg_order;
    }

    public String getCg_image_url() {
        return cg_image_url;
    }

    public void setCg_image_url(String cg_image_url) {
        this.cg_image_url = cg_image_url;
    }

    public String getCg_subname() {
        return cg_subname;
    }

    public void setCg_subname(String cg_subname) {
        this.cg_subname = cg_subname;
    }
}
