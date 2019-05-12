package pl.mendroch.modularization.application.internal.util;

import java.util.ServiceLoader;
import java.util.logging.Logger;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINER;

public final class ReflectionUtil {
    private static final Logger LOGGER = Logger.getLogger(ReflectionUtil.class.getName());

    private ReflectionUtil() {
        //Hide implicit constructor
    }

    @SuppressWarnings("unchecked")
    public static <T> T createInstanceWithOptionalParameters(Class<T> aClass, String name, Class parameter, Object parameters) {
        if (name == null) {
            return ServiceLoader.load(aClass).findFirst().orElse(null);
        }
        Class<T> instanceClass;
        try {
            instanceClass = (Class<T>) Class.forName(name);
        } catch (Exception e) {
            LOGGER.info("Failed to found class: " + name);
            LOGGER.log(FINE, e.getMessage(), e);
            return null;
        }
        try {
            return instanceClass.getConstructor(parameter).newInstance(parameters);
        } catch (Exception e) {
            LOGGER.info("Failed to found " + name + " constructor with parameter " + parameter);
            LOGGER.log(FINER, e.getMessage(), e);
        }
        try {
            return instanceClass.getConstructor().newInstance();
        } catch (Exception e) {
            LOGGER.info("Failed to found " + name + " constructor without parameters");
            LOGGER.log(FINER, e.getMessage(), e);
        }
        return null;
    }
}
