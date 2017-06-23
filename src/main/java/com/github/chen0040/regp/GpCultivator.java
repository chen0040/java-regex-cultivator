package com.github.chen0040.regp;


import com.github.chen0040.gp.commons.Observation;
import com.github.chen0040.gp.treegp.TreeGP;
import com.github.chen0040.gp.treegp.program.Solution;
import com.github.chen0040.regp.operators.Concat;
import com.github.chen0040.regp.operators.GrokPattern;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xschen on 22/6/2017.
 */
public class GpCultivator {
   private TreeGP treeGP ;

   public Solution fit(List<String> trainingData) {
        treeGP= new TreeGP();

      treeGP.getOperatorSet().addAll(new Concat(), new GrokPattern());
      treeGP.setCostEvaluator((solution, observations) -> {
         double cost = 0;
         for(int i=0; i < observations.size(); ++i){
            GrokObservation observation = (GrokObservation) observations.get(i);
            observation.reset();

            solution.execute(observation);
            String regex = GrokRepository.regex(observation.getPatterns());
            cost += observation.evaluate(regex);
         }
         return cost;
      });

      int patternCount = GrokRepository.countPatterns();
      for(int i= patternCount / 2; i < patternCount; ++i) {
         treeGP.addConstant(i, 1.0);
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
