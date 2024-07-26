import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PracticalExercisesTest {

    @Test
    public void testAdditionOfSmallPositiveNumbers() {
        float sum = 0.1f + 0.2f;
        String ieee754Sum = PracticalExercises.to754(sum);
        String expectedIEEE754Sum = "0 01111101 00110011001100110011000"; // Updated expected IEEE 754 representation
        assertEquals(expectedIEEE754Sum, ieee754Sum);
    }

    @Test
    public void testDivisionByTwo() {
        assertEquals("0 01111101 00000000000000000000000", PracticalExercises.to754(0.5f / 2.0f));
    }

    @Test
    public void testDivisionOfSameNumbers() {
        assertEquals("0 01111111 00000000000000000000000", PracticalExercises.to754(0.1f / 0.1f));
    }

    @Test
    public void testDivisionByZero() {
        assertEquals("0 11111111 00000000000000000000000", PracticalExercises.to754(1.0f / 0.0f)); // Positive Infinity
    }

    @Test
    public void testDivisionOfZeroByNumber() {
        assertEquals("0 00000000 00000000000000000000000", PracticalExercises.to754(0.0f / 2.0f)); // Zero
    }

    @Test
    public void testPositiveOverflow() {
        float overflow = Float.MAX_VALUE * 2.0f;
        String ieee754Overflow = PracticalExercises.to754(overflow);
        String expectedIEEE754Overflow = "0 11111111 00000000000000000000000"; // Expected IEEE 754 representation of infinity
        assertEquals(expectedIEEE754Overflow, ieee754Overflow);
    }

    @Test
    public void testSubnormalNumber() {
        float minDenorm = Float.MIN_VALUE;
        String ieee754MinDenorm = PracticalExercises.to754(minDenorm);
        String expectedIEEE754MinDenorm = "0 00000000 00000000000000000000001"; // Expected IEEE 754 representation of smallest subnormal number
        assertEquals(expectedIEEE754MinDenorm, ieee754MinDenorm);
    }

    @Test
    public void testNegativeOverflow() {
        float underflow = Float.MIN_VALUE / 2.0f;
        String ieee754Underflow = PracticalExercises.to754(underflow);
        String expectedIEEE754Underflow = "0 00000000 00000000000000000000000"; // Expected IEEE 754 representation of underflow (essentially zero)
        assertEquals(expectedIEEE754Underflow, ieee754Underflow);
    }

    @Test
    public void testNaN() {
        float nan = 0.0f / 0.0f;
        String ieee754Nan = PracticalExercises.to754(nan);
        String expectedIEEE754Nan = "0 11111111 10000000000000000000000"; // Expected IEEE 754 representation of NaN
        assertEquals(expectedIEEE754Nan, ieee754Nan);
    }
}