package com.github.and11.basex.utils.options;

import com.github.and11.basex.utils.Option;

import java.nio.file.Path;

public class WorkingDirectoryOption implements InitializationOption<WorkingDirectoryOption> {
    private final Path workingDirectory;

    public Path getWorkingDirectory() {
        return workingDirectory;
    }

    public WorkingDirectoryOption(Path workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public String toString() {
        return "WorkingDirectoryOption{" +
                "workingDirectory=" + workingDirectory +
                '}';
    }
}
