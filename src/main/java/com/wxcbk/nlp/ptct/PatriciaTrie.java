package com.wxcbk.nlp.ptct;


import com.wxcbk.nlp.util.DeepCopyUtil;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author :owen
 * @date :2020/11/6 16:54
 * @Description : PatriciaTrie：紧凑形式的字典树
 * T : 自定义类，必须实现序列化
 */
public class PatriciaTrie<T> {

    private List<TrieNode<T>> leafNodes;
    private Map<String, TrieNode<T>> indexSuffixMap;
    private static final int WORD_MAX_LENGTH = 30;

    public PatriciaTrie buildPatriciaTrie(List<Pair<String, T>> wordAndValues) {
        this.addWordsToTrie(wordAndValues);
        return this;
    }

    public PatriciaTrie() {
        this.indexSuffixMap = new HashMap<>(64);
        this.leafNodes = new ArrayList<>();
    }

    public PatriciaTrie(int indexInitSize) {
        if (indexInitSize == 0) {
            this.indexSuffixMap = new HashMap<>(512);
        } else {
            this.indexSuffixMap = new HashMap<>(indexInitSize);
        }
        this.leafNodes = new ArrayList<>();
    }

    public void addWordsToTrie(List<Pair<String, T>> wordAndValues) {
        wordAndValues.forEach(this::insertWord);
    }

    /**
     * @param query 输入一句话，检索出树中所有的节点
     * @return
     */
    public List<NodeItem<T>> search(String query) {
        List<NodeItem<T>> nodeItems = new ArrayList<>();
        List<SuffixToken> suffixTokenList = this.splitToToken(query.toLowerCase(), WORD_MAX_LENGTH);
        suffixTokenList.forEach(suffixToken -> {
            List<NodeItem<T>> tokenNodeItems = this.searchByTokenSeq(suffixToken);
            nodeItems.addAll(tokenNodeItems);
        });
        return nodeItems;
    }

    /**
     * @param wordAndValue wordAndValue为Pair<String, T>类型， word表示要插入的词,
     *                     value为自定义类型， 表示被插入这个词要存储的信息
     */
    public void insertWord(Pair<String, T> wordAndValue) {
        String[] strSeq = wordAndValue.getKey().split("");
        T value = wordAndValue.getValue();
        int len = strSeq.length;
        TrieNode<T> parentNode;
        if (len < 1) {
            return;
        } else {
            parentNode = this.getIndexTrieNode(strSeq, len - 1);
            if (len == 1) {
                this.setLeafNode(parentNode, value);
                leafNodes.add(parentNode);
                return;
            }
        }
        int step;
        for (int i = len - 2; i >= 0; i = i - step) {
            step = 0;
            Map<String, TrieNode<T>> childNodeMap = parentNode.getChildNodeMap();
            if (childNodeMap == null) {
                childNodeMap = new HashMap<>(64);
                parentNode.setChildNodeMap(childNodeMap);
                addLeafNodeToTrie(parentNode, value, i, strSeq, childNodeMap, leafNodes);
                break;
            }
            TrieNode<T> childNode;
            childNode = childNodeMap.get(strSeq[i]);
            if (childNode == null) {
                Set<String> childEdges = childNodeMap.keySet();
                Map<String, String> indexToEdge = new HashMap<>();
                for (String edge : childEdges) {
                    String index = edge.substring(0, 1);
                    indexToEdge.put(index, edge);
                }
                String edge = indexToEdge.get(strSeq[i]);
                if (StringUtils.isEmpty(edge)) {
                    addLeafNodeToTrie(parentNode, value, i, strSeq, childNodeMap, leafNodes);
                    break;
                }
                String[] edgeChar = edge.split("");
                StringBuilder newIndexSb = new StringBuilder(strSeq[i]);
                int k = i - 1;
                for (int j = 1; j < edgeChar.length; j++) {
                    if (k < 0) {
                        break;
                    }
                    if (edgeChar[j].equals(strSeq[k])) {
                        k--;
                        newIndexSb.append(edgeChar[j]);
                    } else {
                        break;
                    }
                }
                String newIndex = newIndexSb.toString();
                if (i + 1 == newIndex.length()) {
                    if (edge.length() == newIndex.length()) {
                        childNode = childNodeMap.get(edge);
                        this.setLeafNode(childNode, value);
                        leafNodes.add(childNode);
                        break;
                    } else if (edge.length() > newIndex.length()) {
                        TrieNode<T> newLeafNode = reSetEdgeNode(parentNode, edge, childNodeMap, newIndex);
                        this.setLeafNode(newLeafNode, value);
                        leafNodes.add(newLeafNode);
                        break;
                    }
                } else if (i + 1 > newIndex.length()) {
                    if (edge.length() == newIndex.length()) {
                        step = step + newIndex.length();
                        childNode = childNodeMap.get(edge);
                    } else if (edge.length() > newIndex.length()) {
                        TrieNode<T> nonLeafNode = reSetEdgeNode(parentNode, edge, childNodeMap, newIndex);
                        addLeafNodeToTrie(parentNode, value, i - newIndex.length(), strSeq, nonLeafNode.getChildNodeMap(), leafNodes);
                        break;
                    }
                }
            } else {
                step++;
                if (i == 0) {
                    this.setLeafNode(childNode, value);
                    leafNodes.add(childNode);
                    break;
                }
            }
            parentNode = childNode;
        }

    }

    private TrieNode<T> getIndexTrieNode(String[] strSeq, int index) {
        TrieNode<T> trieNode;
        if (indexSuffixMap.containsKey(strSeq[index])) {
            trieNode = indexSuffixMap.get(strSeq[index]);
        } else {
            trieNode = new TrieNode<T>(strSeq[index]);
            indexSuffixMap.put(strSeq[index], trieNode);
        }
        return trieNode;
    }


    private void setLeafNode(TrieNode<T> trieNode, T value) {
        trieNode.setEol(true);
        if (trieNode.getNodeValues() == null) {
            List<T> nodeItemList = new ArrayList<T>();
            nodeItemList.add(value);
            trieNode.setNodeValues(nodeItemList);
        } else {
            trieNode.getNodeValues().add(value);
        }
    }

    private TrieNode<T> reSetEdgeNode(TrieNode<T> parentNode, String edge, Map<String, TrieNode<T>> childNodeMap, String newEdgeIndex) {
        TrieNode<T> childNode = childNodeMap.get(edge);
        TrieNode<T> newEdgeNode = new TrieNode<T>(newEdgeIndex);
        newEdgeNode.setParentNode(parentNode);
        String childNewIndex = edge.substring(newEdgeIndex.length());
        childNode.setEdgeValue(childNewIndex);
        childNode.setParentNode(newEdgeNode);
        Map<String, TrieNode<T>> newChildNodeMap = new HashMap<String, TrieNode<T>>(4);
        newChildNodeMap.put(childNewIndex, childNode);
        newEdgeNode.setChildNodeMap(newChildNodeMap);
        parentNode.getChildNodeMap().remove(edge);
        parentNode.getChildNodeMap().put(newEdgeIndex, newEdgeNode);
        return newEdgeNode;
    }

    private TrieNode<T> createLeafNode(TrieNode<T> parentNode, T value, String index) {
        TrieNode<T> leafNode = new TrieNode<T>(index);
        List<T> nodeItemList = new ArrayList<T>();
        nodeItemList.add(value);
        leafNode.setNodeValues(nodeItemList);
        leafNode.setEol(true);
        leafNode.setParentNode(parentNode);
        return leafNode;
    }

    private String generateIndex(int i, String[] strSeq) {
        StringBuilder index = new StringBuilder();
        for (int k = i; k >= 0; k--) {
            index.append(strSeq[k]);
        }
        return index.toString();
    }

    private void addLeafNodeToTrie(TrieNode<T> parentNode, T value, int beginI, String[] strSeq, Map<String, TrieNode<T>> childNodeMap, List<TrieNode<T>> leafNodes) {
        String index = generateIndex(beginI, strSeq);
        TrieNode<T> leafNode = createLeafNode(parentNode, value, index);
        childNodeMap.put(index, leafNode);
        leafNodes.add(leafNode);
    }

    private List<NodeItem<T>> searchByTokenSeq(SuffixToken token) {
        List<NodeItem<T>> nodeItems = new ArrayList<>();
        String[] sequence = token.getSequence();
        int len = token.getLength();
        TrieNode<T> parentNode = indexSuffixMap.get(sequence[len - 1]);
        if (parentNode == null) {
            return nodeItems;
        } else if (parentNode.isEol()) {
            List<NodeItem<T>> preNodeItems = new ArrayList<>();
            parentNode.getNodeValues().forEach(nodeValue -> {
                T copyNodeValue;
                try {
                    copyNodeValue = DeepCopyUtil.deepClone(nodeValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                NodeItem<T> nodeItem = new NodeItem<>();
                nodeItem.setBegin(token.getEnd() - 1);
                nodeItem.setEnd(token.getEnd());
                nodeItem.setTextValue(sequence[len - 1]);
                nodeItem.setNodeValue(copyNodeValue);
                preNodeItems.add(nodeItem);
            });
            nodeItems.addAll(preNodeItems);
        }
        if (len < 2) {
            return nodeItems;
        }
        int step;
        label:
        for (int i = len - 2; i >= 0; i = i - step) {
            step = 0;
            if (parentNode.getChildNodeMap() == null) {
                break;
            }
            Set<String> childEdge = parentNode.getChildNodeMap().keySet();
            Map<String, String> indexToEdge = new HashMap<>();
            childEdge.forEach(e -> {
                String index = e.substring(0, 1);
                indexToEdge.put(index, e);
            });
            String edge = indexToEdge.get(sequence[i]);
            if (edge == null) {
                break;
            }
            if (i + 1 < edge.length()) {
                break;
            }
            int k = i;
            String[] edgeTokens = edge.split("");
            for (String ch : edgeTokens) {
                if (ch.equals(sequence[k])) {
                    k--;
                    step++;
                } else {
                    break label;
                }

            }
            TrieNode<T> childNode = parentNode.getChildNodeMap().get(edge);
            if (childNode == null) {
                break;
            } else {
                if (childNode.isEol()) {
                    List<NodeItem<T>> preNodeItems1 = new ArrayList<>();
                    String value = stringArrToString(sequence).substring(i + 1 - step);
                    childNode.getNodeValues().forEach(nodeValue -> {
                        T copyNodeValue;
                        try {
                            copyNodeValue = DeepCopyUtil.deepClone(nodeValue);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                        NodeItem<T> nodeItem = new NodeItem<>();
                        nodeItem.setBegin(token.getEnd() - value.length());
                        nodeItem.setEnd(token.getEnd());
                        nodeItem.setTextValue(value);
                        nodeItem.setNodeValue(copyNodeValue);
                        preNodeItems1.add(nodeItem);
                    });
                    nodeItems.addAll(preNodeItems1);
                }
                parentNode = childNode;
            }
        }
        return nodeItems;
    }

    private List<SuffixToken> splitToToken(String query, int wordMaxLength) {
        List<SuffixToken> suffixTokenList = new ArrayList<>();
        String[] strParts = query.split("");
        for (int i = 0; i < strParts.length; i++) {
            SuffixToken token;
            if (i + 1 <= wordMaxLength) {
                token = this.subToken(strParts, 0, i);
            } else {
                token = this.subToken(strParts, i + 1 - wordMaxLength, i);
            }
            suffixTokenList.add(token);
        }
        return suffixTokenList;
    }

    private SuffixToken subToken(String[] strParts, int begin, int end) {
        SuffixToken token = new SuffixToken();
        int length = end - begin + 1;
        String[] strSequence = new String[length];
        System.arraycopy(strParts, begin, strSequence, 0, length);
        token.setBegin(begin);
        token.setEnd(end + 1);
        token.setLength(length);
        token.setSequence(strSequence);
        return token;
    }

    private String stringArrToString(String[] arr) {
        if (arr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            sb.append(s);
        }
        return sb.toString();
    }

    public List<TrieNode<T>> getLeafNodes() {
        return leafNodes;
    }

    public void setLeafNodes(List<TrieNode<T>> leafNodes) {
        this.leafNodes = leafNodes;
    }

    public Map<String, TrieNode<T>> getIndexSuffixMap() {
        return indexSuffixMap;
    }

    public void setIndexSuffixMap(Map<String, TrieNode<T>> indexSuffixMap) {
        this.indexSuffixMap = indexSuffixMap;
    }
}
