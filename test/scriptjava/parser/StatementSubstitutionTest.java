package scriptjava.parser;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Created by Дмитрий on 15.08.2016.
 */
public class StatementSubstitutionTest {

    @Test
    public void testSubstitute() {
        check("map(key1: 1, key2: 2)", "map(new String[] { \"key1\", \"key2\" }, new Object[] { 1, 2 })");
    }

    @Test
    public void testNoSubstitute() {
        check("hello", "hello");
        check("Map map = new HashMap()", "Map map = new HashMap()");
    }

    private void check(String in, String expected) {
        assertEquals(String.format(Locale.US,"in: %s, expected: %s", in, expected), expected, StatementSubstitution.substitute(in));
    }
}