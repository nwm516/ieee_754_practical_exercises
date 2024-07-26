import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PracticalExercises {

    public static String to754(float value) {
        if (Float.isInfinite(value)) {
            return (value > 0 ? "0 " : "1 ") + "11111111 00000000000000000000000";
        } else if (Float.isNaN(value)) {
            return "0 11111111 10000000000000000000000"; // NaN with non-zero mantissa
        }

        int signBit = value < 0 ? 1 : 0;
        value = Math.abs(value);

        if (value == 0) {
            return signBit + " 00000000 00000000000000000000000"; // Zero representation
        }

        // Handle normalized numbers
        int exponent;
        StringBuilder mantissaBuilder = new StringBuilder();

        if (value < Float.MIN_NORMAL) {
            // Handle subnormal numbers
            exponent = -126;
            float mantissa = value / (float) Math.pow(2, exponent);
            while (mantissa < 1 && mantissa > 0) {
                mantissa *= 2;
                mantissaBuilder.append(mantissa >= 1 ? "1" : "0");
                if (mantissa >= 1) {
                    mantissa -= 1;
                }
            }
            // Format mantissa to 23 bits
            while (mantissaBuilder.length() < 23) {
                mantissaBuilder.append("0");
            }
            return signBit + " 00000000 " + mantissaBuilder.toString();
        } else {
            // Normalize the number
            int intPart = (int) value;
            float fracPart = value - intPart;

            // Convert integer part to binary
            String intBinary = Integer.toBinaryString(intPart);
            StringBuilder fracBinary = new StringBuilder();
            while (fracPart > 0 && fracBinary.length() < 23) {
                fracPart *= 2;
                if (fracPart >= 1) {
                    fracBinary.append("1");
                    fracPart -= 1;
                } else {
                    fracBinary.append("0");
                }
            }

            // Handle the exponent and mantissa
            int actualExponent = intBinary.length() - 1;
            String mantissa = intBinary.substring(1) + fracBinary.toString();
            if (intPart == 0) {
                actualExponent = -1;
                for (int i = 0; i < fracBinary.length(); i++) {
                    if (fracBinary.charAt(i) == '1') {
                        actualExponent = -(i + 1);
                        mantissa = fracBinary.substring(i + 1);
                        break;
                    }
                }
            }

            int biasedExponent = actualExponent + 127;
            String exponentBinary = String.format("%8s", Integer.toBinaryString(biasedExponent)).replace(' ', '0');

            if (mantissa.length() > 23) {
                mantissa = mantissa.substring(0, 23);
            } else {
                mantissa = String.format("%-23s", mantissa).replace(' ', '0');
            }

            return signBit + " " + exponentBinary + " " + mantissa;
        }
    }


    private static XYSeriesCollection createPrecisionDataset() {
        XYSeries denormalizedSeries = new XYSeries("Denormalized Numbers");
        XYSeries largeNumbersSeries = new XYSeries("Large Numbers");

        // Defining reasonable bounds
        float minDenormValue = Float.MIN_VALUE; // Smallest positive subnormal number
        float maxDenormValue = Float.MIN_NORMAL; // Largest subnormal number

        float minLargeValue = 1e-10f; // Lower bound for large numbers
        float maxLargeValue = 1e9f;   // Cap for large numbers

        // Plot points for denormalized numbers (very small)
        for (float i = minDenormValue; i <= maxDenormValue; i *= 10) {
            if (Float.isFinite(i) && i != 0) {
                float precision = (float)Math.log10(1 / i);
                denormalizedSeries.add(i, precision);
            }
        }

        // Plot points for large numbers
        for (float i = minLargeValue; i <= maxLargeValue; i *= 10) {
            if (Float.isFinite(i)) {
                float precision = (float)Math.log10(i);
                largeNumbersSeries.add(i, precision);
            }
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(denormalizedSeries);
        dataset.addSeries(largeNumbersSeries);

        return dataset;
    }

    private static void createAndShowPlot() {
        XYSeriesCollection dataset = createPrecisionDataset();
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Precision vs. Magnitude",
                "Number",
                "Precision Factor",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();

        // Adjust domain axis range to cover denormalized numbers
        domainAxis.setRange(1e-45, 1e9); // Adjusting lower bound to cover denormalized numbers
        rangeAxis.setRange(-10, 10);

        JFrame frame = new JFrame("Precision Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }

    private static void roundingModeAnswers(RoundingMode[] roundingModes, BigDecimal sumValue) {
        for (RoundingMode mode : roundingModes) {
            BigDecimal roundedSumValue = sumValue.setScale(7, mode);
            float floatSumValue = roundedSumValue.floatValue();
            String ieeeRoundedSum = to754(floatSumValue);
            System.out.println("RoundingMode." + mode + " --> IEEE 754 Representation: " + ieeeRoundedSum);
        }
        System.out.println();
    }

    public static void main(String[] args) {
        // Rounding modes that will be used for testing
        RoundingMode[] roundingModes = {
                RoundingMode.UP,
                RoundingMode.DOWN,
                RoundingMode.CEILING,
                RoundingMode.FLOOR,
                RoundingMode.HALF_UP,
                RoundingMode.HALF_DOWN,
                RoundingMode.HALF_EVEN
        };

        // Example values from the assignment requirements
        BigDecimal sumValue = new BigDecimal("0.1").add(new BigDecimal("0.2"));
        BigDecimal divValue = new BigDecimal("1.0").divide(new BigDecimal("3.0"), 10, RoundingMode.HALF_UP);

        float sumNums = 0.1f + 0.2f;
        String ieeeSum = to754(sumNums);
        System.out.println("IEEE 754 Representation of 0.1 + 0.2: " + ieeeSum);
        System.out.println();

        System.out.println("Rounding mode testing for 0.1 + 0.2: ");
        roundingModeAnswers(roundingModes, sumValue);

        float divNums = 1.0f / 3.0f;
        String ieeeDiv = to754(divNums);
        System.out.println("IEEE 754 Representation of 1.0 / 3.0: " + ieeeDiv);
        System.out.println();

        System.out.println("Rounding mode testing for 1.0 / 3.0: ");
        roundingModeAnswers(roundingModes, divValue);

        float positiveInf = 1.0f / 0.0f;
        String ieeePosInf = to754(positiveInf);
        System.out.println("IEEE 754 Representation of Positive Infinity: " + ieeePosInf);
        System.out.println("Is it ACTUALLY Positive Infinity?: " + Float.isInfinite(positiveInf));
        System.out.println();

        float negativeInf = -1.0f / 0.0f;
        String ieeeNegInf = to754(negativeInf);
        System.out.println("IEEE 754 Representation of Negative Infinity: " + ieeeNegInf);
        System.out.println("Is it ACTUALLY Negative Infinity?: " + Float.isInfinite(negativeInf));
        System.out.println();

        float nan = 0.0f / 0.0f;
        String ieeeNan = to754(nan);
        System.out.println("IEEE 754 Representation of NaN: " + ieeeNan);
        System.out.println("Is it ACTUALLY NaN?: " + Float.isNaN(nan));
        System.out.println("Is NaN equal to itself?: " + (nan == nan));
        System.out.println();

        float overflow = Float.MAX_VALUE * 2.0f;
        String ieeeOverflow = to754(overflow);
        System.out.println("IEEE 754 Representation of Overflow: " + ieeeOverflow);
        System.out.println("Is it ESSENTIALLY Positive Infinity?: " + Float.isInfinite(overflow));
        System.out.println();

        float underflow = Float.MIN_VALUE / 2.0f;
        String ieeeUnderflow = to754(underflow);
        System.out.println("IEEE 754 Representation of Underflow: " + ieeeUnderflow);
        System.out.println("Is it ESSENTIALLY Zero?: " + (underflow == 0.0f));
        System.out.println();

        createAndShowPlot();
    }
}