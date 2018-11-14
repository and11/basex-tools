package com.github.and11.basex.utils.streamhandlers;

import com.github.and11.basex.utils.UrlStreamHandler;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilder;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.ArtifactProperties;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Map;

public class MavenUrlStreamHandler implements UrlStreamHandler {

    public static final String userHome = System.getProperty( "user.home" );
    public static final File userMavenConfigurationHome = new File( userHome, ".m2" );
    public static final String envM2Home = System.getenv("M2_HOME");
    public static final File DEFAULT_USER_SETTINGS_FILE = new File( userMavenConfigurationHome, "settings.xml" );
    public static final File DEFAULT_GLOBAL_SETTINGS_FILE =
            new File( System.getProperty( "maven.home", envM2Home != null ? envM2Home : "" ), "conf/settings.xml" );


    private static Artifact parseUrl(String pUrl) {
        System.out.println("URL::: " + pUrl);
        String[] p = pUrl.split("@");

        String url = p[0];
        String localPath = p.length > 1 ? p[1] : null;

        String[] parts = url.replaceFirst("mvn:", "").split("/");
        String groupId = parts[0];
        String artifactId = parts[1];
        String version = parts[2];
        String extension = parts.length == 4 ? parts[3] : "jar";



        DefaultArtifact artifact = new DefaultArtifact(groupId, artifactId, extension, version);
        if(localPath != null){
            Map<String, String> props = Collections.singletonMap(ArtifactProperties.LOCAL_PATH, localPath);
            return artifact.setProperties(props);
        }

        return artifact;
    }


    @Override
    public InputStream openStream(String url) throws UnresolvableUrlException {

        RepositorySystem repositorySystem = getRepositorySystem();
        RepositorySystemSession repositorySystemSession = getRepositorySystemSession(repositorySystem);
        Artifact artifact = parseUrl(url);
        try {

        if(artifact.getProperties() != null){
            String localPath = artifact.getProperties().get(ArtifactProperties.LOCAL_PATH);
            if(localPath != null){
                return Files.newInputStream(new File(localPath).toPath());
            }
        }

        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
            ArtifactResult artifactResult = repositorySystem
                    .resolveArtifact(repositorySystemSession, artifactRequest);
            artifact = artifactResult.getArtifact();
            return Files.newInputStream(artifactResult.getArtifact().getFile().toPath());

        } catch (ArtifactResolutionException e) {
            throw new UnresolvableUrlException(url, e);
        } catch (IOException e) {
            throw new UnresolvableUrlException(url, e);
        }
    }

    public static RepositorySystem getRepositorySystem() {
        DefaultServiceLocator serviceLocator = MavenRepositorySystemUtils.newServiceLocator();
        serviceLocator
                .addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);

        serviceLocator.addService(TransporterFactory.class, FileTransporterFactory.class);
        serviceLocator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        serviceLocator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
            @Override
            public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
                System.err.printf("error creating service: %s\n", exception.getMessage());
                exception.printStackTrace();
            }
        });

        return serviceLocator.getService(RepositorySystem.class);
    }

    public static DefaultRepositorySystemSession getRepositorySystemSession(RepositorySystem system) {
        DefaultRepositorySystemSession repositorySystemSession = MavenRepositorySystemUtils
                .newSession();

        LocalRepository localRepository = null;//new LocalRepository(TARGET_LOCAL_REPOSITORY);
        try {
            localRepository = getLocalRepository();
        } catch (SettingsBuildingException e) {
            throw new RuntimeException(e);
        }
        repositorySystemSession.setLocalRepositoryManager(
                system.newLocalRepositoryManager(repositorySystemSession, localRepository));

        repositorySystemSession.setRepositoryListener(new ConsoleRepositoryEventListener());

        return repositorySystemSession;
    }

    private static LocalRepository getLocalRepository() throws SettingsBuildingException {

        SettingsBuildingRequest settingsBuildingRequest = new DefaultSettingsBuildingRequest();
        settingsBuildingRequest.setSystemProperties(System.getProperties());
        settingsBuildingRequest.setUserSettingsFile(DEFAULT_USER_SETTINGS_FILE);
        settingsBuildingRequest.setGlobalSettingsFile(DEFAULT_GLOBAL_SETTINGS_FILE);

        String customSettings = System.getProperty( "maven.settings");
        if(customSettings != null){
            System.out.println("using custom maven.settings path: " + customSettings);
            settingsBuildingRequest.setUserSettingsFile(new File(customSettings));
        }

        SettingsBuildingResult settingsBuildingResult;
        DefaultSettingsBuilderFactory mvnSettingBuilderFactory = new DefaultSettingsBuilderFactory();
        DefaultSettingsBuilder settingsBuilder =  mvnSettingBuilderFactory.newInstance();
        settingsBuildingResult = settingsBuilder.build(settingsBuildingRequest);

        Settings effectiveSettings = settingsBuildingResult.getEffectiveSettings();

        return new LocalRepository(effectiveSettings.getLocalRepository());
    }


    private static RemoteRepository getCentralMavenRepository() {
        return new RemoteRepository.Builder("central", "default", "http://central.maven.org/maven2/")
                .build();
    }

    @Override
    public boolean canHandle(String url) {
        return url != null && url.toLowerCase().startsWith("mvn:");
    }

    public static class ConsoleRepositoryEventListener
            extends AbstractRepositoryListener {

        @Override
        public void artifactInstalled(RepositoryEvent event) {
            System.out.printf("artifact %s installed to file %s\n", event.getArtifact(), event.getFile());
        }

        @Override
        public void artifactInstalling(RepositoryEvent event) {
            System.out.printf("installing artifact %s to file %s\n", event.getArtifact(), event.getFile());
        }

        @Override
        public void artifactResolved(RepositoryEvent event) {
            System.out.printf("artifact %s resolved from repository %s\n", event.getArtifact(),
                    event.getRepository());
        }

        @Override
        public void artifactDownloading(RepositoryEvent event) {
            System.out.printf("downloading artifact %s from repository %s\n", event.getArtifact(),
                    event.getRepository());
        }

        @Override
        public void artifactDownloaded(RepositoryEvent event) {
            System.out.printf("downloaded artifact %s from repository %s\n", event.getArtifact(),
                    event.getRepository());
        }

        @Override
        public void artifactResolving(RepositoryEvent event) {
            System.out.printf("resolving artifact %s\n", event.getArtifact());
        }

    }
}
