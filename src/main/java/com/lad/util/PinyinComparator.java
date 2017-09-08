package com.lad.util;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.Comparator;

/**
 * 功能描述：
 * Copyright: Copyright (c) 2017
 * Version: 1.0
 * Time:2017/9/8
 */
public class PinyinComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        return toPinYinString(o1).compareTo(toPinYinString(o2));
    }

    private String toPinYinString(String str){
        StringBuilder sb = new StringBuilder();
        String[] arr=null;
        for(int i=0; i<str.length(); i++){
            arr = PinyinHelper.toHanyuPinyinStringArray(str.charAt(i));
            if(arr != null && arr.length >0){
                for (String string : arr) {
                    sb.append(string);
                }
            }
        }
        return sb.toString();
    }
}
