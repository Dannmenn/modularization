package pl.mendroch.modularization.application.internal.util;

import lombok.extern.java.Log;

import java.util.ServiceLoader;

import static java.util.logging.Level.FINE;
import static java.util.logging.Level.FINER;

@Log
public final class ReflectionUtil {
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
            log.info("Failed to found class: " + name);
            log.log(FINE, e.getMessage(), e);
            return null;
        }
        try {
            return instanceClass.getConstructor(parameter).newInstance(parameters);
        } catch (Exception e) {
            log.info("Failed to found " + name + " constructor with parameter " + parameter);
            log.log(FINER, e.getMessage(), e);
        }
        try {
            return instanceClass.getConstructor().newInstance();
        } catch (Exception e) {
            log.info("Failed to found " + name + " constructor without parameters");
            log.log(FINER, e.getMessage(), e);
        }
        return null;
    }
}
