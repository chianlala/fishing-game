package com.maple.game.osee.util;

import cn.hutool.core.lang.TypeReference;

import java.util.List;
import java.util.Set;

public interface TypeReferenceUtil {

    TypeReference<Set<String>> STRING_SET = new TypeReference<Set<String>>() {};

    TypeReference<List<List<Integer>>> INTEGER_LIST_LIST = new TypeReference<List<List<Integer>>>() {};

}
