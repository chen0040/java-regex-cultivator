package com.github.chen0040.regp.operators;


import com.github.chen0040.gp.commons.Observation;
import com.github.chen0040.gp.treegp.program.Operator;
import com.github.chen0040.gp.treegp.program.Primitive;
import com.github.chen0040.regp.GrokObservation;

import java.util.List;


/**
 * Created by xschen on 22/6/2017.
 */
public class Concat extends Operator {
   public static int SYMBOL = 10000;

   public Concat(){

   }

   @Override public Primitive makeCopy() {
      return new Concat();
   }


   @Override public void execute(Observation observation) {
      GrokObservation go = (GrokObservation) observation;
      go.append(-1);
   }

   @Override public void beforeExecute(List<Double> values, Observation observation) {
      super.beforeExecute(values, observation);

      GrokObservation go = (GrokObservation) observation;
      go.append(SYMBOL);

   }
}
