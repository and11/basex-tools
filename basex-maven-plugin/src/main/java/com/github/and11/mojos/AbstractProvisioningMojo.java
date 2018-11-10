package com.github.and11.mojos;

import com.github.and11.DependencySet;
import com.github.and11.FileSet;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.List;

public abstract class AbstractProvisioningMojo extends AbstractMojo {


    /*

    <dependencySet>
        <include></include>
        <exclude></exclude>
    </dependencySet>
    <fileSet>
        <include></include>
        <exclude></exclude>
    </fileSet>

    <provision>
        <module>${target}/some.xqm<module>
        <module>mvn:group/id/version/type/classifier<module>
        <document>classpath:asdasdasd.xml<module>
        <xar>mvn:group/id/version/type/classifier</xar>
        <xar>set:id</xar>
    <function>
    </function>

     */


}
