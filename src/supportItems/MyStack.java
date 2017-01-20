package supportItems;

import java.util.*;

public class MyStack<E> extends Stack<E> {


    public void showStack(){
        if(super.isEmpty())
            System.out.println("Empty stack");
        else
        System.out.println("stack: " + this);
    }


/*
    public static void main(String args[]) {
        MyStack<Integer> st = new MyStack();
        st.push(42);
        st.push(66);
        System.out.println(st.get(1));
        st.showStack();
        st.push(99);

        try {
            st.showStack();
        }catch (EmptyStackException e) {
            System.out.println("empty stack");
        }
    }
    */
}