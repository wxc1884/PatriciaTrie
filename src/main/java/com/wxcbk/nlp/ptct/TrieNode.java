package com.wxcbk.nlp.ptct;

import java.util.List;
import java.util.Map;

/**
 * @author :owen
 * @date :2020/11/6 16:56
 * @Description : TrieNode树的节点， E为节点存储的信息
 */
public class TrieNode<E> {

    private boolean eol;
    private String edgeValue;
    private TrieNode<E> parentNode;
    private Map<String, TrieNode<E>> childNodeMap;
    private List<E> nodeValues;


    public TrieNode(String edgeValue) {
        this.edgeValue = edgeValue;
    }

    public boolean isEol() {
        return eol;
    }

    public void setEol(boolean eol) {
        this.eol = eol;
    }

    public String getEdgeValue() {
        return edgeValue;
    }

    public void setEdgeValue(String edgeValue) {
        this.edgeValue = edgeValue;
    }

    public TrieNode<E> getParentNode() {
        return parentNode;
    }

    public void setParentNode(TrieNode<E> parentNode) {
        this.parentNode = parentNode;
    }

    public Map<String, TrieNode<E>> getChildNodeMap() {
        return childNodeMap;
    }

    public void setChildNodeMap(Map<String, TrieNode<E>> childNodeMap) {
        this.childNodeMap = childNodeMap;
    }

    public List<E> getNodeValues() {
        return nodeValues;
    }

    public void setNodeValues(List<E> nodeValues) {
        this.nodeValues = nodeValues;
    }


}
