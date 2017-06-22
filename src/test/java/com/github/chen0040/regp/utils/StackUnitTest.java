package com.github.chen0040.regp.utils;


import org.testng.annotations.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.testng.Assert.*;


/**
 * Created by xschen on 22/6/2017.
 */
public class StackUnitTest {

   @Test
   public void test(){
      Stack<Integer> stack = new Stack<>();
      stack.push(0);
      stack.push(1);
      stack.push(2);
      assertThat(stack.size()).isEqualTo(3);
      assertFalse(stack.isEmpty());

      assertThat(stack.pop()).isEqualTo(2);
      assertThat(stack.pop()).isEqualTo(1);
      assertThat(stack.pop()).isEqualTo(0);
   }

}
