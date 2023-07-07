package de.fraunhofer.iem;

import de.fraunhofer.iem.exception.DotToImgException;
import de.fraunhofer.iem.exception.DtsSerializeUtilException;
import de.fraunhofer.iem.exception.DtsZipUtilException;
import de.fraunhofer.iem.exception.UnexpectedError;
import de.fraunhofer.iem.hybridCG.HybridCallGraph;
import de.fraunhofer.iem.hybridCG.ImageType;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FileUtils;
import soot.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;
import soot.util.dot.DotGraph;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainRunner {
    private static final String JavaReflectionTestRootPath = "JarFiles/Reflection-Projects/JavaReflectionTestCases";

    private static final String OUTPUT_ROOT_DIR = "avighna-output/Reflection-Projects/JavaReflectionTestCases";
    private static final String ROOT_BASE_PACKAGE = "de.fraunhofer.iem";
    private static final String AVIGHNA_CMD_JAR = "JarFiles/avighna-cmd-interface-1.0.0.jar";
    private static final String AVIGHNA_AGENT_JAR = "JarFiles/avighna-agent-1.0.0.jar";
    private static void runJava(ProcessBuilder processBuilder) {
        processBuilder.redirectErrorStream(true);
        Process p = null;
        try {
            p = processBuilder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while (true) {
                line = r.readLine();
                if (line == null) { break; }
                System.out.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int generateInitialDotGraph() {
        CallGraph callGraph = Scene.v().getCallGraph();

        int numberOfEdgesInStaticCallGraph = 0;
        DotGraph dotGraph = new DotGraph("final:callgraph");

        for (Edge edge : callGraph) {
            String node_src = edge.getSrc().toString();
            String node_tgt = edge.getTgt().toString();

            if (node_src.startsWith("<java.") || node_tgt.startsWith("<java.")) continue;

            if (node_src.startsWith("<sun.") || node_tgt.startsWith("<sun.")) continue;

            if (node_src.startsWith("<javax.") || node_tgt.startsWith("<javax.")) continue;

            if (node_src.startsWith("<jdk.") || node_tgt.startsWith("<jdk.")) continue;

            if (node_src.startsWith("<com.sun.crypto.provider.") || node_tgt.startsWith("<com.sun.crypto.provider.")) continue;

            ++numberOfEdgesInStaticCallGraph;
        }

        return numberOfEdgesInStaticCallGraph;
    }

    private static void initializeSoot(String appClassPath, String dtsFileName) throws DtsZipUtilException, FileNotFoundException {
        //TODO: Remove this after testing
        G.reset();
        Options.v().set_keep_line_number(true);

        Options.v().setPhaseOption("cg.spark", "on");

        Options.v().setPhaseOption("cg", "all-reachable:true");
        Options.v().set_allow_phantom_refs(true);

        String dynamicCP = null;

        if (dtsFileName == null) {
            Options.v().set_soot_classpath(appClassPath + File.pathSeparator);
        } else {
            dynamicCP = new HybridCallGraph().getDynamicClassesPath(dtsFileName);
            System.out.println();
            Options.v().set_soot_classpath(appClassPath + File.pathSeparator + dynamicCP);
        }

        Options.v().set_prepend_classpath(true);
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);

        Options.v().setPhaseOption("jb", "use-original-names:true");
        //Options.v().setPhaseOption("jb.lns", "enabled:false");

        Options.v().set_output_format(Options.output_format_none);

        List<String> appClasses = new ArrayList<>(FilesUtils.getClassesAsList(appClassPath));

        if (dtsFileName != null) {
            if (new File(dynamicCP).exists()) {
                appClasses.addAll(new ArrayList<>(FilesUtils.getClassesAsList(dynamicCP)));
            }
        }

        List<SootMethod> entries = new ArrayList<SootMethod>();
        for (String appClass : appClasses) {
            System.out.println(appClass);
            SootClass sootClass = Scene.v().forceResolve(appClass, SootClass.BODIES);
            sootClass.setApplicationClass();
            entries.addAll(sootClass.getMethods());
        }

        Scene.v().setEntryPoints(entries);
        Scene.v().loadNecessaryClasses();
        PackManager.v().getPack("cg").apply();
    }

    private static void mergeZipFiles(File dst1, File dst2, File dstOut) throws IOException {
        List<File> zipFilesToMerge = Arrays.asList(
                dst1,
                dst2
        );

        ZipFile mergedZipFile = new ZipFile(dstOut.getAbsoluteFile().getAbsolutePath());

        for (File fileToMerge : zipFilesToMerge) {
            ZipFile zipFileToMerge = new ZipFile(fileToMerge);

            for (FileHeader fileHeader : zipFileToMerge.getFileHeaders()) {
                try (InputStream inputStream = zipFileToMerge.getInputStream(fileHeader)) {
                    ZipParameters zipParameters = getZipParametersFromFileHeader(fileHeader);
                    mergedZipFile.addStream(inputStream, zipParameters);
                }
            }
        }
    }

    private static ZipParameters getZipParametersFromFileHeader(FileHeader fileHeader) {
        ZipParameters zipParameters = new ZipParameters();
        zipParameters.setCompressionMethod(fileHeader.getCompressionMethod());
        zipParameters.setFileNameInZip(fileHeader.getFileName());
        return zipParameters;
    }


    public static void main(String[] args) throws IOException, UnexpectedError, DotToImgException, DtsSerializeUtilException, DtsZipUtilException {
        File rootProjectDir = new File(JavaReflectionTestRootPath);
        String[] modules = rootProjectDir.list();

        for (Object obj : Arrays.stream(modules).sorted().toArray()) {
            String module = (String) obj;

            if (module.startsWith("CSR")
                    || module.startsWith("TR")
                    || module.startsWith("CFNE")
                    || module.startsWith("LRR")) {

                ProcessBuilder processBuilder;

                switch (module) {
                    case "CSR2":
                        break;
                    case "LRR2":
                        System.out.println("Running for " + module);
                        List<String> lrr2Arguments = Arrays.asList("param1:param2", "param1");

                        File mainOutputDir = new File(OUTPUT_ROOT_DIR + File.separator + module + File.separator + "avighna-agent-output");

                        FileUtils.deleteDirectory(mainOutputDir);

                        mainOutputDir.mkdirs();

                        new File(mainOutputDir.getAbsolutePath() + File.separator + "allDotFiles").mkdirs();

                        String ser1 = "";
                        String ser2 = "";
                        String dstOut = mainOutputDir.getAbsolutePath() + "dynamic_cg.dst";

                        int runCount = 1;
                        for (String lrr2Argument : lrr2Arguments) {
                            processBuilder = new ProcessBuilder(
                                    "java",
                                    "-jar",
                                    AVIGHNA_CMD_JAR,
                                    "-aj",
                                    JavaReflectionTestRootPath +
                                            File.separator + module + File.separator + "target" +
                                            File.separator + module + "-1.0-SNAPSHOT-jar-with-dependencies.jar",
                                    "-aaj",
                                    AVIGHNA_AGENT_JAR,
                                    "-od",
                                    OUTPUT_ROOT_DIR +
                                            File.separator + module + File.separator + "avighna-agent-output-run-" + runCount,
                                    "-rap",
                                    ROOT_BASE_PACKAGE,
                                    "-sdf",
                                    "-sif",
                                    "-sra",
                                    "-aa",
                                    lrr2Argument);

                            runJava(processBuilder);

                            String serFile = OUTPUT_ROOT_DIR +
                                    File.separator + module + File.separator +
                                    "avighna-agent-output-run-" + runCount + File.separator + "allDotFiles" + File.separator + "dynamic_callgraph_1.ser";

                            if (runCount == 1)
                                ser1 = mainOutputDir.getAbsoluteFile().getAbsolutePath() + "dynamic_cg_" + runCount + ".dst";

                            if (runCount == 2)
                                ser2 = mainOutputDir.getAbsoluteFile().getAbsolutePath() + "dynamic_cg_" + runCount + ".dst";

                            Files.copy(new File(serFile).toPath(), new File(mainOutputDir.getAbsoluteFile().getAbsolutePath() + File.separator + "allDotFiles" + File.separator + "dynamic_callgraph_" + runCount + ".ser").toPath());

                            runCount++;
                        }

                        FileUtils.copyDirectory(new File(OUTPUT_ROOT_DIR +
                                File.separator + module + File.separator +
                                "avighna-agent-output-run-" + (runCount - 1) + File.separator + "dynamicCP"), new File(mainOutputDir + File.separator + "allDotFiles" + File.separator + "dynamicCP"));

                        ZipUtil.generateDst(mainOutputDir.toPath().toString());

                        new File(OUTPUT_ROOT_DIR +
                                File.separator + module + File.separator + "avighna-agent-output"  +
                                File.separator + "hybrid-merger-output").mkdirs();

                        String appClassPath = JavaReflectionTestRootPath +
                                File.separator + module + File.separator +
                                "target" + File.separator + "classes";
                        String dtsFileName = OUTPUT_ROOT_DIR +
                                File.separator + module + File.separator +
                                "avighna-agent-output" + File.separator + "dynamic_cg.dst";
                        String hybridOutputPath = OUTPUT_ROOT_DIR +
                                File.separator + module + File.separator +
                                "avighna-agent-output" + File.separator +
                                "hybrid-merger-output" + File.separator;

                        initializeSoot(appClassPath, null);

                        int numberOfEdgesInPureStaticCallgraph = generateInitialDotGraph();

                        initializeSoot(appClassPath, dtsFileName);

                        new HybridCallGraph(true, numberOfEdgesInPureStaticCallgraph).merge(
                                dtsFileName,
                                Scene.v().getCallGraph(),
                                hybridOutputPath,
                                "callgraph",
                                "callgraph",
                                ImageType.SVG
                        );
                        break;
                    case "LRR1":
                        continue;
                    default:
                        System.out.println("Running for " + module);
                        processBuilder = new ProcessBuilder(
                                "java",
                                "-jar",
                                AVIGHNA_CMD_JAR,
                                "-aj",
                                JavaReflectionTestRootPath +
                                        File.separator + module + File.separator + "target" +
                                        File.separator + module + "-1.0-SNAPSHOT-jar-with-dependencies.jar",
                                "-aaj",
                                AVIGHNA_AGENT_JAR,
                                "-od",
                                OUTPUT_ROOT_DIR +
                                        File.separator + module + File.separator + "avighna-agent-output",
                                "-rap",
                                ROOT_BASE_PACKAGE,
                                "-sdf",
                                "-sif",
                                "-sra");

                        runJava(processBuilder);


                        new File(OUTPUT_ROOT_DIR +
                                File.separator + module + File.separator + "avighna-agent-output"  +
                                File.separator + "hybrid-merger-output").mkdirs();

                        String appClassPath = JavaReflectionTestRootPath +
                                File.separator + module + File.separator +
                                "target" + File.separator + "classes";
                        String dtsFileName = OUTPUT_ROOT_DIR +
                                File.separator + module + File.separator +
                                "avighna-agent-output" + File.separator + "dynamic_cg.dst";
                        String hybridOutputPath = OUTPUT_ROOT_DIR +
                                File.separator + module + File.separator +
                                "avighna-agent-output" + File.separator +
                                "hybrid-merger-output" + File.separator;

                        initializeSoot(appClassPath, null);

                        int numberOfEdgesInPureStaticCallgraph = generateInitialDotGraph();

                        initializeSoot(appClassPath, dtsFileName);

                        new HybridCallGraph(true, numberOfEdgesInPureStaticCallgraph).merge(
                                dtsFileName,
                                Scene.v().getCallGraph(),
                                hybridOutputPath,
                                "callgraph",
                                "callgraph",
                                ImageType.SVG
                        );
                        break;
                }
            }
        }
    }
}
