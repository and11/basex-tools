package com.github.and11.basex.utils.options;

import static com.github.and11.basex.utils.options.RepositoryProvisionOption.RepositoryType.JAR;
import static com.github.and11.basex.utils.options.RepositoryProvisionOption.RepositoryType.XAR;
import static com.github.and11.basex.utils.options.RepositoryProvisionOption.RepositoryType.XQM;

public class RepositoryProvisionOption
        extends AbstractUrlProvisionOption<RepositoryProvisionOption>{

    public enum RepositoryType {
        XAR, JAR, XQM
    }

    public RepositoryType getType() {
        return type;
    }

    private RepositoryType type;

    public RepositoryProvisionOption(UrlReference url) {
        super(url);
        this.type = XQM;
    }

    public RepositoryProvisionOption xar(){
        this.type = XAR;
        return this;
    }

    public RepositoryProvisionOption jar(){
        this.type = JAR;
        return this;
    }
    public RepositoryProvisionOption xqm(){
        this.type = XQM;
        return this;
    }

    public RepositoryProvisionOption type(RepositoryType type){
        this.type = type;
        return this;
    }
    public RepositoryProvisionOption(String url) {
        super(url);
        this.type = XQM;
    }
}
