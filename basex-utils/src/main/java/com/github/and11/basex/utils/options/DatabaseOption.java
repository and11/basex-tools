package com.github.and11.basex.utils.options;

import com.github.and11.basex.utils.Option;

public class DatabaseOption implements Option {

    public enum OpenMode {
        CREATE, OPEN
    }

    private String databaseName;

    public DatabaseOption databaseName(String name){
        this.databaseName = name;
        return this;
    }

}
