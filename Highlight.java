import java.io.*;
import java.util.*;

public class Highlight {

    //environment variables
    private static final String ENV_DEFAULT_COLOR = "HL_DEFAULT_COLOR";                      //user-defined default color
    private static final String ENV_ALWAYS_CASE_INSENSTIVE = "HL_ALWAYS_CASE_INSENSITIVE";   //always perform case insensitive matching

    //colors
    enum Color {
        BLACK,
        RED,
        GREEN,
        YELLOW,
        BLUE,
        PURPLE,
        CYAN,
        WHITE
    }

    private static Color sDefaultColor = Color.WHITE;

    //attributes
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String UNDERLINE = "\u001B[4m";
    private static final String BLINK = "\u001B[5m";

    //text colors
    private static final String TEXT_BLACK = BOLD + "\u001B[30m";
    private static final String TEXT_RED = BOLD + "\u001B[31m";
    private static final String TEXT_GREEN = BOLD + "\u001B[32m";
    private static final String TEXT_YELLOW = BOLD + "\u001B[33m";
    private static final String TEXT_BLUE = BOLD + "\u001B[34m";
    private static final String TEXT_PURPLE = BOLD + "\u001B[35m";
    private static final String TEXT_CYAN = BOLD + "\u001B[36m";
    private static final String TEXT_WHITE = BOLD + "\u001B[37m";

    //background colors
    private static final String BACK_BLACK = BOLD + "\u001B[40m";
    private static final String BACK_RED = BOLD + "\u001B[41m";
    private static final String BACK_GREEN = BOLD + "\u001B[42m";
    private static final String BACK_YELLOW = BOLD + "\u001B[43m";
    private static final String BACK_BLUE = BOLD + "\u001B[44m";
    private static final String BACK_PURPLE = BOLD + "\u001B[45m";
    private static final String BACK_CYAN = BOLD + "\u001B[46m";
    private static final String BACK_WHITE = BOLD + "\u001B[47m";

    //color options mapping
    private static final Map<String, Color> COLORS_MAP;
    static {
        final Map<String, Color> colorsMap = new HashMap<String, Color>();
        colorsMap.put("-a", Color.BLACK);             //-a for black because -b is for blue!
        colorsMap.put("--black", Color.BLACK);
        colorsMap.put("-r", Color.RED);
        colorsMap.put("--red", Color.RED);
        colorsMap.put("-g", Color.GREEN);
        colorsMap.put("--green", Color.GREEN);
        colorsMap.put("-y", Color.YELLOW);
        colorsMap.put("--yellow", Color.YELLOW);
        colorsMap.put("-b", Color.BLUE);
        colorsMap.put("--blue", Color.BLUE);
        colorsMap.put("-p", Color.PURPLE);
        colorsMap.put("--purple", Color.PURPLE);
        colorsMap.put("-c", Color.CYAN);
        colorsMap.put("--cyan", Color.CYAN);
        colorsMap.put("-w", Color.WHITE);
        colorsMap.put("--white", Color.WHITE);
        COLORS_MAP = Collections.unmodifiableMap(colorsMap);
    }

    //text colors
    private static final Map<Color, String> TEXT_COLORS_MAP;
    static {
        final Map<Color, String> colorsMap = new HashMap<Color, String>();
        colorsMap.put(Color.BLACK, TEXT_BLACK);
        colorsMap.put(Color.RED, TEXT_RED);
        colorsMap.put(Color.GREEN, TEXT_GREEN);
        colorsMap.put(Color.YELLOW, TEXT_YELLOW);
        colorsMap.put(Color.BLUE, TEXT_BLUE);
        colorsMap.put(Color.PURPLE, TEXT_PURPLE);
        colorsMap.put(Color.CYAN, TEXT_CYAN);
        colorsMap.put(Color.WHITE, TEXT_WHITE);
        TEXT_COLORS_MAP = Collections.unmodifiableMap(colorsMap);
    }

    //background colors
    private static final Map<Color, String> BACK_COLORS_MAP;
    static {
        final Map<Color, String> colorsMap = new HashMap<Color, String>();
        colorsMap.put(Color.BLACK, BACK_BLACK);
        colorsMap.put(Color.RED, BACK_RED);
        colorsMap.put(Color.GREEN, BACK_GREEN);
        colorsMap.put(Color.YELLOW, BACK_YELLOW);
        colorsMap.put(Color.BLUE, BACK_BLUE);
        colorsMap.put(Color.PURPLE, BACK_PURPLE);
        colorsMap.put(Color.CYAN, BACK_CYAN);
        colorsMap.put(Color.WHITE, BACK_WHITE);
        BACK_COLORS_MAP = Collections.unmodifiableMap(colorsMap);
    }

    private static String sTargetText;
    private static String sAttributes = "";
    private static boolean sHighlightWholeLine = false;
    private static boolean sCaseSensitive = true;               //set by command line option
    private static boolean sAlwaysCaseInsensitive = false;      //set by environment variable

    public static void main(final String args[]) throws IOException {
        if (parseEnvVars() && parseArgs(args)) {
            String line = "";
            final BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            while ((line = stdin.readLine()) != null) {
                printlnWithHighlight(line);
            }
        }
    }

    private static boolean parseEnvVars() {
        boolean result = true;

        final Map<String, String> envVars = System.getenv();

        final String envDefaultColor = envVars.get(ENV_DEFAULT_COLOR);
        if (envDefaultColor != null) {
            final Color color = COLORS_MAP.get("--" + envDefaultColor.toLowerCase());
            if (color != null) {
                sDefaultColor = color;
            } else {
                System.err.println("Environment variable: " + TEXT_WHITE + ENV_DEFAULT_COLOR + RESET +
                                   " has unknown value: " + TEXT_WHITE + envDefaultColor + RESET);
                result = false;
            }
        }

        final String envCaseInsensitive = envVars.get(ENV_ALWAYS_CASE_INSENSTIVE);
        if (envCaseInsensitive != null) {
            final String value = envCaseInsensitive.toLowerCase();
            sAlwaysCaseInsensitive = value.equals("true") || value.equals("1");
            if (sAlwaysCaseInsensitive) {
                sCaseSensitive = false;
            } else if (!value.equals("") && !value.equals("false") && !value.equals("0")) {
                System.err.println("Environment variable: " + TEXT_WHITE + ENV_ALWAYS_CASE_INSENSTIVE + RESET +
                                   " has unknown value: " + TEXT_WHITE + envCaseInsensitive + RESET);
                result = false;
            }
        }

        if (!result) {
            System.err.println();
        }

        return result;
    }

    private static boolean parseArgs(final String args[]) {
        if (args.length > 0) {
            Color color = sDefaultColor;
            boolean underline = false;
            boolean blink = false;
            boolean background = false;

            final int lastArgIndex = (args.length - 1);
            for (int i = 0; i < lastArgIndex; ++i) {
                final String arg = args[i];

                if (arg.equals("-i") || arg.equals("--ignore-case")) {
                    sCaseSensitive = false;
                } else if (arg.equals("-l") || arg.equals("--line")) {
                    sHighlightWholeLine = true;
                } else if (arg.equals("-u") || arg.equals("--underline")) {
                    underline = true;
                } else if (arg.equals("-k") || arg.equals("--blink")) {
                    blink = true;
                } else if (arg.equals("-n") || arg.equals("--background")) {
                    background = true;
                } else {
                    final Color mappedColor = COLORS_MAP.get(arg);
                    if (mappedColor != null) {
                        color = mappedColor;
                    } else {
                        System.err.println("Invalid parameter: " + arg);
                        System.err.println();
                        return false;
                    }
                }
            }

            sAttributes = (background ? BACK_COLORS_MAP.get(color) : TEXT_COLORS_MAP.get(color));
            if (underline) {
                sAttributes += UNDERLINE;
            }
            if (blink) {
                sAttributes += BLINK;
            }

            sTargetText = args[lastArgIndex];

            return true;
        } else {
            showHelp();

            return false;
        }
    }

    private static void showHelp() {
        System.out.println("Highlight text passed to stdin.");
        System.out.println();
        System.out.println("Usage: hl [options] text");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("cat file.txt | hl -r text");
        System.out.println("adb logcat | hl -c \"some logging text\"");
        System.out.println();
        System.out.println("Color options:");
        System.out.print("    -a, --black         " + TEXT_BLACK + "Black" + RESET);    printlnDefault(Color.BLACK);
        System.out.print("    -r, --red           " + TEXT_RED + "Red" + RESET);        printlnDefault(Color.RED);
        System.out.print("    -g, --green         " + TEXT_GREEN + "Green" + RESET);    printlnDefault(Color.GREEN);
        System.out.print("    -y, --yellow        " + TEXT_YELLOW + "Yellow" + RESET);  printlnDefault(Color.YELLOW);
        System.out.print("    -b, --blue          " + TEXT_BLUE + "Blue" + RESET);      printlnDefault(Color.BLUE);
        System.out.print("    -p, --purple        " + TEXT_PURPLE + "Purple" + RESET);  printlnDefault(Color.PURPLE);
        System.out.print("    -c, --cyan          " + TEXT_CYAN + "Cyan" + RESET);      printlnDefault(Color.CYAN);
        System.out.print("    -w, --white         " + TEXT_WHITE + "White" + RESET);    printlnDefault(Color.WHITE);
        System.out.println();
        System.out.println("Other options:");
        if (!sAlwaysCaseInsensitive) {
            System.out.println("    -i, --ignore-case   Perform case insensitive matching");
        }
        System.out.println("    -l, --line          Highlight whole line");
        System.out.println("    -u, --underline     Underline matching text");
        System.out.println("    -k, --blink         Blink matching text");
        System.out.println("    -n, --background    Highlight background of matching text instead");
        System.out.println();
    }

    private static void printlnDefault(final Color color) {
        System.out.println(sDefaultColor == color ? " (default)" : "");
    }

    private static void printlnWithHighlight(String line) {
        while (line.length() > 0) {
            int index = indexOf(line, sTargetText);
            if (index >= 0) {
                if (sHighlightWholeLine) {
                    System.out.print(sAttributes + line + RESET);
                    break;
                }

                if (index != 0) {
                    System.out.print(line.substring(0, index));
                }

                int endIndex = (index + sTargetText.length());
                System.out.print(sAttributes + line.substring(index, endIndex) + RESET);

                line = line.substring(endIndex);
            } else {
                System.out.print(line);
                break;
            }
        }

        System.out.println();
    }

    private static int indexOf(final String line, final String text) {
        if (sCaseSensitive) {
            return line.indexOf(sTargetText);
        } else {
            return line.toUpperCase().indexOf(text.toUpperCase());
        }
    }
}
