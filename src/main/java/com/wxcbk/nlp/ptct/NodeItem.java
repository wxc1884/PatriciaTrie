package com.wxcbk.nlp.ptct;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * @author :owen
 * @date :2020/11/9 18:49
 * @Description : nodeItem: 从前缀树节点检索到节点的信息， 并把检索query的位置和text返回
 */
public class NodeItem<E> implements Serializable {
    private int begin;
    private int end;
    private String textValue;
    private E nodeValue;

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public E getNodeValue() {
        return nodeValue;
    }

    public void setNodeValue(E nodeValue) {
        this.nodeValue = nodeValue;
    }

    @Override
    public String toString() {
        return "NodeItem{" +
                "begin=" + begin +
                ", end=" + end +
                ", textValue='" + textValue + '\'' +
                ", nodeValue=" + JSON.toJSONString(nodeValue )+
                '}';
    }
}
