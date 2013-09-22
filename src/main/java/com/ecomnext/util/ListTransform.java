package com.ecomnext.util;

import com.google.common.base.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * Equivalent code to com.google.common.collect.Collections2 but specifically for
 * List conversions between T objects and domain objects. It also supports null
 * objects.
 */
public class ListTransform<F, T> {

    public static <F, T> List<T> transform(List<F> from,
                                           Function<? super F, T> function) {
        if (from == null) return null;

        List<T> output = new ArrayList<T>(from.size());
        for (F item : from) {
            output.add(function.apply(item));
        }

        return output;
    }
}
