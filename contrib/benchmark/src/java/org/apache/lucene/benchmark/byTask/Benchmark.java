package org.apache.lucene.benchmark.byTask;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;

import org.apache.lucene.benchmark.byTask.utils.Algorithm;
import org.apache.lucene.benchmark.byTask.utils.Config;


/**
 * Run the benchmark algorithm.
 * <p>Usage: java Benchmark  algorithm-file
 * <ol>
 * <li>Read algorithm.
 * <li> Run the algorithm.
 * </ol>
 */
public class Benchmark {

  /**
   * Run the benchmark algorithm.
   * @param args benchmark config and algorithm files
   */
  public static void main(String[] args) {
    // verify command line args
    if (args.length < 1) {
      System.err.println("Usage: java Benchmark <algorithm file>");
      System.exit(1);
    }
    
    // verify input files 
    File algFile = new File(args[0]);
    if (!algFile.exists() || !algFile.isFile() || !algFile.canRead()) {
      System.err.println("cannot find/read algorithm file: "+algFile.getAbsolutePath()); 
      System.exit(1);
    }
    
    // last preparations
    PerfRunData runData = null;
    try {
      runData = new PerfRunData(new Config(algFile));
    } catch (Exception e) {
      System.err.println("Error: cannot init PerfRunData: "+e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    
    // parse algorithm
    Algorithm algorithm = null;
    try {
      algorithm = new Algorithm(runData);
    } catch (Exception e) {
      System.err.println("Error: cannot understand algorithm from file: "+algFile.getAbsolutePath());
      e.printStackTrace();
      System.exit(1);
    }

    System.out.println("------------> algorithm:");
    System.out.println(algorithm.toString());

    // execute
    try {
      algorithm.execute();
    } catch (Exception e) {
      System.err.println("Error: cannot execute the algorithm! "+e.getMessage());
      e.printStackTrace();
    }

    System.out.println("####################");
    System.out.println("###  D O N E !!! ###");
    System.out.println("####################");

  }

}