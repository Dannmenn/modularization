package pl.mendroch.modularization.core;

public class CoreModule {
    public static void main(String[] args) {
        Module module = CoreModule.class.getModule();
        ModuleLayer layer = module.getLayer();
        ClassLoader classLoader = CoreModule.class.getClassLoader();
//        classLoader.getResource()
    }
}
