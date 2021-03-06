/*
 * The MIT License
 *
 *  Copyright (c) 2015, Mahmoud Ben Hassine (mahmoud@benhassine.fr)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package org.easybatch.tutorials.advanced.jmx;

import org.easybatch.core.api.Engine;
import org.easybatch.core.api.RecordFilter;
import org.easybatch.core.filter.RecordNumberGreaterThanFilter;
import org.easybatch.core.filter.RecordNumberLowerThanFilter;
import org.easybatch.core.impl.EngineBuilder;
import org.easybatch.core.reader.StringRecordReader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.Arrays.asList;

/**
 * Main class to run the parallel JMX tutorial.
 *
 * @author Mahmoud Ben Hassine (mahmoud@benhassine.fr)
 */
public class ParallelEngineJmxTutorial {

    private static final int THREAD_POOL_SIZE = 2;

    public static void main(String[] args) throws Exception {

        // Create the String data source
        String dataSource =
                "1,foo,easy batch rocks! #EasyBatch\n" +
                "2,bar,@foo I do confirm :-)\n" +
                "3,baz,@foo @bar what are you talking about? Am I in trouble?\n" +
                "4,foo,@baz yes you are in trouble!\n" +
                "5,bar,@baz It's about easy batch. See in here: http://www.easybatch.org cc @md_benhassine\n" +
                "6,baz,@foo @bar @md_benhassine Oh damn that's really easy!\n" +
                "7,md_benhassine,Thank you all! your feedback is welcome :-)\n" +
                "8,foo,@md_benhassine Have you some benchmarks out there?\n" +
                "9,md_benhassine,@foo yep check them out here: https://github.com/EasyBatch/easybatch-benchmarks\n" +
                "10,foo,@md_benhassine I'll see there thx!";

        // Build worker engines
        // worker engine 1: process records 1-5 and filters records 6-10
        Engine engine1 = buildEngine(dataSource, new RecordNumberGreaterThanFilter(5), "worker-engine1");
        // worker engine 2: process 6-10 and filters records 1-5
        Engine engine2 = buildEngine(dataSource, new RecordNumberLowerThanFilter(6), "worker-engine2");

        //create a 2 threads pool to call worker engines in parallel
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        executorService.invokeAll(asList(engine1, engine2));

        executorService.shutdown();

    }

    private static Engine buildEngine(String dataSource, RecordFilter recordFilter, String engineName) {
        return EngineBuilder.aNewEngine()
                .named(engineName)
                .reader(new StringRecordReader(dataSource))
                .filter(recordFilter)
                .processor(new TweetSlowProcessor())
                .enableJMX(true)
                .build();
    }

}