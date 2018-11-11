package com.github.and11.basex.utils.options;

import com.github.and11.basex.utils.Option;

public class CreateDatabaseProvisionOption extends AbstractProvisionOption<CreateDatabaseProvisionOption> {

    private final String databaseName;

    public CreateDatabaseProvisionOption(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public String getURL() {
        return "db:create:" + getDatabaseName();
    }
}
