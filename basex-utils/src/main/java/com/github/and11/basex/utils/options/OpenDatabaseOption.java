package com.github.and11.basex.utils.options;

public class OpenDatabaseOption implements InitializationOption<OpenDatabaseOption>{

    private final String databaseName;
    private String collection;

    public OpenDatabaseOption(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getCollection() {
        return collection;
    }

    public OpenDatabaseOption collection(String collectionName) {
        this.collection = collectionName;
        return this;
    }

    @Override
    public String toString() {
        return "OpenDatabaseOption{" +
                "databaseName='" + databaseName + '\'' +
                ", collection='" + collection + '\'' +
                '}';
    }
}
