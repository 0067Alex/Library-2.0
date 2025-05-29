package es.ing.tomillo.library.config;

public class LibraryConfig {
    private static boolean useDatabase = false;

    public static boolean isUseDatabase() {
        return useDatabase;
    }

    public static void setUseDatabase(boolean useDatabase) {
        LibraryConfig.useDatabase = useDatabase;
    }
} 