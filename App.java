import java.util.Random;
import java.util.Arrays;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.io.IOException;

public class App {
    final static int MINRANDOM = 1;
    final static int MAXRANDOM = 40;
    final static int LOTTOLENGTH = 7;
    final static float LOTTOPRICE = 1.0f;
    public static void main(String[] args) {
        // Read the lotto.csv file.
        boolean quit = false;
        Scanner scanner = new Scanner(System.in);
        do {
            int[][] currentCoupon = readCurrentLines();
            if (currentCoupon[0].length == LOTTOLENGTH) {
                System.out.println("Current Lines");
                for (int i=0; i<currentCoupon.length; i++) {
                    System.out.print((i+1) + ".  ");
                    displayCouponLine(currentCoupon[i]);
                }
                System.out.println("");
            } else {
                System.out.println("No Current Lines");
                System.out.println("");
            }
            // Display the user interface (UI).
            System.out.println("N = Generate new coupon");
            System.out.println("R = Regenerate n line (give R1, regenerante line 1 )");
            System.out.println("P = Print coupon");
            System.out.println("Q = Quit");

            // Ask user to select one of the functions.
            String option = "";
            char functionSelector = ' ';
            System.out.print("\nChoice: ");
            option = scanner.nextLine();
            functionSelector = option.charAt(0);
            switch (functionSelector) {
                case 'N':
                    int[][] lottoCoupon = generateNewCoupon(scanner);
                    saveLottoLines(lottoCoupon);
                    continue;
                case 'R':
                    currentCoupon = regenerateLine(currentCoupon,option);
                    if (currentCoupon[0].length == LOTTOLENGTH) { // Deal with there is no current Lotto Lines.
                        saveLottoLines(currentCoupon);
                    }
                    continue;
                case 'P':
                    if (currentCoupon[0].length == LOTTOLENGTH) { // Deal with there is no current Lotto Lines.
                        printCoupon(currentCoupon);
                    } else {
                        System.out.println("No Current Line. Can not print a Lotto Coupon.");
                    }
                    continue;
                case 'Q':
                default:
                    quit = true;
            }
        } while (!quit);
        scanner.close();
    }

    public static int[][] generateNewCoupon(Scanner scanner) {
        // Generate a new coupon and display it with its cost on the screen.
        String save = "";
        System.out.print("How many lines: "); // Ask user to input how much lines of Lotto coupon.
        int numberOfLines = scanner.nextInt();
        scanner.nextLine();
        int[][] lottoCoupon = new int[numberOfLines][LOTTOLENGTH];
        for (int i=0; i<lottoCoupon.length; i++) {
            do {
                lottoCoupon[i] = generateLottoLine();
                displayCouponLine(lottoCoupon[i]);
                System.out.print("Save the numbers (y/n)? "); // Ask user if they want to save the line of Lotto numbers or generate a new line.
                save = scanner.nextLine();
            } while (!save.equals("y"));
        }
        System.out.println("Lotto numbers"); // Display the numbers and the cost of the Lotto coupon on the screen.
        for (int[] line : lottoCoupon) {
            displayCouponLine(line);
        }
        calculateTotalCost(numberOfLines);
        return lottoCoupon;
    }

    public static int[] generateLottoLine() {
        Random random = new Random();
        int randomNumber = 0;
        int[] lottoNumbers = new int[LOTTOLENGTH];
        boolean duplicate;
        for (int i=0; i<lottoNumbers.length; i++) {      
            do {
                duplicate = false; // Initiate the sign variable of duplicated number.
                randomNumber = random.nextInt(MAXRANDOM) + MINRANDOM; // Generate a Lotto number randomly between 1 and 40.
                for (int number : lottoNumbers) {
                    // Ensure that there are no duplicate numbers in a Lotto Line.
                    if (randomNumber == number) {
                        duplicate = true;
                        break;
                    } else {
                        continue;
                    }
                }
            } while (duplicate == true);
            lottoNumbers[i] = randomNumber;
        }
        Arrays.sort(lottoNumbers);
        return lottoNumbers;
    }

    public static void displayCouponLine(int[] lottoNumbers) {
        // Display a line of the Lotto Coupon.        
        for (int i=0; i<lottoNumbers.length; i++) {
            if (i != (lottoNumbers.length-1)) {
                if (lottoNumbers[i] < 10) {
                    System.out.print(lottoNumbers[i] + "  ");
                } else {
                    System.out.print(lottoNumbers[i] + " ");
                }
            } else {
                System.out.print(lottoNumbers[i] + "\n");
            }
        }
    }

    public static void calculateTotalCost(int numberOfLines) {
        // Calculate and display the total cost to play all the numbers.
        float totalCost = LOTTOPRICE * (float) numberOfLines;
        System.out.printf("\nCost: \u20AC%.2f\n",totalCost);
    }

    public static void printCoupon(int[][] lottoCoupon) {
        // Print the Lotto coupon as an easy human readable format.
        List<String> list = Arrays.asList(convertToPrintFormat(lottoCoupon));
        // Write the formatted string list into a coupon.txt file.
        try {
            Files.write(Paths.get("coupon.txt"), list);
        } catch (IOException e) {
            System.out.println("An error occurred to print a coupon: " + e.getMessage());
        }
    }

    public static String[] convertToPrintFormat(int[][] lottoCoupon) {
        // Convert to the printing format.
        String[] strings = new String[lottoCoupon.length];
        for (int i=0; i<lottoCoupon.length; i++) {
            strings[i] = "Line " + Integer.toString(i+1) + ":\n";
            for (int j=0; j<lottoCoupon[i].length; j++) {
                if (j != (lottoCoupon[i].length-1)) {
                    if (lottoCoupon[i][j] < 10) {
                        strings[i] = strings[i] + Integer.toString(lottoCoupon[i][j]) + "   ";
                    } else {
                        strings[i] = strings[i] + Integer.toString(lottoCoupon[i][j]) + "  ";
                    }
                } else {
                    strings[i] = strings[i] + Integer.toString(lottoCoupon[i][j]) + "\n";
                }
            }
        }
        return strings;
    }

    public static void saveLottoLines(int[][] lottoCoupon) {
        // Save the Lotto lines into a lotto.csv file.
        List<String> savingList = Arrays.asList(convertToSaveFormat(lottoCoupon));
        try {
            Files.write(Paths.get("lotto.csv"), savingList);
        } catch (IOException e) {
            System.out.println("An error occurred to save Lotto lines: " + e.getMessage());
        }
    }

    public static String[] convertToSaveFormat(int[][] lottoCoupon) {
        // Convert to the save format.
        String[] strings = new String[lottoCoupon.length];
        for (int i=0; i<lottoCoupon.length; i++) {
            for (int j=0; j<lottoCoupon[i].length; j++) {
                if (j == (lottoCoupon[i].length - 1)) {
                    strings[i] = strings[i] + Integer.toString(lottoCoupon[i][j]);
                } else if (j == 0) {
                    strings[i] = Integer.toString(lottoCoupon[i][j]) + ","; // Initiate the string.
                } else {
                    strings[i] = strings[i] + Integer.toString(lottoCoupon[i][j]) + ",";
                }
            }
        }
        return strings;
    }

    public static int[][] readCurrentLines() {
        // Read the data from the lotto.csv file.
        try {
            List<String> lines = Files.readAllLines(Paths.get("lotto.csv"));
            String[] stringLines = lines.toArray(new String[lines.size()]);
            int[][] currentCoupon = new int[stringLines.length][LOTTOLENGTH];
            for (int i=0; i<currentCoupon.length; i++) {
                String[] numberStrings = stringLines[i].split(",");
                for (int j=0; j<numberStrings.length; j++) {
                    currentCoupon[i][j] = Integer.parseInt(numberStrings[j]);
                }
            }
            return currentCoupon;
        } catch (IOException e) {
            int[][] noCurrentLines = new int[1][1];
            return noCurrentLines;
        }
    }

    public static int[][] regenerateLine(int[][] currentCoupon, String option) {
        // Regenerate one of the current Lotto lines.
        String lineNumberString = option.substring(1); // Catch line number.
        int lineNumber = 0;
        try {
            lineNumber = Integer.parseInt(lineNumberString);
            if (lineNumber > 0 && lineNumber <= currentCoupon.length) {
                currentCoupon[lineNumber-1] = generateLottoLine();
                System.out.println("Regenerante line " + lineNumber);
                displayCouponLine(currentCoupon[lineNumber-1]);
                return currentCoupon;
            } else {
                System.out.println("Can not find the line.");
                return currentCoupon;
            }
        } catch (NumberFormatException exception) {
            System.out.println("Invalid line number. The correct format is (give R1 to regenerante line 1).");
            return currentCoupon;
        }
    }
}