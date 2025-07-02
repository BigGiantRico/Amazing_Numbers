package numbers;

import java.util.*;

class AmazingNumbers {

    static final Scanner sc = new Scanner(System.in);
    static ArrayList<Boolean> properties = new ArrayList<>();
    static long num = 0, countOfNum = 0;
    static ArrayList<String> availableProperties = new ArrayList<>(List.of(
            "EVEN", "ODD", "BUZZ", "DUCK", "PALINDROMIC", "GAPFUL",
            "SPY", "SQUARE", "SUNNY", "JUMPING", "HAPPY", "SAD"
    ));

    public static void main(String[] args) {
        System.out.println("Welcome to Amazing Numbers!\n\nSupported requests:\n- enter a natural number to know its properties;\n- enter two natural numbers to obtain the properties of the list:\n  * the first parameter represents a starting number;\n  * the second parameter shows how many consecutive numbers are to be processed;\n- two natural numbers and properties to search for;\n- a property preceded by minus must not be present in numbers;\n- enter 0 to exit.\n");

        while (true) {
            System.out.print("\nEnter a request: ");
            String input = sc.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }

            String[] parts = input.split("\\s+");
            long start;
            int count = 1;
            List<String> filters = new ArrayList<>();

            try {
                start = Long.parseLong(parts[0]);
                if (start == 0) {
                    System.out.println("Goodbye!");
                    break;
                }
                if (start < 1) {
                    System.out.println("The first parameter should be a natural number or zero.");
                    continue;
                }
                if (parts.length >= 2) {
                    count = Integer.parseInt(parts[1]);
                    if (count < 1) {
                        System.out.println("The second parameter should be a natural number.");
                        continue;
                    }
                }
                if (parts.length > 2) {
                    filters = Arrays.asList(Arrays.copyOfRange(parts, 2, parts.length));
                    List<String> wrong = new ArrayList<>();
                    for (String f : filters) {
                        String prop = f.replace("-", "").toUpperCase();
                        if (!availableProperties.contains(prop)) {
                            wrong.add(prop);
                        }
                    }
                    if (!wrong.isEmpty()) {
                        if (wrong.size() == 1) {
                            System.out.println("The property [" + wrong.get(0) + "] is wrong.");
                        } else {
                            System.out.println("The properties " + wrong + " are wrong.");
                        }
                        System.out.println("Available properties: " + availableProperties);
                        continue;
                    }
                }

                List<String> required = new ArrayList<>();
                List<String> excluded = new ArrayList<>();
                for (String f : filters) {
                    if (f.startsWith("-")) excluded.add(f.substring(1).toUpperCase());
                    else required.add(f.toUpperCase());
                }

                if (areMutuallyExclusive(required, excluded)) continue;

                int matched = 0;
                num = start;

                while (matched < count) {
                    checkVariousPropertiesOfNum(false);
                    String[] labels = {"BUZZ", "DUCK", "PALINDROMIC", "GAPFUL", "SPY", "SQUARE", "SUNNY", "JUMPING", "EVEN", "ODD", "HAPPY", "SAD"};
                    Map<String, Boolean> props = new HashMap<>();
                    for (int j = 0; j < labels.length; j++) props.put(labels[j], properties.get(j));

                    boolean matches = true;
                    for (String r : required) {
                        if (!props.getOrDefault(r.toUpperCase(), false)) { matches = false; break; }
                    }
                    for (String e : excluded) {
                        if (props.getOrDefault(e.toUpperCase(), false)) { matches = false; break; }
                    }
                    if (matches) {
                        if (count == 1 && parts.length == 1) {
                            System.out.println("\nProperties of " + num);
                            printDetailedProperties();
                        } else {
                            printInlineProperties(num);
                        }
                        matched++;
                    }
                    num++;
                }

            } catch (NumberFormatException e) {
                if (parts.length == 1) {
                    System.out.println("The first parameter should be a natural number or zero.");
                } else {
                    System.out.println("The second parameter should be a natural number.");
                }
            } catch (Exception e) {
                System.out.println("An unexpected error occurred.");
            }
        }
    }

    static boolean areMutuallyExclusive(List<String> required, List<String> excluded) {
        List<List<String>> exclusivePairs = List.of(
                List.of("EVEN", "ODD"),
                List.of("DUCK", "SPY"),
                List.of("SQUARE", "SUNNY"),
                List.of("HAPPY", "SAD"),
                List.of("-HAPPY", "-SAD"),
                List.of("-EVEN", "-ODD")
        );

        Set<String> req = new HashSet<>();
        for (String r : required) req.add(r.toUpperCase());
        Set<String> exc = new HashSet<>();
        for (String e : excluded) exc.add(e.toUpperCase());

        for (List<String> pair : exclusivePairs) {
            Set<String> upperPair = new HashSet<>();
            for (String p : pair) upperPair.add(p.toUpperCase());
            if (req.containsAll(upperPair) || exc.containsAll(upperPair)) {
                System.out.println("The request contains mutually exclusive properties: " + pair);
                System.out.println("There are no numbers with these properties.");
                return true;
            }
        }

        for (String prop : availableProperties) {
            if (req.contains(prop) && exc.contains(prop)) {
                System.out.println("The request contains mutually exclusive properties: [" + prop + ", -" + prop + "]");
                System.out.println("There are no numbers with these properties.");
                return true;
            }
        }

        return false;
    }

    static void checkVariousPropertiesOfNum(boolean printFlag) {
        properties.clear();
        properties.add(isBuzzNumber(num, printFlag));
        properties.add(isDuckNumber(num, printFlag));
        properties.add(isPalindromicNumber(num, printFlag));
        properties.add(isGapfulNumber(num, printFlag));
        properties.add(isSpyNumber(num, printFlag));
        properties.add(isSquareNumber(num, printFlag));
        properties.add(isSunnyNumber(num, printFlag));
        properties.add(isJumpingNumber(num, printFlag));
        properties.add(isEvenNumber(num, printFlag));
        properties.add(isOddNumber(num, printFlag));
        boolean happy = isHappyNumber(num, printFlag);
        properties.add(happy);
        properties.add(!happy);
    }

    static void printDetailedProperties() {
        String[] labels = { "buzz", "duck", "palindromic", "gapful", "spy", "square", "sunny", "jumping", "even", "odd", "happy", "sad" };
        for (int i = 0; i < labels.length; i++) {
            System.out.printf("    %s: %b%n", labels[i], properties.get(i));
        }
    }

    static void printInlineProperties(long number) {
        String[] labels = { "buzz", "duck", "palindromic", "gapful", "spy", "square", "sunny", "jumping", "even", "odd", "happy", "sad" };
        StringBuilder line = new StringBuilder(number + " is");
        for (int i = 0; i < labels.length; i++) {
            if (properties.get(i)) {
                line.append(" ").append(labels[i]).append(",");
            }
        }
        if (line.charAt(line.length() - 1) == ',') {
            line.deleteCharAt(line.length() - 1);
        }
        System.out.println(line);
    }

    // Dummy implementations (replace with actual logic)
    static boolean isBuzzNumber(long n, boolean p) { return n % 7 == 0 || n % 10 == 7; }
    static boolean isDuckNumber(long n, boolean p) { return String.valueOf(n).contains("0"); }
    static boolean isPalindromicNumber(long n, boolean p) { String s = String.valueOf(n); return s.equals(new StringBuilder(s).reverse().toString()); }
    static boolean isGapfulNumber(long n, boolean p) {
        String s = String.valueOf(n);
        if (s.length() < 3) return false;
        long d = Long.parseLong("" + s.charAt(0) + s.charAt(s.length()-1));
        return n % d == 0;
    }
    static boolean isSpyNumber(long n, boolean p) {
        int sum = 0, prod = 1;
        for (char c : String.valueOf(n).toCharArray()) {
            int digit = c - '0'; sum += digit; prod *= digit;
        }
        return sum == prod;
    }
    static boolean isSquareNumber(long n, boolean p) { long s = (long)Math.sqrt(n); return s * s == n; }
    static boolean isSunnyNumber(long n, boolean p) { return isSquareNumber(n + 1, false); }
    static boolean isJumpingNumber(long n, boolean p) {
        String s = String.valueOf(n);
        for (int i = 1; i < s.length(); i++) {
            if (Math.abs(s.charAt(i) - s.charAt(i-1)) != 1) return false;
        }
        return true;
    }
    static boolean isEvenNumber(long n, boolean p) { return n % 2 == 0; }
    static boolean isOddNumber(long n, boolean p) { return n % 2 != 0; }
    static boolean isHappyNumber(long n, boolean p) {
        Set<Long> seen = new HashSet<>();
        while (n != 1 && !seen.contains(n)) {
            seen.add(n);
            n = sumOfSquares(n);
        }
        return n == 1;
    }
    static long sumOfSquares(long n) {
        long sum = 0;
        while (n > 0) {
            long d = n % 10;
            sum += d * d;
            n /= 10;
        }
        return sum;
    }
}