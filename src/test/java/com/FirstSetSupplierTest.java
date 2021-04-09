package com;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.junit.Test;

import java.util.Arrays;



import static com.FirstSetSupplier.EPSILON;
import static org.junit.Assert.*;

public class FirstSetSupplierTest {

    @Test
    public void S__ABCD() {
        final Multimap<Character, String> in = ArrayListMultimap.create();
        /*
          S -> ACBD
          A -> CD | aA
          B -> b
          C -> cC | ∈
          D -> dD | ∈
         */
        in.put('S', "ABCD");
        in.put('A', "CD");
        in.put('A', "aA");
        in.put('B', "b");
        in.put('C', "cC");
        in.put('C', "$");
        in.put('D', "dD");
        in.put('D', "$");

        FirstSetSupplier firstSetSupplier = new FirstSetSupplier(in);
        firstSetSupplier.determineFollow();

        assertTrue(firstSetSupplier.getFirstSetForRule('S').containsAll(Arrays.asList('a', 'c', 'd', 'b')));
        assertTrue(firstSetSupplier.getFirstSetForRule('A').containsAll(Arrays.asList('a', 'c', 'd', EPSILON)));
        assertTrue(firstSetSupplier.getFirstSetForRule('B').contains('b'));
        assertTrue(firstSetSupplier.getFirstSetForRule('C').containsAll(Arrays.asList('c', EPSILON)));
        assertTrue(firstSetSupplier.getFirstSetForRule('D').containsAll(Arrays.asList('d', EPSILON)));
    }

    @Test
    public void S__A_B_C() {
        final Multimap<Character, String> in = ArrayListMultimap.create();
        /* S → A | B | C
         A → a
         B → Bb | b
         C → Cc | ∈
         */
        in.put('S', "A");
        in.put('S', "B");
        in.put('S', "C");
        in.put('A', "a");
        in.put('B', "Bb");
        in.put('B', "b");
        in.put('C', "Cc");
        in.put('C', "$");

        FirstSetSupplier firstSetSupplier = new FirstSetSupplier(in);
        firstSetSupplier.determineFollow();

        assertTrue(firstSetSupplier.getFirstSetForRule('S').containsAll(Arrays.asList('a', 'c', EPSILON, 'b')));
        assertTrue(firstSetSupplier.getFirstSetForRule('A').contains('a'));
        assertTrue(firstSetSupplier.getFirstSetForRule('B').contains('b'));
        assertTrue(firstSetSupplier.getFirstSetForRule('C').containsAll(Arrays.asList('c', EPSILON)));
    }

    @Test
    public void S__XYZ_d() {
        final Multimap<Character, String> in = ArrayListMultimap.create();
        /*
         Z → XYZ | d
         Y → c | $
         X → Y | a
         */
        in.put('Z', "XYZ");
        in.put('Z', "d");
        in.put('Y', "c");
        in.put('Y', "$");
        in.put('X', "Y");
        in.put('X', "a");

        FirstSetSupplier firstSetSupplier = new FirstSetSupplier(in);
        firstSetSupplier.determineFollow();

        assertTrue(firstSetSupplier.getFirstSetForRule('Z').containsAll(Arrays.asList('a', 'c', 'd')));
        assertTrue(firstSetSupplier.getFirstSetForRule('Y').containsAll(Arrays.asList('c', EPSILON)));
        assertTrue(firstSetSupplier.getFirstSetForRule('X').containsAll(Arrays.asList('a', 'c', EPSILON)));
    }

    @Test
    public void S__ACB_Cbb_Ba() {
        final Multimap<Character, String> in = ArrayListMultimap.create();
        /*
         S -> ACB | Cbb | Ba
         A -> da | BC
         B -> g | ∈
         C -> h | ∈
         */
        in.put('S', "ACB");
        in.put('S', "Cbb");
        in.put('S', "Ba");
        in.put('A', "da");
        in.put('A', "BC");
        in.put('B', "g");
        in.put('B', "$");
        in.put('C', "h");
        in.put('C', "$");
        FirstSetSupplier firstSetSupplier = new FirstSetSupplier(in);
        firstSetSupplier.determineFollow();
        assertTrue(firstSetSupplier.getFirstSetForRule('S').containsAll(Arrays.asList('d', 'g', 'h', EPSILON, 'b', 'a')));
        assertTrue(firstSetSupplier.getFirstSetForRule('A').containsAll(Arrays.asList('d', 'g', 'h', EPSILON)));
        assertTrue(firstSetSupplier.getFirstSetForRule('B').containsAll(Arrays.asList('g', EPSILON)));
        assertTrue(firstSetSupplier.getFirstSetForRule('C').containsAll(Arrays.asList('h', EPSILON)));
    }

    @Test
    public void S__aBDh() {
        final Multimap<Character, String> in = ArrayListMultimap.create();
        /*
          S → aBDh
          B → cC
          C → bC / ∈
          D → EF
          E → g / ∈
          F → f / ∈
          */
        in.put('S', "aBDh");
        in.put('B', "cC");
        in.put('C', "bC");
        in.put('C', "$");
        in.put('D', "EF");
        in.put('E', "g");
        in.put('E', "$");
        in.put('F', "f");
        in.put('F', "$");

        FirstSetSupplier firstSetSupplier = new FirstSetSupplier(in);
        firstSetSupplier.determineFollow();
        assertTrue(firstSetSupplier.getFirstSetForRule('S').contains('a'));
        assertTrue(firstSetSupplier.getFirstSetForRule('B').contains('c'));
        assertTrue(firstSetSupplier.getFirstSetForRule('C').containsAll(Arrays.asList('b', EPSILON)));
        assertTrue(firstSetSupplier.getFirstSetForRule('D').containsAll(Arrays.asList('g', 'f', EPSILON)));
        assertTrue(firstSetSupplier.getFirstSetForRule('E').containsAll(Arrays.asList('g', EPSILON)));
        assertTrue(firstSetSupplier.getFirstSetForRule('F').containsAll(Arrays.asList('f', EPSILON)));
    }
}
