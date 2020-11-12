package com.wxcbk.nlp.ptct;

/**
 * @author :owen
 * @date :2020/11/9 18:42
 * @Description : suffixToken, 从后向前的句子切分token, 如"中国人"的suffixToken 为["人","国","中"]
 */
public class SuffixToken {

    private String[] sequence;
    private int begin;
    private int end;
    private int length;

    public String[] getSequence() {
        return sequence;
    }

    public void setSequence(String[] sequence) {
        this.sequence = sequence;
    }

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

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
