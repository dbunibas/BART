package bart.utility;

import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class JavaUtilLoggingConfig {

    private final LogManager logManager;
    private final Logger rootLogger;
    private final Handler defaultHandler = new ConsoleHandler();
    private final Formatter defaultFormatter = new SimpleFormatter();

    public JavaUtilLoggingConfig() {
        super();
        this.logManager = LogManager.getLogManager();
        this.rootLogger = Logger.getLogger("");
        configure();
    }

    final void configure() {
        defaultHandler.setFormatter(defaultFormatter);
        defaultHandler.setLevel(Level.ALL);
        rootLogger.setLevel(Level.SEVERE);
        rootLogger.addHandler(defaultHandler);
        logManager.addLogger(rootLogger);
        Logger.getLogger("org.apache.jcs").setLevel(Level.SEVERE);
        Logger.getLogger("com.mchange").setLevel(Level.SEVERE);
        Logger.getLogger("com.sun").setLevel(Level.SEVERE);
        Logger.getLogger("org.postgresql").setLevel(Level.OFF);
    }
}
