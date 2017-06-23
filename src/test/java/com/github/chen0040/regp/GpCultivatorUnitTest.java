package com.github.chen0040.regp;


import com.github.chen0040.gp.treegp.program.Solution;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.*;


/**
 * Created by xschen on 23/6/2017.
 */
public class GpCultivatorUnitTest {

   @Test
   public void test_simple(){
      GpCultivator generator = new GpCultivator();
      generator.setDisplayEvery(2);
      generator.setPopulationSize(1000);
      generator.setMaxGenerations(50);

      List<String> trainingData = new ArrayList<>();
      trainingData.add("Starting transaction for session -464410bf-37bf-475a-afc0-498e0199f008");
      Solution solution = generator.fit(trainingData);



   }
}
