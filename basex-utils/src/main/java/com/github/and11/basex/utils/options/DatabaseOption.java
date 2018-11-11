package com.github.and11.basex.utils.options;

import com.github.and11.basex.utils.Option;

import java.util.Optional;

public class DatabaseOption implements Option {

    private OpenMode mode;

    public enum OpenMode {
        CREATE, OPEN
    }

    public DatabaseOption(String databaseName) {
        this.databaseName = databaseName;
    }

    private String databaseName;

    public DatabaseOption create(){
        this.mode = OpenMode.CREATE;
        return this;
    }

    public DatabaseOption open(){
        this.mode = OpenMode.OPEN;
        return this;
    }

    public Optional<OpenMode> getMode() {
        return Optional.ofNullable(mode);
    }

    public String getDatabaseName() {
        return databaseName;
    }
}
