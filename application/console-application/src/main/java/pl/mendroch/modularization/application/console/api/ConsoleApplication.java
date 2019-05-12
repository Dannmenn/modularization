package pl.mendroch.modularization.application.console.api;

import pl.mendroch.modularization.application.api.ApplicationLoader;
import pl.mendroch.modularization.application.internal.ApplicationArgumentName;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import static pl.mendroch.modularization.application.api.ApplicationConstants.APPLICATION_LOADER_CLASS;
import static pl.mendroch.modularization.application.internal.ApplicationArgumentName.LOADER;
import static pl.mendroch.modularization.application.internal.ApplicationArgumentName.PATH;

public class ConsoleApplication {
    private static final Logger LOGGER = Logger.getLogger(ConsoleApplication.class.getName());

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        LOGGER.info("Console Application: main(" + Arrays.toString(args) + ")");
        Map<ApplicationArgumentName, String> parameters = prepareParameters(args);

        LOGGER.info("Application parameters: " + parameters.toString());
        ApplicationLoader application = new ApplicationLoader(parameters);

        LOGGER.info("Loading custom configuration");
        application.loadCustomConfiguration();

        LOGGER.info("Loading application");
        application.load();

        LOGGER.info("Starting application");
        application.run();
        LOGGER.info("Exiting application");
    }

    private static Map<ApplicationArgumentName, String> prepareParameters(String[] args) {
        validateArgs(args);
        Map<ApplicationArgumentName, String> parameters = parseArguments(args);
        overrideApplicationLoaderWithSystemProperty(parameters);
        return parameters;
    }

    private static void overrideApplicationLoaderWithSystemProperty(Map<ApplicationArgumentName, String> arguments) {
        if (System.getProperty(APPLICATION_LOADER_CLASS) != null)
            arguments.put(LOADER, System.getProperty(APPLICATION_LOADER_CLASS).trim());
    }

    private static Map<ApplicationArgumentName, String> parseArguments(String[] args) {
        var arguments = new EnumMap<ApplicationArgumentName, String>(ApplicationArgumentName.class);
        ApplicationArgumentName tmp = parseArgumentsKey(args[0]);
        for (int i = 1; i < args.length; i++) {
            if (tmp == null) {
                tmp = parseArgumentsKey(args[i]);
            } else {
                arguments.put(tmp, args[i]);
            }
        }
        if (tmp == null) {
            arguments.put(PATH, args[args.length - 1]);
        }
        return arguments;
    }

    private static ApplicationArgumentName parseArgumentsKey(String arg) {
        switch (arg.trim()) {
            case "-l":
            case "--loader":
                return LOADER;
            case "-p":
            case "--path":
                return PATH;
            default:
                return null;
        }
    }

    private static void validateArgs(String[] args) {
        if (args == null || args.length == 0)
            throw new IllegalArgumentException();
    }
}