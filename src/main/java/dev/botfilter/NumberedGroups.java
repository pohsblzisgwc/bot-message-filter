package dev.botfilter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Learns prefixes from distinct numeric suffixes observed in the current connection. */
final class NumberedGroups {
    static final int THRESHOLD=10;
    private final Map<String,Set<String>> groups=new HashMap<>();

    void reset() { groups.clear(); }
    void observe(String name) {
        Parts p=parse(name);
        if (p==null) return;
        Set<String> numbers=groups.computeIfAbsent(p.prefix, ignored -> new HashSet<>());
        // Once a group qualifies, retaining more suffixes provides no additional information.
        if (numbers.size()<THRESHOLD) numbers.add(p.number);
    }
    boolean matches(String name) {
        Parts p=parse(name);
        if (p==null) return false;
        Set<String> numbers=groups.get(p.prefix);
        return numbers!=null && numbers.size()>=THRESHOLD;
    }
    private static Parts parse(String name) {
        if (name==null || name.isEmpty() || name.length()>16 || !name.matches("[A-Za-z0-9_]+")) return null;
        int split=name.length();
        while (split>0 && name.charAt(split-1)>='0' && name.charAt(split-1)<='9') split--;
        if (split==0 || split==name.length()) return null;
        String number=name.substring(split);
        int first=0;
        while (first<number.length()-1 && number.charAt(first)=='0') first++;
        return new Parts(name.substring(0,split).toLowerCase(Locale.ROOT),number.substring(first));
    }
    private record Parts(String prefix,String number) {}
}
