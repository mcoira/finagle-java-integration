package com.ecomnext.util;

import scala.collection.JavaConversions;
import scala.collection.JavaConverters;

import java.util.Map;

/**
 * Utility class to make easier scala/java integration.
 * http://www.scala-lang.org/docu/files/collections-api/collections_46.html
 */
public class ScalaSupport {
    public static <T> java.util.List<T> toJavaList(scala.collection.immutable.List<T> scalaList) {
        return JavaConverters.asJavaListConverter(scalaList).asJava();
//        return JavaConversions.asJavaList(scalaList);
    }

    public static <T> java.util.List<T> toJavaList(scala.collection.Seq<T> scalaSeq) {
        return JavaConverters.asJavaListConverter(scalaSeq).asJava();
//        return JavaConversions.asJavaList(scalaSeq);
    }

    public static <T> scala.collection.immutable.List<T> toScalaList(java.util.List<T> javaList) {
        return JavaConversions.asScalaIterable(javaList).toList();
    }

    public static <T> java.util.Set toJavaSet(scala.collection.Set<T> scalaSet) {
        return JavaConversions.asJavaSet(scalaSet);
    }

    public static <T> scala.collection.Set toScalaSet(java.util.Set<T> javaSet) {
        return JavaConversions.asScalaSet(javaSet);
    }

    public static <T> scala.collection.Seq<T> toScalaSeq(java.util.List<T> javaList) {
        return JavaConversions.asScalaBuffer(javaList);
    }

    public static <K, V> Map<K, V> toJavaMap(scala.collection.Map<K,V> scalaMap) {
        return JavaConversions.asJavaMap(scalaMap);
    }

    public static <K, V> scala.collection.Map<K,V> toScalaMap(Map<K,V> javaMap) {
        return JavaConversions.asScalaMap(javaMap);
    }
}
