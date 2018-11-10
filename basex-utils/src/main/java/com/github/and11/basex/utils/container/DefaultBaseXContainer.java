package com.github.and11.basex.utils.container;

import com.github.and11.basex.utils.BaseXContainer;
import com.github.and11.basex.utils.CoreOptions;
import com.github.and11.basex.utils.Option;
import com.github.and11.basex.utils.UrlStreamHandler;
import com.github.and11.basex.utils.options.DocumentProvisionOption;
import com.github.and11.basex.utils.options.FunctionUrlReference;
import com.github.and11.basex.utils.options.OptionUtils;
import com.github.and11.basex.utils.options.ProvisionOption;
import com.github.and11.basex.utils.options.RepositoryProvisionOption;
import com.github.and11.basex.utils.options.WorkingDirectoryOption;
import com.github.and11.basex.utils.resolvers.CompositeUrlStreamHandler;
import com.github.and11.basex.utils.resolvers.DefaultUrlStreamHandler;
import com.github.and11.basex.utils.resolvers.XQFunctionUrlStreamHandler;
import org.apache.commons.io.IOUtils;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.StaticOptions;
import org.basex.core.cmd.Add;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.RepoInstall;
import org.basex.core.cmd.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

public class DefaultBaseXContainer implements BaseXContainer {

    private final Option[] options;
    private final CompositeUrlStreamHandler urlStreamHandler;
    private CloseableContext context;
    private UrlStreamHandler resolvers;
    private ClassLoader classLoader;

    public DefaultBaseXContainer(UrlStreamHandler urlStreamHandler, Option... options) {
        this.options = options;
        this.urlStreamHandler = new CompositeUrlStreamHandler(urlStreamHandler);
    }

    void start() throws BaseXContainerException {

        WorkingDirectoryOption workingDirectoryOption =
                extractOption(OptionUtils.filter(WorkingDirectoryOption.class, options)).
                        orElse(CoreOptions.workingDirectory(Paths.get("dddd")));

        System.out.println("workgin: " + workingDirectoryOption);

        CloseableContext basexContext = new CloseableContext(createDatabase("main", workingDirectoryOption.getWorkingDirectory()));

        this.context = basexContext;
        urlStreamHandler.addHandler(new XQFunctionUrlStreamHandler(basexContext));

        extractOption(OptionUtils.filter(RepositoryProvisionOption.class, options));
        extractOption(OptionUtils.filter(DocumentProvisionOption.class, options));
    }

    private Optional<WorkingDirectoryOption> extractOption(RepositoryProvisionOption[] filter) {
        for (RepositoryProvisionOption repositoryProvisionOption : filter) {
            System.out.println("repo prov module: " + repositoryProvisionOption);
            try (InputStream is = getUrlStreamHandler(repositoryProvisionOption.getURL()).openStream(repositoryProvisionOption.getURL())) {

                Path temp = Files.createTempFile(UUID.randomUUID().toString(), "." + repositoryProvisionOption.getType().name().toLowerCase());
                System.out.println("tempfile: " + temp);
                Files.copy(is, temp, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("installing");
                RepoInstall cmd = new RepoInstall(temp.toString(), null);
                context.execute(cmd);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (UrlStreamHandler.UnresolvableUrlException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private UrlStreamHandler getUrlStreamHandler(String url) {
        return urlStreamHandler.canHandle(url) ? urlStreamHandler
                : new DefaultUrlStreamHandler();
    }

//    private Optional<WorkingDirectoryOption> extractOption(ModuleProvisionOption[] filter) {
//
//        for (ModuleProvisionOption repositoryProvisionOption : filter) {
//            System.out.println("repo prov module: " + repositoryProvisionOption);
//            try (InputStream is = getUrlStreamHandler(repositoryProvisionOption.getURL()).openStream(repositoryProvisionOption.getURL())) {
//                Path temp = Files.createTempFile(UUID.randomUUID().toString(), ".xqm");
//                System.out.println("tempfile: " + temp);
//                Files.copy(is, temp, StandardCopyOption.REPLACE_EXISTING);
//                System.out.println("installing");
//                RepoInstall cmd = new RepoInstall(temp.toString(), null);
//                context.execute(cmd);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            } catch (UrlStreamHandler.UnresolvableUrlException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        return null;
//    }
//
//    private Optional<WorkingDirectoryOption> extractOption(XarProvisionOption[] filter) {
//
//        for (XarProvisionOption repositoryProvisionOption : filter) {
//            System.out.println("repo prov: " + repositoryProvisionOption);
//            try (InputStream is = getUrlStreamHandler(repositoryProvisionOption.getURL()).openStream(repositoryProvisionOption.getURL())) {
//                Path temp = Files.createTempFile(UUID.randomUUID().toString(), ".xar");
//                System.out.println("tempfile: " + temp);
//                Files.copy(is, temp, StandardCopyOption.REPLACE_EXISTING);
//                System.out.println("installing");
//                RepoInstall cmd = new RepoInstall(temp.toString(), null);
//                context.execute(cmd);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            } catch (UrlStreamHandler.UnresolvableUrlException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        return null;
//    }

    private Optional<DocumentProvisionOption> extractOption(DocumentProvisionOption[] documentProvisionOptions) throws BaseXContainerException {
        for (DocumentProvisionOption documentProvisionOption : documentProvisionOptions) {
            System.out.println("DocumentProvisionOption: " + documentProvisionOption);
            System.out.println("ee: " + documentProvisionOption.getURL());

            loadDocument(documentProvisionOption);


        }

        return null;
    }

    private void loadDocument(DocumentProvisionOption documentProvisionOption) throws BaseXContainerException {
        String collection = documentProvisionOption.getCollection();
        if (collection == null) {
            collection = "/collection";
        }
        Add add = new Add(collection);
        try (InputStream is = getUrlStreamHandler(documentProvisionOption.getURL()).openStream(documentProvisionOption.getURL())) {
            add.setInput(is);
            context.execute(add);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (UrlStreamHandler.UnresolvableUrlException e) {

        }
    }

    private Optional<WorkingDirectoryOption> extractOption(WorkingDirectoryOption[] workingDirectoryOptions) {
        if (workingDirectoryOptions != null && workingDirectoryOptions.length > 0) {
            return Optional.of(workingDirectoryOptions[0]);
        }
        return Optional.empty();
    }

    @Override
    public void provision(ProvisionOption... option) {
        for (ProvisionOption provisionOption : option) {
            String url = provisionOption.getURL();
            System.out.println("provisionong: " + url);
        }
    }

    @Override
    public void export(FunctionUrlReference function, OutputStream os) throws UrlStreamHandler.UnresolvableUrlException, IOException {
        try (InputStream is = getUrlStreamHandler(function.getURL()).openStream(function.getURL())) {
            IOUtils.copy(is, os);
        }
    }

    @Override
    public void test(String url, OutputStream os) {
        try (InputStream is = getUrlStreamHandler(url).openStream(url)) {
            Path temp = Files.createTempFile(UUID.randomUUID().toString(), ".xq");
            Files.copy(is, temp, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("running test  " + url + ", from " + temp);
            context.execute(new Test(temp.toString()), os);
        } catch (BaseXException e) {
            throw new RuntimeException(e);
        } catch (UrlStreamHandler.UnresolvableUrlException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        if (context != null) {
            context.close();
        }
    }

    private Context createDatabase(String dbName, Path path) {

        Context context = new Context();

        context.soptions.set(StaticOptions.DBPATH, path.toFile().getAbsolutePath());
        context.soptions.set(StaticOptions.REPOPATH, new File(path.toFile().getAbsolutePath()).toPath().resolve("repo").toFile().getAbsolutePath());

        try {
            new CreateDB(dbName).execute(context);
            return context;
        } catch (BaseXException e) {
            throw new RuntimeException(e);
        }
    }

}
