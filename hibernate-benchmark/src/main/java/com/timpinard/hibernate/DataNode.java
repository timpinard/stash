package com.timpinard.hibernate;

import java.util.ArrayList;
import java.util.List;

public class DataNode {

    Data parent;
    List<DataNode> children;

    public DataNode(Data parent, List<DataNode> children) {
        this.parent = parent;
        this.children = children;
    }

    public DataNode(Data parent) {
        this(parent, new ArrayList<>());
    }

    public Data getParent() {
        return parent;
    }

    public List<DataNode> getChildren() {
        return children;
    }
}