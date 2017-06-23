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

   public Concat(){
      super(2, "concat");
   }

   @Override public Primitive makeCopy() {
      return new Concat();
   }


   @Override public void execute(Observation observation) {

   }


   @Override public void executeWithText(Observation observation) {
      String regex1 = getTextInput(0);
      String regex2 = getTextInput(1);

      String output = regex1 + " " + regex2;

      setValue(output);
   }


}
