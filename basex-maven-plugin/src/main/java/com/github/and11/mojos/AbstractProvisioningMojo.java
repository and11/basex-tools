package com.github.and11.mojos;

import com.github.and11.ProvisionOptions;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class AbstractProvisioningMojo extends AbstractMojo {

    @Parameter
    private ProvisionOptions provision;



}
