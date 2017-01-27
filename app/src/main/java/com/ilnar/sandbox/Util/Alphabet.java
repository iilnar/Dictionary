package com.ilnar.sandbox.Util;

import java.util.HashMap;

/**
 * Created by ilnar on 06.06.16.
 */
public class Alphabet {
    private final HashMap<Character, Integer> map;
    private final String order = "АӘБВГДЕЁЖҖЗИЙКЛМНҢОӨПРСТУҮФХҺЦЧШЩЪЫЬЭЮЯ";
    private static Alphabet instance = new Alphabet();

    public static Alphabet getInstance() {
        return instance;
    }

    public int compare(char a, char b) {
        Integer aa = map.get(a);
        if (aa == null) {
            aa = -1;
        }
        Integer bb = map.get(b);
        if (bb == null) {
            bb = -1;
        }
        return aa.compareTo(bb);
    }

    private Alphabet() {
        map = new HashMap<>();
        for (int i = 0; i < order.length(); i++) {
            map.put(order.charAt(i), i);
            map.put(Character.toLowerCase(order.charAt(i)), i);
        }
    }
}
