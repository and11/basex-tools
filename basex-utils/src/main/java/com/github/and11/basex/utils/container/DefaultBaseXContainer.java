package com.github.and11.basex.utils.container;

import com.github.and11.basex.utils.BaseXContainer;
import com.github.and11.basex.utils.Option;
import com.github.and11.basex.utils.UrlStreamHandler;
import com.github.and11.basex.utils.ZipUtils;
import com.github.and11.basex.utils.options.CreateDatabaseOption;
import com.github.and11.basex.utils.options.DatabaseOption;
import com.github.and11.basex.utils.options.FunctionUrlReference;
import com.github.and11.basex.utils.options.InitializationOption;
import com.github.and11.basex.utils.options.OpenDatabaseOption;
import com.github.and11.basex.utils.options.ProvisionOption;
import com.github.and11.basex.utils.options.UrlProvisionOption;
import com.github.and11.basex.utils.options.UrlReference;
import com.github.and11.basex.utils.options.WorkingDirectoryOption;
import com.github.and11.basex.utils.streamhandlers.CompositeUrlStreamHandler;
import com.github.and11.basex.utils.streamhandlers.DefaultUrlStreamHandler;
import com.github.and11.basex.utils.streamhandlers.XQFunctionUrlStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.StaticOptions;
import org.basex.core.cmd.Add;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.Open;
import org.basex.core.cmd.RepoInstall;
import org.basex.core.cmd.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.and11.basex.utils.CoreOptions.workingDirectory;
import static com.github.and11.basex.utils.OptionUtils.filter;

public class DefaultBaseXContainer implements BaseXContainer {

    private final InitializationOption[] options;
    private final CompositeUrlStreamHandler urlStreamHandler;
    private final AtomicReference<CloseableContext> context = new AtomicReference<>(null);
    private UrlStreamHandler resolvers;
    private ClassLoader classLoader;
    private String defaultCollection = UUID.randomUUID().toString();
    private File workingDirectory;
    private ArrayList<Path> tempdirs = new ArrayList<>();

    DefaultBaseXContainer(UrlStreamHandler urlStreamHandler, InitializationOption<?>... options) {
        this.options = options;
        this.urlStreamHandler = new CompositeUrlStreamHandler(urlStreamHandler);
    }

    private boolean isTemporary(Path path){
        return tempdirs.contains(path);
    }

    private void dropTempDirs(){
        for (Path tempdir : tempdirs) {
            try {
                FileUtils.deleteDirectory(tempdir.toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Path createTemporaryDirectory() throws IOException {
        Path tempdir = Files.createTempDirectory("basex.");
        tempdirs.add(tempdir);
        return tempdir;
    }

    private void initializeContext() throws IOException {
        WorkingDirectoryOption workingDirectoryOption = extractOption(filter(WorkingDirectoryOption.class, options)).
                orElse(workingDirectory(createTemporaryDirectory()));
        System.out.println("working directory is " + workingDirectoryOption.getWorkingDirectory());
        context.getAndSet(new CloseableContext(createContext(workingDirectoryOption.getWorkingDirectory())));
        this.workingDirectory = workingDirectoryOption.getWorkingDirectory().toFile();
        urlStreamHandler.addHandler(new XQFunctionUrlStreamHandler(context.get()));
    }

    void start() throws BaseXContainerException {
        try {
            initializeContext();
            initialize(filter(CreateDatabaseOption.class, options));
            initialize(filter(DatabaseOption.class, options));
            initialize(filter(OpenDatabaseOption.class, options));
        } catch (IOException e) {
            throw new BaseXContainerException(e);
        }
    }

    private void initialize(DatabaseOption... options) throws BaseXContainerException {
        if (options == null || options.length == 0) {
            return;
        }

        if (options.length > 1) {
            throw new IllegalArgumentException("can't have more than one DatabaseOption");
        }

        if(!isTemporary(workingDirectory.toPath()) && Files.exists(workingDirectory.toPath())){
            throw new BaseXContainerException("directory " + workingDirectory + " already exists");
        }

        DatabaseOption option = options[0];

        try (InputStream is = getUrlStreamHandler(option.getDatabase().getURL()).openStream(option.getDatabase().getURL());
        ) {
            System.out.println("installing database " + option.getDatabase().getURL() + " into " + workingDirectory.toPath());
            Files.createDirectories(workingDirectory.toPath());
            ZipUtils.unzip(is, workingDirectory);
        } catch (UrlStreamHandler.UnresolvableUrlException e) {
            throw new BaseXContainerException(e);
        } catch (IOException e) {
            throw new BaseXContainerException(e);
        }
    }

    private void initialize(OpenDatabaseOption... options) throws BaseXContainerException {
        if (options == null || options.length == 0) {
            return;
        }

        if (options.length > 1) {
            throw new BaseXContainerException("can't open more than one database");
        }

        OpenDatabaseOption database = options[0];
        this.defaultCollection = database.getCollection();
        try {
            System.out.println("opening database " + database.getDatabaseName());
            context.get().execute(new Open(database.getDatabaseName()));
        } catch (BaseXException e) {
            throw new BaseXContainerException(e);
        }
    }

    private void initialize(CreateDatabaseOption... options) {
        if (options == null || options.length == 0) {
            return;
        }

        for (CreateDatabaseOption option : options) {
            try {
                System.out.println("creating database " + option.getDatabaseName());
                context.get().execute(new CreateDB(option.getDatabaseName()));
                System.out.println("opening database " + option.getDatabaseName());
                context.get().execute(new Open(option.getDatabaseName()));

            } catch (BaseXException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<String> parseControlBlocks(String url) {
        Pattern withType = Pattern.compile("^(.+)@(.+)");
        Matcher matcher = withType.matcher(url);
        if (matcher.matches()) {
            return Arrays.asList(matcher.group(1), matcher.group(2));
        }

        return Arrays.asList(url);
    }

    Function<String, Boolean> handleDocumentProvisionUrl(Function<String, Boolean> handler) {
        return (url) -> {
            if (!url.startsWith("doc:")) {
                return handler.apply(url);
            }

            String u = url.replaceFirst("doc:", "");
            List<String> blocks = parseControlBlocks(u);

            loadDocument(blocks.get(0), blocks.size() > 1 ? blocks.get(1) : defaultCollection);

            return true;
        };
    }

    Function<String, Boolean> handleRepositoryProvisionUrl(Function<String, Boolean> handler) {
        return (url) -> {
            if (!url.startsWith("repo:")) {
                return handler.apply(url);
            }

            String u = url.replaceFirst("repo:", "");
            List<String> blocks = parseControlBlocks(u);
            installRepository(blocks.get(0), blocks.size() > 1 ? blocks.get(1) : "xqm");

            return true;
        };
    }

    private UrlStreamHandler getUrlStreamHandler(String url) {
        return urlStreamHandler.canHandle(url) ? urlStreamHandler
                : new DefaultUrlStreamHandler();
    }

    private void loadDocument(String url, final String collection) {
        Add add = new Add(collection);
        try (InputStream is = getUrlStreamHandler(url).openStream(url)) {
            add.setInput(is);
            context.get().execute(add);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (UrlStreamHandler.UnresolvableUrlException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<WorkingDirectoryOption> extractOption(WorkingDirectoryOption[] workingDirectoryOptions) {
        if (workingDirectoryOptions != null && workingDirectoryOptions.length > 0) {
            return Optional.of(workingDirectoryOptions[0]);
        }
        return Optional.empty();
    }

    private void installRepository(String url, String type) {
        Path temp = null;
        try (InputStream is = getUrlStreamHandler(url).openStream(url)) {
            temp = Files.createTempFile(UUID.randomUUID().toString(), "." + type);
            Files.copy(is, temp, StandardCopyOption.REPLACE_EXISTING);
            RepoInstall cmd = new RepoInstall(temp.toString(), null);
            context.get().execute(cmd);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (UrlStreamHandler.UnresolvableUrlException e) {
            throw new RuntimeException(e);
        }
        finally {
            if(temp != null){
                try {
                    Files.deleteIfExists(temp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public File getWorkingDirectory() {
        return workingDirectory;
    }

    @Override
    public void provision(Option... urls) throws BaseXContainerException {

        Function<String, Boolean> handler = handleDocumentProvisionUrl(
                handleRepositoryProvisionUrl((__) -> false));

        ProvisionOption[] urlProvisionOptions = filter(ProvisionOption.class, urls);
        System.out.println("provisioning options: " + Arrays.asList(urlProvisionOptions));
        for (ProvisionOption url : urlProvisionOptions) {
            System.out.println("provisioning: " + url);
            Boolean result = handler.apply(url.getURL());
            if (!result) {
                System.out.println("provisioning failed");
                throw new BaseXContainerException("can't handle url");
            }
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
        Path temp = null;
        try (InputStream is = getUrlStreamHandler(url).openStream(url)) {
            temp = Files.createTempFile(UUID.randomUUID().toString(), ".xq");
            Files.copy(is, temp, StandardCopyOption.REPLACE_EXISTING);
            context.get().execute(new Test(temp.toString()), os);
        } catch (BaseXException e) {
            throw new RuntimeException(e);
        } catch (UrlStreamHandler.UnresolvableUrlException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(temp != null){
            try {
                Files.deleteIfExists(temp);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void close() throws Exception {
        if (context != null) {
            context.get().close();
        }
        dropTempDirs();
    }

    private static Context createContext(Path path) {
        Context context = new Context();

        context.soptions.set(StaticOptions.DBPATH, path.toFile().getAbsolutePath());
        context.soptions.set(StaticOptions.REPOPATH, new File(path.toFile().getAbsolutePath()).toPath().resolve("repo").toFile().getAbsolutePath());
        return context;
    }
}
