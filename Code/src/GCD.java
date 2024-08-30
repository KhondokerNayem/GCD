import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

class JavaClassAnalyzer {
    private String javaCode;
    private HashMap<String, Integer> methodInvocations;

    public JavaClassAnalyzer(String javaCode) {
        this.javaCode = javaCode;
        this.methodInvocations = new HashMap<>();
    }

    public void extractMethodInvocations() {
        Pattern methodPattern = Pattern
                .compile("\\b(?:public|private|protected|static|\\s)+[\\w<>\\[\\]]+\\s+(\\w+)\\s*\\([^\\)]*\\)\\s\\{");
        Matcher methodMatcher = methodPattern.matcher(javaCode);

        while (methodMatcher.find()) {
            String methodName = methodMatcher.group(1);
            methodInvocations.put(methodName, 0);
        }

        for (String methodName : methodInvocations.keySet()) {
            String regex = "\\b" + methodName + "\\s*\\([^\\)]\\)\\s\\{([^}])\\}";
            Pattern callsPattern = Pattern.compile(regex, Pattern.DOTALL);
            Matcher callsMatcher = callsPattern.matcher(javaCode);

            if (callsMatcher.find()) {
                String methodBody = callsMatcher.group(1);
                Pattern callPattern = Pattern.compile("\\b(\\w+)\\s*\\(");
                Matcher callMatcher = callPattern.matcher(methodBody);

                while (callMatcher.find()) {
                    String call = callMatcher.group(1);
                    if (methodInvocations.containsKey(call)) {
                        methodInvocations.put(methodName, methodInvocations.get(methodName) + 1);
                    }
                }
            }
        }
    }

    public double calculateTcc() {
        int actualMethodCount = methodInvocations.size();
        if (methodInvocations.containsKey("main")) {
            actualMethodCount--;
        }

        int n = actualMethodCount;
        int np = n * (n - 1) / 2;

        int ndc = 0;
        for (String methodName : methodInvocations.keySet()) {
            if (!methodName.equals("main")) {
                int methodCalls = methodInvocations.get(methodName);
                ndc += methodCalls;
            }
        }

        return np != 0 ? (double) ndc / np : 0;
    }
}

public class GCD {

	public static void main(String[] args) {
	    String projectPath = promptForProjectFolder();
	    if (projectPath == null) {
	        System.out.println("Project folder selection canceled. Exiting.");
	        return;
	    }

	    Map<String, List<String>> packageJavaFiles = findJavaFilesByPackage(projectPath);

	    String csvFileName = "E:\\Thesis Result\\Loc 500\\junit4.csv";

	    try (PrintWriter writer = new PrintWriter(new FileWriter(csvFileName))) {
	        writer.println("Project Name,Package Name,Class Name,ATFD,WMC,TCC,LOC,Avgloc,God Class (Case 5)");

	        int totalLoc = 0;
	        int totalClasses = 0;

	        for (Map.Entry<String, List<String>> entry : packageJavaFiles.entrySet()) {
	            String projectName = Paths.get(projectPath).getFileName().toString();
	            String packageName = entry.getKey();
	            List<String> javaFiles = entry.getValue();

	            for (String filePath : javaFiles) {
	                int loc = calculateLOC(filePath);
	                totalLoc += loc;
	                totalClasses++;

	                int atfd = calculateATFD(filePath, javaFiles);
	                int wmc = calculateWMC(filePath);
	                double tcc = calculateTCC(filePath);
	                int Avgloc = (totalLoc / totalClasses);
	                boolean isGodClass1 = isGodClass1(atfd, wmc, tcc, loc);
	               // boolean isGodClass2 = isGodClass2(atfd, wmc, tcc, loc, Avgloc);

	                String className = Paths.get(filePath).getFileName().toString().replace(".java", "");
	                writer.println(String.join(",", projectName, packageName, className, String.valueOf(atfd),
	                        String.valueOf(wmc), String.valueOf(tcc), String.valueOf(loc),
	                        String.valueOf(Avgloc),
	                        isGodClass1 ? "Yes" : "No"));
	            }
	        }

	        System.out.println("Data has been written to " + csvFileName);
	        System.out.println("Total LOC " + totalLoc);
	        System.out.println("Total Class " + totalClasses);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

    private static String promptForProjectFolder() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter the path of your Java project folder:");
            return scanner.nextLine().trim();
        }
    }

    private static Map<String, List<String>> findJavaFilesByPackage(String projectPath) {
        Map<String, List<String>> packageJavaFiles = new HashMap<>();
        try {
            Files.walk(Paths.get(projectPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .forEach(path -> {
                        String packageName = getPackageName(path.toString());
                        packageJavaFiles.computeIfAbsent(packageName, k -> new ArrayList<>()).add(path.toString());
                    });
        } catch (IOException e) {
            System.err.println("Error while traversing files: " + e.getMessage());
        }
        return packageJavaFiles;
    }

    private static String getPackageName(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith("package ")) {
                    return line.trim().substring(8, line.trim().length() - 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static int calculateATFD(String filePath, List<String> allFiles) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            int atfd = 0;

            for (String otherFile : allFiles) {
                if (!filePath.equals(otherFile) && isAccessingForeignClass(content, getClassName(otherFile))) {
                    atfd++;
                }
            }

            return atfd;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static boolean isAccessingForeignClass(String content, String otherClassName) {
        return content.contains(otherClassName);
    }

    private static String getClassName(String filePath) {
        String fileName = Paths.get(filePath).getFileName().toString();
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    public static int calculateWMC(String filePath) {
        int complexity = 0;
        boolean inMultilineComment = false;
        boolean inSwitch = false;
        boolean afterDo = false;
        int switchCases = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!inMultilineComment) {
                    line = removeInlineComments(line);
                    inMultilineComment = startOfMultilineComment(line);
                } else {
                    inMultilineComment = !endOfMultilineComment(line);
                    continue;
                }

                if (line.isEmpty())
                    continue;

                if (containsSwitch(line)) {
                    complexity++;
                    inSwitch = true;
                } else if (inSwitch && containsCase(line)) {
                    switchCases++;
                } else if (inSwitch && containsDefault(line)) {
                    switchCases++;
                } else if (inSwitch && line.startsWith("}")) {
                    inSwitch = false;
                    complexity += switchCases;
                    switchCases = 0;
                } else if (afterDo && containsLoop(line)) {
                    afterDo = false;
                } else if (containsLoop(line)) {
                    complexity += 1;
                    if (line.startsWith("do")) {
                        afterDo = true;
                    }
                } else if (containsConditional(line)) {
                    complexity++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return complexity;
    }

    private static String removeInlineComments(String line) {
        return line.replaceAll("//.*", "");
    }

    private static boolean startOfMultilineComment(String line) {
        return line.startsWith("/*");
    }

    private static boolean endOfMultilineComment(String line) {
        return line.endsWith("*/");
    }

    private static boolean containsLoop(String line) {
        Pattern loopPattern = Pattern.compile("\\b(while|do|for)\\b");
        Matcher matcher = loopPattern.matcher(line);
        return matcher.find();
    }

    private static boolean containsConditional(String line) {
        return line.contains("if") || line.contains("else");
    }

    private static boolean containsSwitch(String line) {
        return line.contains("switch");
    }

    private static boolean containsCase(String line) {
        return line.contains("case");
    }

    private static boolean containsDefault(String line) {
        return line.contains("default");
    }

    public static double calculateTCC(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder javaCodeBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                javaCodeBuilder.append(line).append("\n");
            }
            String javaCode = javaCodeBuilder.toString();
            JavaClassAnalyzer analyzer = new JavaClassAnalyzer(javaCode);
            analyzer.extractMethodInvocations();
            return analyzer.calculateTcc();
        } catch (IOException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public static int calculateLOC(String filePath) {
        int loc = 0;
        boolean[] withinMultiLineComment = { false };
        try {

            long lineCount = Files.lines(Paths.get(filePath))
                    .filter(line -> {

                        if (withinMultiLineComment[0]) {

                            if (line.trim().endsWith("*/")) {
                                withinMultiLineComment[0] = false;
                            }
                            return false;
                        }

                        if (line.trim().startsWith("/*")) {

                            withinMultiLineComment[0] = true;
                            return false;
                        }

                        return !line.trim().isEmpty() &&
                                !line.trim().startsWith("//") &&
                                !line.trim().startsWith("import ") &&
                                !line.trim().startsWith("package ");
                    })
                    .count();
            loc = (int) lineCount;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loc;
    }

    private static boolean isGodClass1(int atfd, int wmc, double tcc, int loc) {
        return ((atfd > 5 && wmc >= 47 && tcc < 0.33) && loc > 500);
    }
    
    
    /*private static boolean isGodClass2(int atfd, int wmc, double tcc, int loc, int Avgloc) {
        return ((atfd > 5 && wmc >= 47 && tcc < 0.33) && loc > Avgloc);
    }*/
    
    /* private static boolean isGodClass2(int atfd, int wmc, double tcc, int loc, int Avgloc) {
        return ((atfd > 2 && wmc >= 47 && tcc < 0.33) && loc > Avgloc);
    }*/
    
    /*private static boolean isGodClass2(int atfd, int wmc, double tcc, int loc, int Avgloc) {
        return ((atfd > 3 && wmc >= 47 && tcc < 0.33) && loc > Avgloc);
    }*/
    
     /*private static boolean isGodClass2(int atfd, int wmc, double tcc, int loc, int Avgloc) {
        return ((atfd > 4 && wmc >= 47 && tcc < 0.33) && loc > Avgloc);
    }*/
}