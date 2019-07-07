package com.kaka.util.text;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 双数组字典树
 *
 * @author zkpursuit
 */
public class DoubleArrayTrie {

    private final static int BUF_SIZE = 16384;
    private final static int UNIT_SIZE = 8; // size of int + int

    private static class Node {

        int code; // 字符编码
        int depth; // 节点在树中的深度
        int left; // 节点在字典中的索引范围左边界
        int right; // 节点在字典中的索引范围右边界
    };

    private int check[];
    private int base[];

    private boolean used[]; // 记录计算出的begin所对应的使用情况
    private int size; // base和check的数组大小
    private int allocSize; // 被分配的数组大小
    private List<String> key; // 字典
    private int keyMaxLen = 0; //字典中单个关键词的最大长度
    private int keySize; // 字典大小
    private int length[];
    private int value[];
    private int progress;
    private int nextCheckPosition;
    int error_; // 错误标识

    // 内联调整大小数组大小
    private int resize(int newSize) {
        int[] base2 = new int[newSize];
        int[] check2 = new int[newSize];
        boolean used2[] = new boolean[newSize];
        if (allocSize > 0) {
            System.arraycopy(base, 0, base2, 0, allocSize);
            System.arraycopy(check, 0, check2, 0, allocSize);
            System.arraycopy(used2, 0, used2, 0, allocSize);
        }

        base = base2;
        check = check2;
        used = used2;

        return allocSize = newSize;
    }

    /**
     * 为节点parent生成子节点
     *
     * @param parent
     * @param siblings
     * @return
     */
    private int fetch(Node parent, List<Node> siblings) {
        if (error_ < 0) {
            return 0;
        }

        int prevCode = 0;

        // 在范围内遍历
        for (int i = parent.left; i < parent.right; i++) {
            if ((length != null ? length[i] : key.get(i).length()) < parent.depth) {
                // 遍历到了结束的节点
                continue;
            }

            String tmp = key.get(i);

            if (tmp.length() > keyMaxLen) {
                keyMaxLen = tmp.length();
            }

            int currCode = 0; // 遍历的当前字符的字符编码 + 1
            if ((length != null ? length[i] : tmp.length()) != parent.depth) {
                // tmp.length() != parent.depth的情况为排除自身的干扰因素
                // 比如若以"一举"中的'举'字符为parent，那么parent.depth = 2，"一举".length = 2
                currCode = (int) tmp.charAt(parent.depth) + 1;
            }

            // 消除非字典序的干扰
            if (prevCode > currCode) {
                error_ = -3;
                return 0;
            }

            if (currCode != prevCode || siblings.isEmpty()) {
                Node tmp_node = new Node();
                tmp_node.depth = parent.depth + 1;
                tmp_node.code = currCode;
                tmp_node.left = i;
                if (!siblings.isEmpty()) {
                    siblings.get(siblings.size() - 1).right = i;
                }

                siblings.add(tmp_node);
            }

            prevCode = currCode;
        }

        if (!siblings.isEmpty()) {
            siblings.get(siblings.size() - 1).right = parent.right;
        }

        return siblings.size();
    }

    /**
     * 向Trie树中插入子节点
     *
     * @param siblings
     * @return
     */
    private int insert(List<Node> siblings) {
        if (error_ < 0) {
            return 0;
        }

        int begin = 0;
        // 当前正在使用和检查的位置(index)
        int position = ((siblings.get(0).code + 1 > nextCheckPosition) ? siblings.get(0).code + 1 : nextCheckPosition) - 1;
        int nonzero_num = 0;
        int first = 0;

        if (allocSize <= position) {
            resize(position + 1);
        }

        // 此步循环的目的在于：寻找一个begin值使得check[begin + a1...an] == 0
        outer:
        while (true) {
            position++;

            if (allocSize <= position) {
                resize(position + 1);
            }

            if (check[position] != 0) {
                nonzero_num++;
                continue;
            } else if (first == 0) {
                nextCheckPosition = position;
                first = 1;
            }

            begin = position - siblings.get(0).code; // 当前位置离第一个兄弟节点的距离
            if (allocSize <= (begin + siblings.get(siblings.size() - 1).code)) {
                // progress can be zero
                double l = (1.05 > 1.0 * keySize / (progress + 1)) ? 1.05 : 1.0 * keySize / (progress + 1);
                resize((int) (allocSize * l));
            }

            // 对于当前位置已经被占用的情况，位置就向后移动
            if (used[begin]) {
                continue;
            }

            for (int i = 1; i < siblings.size(); i++) {
                if (check[begin + siblings.get(i).code] != 0) {
                    continue outer;
                }
            }

            break;
        }

        // -- Simple heuristics --
        // if the percentage of non-empty contents in check between the
        // index
        // 'next_check_pos' and 'check' is greater than some constant value
        // (e.g. 0.9),
        // new 'next_check_pos' index is written by 'check'.
        if (1.0 * nonzero_num / (position - nextCheckPosition + 1) >= 0.95) {
            nextCheckPosition = position;
        }

        used[begin] = true;
        size = (size > begin + siblings.get(siblings.size() - 1).code + 1) ? size : begin + siblings.get(siblings.size() - 1).code + 1;

        // 计算所有子节点的check值
        for (int i = 0; i < siblings.size(); i++) {
            // 计算check数组.使得同一个父节点下的所有子节点对应的check值相等(begin)
            // check设为check[begin + a1…an] = begin
            check[begin + siblings.get(i).code] = begin;
        }

        // 计算所有子节点的base值
        for (int i = 0; i < siblings.size(); i++) {
            List<Node> new_siblings = new ArrayList<>();

            // 无子节点，也就是叶子节点，代表一个词的终止且不为其他词的前缀
            if (fetch(siblings.get(i), new_siblings) == 0) {
                base[begin + siblings.get(i).code] = (value != null) ? (-value[siblings.get(i).left] - 1) : (-siblings.get(i).left - 1);
                if (value != null && (-value[siblings.get(i).left] - 1) >= 0) {
                    error_ = -2;
                    return 0;
                }

                progress++;
            } else {
                int h = insert(new_siblings);
                // TODO
                base[begin + siblings.get(i).code] = h;
            }
        }

        return begin;
    }

    public DoubleArrayTrie() {
        check = null;
        base = null;
        used = null;
        size = 0;
        allocSize = 0;
        error_ = 0;
    }

    /**
     * 清空数组
     */
    void clear() {
        check = null;
        base = null;
        used = null;
        allocSize = 0;
        size = 0;
    }

    public int getUnitSize() {
        return UNIT_SIZE;
    }

    public int getSize() {
        return size;
    }

    public int getTotalSize() {
        return size * UNIT_SIZE;
    }

    public int getNonzeroSize() {
        int result = 0;
        for (int i = 0; i < size; i++) {
            if (check[i] != 0) {
                result++;
            }
        }
        return result;
    }

    /**
     * 构建Double-array Trie
     *
     * @param key
     * @return
     */
    public int build(List<String> key) {
        return build(key, null, null, key.size());
    }

    /**
     * 构建Double-array Trie
     *
     * @param _key
     * @param _length
     * @param _value
     * @param _keySize
     * @return
     */
    public int build(List<String> _key, int _length[], int _value[], int _keySize) {
        if (_key == null || _keySize > _key.size()) {
            return 0;
        }

        key = _key;
        length = _length;
        keySize = _keySize;
        value = _value;
        progress = 0;

        resize(65536 * 32);

        base[0] = 1;
        nextCheckPosition = 0;

        Node root_node = new Node();
        root_node.left = 0;
        root_node.right = keySize;
        root_node.depth = 0;

        List<Node> siblings = new ArrayList<>();
        fetch(root_node, siblings);
        insert(siblings);

        used = null;
        key = null;

        return error_;
    }

    /**
     * 从文件中导入双数组
     *
     * @param fileName
     * @throws IOException
     */
    public void open(String fileName) throws IOException {
        File file = new File(fileName);
        size = (int) file.length() / UNIT_SIZE;
        check = new int[size];
        base = new int[size];

        try (DataInputStream is = new DataInputStream(new BufferedInputStream(new FileInputStream(file), BUF_SIZE))) {
            for (int i = 0; i < size; i++) {
                base[i] = is.readInt();
                check[i] = is.readInt();
            }
        }
    }

    /**
     * 把双数据导出到文件中
     *
     * @param fileName
     * @throws IOException
     */
    public void save(String fileName) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)))) {
            for (int i = 0; i < size; i++) {
                out.writeInt(base[i]);
                out.writeInt(check[i]);
            }
            out.close();
        }
    }

    /**
     * 把双数据导出到文件中
     *
     * @param fileName
     * @throws IOException
     */
    public void dump2(String fileName) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(fileName, true)) {
            StringBuilder strBuffer = new StringBuilder("INDEX\tBASE\tCHECK\n");
            for (int i = 0; i < size; i++) {
                strBuffer.append(i).append("\t").append(base[i]).append("\t").append(check[i]).append("\n");
            }

            outputStream.write(strBuffer.toString().getBytes());
        }
    }

    /**
     * 查找key在字典中的范围左边界
     *
     * @param key
     * @return
     */
    public int exactMatchSearch(String key) {
        return exactMatchSearch(key, 0, 0, 0);
    }

    /**
     * 查找key在字典中的范围左边界
     *
     * @param key
     * @param pos
     * @param len
     * @param nodePos
     * @return
     */
    public int exactMatchSearch(String key, int pos, int len, int nodePos) {
        if (len <= 0) {
            len = key.length();
        }
        if (nodePos <= 0) {
            nodePos = 0;
        }

        int result = -1;

        char[] keyChars = key.toCharArray();

        int b = base[nodePos];
        int p;

        for (int i = pos; i < len; i++) {
            p = b + (int) (keyChars[i]) + 1;
            if (b == check[p]) {
                b = base[p];
            } else {
                return result;
            }
        }

        p = b;
        int n = base[p];
        if (b == check[p] && n < 0) {
            result = -n - 1;
        }
        return result;
    }

    /**
     * 前缀查询
     *
     * @param key
     * @return
     */
    public List<Integer> commonPrefixSearch(String key) {
        return commonPrefixSearch(key, 0, 0, 0);
    }

    /**
     * 前缀查询
     *
     * @param key
     * @param pos
     * @param len
     * @param nodePos
     * @return
     */
    public List<Integer> commonPrefixSearch(String key, int pos, int len, int nodePos) {
        if (len <= 0) {
            len = key.length();
        }
        if (nodePos <= 0) {
            nodePos = 0;
        }

        List<Integer> result = new ArrayList<>();

        char[] keyChars = key.toCharArray();

        int b = base[nodePos];
        int n;
        int p;

        for (int i = pos; i < len; i++) {
            p = b;
            n = base[p];

            if (b == check[p] && n < 0) {
                result.add(-n - 1);
            }

            p = b + (int) (keyChars[i]) + 1;
            if (b == check[p]) {
                b = base[p];
            } else {
                return result;
            }
        }

        p = b;
        n = base[p];

        if (b == check[p] && n < 0) {
            result.add(-n - 1);
        }

        return result;
    }

    // debug
    public void dump() {
        for (int i = 0; i < size; i++) {
            System.err.println("i: " + i + " [" + base[i] + ", " + check[i] + "]");
        }
    }

    // debug
    public void dumpEffective() {
        System.err.println("INDEX\t[BASE, CHECK]");
        for (int i = 0; i < size; i++) {
            if (base[i] != 0 || check[i] != 0) {
                System.err.println(i + "\t[" + base[i] + ", " + base[i] + "]");
            }
        }
    }

    /**
     * 前缀查找
     *
     * @param text 查找的文本
     * @param pos 起始查找位置（字符索引）
     * @param hit 查找到一个关键词的回调
     */
    private void commonPrefixSearch(String text, int pos, Hit<Integer> hit) {
        int len = text.length();
        int b = base[0];
        int n;
        int p;
        int i;
        for (i = pos; i < len && (i - pos) < keyMaxLen; i++) {
            p = b;
            n = base[p];
            if (b == check[p] && n < 0) {
                if (hit != null) {
                    hit.visit(pos, i, -n - 1);
                }
            }
            char c = text.charAt(i);
            p = b + (int) (c) + 1;
            if (b == check[p]) {
                b = base[p];
            } else {
                return;
            }
        }
        p = b;
        n = base[p];
        if (b == check[p] && n < 0) {
            if (hit != null) {
                hit.visit(pos, i, -n - 1);
            }
        }
    }

    /**
     * 查找text文本中包含的所有关键词
     *
     * @param text 查找的文本
     * @param hit 查找到一个关键词的回调
     */
    public void findKeywords(String text, final Hit<Integer> hit) {
        int len = text.length();
        for (int i = 0; i < len; i++) {
            commonPrefixSearch(text, i, hit);
        }
    }

}
