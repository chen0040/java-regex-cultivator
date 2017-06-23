package com.github.chen0040.regp;


import com.github.chen0040.gp.commons.BasicObservation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xschen on 22/6/2017.
 */
@Getter
@Setter
public class GrokObservation extends BasicObservation {

   private String text;
   private List<Integer> patterns = new ArrayList<>();

   public GrokObservation() {
      super(0, 1);



   }

   public void reset(){
      patterns.clear();
   }

   public void append(int pat) {
      patterns.add(pat);
   }

   public double evaluate(String regex){
      return 0;
   }

}
