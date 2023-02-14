package com.github.yanghyu.isid.common.core.util.string;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class DfaPattern {

    int capturingGroupCount = 0;

    private HashMap<Character, HashMap<?,?>> root = new HashMap<>();

    public static DfaPattern compile(Set<String> words) {
        DfaPattern t = new DfaPattern();
        t.reCompile(words);
        return t;
    }

    public void reCompile(Set<String> words) {
        HashMap<Character, HashMap<?,?>> root = new HashMap<>();
        int capturingGroupCount = 0;
        if (words != null && words.size() > 0) {
            for (String w : words) {
                int len = w.length();
                if (len > 0 && !w.startsWith("\n")) {
                    HashMap<Character, HashMap<?,?>> pMap = root, cMap;
                    int count = 0;
                    for (int i = 0; i < len; i++) {
                        char c = w.charAt(i);
                        c = Character.toLowerCase(c);
                        if (c == '\n') {
                            break;
                        }
                        count ++;
                        //noinspection unchecked
                        cMap = (HashMap<Character, HashMap<?,?>>) pMap.get(c);
                        if (cMap == null) {
                            cMap = new HashMap<>();
                            pMap.put(c, cMap);
                        }
                        pMap = cMap;
                    }
                    if (count > capturingGroupCount) {
                        capturingGroupCount = count;
                    }
                    pMap.put('\n', null);
                }
            }
        }
        this.root = root;
        this.capturingGroupCount = capturingGroupCount;
    }

    public DfaMatcher matcher(String input) {
        return new DfaMatcher(this, input);
    }

    boolean match(DfaMatcher dfaMatcher, int from, String text) {
        int i = from;
        for (; i < dfaMatcher.to; i++) {
            HashMap<Character, HashMap<?,?>> map = root;
            int j = i;
            for (; j < dfaMatcher.to; j++) {
                char c = text.charAt(j);
                c = Character.toLowerCase(c);
                //noinspection unchecked
                map = (HashMap<Character, HashMap<?, ?>>) map.get(c);
                if (map == null) {
                    // 到此字符处未匹配到上
                    break;
                } else if (map.containsKey('\n')){
                    // 到该字符处匹配上了词
                    dfaMatcher.groups[j - i] = 2;
                } else {
                    dfaMatcher.groups[j - i] = 1;
                }
            }
            if (j > i) {
                for (int k = j; k >= i; k--) {
                    if (dfaMatcher.groups[k - i] == 2) {
                        dfaMatcher.first = i;
                        dfaMatcher.last = k + 1;
                        return true;
                    }
                }
                Arrays.fill(dfaMatcher.groups, -1);
            }

        }
        dfaMatcher.first = -1;
        dfaMatcher.last = i + 1;
        return false;
    }

}
