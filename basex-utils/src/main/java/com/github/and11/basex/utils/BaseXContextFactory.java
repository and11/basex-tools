package com.github.and11.basex.utils;

import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.StaticOptions;
import org.basex.core.cmd.CreateDB;
import org.basex.core.cmd.Open;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class BaseXContextFactory {
//
//    private final Path dbPath;
//    private Path repoPath;
//
//    public BaseXContextFactory() {
//        try {
//            dbPath = Files.createTempDirectory("db");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public BaseXContextFactory(Path dbPath) {
//        this.dbPath = dbPath;
//    }
//
//    public BasexContext openDatabase(String dbName) {
//        Context context = new Context();
//        context.soptions.set(StaticOptions.DBPATH, dbPath.toFile().getAbsolutePath());
//        context.soptions.set(StaticOptions.REPOPATH, new File(dbPath.toFile().getAbsolutePath()).toPath().resolve("repo").toFile().getAbsolutePath());
//
//        try {
//            new Open(dbName, dbPath.toFile().getAbsolutePath()).execute(context);
//            return new BasexContext(context, dbPath, dbName);
//        } catch (BaseXException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public BasexContext createDatabase(String dbName) {
//
//        Context context = new Context();
//
//        context.soptions.set(StaticOptions.DBPATH, dbPath.toFile().getAbsolutePath());
//        context.soptions.set(StaticOptions.REPOPATH, new File(dbPath.toFile().getAbsolutePath()).toPath().resolve("repo").toFile().getAbsolutePath());
//
//        try {
//
//            new CreateDB(dbName).execute(context);
//            return new BasexContext(context, dbPath, dbName);
//
//        } catch (BaseXException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public BasexContext createTemporaryDatabase() {
//        BasexContext context = createDatabase(UUID.randomUUID().toString());
//        context.setDropOnClose(true);
//        return context;
//    }
}
