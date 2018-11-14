package com.github.and11.mojos;

import com.github.and11.Function;
import com.github.and11.basex.utils.BaseXContainer;
import com.github.and11.basex.utils.container.DefaultBaseXContainersFactory;
import com.github.and11.basex.utils.options.FunctionUrlReference;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.github.and11.basex.utils.CoreOptions.document;
import static com.github.and11.basex.utils.CoreOptions.function;
import static com.github.and11.basex.utils.CoreOptions.openDatabase;
import static com.github.and11.basex.utils.CoreOptions.url;
import static com.github.and11.basex.utils.CoreOptions.workingDirectory;

@Mojo(name = "exec", requiresProject = true, threadSafe = true, defaultPhase = LifecyclePhase.PACKAGE)
public class ExecMojo extends AbstractBaseXMojo {

    @Parameter(readonly = true, required = true)
    List<Function> functions;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        try (BaseXContainer container = getBaseXContainersFactory()
                .createContainer(
                        workingDirectory(getDatabaseDir().toPath()),
                        openDatabase(getDatabaseName()))) {

            for (Function function : functions) {

                FunctionUrlReference f = function(url(function.getDescriptor()));

                if (function.getArguments().isPresent()) {
                    f.arguments(function.getArguments().get().toArray(new String[]{}));
                }

                Path outputFile = function.getOutput().toPath();
                try (OutputStream output = Files.newOutputStream(outputFile)) {
                    container.export(f, output);

                    if (function.getRecipientCollectionName().isPresent()) {
                        container.provision(
                                document(url(outputFile.toUri().toString())).collection(function.getRecipientCollectionName().get()));
                    }
                }
            }

        } catch (Exception e) {
            throw new MojoExecutionException("provisioning failed", e);
        }
    }

}
