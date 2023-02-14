package com.github.yanghyu.isid.common.core.util.string;


import com.github.yanghyu.isid.common.core.util.CharUtil;

import java.util.Arrays;

public class DfaMatcher {

    /**
     * The range of string that last matched the pattern. If the last
     * match failed then first is -1; last initially holds 0 then it
     * holds the index of the end of the last match (which is where the
     * next search starts).
     */
    int first = -1, last = 0;

    /**
     * The end index of what matched in the last match operation.
     */
    int oldLast = -1;

    /**
     * The range within the sequence that is to be matched. Anchors
     * will match at these "hard" boundaries. Changing the region
     * changes these values.
     */
    int from, to;

    /**
     * The storage used by groups. They may contain invalid values if
     * a group was skipped during the matching.
     */
    int[] groups;

    /**
     * 匹配规则
     */
    DfaPattern pattern;

    /**
     * 原始字符串
     */
    String text;

    DfaMatcher(DfaPattern pattern, String input) {
        this.pattern = pattern;
        this.text = input;

        // Allocate state storage
        int count = Math.max(pattern.capturingGroupCount, 10);
        groups = new int[count];

        reset();
    }

    public String replaceAll(Character replacement) {
        reset();
        boolean result = find();
        if (result) {
            StringBuffer sb = new StringBuffer();
            do {
                appendReplacement(sb, replacement);
                result = find();
            } while (result);
            appendTail(sb);
            return sb.toString();
        }
        return text;
    }

    private void appendTail(StringBuffer sb) {
        sb.append(text, oldLast, text.length());
    }

    private void appendReplacement(StringBuffer sb, Character replacement) {
        sb.append(text, oldLast, first).append(CharUtil.nCharString(last - first, replacement));
    }

    private boolean find() {
        int nextSearchIndex = last;
        if (nextSearchIndex == first)
            nextSearchIndex++;

        // If next search starts before region, start it at region
        if (nextSearchIndex < from)
            nextSearchIndex = from;

        // If next search starts beyond region then it fails
        if (nextSearchIndex > to) {
            Arrays.fill(groups, -1);
            return false;
        }
        return search(nextSearchIndex);
    }

    private boolean search(int from) {
        from        = Math.max(from, 0);
        this.oldLast = this.last;
        this.first  = from;
        Arrays.fill(groups, -1);
        boolean result = pattern.match(this, from, text);
        if (!result)
            this.first = -1;

        return result;
    }

    private void reset() {
        first = -1;
        last = 0;
        oldLast = -1;
        Arrays.fill(groups, -1);
        from = 0;
        to = getTextLength();
    }

    private int getTextLength() {
        return text.length();
    }

}
