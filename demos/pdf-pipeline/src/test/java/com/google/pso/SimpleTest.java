package com.google.pso;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.testing.NeedsRunner;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.ValidatesRunner;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;

@RunWith(JUnit4.class)
@Category(NeedsRunner.class)
public class SimpleTest {
    static final Integer[] nums = new Integer[] {
            1,2,3,4
    };

    static final List<Integer> nums_list = Arrays.asList(nums);

    @Rule
    public final transient TestPipeline p = TestPipeline.create();

    @Before
    public void setUp() {

    }

    @Test
    public void testCount() {
        PCollection<Integer> input = p.apply(Create.of(nums_list));

        PCollection<Integer> output = input.
                apply( "filter odd", ParDo.of(new FilterOddTransformation())).
                apply("double element", ParDo.of(new DoubleElementTransformation())
        );


        PAssert.that(output).containsInAnyOrder(4, 8);

        p.run().waitUntilFinish();
    }
}
