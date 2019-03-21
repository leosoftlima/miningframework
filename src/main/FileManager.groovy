import java.nio.file.Files 
import java.nio.file.Paths
import java.nio.file.Path
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

final class FileManager {

    public static Set<String> getModifiedFiles(Project project, String childSHA, String ancestorSHA) {
        Set<String> modifiedFiles = new HashSet<String>()
        Process gitDiff = new ProcessBuilder('git', 'diff', '--name-only', childSHA, ancestorSHA)
            .directory(new File(project.getPath()))
            .start()
        
        gitDiff.getInputStream().eachLine {
            if(it.endsWith('.java'))
                modifiedFiles.add(it)
        }

        return modifiedFiles
    }

    public static File copyFile(Project project, String path, String SHA) {
        Process gitCatFile = new ProcessBuilder('git', 'cat-file', '-p', "${SHA}:${path}")
            .directory(new File(project.getPath()))
            .start()
    
        
        File target = new File("${SHA}.java")
        gitCatFile.getInputStream().eachLine {
            target << "${it}\n"
        }
        return target
    }

    public static void copyAndMoveFile(Project project, String file, String sha, String target) {
        File targetFile = copyFile(project, file, sha)
        Files.move(targetFile.toPath(), Paths.get(target), REPLACE_EXISTING)
    }

    public static File createOutputFiles(String outputPath) {
        File outputDir = new File(outputPath)
        if (!outputDir.exists())
            outputDir.mkdirs()
        
        createStatisticsFiles(outputPath)
        createDataFiles(outputPath)

        return outputDir
    }

    private static File createStatisticsFiles(String outputPath) {
        File statisticsDir = new File(outputPath + '/statistics')
        if (!statisticsDir.exists())
            statisticsDir.mkdirs()

        File statisticsResultsFile = new File(outputPath + "/statistics/results.csv")
        if (statisticsResultsFile.exists())
            statisticsResultsFile.delete()

        statisticsResultsFile << 'project,merge commit,is octopus,number of merge conflicts,merge conflict ocurrence,number of conflicting files, number of developers\' mean,number of commits\' mean,number of changed files\' mean, number of changed lines\' mean,duration mean,conclusion delay\n'

        return statisticsResultsFile
    }

    private static File createDataFiles(String outputPath) {
        File dataDir = new File(outputPath + '/data')
        if (!dataDir.exists())
            dataDir.mkdirs()        

        File dataResultsFile = new File(outputPath + '/data/results.csv')
        if(dataResultsFile.exists())
            dataResultsFile.delete()

        dataResultsFile << 'project;merge commit;class;method;left modifications;left deletions;right modifications;right deletions\n'
        return dataResultsFile
    }

    public static delete(File file) {
        if (!file.isDirectory())
            file.delete()
        else {
            if (file.list().length == 0) 
                file.delete()
            else {
                String[] files = file.list()
                for (temp in files) {
                    delete(new File(file, temp))
                }
                if (file.list().length == 0) 
                    file.delete()
            }
        }
    }

}