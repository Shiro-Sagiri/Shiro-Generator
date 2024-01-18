package com.shiro.cli.example;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
// some exports omitted for the sake of brevity

//@Command(name = "ASCIIArt", version = "ASCIIArt 1.0", mixinStandardHelpOptions = true)
@Command(name = "commit",
        sortOptions = false,
        headerHeading = "@|bold,underline Usage|@:%n%n",
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description|@:%n%n",
        parameterListHeading = "%n@|bold,underline Parameters|@:%n",
        optionListHeading = "%n@|bold,underline Options|@:%n",
        header = "Record changes to the repository.",
        description = "Stores the current contents of the index in a new commit " +
                "along with a log message from the user describing the changes.", mixinStandardHelpOptions = true)
public class ASCIIArt implements Runnable {

    @Option(names = {"-s", "--font-size"}, description = "Font size")
    int fontSize = 19;

    @Parameters(paramLabel = "<word>", defaultValue = "Hello, picocli",
            description = "Words to be translated into ASCII art.")
    private String[] words = {"Hello,", "picocli"};

    @Override
    public void run() {
        System.out.println("Font size: " + fontSize);
        System.out.println("Words: " + String.join(" ", words));
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ASCIIArt()).execute("--help");
        System.exit(exitCode);
    }
}