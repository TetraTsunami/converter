package converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter two numbers in format: {source base} {target base} (To quit type /exit)\t");
            String input = scanner.nextLine();
            if (input.equals("/exit")) {
                break;
            }
            int[] bases;
            try {
                bases = verifyFirstLevelInput(input);
            } catch (IllegalArgumentException e) {
                System.out.println("[EXTREMELY LOUD INCORRECT BUZZER]");
                continue;
            }
            while (true) {
                System.out.printf("Enter number in base %d to convert to base %d (To go back type /back)\t", bases[0], bases[1]);
                input = scanner.nextLine();
                if (input.equals("/back")) {
                    break;
                }
                System.out.printf("Conversion result: %s\n\n", convertToBase(input, bases[0], bases[1]));
            }
        }
    }

    private static int[] verifyFirstLevelInput(String input) {
        String[] inputArray = input.split(" ");
        int[] bases = new int[2];

        if (inputArray.length != 2) {
            throw new IllegalArgumentException("Invalid input");
        }

        try {
            bases[0] = Integer.parseInt(inputArray[0]);
            bases[1] = Integer.parseInt(inputArray[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid input");
        }
        return bases;
    }

    public static String convertToBase(String num, int sourceBase, int destBase) {
        String[] inputArray = num.split("\\.");
        String integerPart = integerFromDecimal(integerToDecimal(inputArray[0], sourceBase), destBase);
        if (inputArray.length == 2) {
            String fractionPart = fractionFromDecimal(fractionToDecimal(inputArray[1], sourceBase).toString().substring(2), destBase).substring(2);
            return integerPart + "." + fractionPart;
        }
        return integerPart;
    }

    private static String integerFromDecimal(BigInteger input, int base) {
        StringBuilder builder = new StringBuilder();
        BigInteger accumulator = input;
        final BigInteger BASE = BigInteger.valueOf(base);
        ArrayList<Character> output = new ArrayList<>();

        while (!accumulator.equals(BigInteger.ZERO)) {
            output.add(decToHexDigit(accumulator.remainder(BASE).intValue()));
            accumulator = accumulator.divide(BASE);
        }

        for (int i = output.size(); i > 0; i--) {
            builder.append(output.get(i - 1));
        }
        return builder.toString();
    }

    private static String fractionFromDecimal(String input, int base) {
        StringBuilder builder = new StringBuilder("0.");
        final BigDecimal BASE = new BigDecimal(base);
        BigDecimal accumulator = new BigDecimal("0." + input);
        ArrayList<Character> output = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            accumulator = accumulator.multiply(BASE);
            output.add(decToHexDigit(accumulator.intValue()));
            accumulator = accumulator.subtract(BigDecimal.valueOf(accumulator.intValue()));
        }
        // rounding by hand
        if (hexToDecDigit(output.get(5)) >= base / 2) {
            output.set(4, decToHexDigit(hexToDecDigit(output.get(4)) + 1));
        }
        output.remove(5);

        for (Character character : output) {
            builder.append(character);
        }
        return builder.toString();
    }

    private static BigInteger integerToDecimal(String input, int sourceBase) {
        BigInteger accumulator = BigInteger.ZERO;
        for (int i = 0; i < input.length(); i++) {
            BigInteger multiplicand = BigInteger.valueOf(sourceBase).pow(input.length() - 1 - i);
            accumulator = accumulator.add(BigInteger.valueOf(hexToDecDigit(input.charAt(i))).multiply(multiplicand));
        }
        return accumulator;
    }

    private static BigDecimal fractionToDecimal(String fractionPart, int sourceBase) {
        // expected inputs could be 111, 2
        BigDecimal accumulator = BigDecimal.ZERO;
        for (int i = 0; i < fractionPart.length(); i++) {
            BigDecimal denominator = new BigDecimal(sourceBase).pow(1 + i);
            accumulator = accumulator.add(new BigDecimal(hexToDecDigit(fractionPart.charAt(i))).divide(denominator, 10, RoundingMode.HALF_DOWN));
        }
        return accumulator.setScale(5, RoundingMode.HALF_DOWN);
    }

    private static char decToHexDigit(int input) {
        if (input < 10) {
            return (char) (input + 48);
        } else {
            // +55 for uppercase, +87 for lowercase
            return (char) (input + 87);
        }
    }

    private static int hexToDecDigit(char input) {
        if (input >= 97){
            return (int) input - 87;
        } else if (input >= 65) {
            return (int) input - 55;
        } else {
            return (int) input - 48;
        }
    }
}
