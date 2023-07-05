package de.fraunhofer.iem;

import de.fraunhofer.iem.exception.DotToImgException;
import de.fraunhofer.iem.exception.DtsSerializeUtilException;
import de.fraunhofer.iem.exception.DtsZipUtilException;
import de.fraunhofer.iem.exception.UnexpectedError;
import de.fraunhofer.iem.hybridCG.HybridCallGraph;
import de.fraunhofer.iem.hybridCG.ImageType;
import org.apache.commons.io.FileUtils;
import soot.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;
import soot.util.dot.DotGraph;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainRunner {
    //TODO: The below variables are set based on the docker container that we developed. Change the variables according if running in different environment
    private static final String CG_BENCH_ROOT_DIR = "JarFiles/Spring-Projects/CGBench";
    private static final String ROOT_BASE_PACKAGE = "de.fraunhofer.iem.springbench.";
    private static final String AVIGHNA_CMD_JAR = "JarFiles/avighna-cmd-interface-1.0.0.jar";
    private static final String AVIGHNA_AGENT_JAR = "JarFiles/avighna-agent-1.0.0.jar";
    private static final String OUTPUT_ROOT_DIR = "avighna-output/Spring-Projects/CGBench";
    private static final String[] CG_BENCH_PROJECT = {
            "bean",
            "beanwithclassxmlconfiguration",
            "component",
            "controlleradvice",
            "cookievalue",
            "deletemapping",
            "exceptionhandler",
            "getmapping",
            "handlerinterceptoradapteraftercompletion",
            "handlerinterceptoradapterposthandle",
            "handlerinterceptoradapterprehandle",
            "handlerinterceptoraftercompletion",
            "handlerinterceptorposthandle",
            "handlerinterceptorprehandle",
            "initbinderwithoutvalue",
            "initbinderwithvalue",
            "matrixvariable",
            "modelattributeonargumentlevel",
            "modelattributeonargumentlevelwithfalsebinding",
            "modelattributewithaddattribute",
            "modelattributewithreturnvalue",
            "patchmapping",
            "pathvariable",
            "postmapping",
            "putmapping",
            "repository",
            "requestattribute",
            "requestheader",
            "requestmapping",
            "requestparam",
            "requestpart",
            "service",
            "sessionattribute",
            "sessionattributes",
            "simplecontroller",
            "simplerestcontroller",
            "springmvcwithfreemarker",
            "springmvcwithjsp",
            "springmvcwiththymeleaf"
    };

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

    public static void main(String[] args) {
        String outRootDir = null;
        String requestYamlFile = null;
        String appJar = null;
        String appPackageName = null;
        String avighnaOutputDir = null;
        String appClassPath = null;
        String dstFile = null;
        String mergerOutputDir = null;

        for (String proj : CG_BENCH_PROJECT) {
            outRootDir = OUTPUT_ROOT_DIR + File.separator + proj;
            requestYamlFile = CG_BENCH_ROOT_DIR + File.separator + proj + File.separator + "requests.yaml";
            appClassPath = CG_BENCH_ROOT_DIR + File.separator + proj + File.separator + "target" + File.separator +
                    "classes" + File.separator;
            appJar = CG_BENCH_ROOT_DIR + File.separator + proj + File.separator + "target" + File.separator +
                    proj + "-0.0.1-SNAPSHOT.jar";
            appPackageName = ROOT_BASE_PACKAGE + proj;
            avighnaOutputDir = outRootDir + File.separator + "avighna-agent-output";
            dstFile = avighnaOutputDir + File.separator + "dynamic_cg.dst";
            mergerOutputDir = avighnaOutputDir + File.separator + "hybrid-merger-output" + File.separator;

            File file1 = new File(avighnaOutputDir);
            File file2 = new File(mergerOutputDir);

            if (file1.exists()) {
                try {
                    FileUtils.deleteDirectory(file1);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }

            System.out.println(file1.mkdirs());

            String[] javaCMD = {
                    "java",
                    "-jar",
                    AVIGHNA_CMD_JAR,
                    "-aj",
                    appJar,
                    "-aaj",
                    AVIGHNA_AGENT_JAR,
                    "-od",
                    avighnaOutputDir,
                    "-rap",
                    appPackageName,
                    "-sdf",
                    "-sif",
                    "-lrf",
                    requestYamlFile
            };

            System.out.println("Executing = \n" + Arrays.toString(javaCMD));

            try {
                runJava(new ProcessBuilder(javaCMD));
                //Runtime.getRuntime().exec(javaCMD).waitFor();

                System.out.println("Merging = ");

                System.out.println(file2.mkdir());

                initializeSoot(appClassPath, null);

                int numberOfEdgesInPureStaticCallgraph = generateInitialDotGraph();

                initializeSoot(appClassPath, dstFile);

                new HybridCallGraph(true, numberOfEdgesInPureStaticCallgraph).merge(
                        dstFile,
                        Scene.v().getCallGraph(),
                        mergerOutputDir,
                        "callgraph",
                        "callgraph",
                        ImageType.SVG
                );
            } catch (IOException | DtsZipUtilException | DtsSerializeUtilException |
                     DotToImgException | UnexpectedError e) {
                e.printStackTrace();
            }
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
}
