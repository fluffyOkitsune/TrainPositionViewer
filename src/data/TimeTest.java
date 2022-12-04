package data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TimeTest {
    @Test
    public void testAdd() {
        Time a, b;
        a = new Time(1, 1, 1);
        b = new Time(2, 3, 4);
        assertEquals(new Time(3, 4, 5), a.add(b));

        a = new Time(10, 30, 30);
        b = new Time(5, 50, 50);
        assertEquals(new Time(16, 21, 20), a.add(b));
    }

    @Test
    public void testSub() {
        Time a, b;
        a = new Time(2, 3, 4);
        b = new Time(1, 1, 1);
        assertEquals(new Time(1, 2, 3), a.sub(b));

        a = new Time(10, 30, 30);
        b = new Time(5, 50, 50);
        assertEquals(new Time(4, 39, 40), a.sub(b));
    }

    @Test
    public void testParseTime() {
        assertEquals(new Time(12, 34, 56), Time.parseTime("12:34:56"));
        assertEquals(new Time(9, 30, 0), Time.parseTime("930"));
        assertEquals(new Time(12, 0, 0), Time.parseTime("1200"));
    }

    @Test
    public void testCompareTo() {
        assertEquals(1, new Time(12, 0, 0).compareTo(new Time(11, 0, 0)));
        assertEquals(0, new Time(12, 0, 0).compareTo(new Time(12, 0, 0)));
        assertEquals(-1, new Time(12, 0, 0).compareTo(new Time(13, 0, 0)));

        assertEquals(1, new Time(12, 30, 0).compareTo(new Time(12, 25, 0)));
        assertEquals(-1, new Time(12, 30, 0).compareTo(new Time(12, 35, 0)));

        assertEquals(1, new Time(12, 0, 30).compareTo(new Time(12, 0, 25)));
        assertEquals(-1, new Time(12, 0, 30).compareTo(new Time(12, 0, 35)));
    }
}
