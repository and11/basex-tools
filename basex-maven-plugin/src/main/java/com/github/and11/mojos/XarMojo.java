package com.github.and11.mojos;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Mojo(name = "xar", requiresProject = true, threadSafe = true, defaultPhase = LifecyclePhase.PACKAGE)
public class XarMojo extends AbstractBaseXMojo {

    @Parameter(required = false, defaultValue = "${project.build.directory}/${project.artifactId}-${project.version}.xar")
    private File artifactFile;

    @Parameter(defaultValue = "${project.artifactId}")
    private String packageAbbrev;

    @Parameter(defaultValue = "${project.groupId}-${project.artifactId}")
    private String packageName;

    @Parameter(defaultValue = "${project.version}")
    private String packageVersion;

    @Component
    private MavenProjectHelper projectHelper;

    @Component
    private MavenProject mavenProject;

    @Component
    private ArchiverManager archiverManager;

    private File getAbbrevDir() {
        return getDatabaseDir().toPath().resolve(packageAbbrev).toFile();
    }


    @Parameter(required = false)
    private List<String> classes;

    @Parameter(required = false, defaultValue = "${project.description}")
    private String packageTitle;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        boolean projectHasJavaClasses = projectHasJavaClasses();

        List<XarResource> resources = identifyResources(getResources());
        if(!projectHasJavaClasses){
            resources = resources.stream().filter(r -> !r.isJar())
                    .collect(Collectors.toList());
        }

        File abbrevDir = getAbbrevDir();

        ensureDirectoriesExist(abbrevDir);


        for (XarResource resource : resources) {
            try {
                Path destPath = abbrevDir.toPath().resolve(resource.getPath().getName());
                getLog().info("copy " + resource + " to " + destPath);
                Files.copy(resource.getPath().toPath(), destPath);
            } catch (IOException e) {
                throw new MojoExecutionException("can't copy " + resource + " into the " + abbrevDir, e);
            }
        }

        if(projectHasJavaClasses()) {
            try {
                createJavaBasexDescriptor(resources, getDatabaseDir().toPath().resolve("basex.xml").toFile());
            } catch (IOException e) {
                throw new MojoExecutionException("can't store basex descriptor", e);
            }
        }

        try {
            createExPathPkgDescriptor(resources, getDatabaseDir().toPath().resolve("expath-pkg.xml").toFile());
        } catch (IOException e) {
            throw new MojoExecutionException("can;t store package", e);
        }

        try {
            createXarArchive(artifactFile);
        } catch (final Exception e) {
            throw new MojoExecutionException("can't create archive", e);
        }

        mavenProject.getArtifact().setFile(artifactFile);
    }
    private boolean projectHasJavaClasses() throws MojoExecutionException {
        try {
            return Files.walk(new File(mavenProject.getBuild().getOutputDirectory()).toPath())
                    .filter(f -> f.toFile().getName().endsWith(".class"))
                    .findAny().isPresent();
        } catch (IOException e) {
            throw new MojoExecutionException("failed looking up for project's java classes", e);
        }
    }

    protected void ensureDirectoriesExist(File... files) throws MojoExecutionException {
        for (File file : files) {
            if (!Files.exists(file.toPath())) {
                try {
                    Files.createDirectories(file.toPath());
                } catch (IOException e) {
                    throw new MojoExecutionException("can't create directory " + file, e);
                }
            }
        }
    }
    private void createXarArchive(File target) throws MojoExecutionException {
        createZipArchive(getDatabaseDir(), target);
    }
    protected void createZipArchive(File sourceDir, File target) throws MojoExecutionException {

        try {
            ZipArchiver archiver = (ZipArchiver) archiverManager.getArchiver("zip");
            archiver.addDirectory(sourceDir);
            archiver.setDestFile(target);
            archiver.setIncludeEmptyDirs(true);
            archiver.createArchive();
        } catch (final Exception e) {
            throw new MojoExecutionException("can't create zip archive " + target, e);
        }
    }
    public enum XarResourceType {
        XQUERY, JAR
    }

    public static class XarResource {
        private File path;
        private XarResourceType type;

        public boolean isJar() {
            return XarResourceType.JAR.equals(type);
        }

        public boolean isModule() {
            return XarResourceType.XQUERY.equals(type);
        }

        public XarResource(File path, XarResourceType type) {
            this.path = path;
            this.type = type;
        }

        public File getPath() {
            return path;
        }

        public XarResourceType getType() {
            return type;
        }

        @Override
        public String toString() {
            return "XarResource{" +
                    "path=" + path +
                    ", type=" + type +
                    '}';
        }
    }


    private void createExPathPkgDescriptor(List<XarResource> resources, File file) throws MojoExecutionException, IOException {
        List<XarResource> xqModules = resources.stream().filter(XarResource::isModule).collect(Collectors.toList());
        if (xqModules.isEmpty()) {
            throw new MojoExecutionException("can't find any module");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            String namespace;
            writer.append("<package xmlns=\"http://expath.org/ns/pkg\" name=\"");
            writer.append(packageName);
            writer.append("\"");
            writer.newLine();
            writer.append("\tabbrev=\"");
            writer.append(packageAbbrev);
            writer.append("\"");
            writer.newLine();
            writer.append("\tspec=\"1.0\"");
            writer.newLine();
            writer.append("\tversion=\"");
            writer.append(packageVersion);
            writer.append("\"");
            writer.append(">");
            writer.newLine();
            writer.newLine();

            writer.append("<title>");
            writer.append("\t");
            writer.append(packageTitle);
            writer.append("</title>");
            writer.newLine();
            writer.newLine();


            writeXqueryModules(xqModules, writer);

            writer.append("</package>");
            writer.newLine();
        }
    }

    private void writeXqueryModules(List<XarResource> resources, BufferedWriter writer) throws IOException, MojoExecutionException {
        if (resources.isEmpty()) {
            return;
        }

        for (XarResource resource : resources) {
            writer.append("<xquery>");
            writer.newLine();
            writer.append("\t<namespace>");
            writer.append(getModuleNamespace(resource.getPath()));
            writer.append("</namespace>");
            writer.newLine();
            writer.append("\t<file>");
            writer.append(resource.getPath().getName());
            writer.append("</file>");
            writer.newLine();
            writer.append("</xquery>");
            writer.newLine();
        }
    }
    private String getModuleNamespace(File path) throws IOException, MojoExecutionException {
        //module namespace  spec = "http://nexign-systems.com/spec" ;
        Pattern p = Pattern.compile("^\\s*module\\s+namespace.+?=\\s*[\"']([^\"]+)[\"'].*");
        Predicate<String> predicate = p.asPredicate();
        try (BufferedReader reader = Files.newBufferedReader(path.toPath())) {
            Optional<String> nsline = reader.lines().filter(predicate).findFirst();
            if (!nsline.isPresent()) {
                throw new MojoExecutionException("can't find namespace for the module " + path);
            }

            String line = nsline.get();
            getLog().info("namespace line " + line);
            Matcher m = p.matcher(line);
            if (m.matches()) {
                String ns = m.group(1);
                return ns;
            } else {
                throw new MojoExecutionException("no match");
            }
        }
    }


    private void createJavaBasexDescriptor(List<XarResource> resources, File target) throws IOException, MojoExecutionException {

        List<XarResource> jars = resources.stream().filter(XarResource::isJar).collect(Collectors.toList());
        if (jars.isEmpty()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<package xmlns=\"http://expath.org/ns/pkg\">\n");

        for (XarResource resource : jars) {
            sb.append("<jar>");
            sb.append(resource.getPath().getName());
            sb.append("</jar>\n");
        }

        if (classes == null || classes.isEmpty()) {
            throw new MojoExecutionException("classes can't be empty");
        }
        for (String aClass : classes) {
            sb.append("<class>");
            sb.append(aClass);
            sb.append("</class>\n");
        }

        sb.append("</package>\n");

        try (BufferedWriter writer = Files.newBufferedWriter(target.toPath());
        ) {
            writer.append(sb.toString());
        }
    }

    private List<File> getResources() {

        List<File> files = new ArrayList<>();

        try {
            files.addAll(Files.walk(Paths.get(mavenProject.getBuild().getOutputDirectory()), 1)
                    .filter(Files::isRegularFile)
                    .map(Path::toFile).collect(Collectors.toList()));
            files.addAll(Files.walk(Paths.get(mavenProject.getBuild().getDirectory()), 1)
                    .filter(Files::isRegularFile)
                    .map(Path::toFile).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return files;
    }

    private List<XarResource> identifyResources(List<File> resources) throws MojoFailureException {
        List<XarResource> result = new ArrayList<>();
        for (File resource : resources) {

            String fileName = resource.getName();
            if (fileName.endsWith(".xq") || fileName.endsWith(".xqm")) {
                result.add(new XarResource(resource, XarResourceType.XQUERY));
            } else if (fileName.endsWith(".jar")) {
                result.add(new XarResource(resource, XarResourceType.JAR));
            } else {
                throw new MojoFailureException("unsupported resource: " + resource);
            }
        }
        return result;
    }


}
