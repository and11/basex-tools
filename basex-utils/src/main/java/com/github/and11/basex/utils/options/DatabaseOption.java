package com.github.and11.basex.utils.options;

public class DatabaseOption
        implements InitializationOption<DatabaseOption> {

    private final UrlReference database;

    public DatabaseOption(UrlReference url) {
        this.database = url;
    }

    public UrlReference getDatabase() {
        return database;
    }

    @Override
    public String toString() {
        return "DatabaseOption{" +
                "database=" + database +
                '}';
    }
}
