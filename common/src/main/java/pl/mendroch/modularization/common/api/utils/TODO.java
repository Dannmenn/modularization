package pl.mendroch.modularization.common.api.utils;

@pl.mendroch.modularization.common.api.annotation.TODO
public class TODO {
    @SuppressWarnings("MethodNameSameAsClassName")
    public static void TODO(String message) {
        throw new UnsupportedOperationException(message);
    }
}
