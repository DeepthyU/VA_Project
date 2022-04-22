package utils;


import java.nio.file.Path;
import java.nio.file.Paths;

public class VisualizerPrefs {
    private static final VisualizerPrefs instance = new VisualizerPrefs();
    private String[] pythonExecutable = {"conda", "run", "python3"};
    private Path rootPath = Paths.get("").toAbsolutePath();
    private Path dataDirPath = Paths.get("src", "main", "data", "gastech_data", "data");

    /**
     * Singleton class holding all preferences that might be needed by the visualizer.
     */
    private VisualizerPrefs() {}

    public static VisualizerPrefs getInstance() {
        return instance;
    }

    /**
     * Gets the command to run Python.
     * @return Array of strings representing the command to run Python. Defaults to {"conda", "run", "python"}
     */
    public String[] getPythonExecutable() {
        return pythonExecutable;
    }
    /**
     * Sets the command to be run as the Python executable.
     * @param pythonExecutable Array of strings representing the command to be run.
     */
    public void setPythonExecutable(String[] pythonExecutable) {
        if (pythonExecutable.length == 1) {
            String[] command = pythonExecutable[0].split("\\s+");
            if (command.length > 1) {
                this.pythonExecutable = command;
            } else {
                this.pythonExecutable = pythonExecutable;
            }
        } else {
            this.pythonExecutable = pythonExecutable;
        }
    }
    /**
     * Accesses the set data directory path.
     * @return The data directory inside of the gastech_data directory as path relative to the project root. Defaults to "src/data/gastech_data/data"
     */
    public Path getDataDirPath() {
        return dataDirPath;
    }

    /**
     * Accesses the data dir path with the project root as part of the path
     * @return [project root / data dir path]
     */
    public Path getFullDataDirPath() {
        return rootPath.resolve(dataDirPath);
    }

    /**
     * Sets the data directory path
     * @param dataDirPath Path to the data directory RELATIVE to the project root
     */
    public void setDataDirPath(Path dataDirPath) {
        this.dataDirPath = dataDirPath;
    }
    /**
     * Acceses the set project root path.
     * @return The project rooth path. Defaults to "."
     */
    public Path getRootPath() {
        return rootPath;
    }
    /**
     * Sets the project root path.
     * @param rootPath Path to the project root.
     */
    public void setRootPath(Path rootPath) {
        this.rootPath = rootPath.toAbsolutePath();
    }
}
