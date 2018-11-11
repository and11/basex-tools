package com.github.and11.basex.utils.options;

public class OpenDatabaseProvisionOption extends AbstractProvisionOption<OpenDatabaseProvisionOption> {

    private final String databaseName;
    private String collection;

    public OpenDatabaseProvisionOption(String databaseName) {
        this.databaseName = databaseName;
    }

    public OpenDatabaseProvisionOption collection(String collectionName) {
        this.collection = collectionName;
        return this;
    }

    @Override
    public String getURL() {
        StringBuilder sb = new StringBuilder();
        sb.append("db:open").append(":").append(databaseName);
        if(collection != null){
            sb.append(":").append(collection);
        }

        return sb.toString();
    }
}
