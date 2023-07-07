package de.fraunhofer.iem;

import net.lingala.zip4j.ZipFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ZipUtil {
    public static void generateDst(String rootOutDir) {
        ArrayList<String> dotFiles = new ArrayList<>();
        try {
            Files.walk(Paths.get(rootOutDir + File.separator + "allDotFiles" + File.separator))
                    .filter(Files::isRegularFile)
                    .filter(it -> it.toAbsolutePath().toString().endsWith(".ser"))
                    .forEach(it -> {dotFiles.add(it.toAbsolutePath().toString());});

            ZipFile zipFile = new ZipFile(rootOutDir + File.separator + "dynamic_cg.dst");

            for (String path : dotFiles) {
                zipFile.addFile(new File(path));
            }

            zipFile.addFolder(new File(rootOutDir +
                    File.separator +
                    "dynamicCP" + File.separator));

            zipFile.close();
        } catch (IOException e) {
            System.err.println("Something went wrong while generating DST file = " + e.getMessage());
        }
    }
}
