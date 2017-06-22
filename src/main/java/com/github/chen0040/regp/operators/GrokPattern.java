package com.github.chen0040.regp.operators;


import com.github.chen0040.gp.commons.Observation;
import com.github.chen0040.gp.treegp.program.Operator;
import com.github.chen0040.gp.treegp.program.Primitive;
import com.github.chen0040.regp.GrokObservation;

import java.util.List;


/**
 * Created by xschen on 22/6/2017.
 */
public class GrokPattern extends Operator {

   public GrokPattern(){
      super(1, "Pat");
   }

   @Override public Primitive makeCopy() {
      return new GrokPattern();
   }





   @Override public void execute(Observation observation) {
      int patIndex = (int)this.getInput(0);

      GrokObservation go = (GrokObservation) observation;
      go.append(patIndex);

   }
}
