package com.github.chen0040.regp;


import com.alibaba.fastjson.JSON;
import com.github.chen0040.gp.commons.Observation;
import com.github.chen0040.gp.treegp.TreeGP;
import com.github.chen0040.gp.treegp.program.Solution;
import com.github.chen0040.regp.operators.Concat;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xschen on 22/6/2017.
 */
@Getter
@Setter
public class GpCultivator {
   @Setter(AccessLevel.NONE)
   private TreeGP treeGP ;
   private int displayEvery = -1;
   private int populationSize = 1000;
   private int maxGenerations = 100;


   public Solution fit(List<String> trainingData) {
      treeGP= new TreeGP();
      treeGP.setDisplayEvery(displayEvery);
      treeGP.setPopulationSize(populationSize);
      treeGP.setMaxGeneration(maxGenerations);
      treeGP.setVariableCount(0);

      treeGP.getOperatorSet().addAll(new Concat());
      treeGP.setCostEvaluator((solution, observations) -> {
         double cost = 0;
         for(int i=0; i < observations.size(); ++i){
            GrokObservation observation = (GrokObservation) observations.get(i);
            solution.executeWithText(observation);
            String regex = observation.getPredictedTextOutput(0);
            cost += GrokService.evaluate(regex, observation.getText());
         }
         return cost;
      });

      int patternCount = GrokService.countPatterns();
      for(int i= 0; i < patternCount; ++i) {
         treeGP.addConstant("%{" + GrokService.getPattern(i) + "}", 1.0);
      }

      List<Observation> observations = new ArrayList<>();
      for(String data : trainingData) {
         GrokObservation observation = new GrokObservation();
         observation.setText(data);
         observations.add(observation);
      }
      return treeGP.fit(observations);
   }
}
