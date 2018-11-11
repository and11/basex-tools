package com.github.and11.basex.utils.options;

public class CreateDatabaseOption implements InitializationOption<CreateDatabaseOption> {

    private final String databaseName;

    public CreateDatabaseOption(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

}
