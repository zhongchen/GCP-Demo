package com.google.pso;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionTuple;
import org.apache.beam.sdk.values.TupleTag;
import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class SimplePipeline {
    public static void main(String[] args) {

        final TupleTag<Integer> evenNumbers = new TupleTag<Integer>(){};
        final TupleTag<Integer> oddNumbers = new TupleTag<Integer>(){};

//        PCollectionTuple mixedCollection =

        Box b = Box.newBuilder()
                .setType(BoxType.IMAGE).build();

        System.out.println(b);

        DateFormat simple = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        simple.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println(simple.format(new Date(System.currentTimeMillis())));

        if(isPdf("gs:://zhong-gcp/pdf/sample.pdf")) {
            System.out.println("y");
        }
    }

    private static boolean isPdf(String filename) {
        String[] parts = filename.split("\\.");
        return parts[parts.length - 1].toLowerCase().equals("pdf");
    }
    public static Boolean filter(Integer v) {
        if (v % 3 == 0) {
            return true;
        }else {
            return false;
        }
    }

    public static <K, V> void foo(K input, V v) {
        System.out.println(input);
        System.out.println(v);
    }

}

class FilterOddTransformation extends DoFn<Integer, Integer> {

    @ProcessElement
    public void process(ProcessContext context) {
        Integer element = context.element();
        if (element % 2 == 0) {
            context.output(element);
        }
    }
}

class DoubleElementTransformation extends DoFn<Integer, Integer> {

    @ProcessElement
    public void process(ProcessContext context) {
        Integer element = context.element();
        context.output(2 * element);
    }
}
