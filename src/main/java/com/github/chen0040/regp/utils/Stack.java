package com.github.chen0040.regp.utils;


/**
 * Created by xschen on 22/6/2017.
 */
public class Stack<T> {
   private int N = 0;
   private class StackNode {
      T value;
      StackNode next;

      public StackNode(T value) {
         this.value = value;
      }
   }

   private StackNode first = null;
   public void push(T value) {
      StackNode oldFirst = first;
      first = new StackNode(value);
      first.next = oldFirst;
      N++;
   }

   public T pop() {
      StackNode oldFirst = first;
      if(oldFirst == null) {
         return null;
      }
      T value = oldFirst.value;
      first = oldFirst.next;
      N--;
      return value;
   }

   public int size() {
      return N;
   }

   public boolean isEmpty() {
      return N == 0;
   }
}
