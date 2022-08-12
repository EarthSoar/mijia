package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(Log.class);
        logger.error("Hello World");
        System.out.println(logger.getClass());
    }
}
