import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

/**
 * ClassGraphGraphVizGenerator.
 */
public class ClassGraphGraphVizGenerator {
    /**
     * The main method.
     *
     * @param args
     *            the arguments
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public static void main(final String[] args) throws IOException {
        try (ScanResult scanResult = new ClassGraph() //
                .whitelistPackages("com.shangde.dragnet.api.web.wechatCollectManager","com.shangde.dragnet.service") //
                .enableMethodInfo() //
                .ignoreMethodVisibility() //
                .enableFieldInfo() //
                .ignoreFieldVisibility() //
                .enableAnnotationInfo() //
                 .enableInterClassDependencies() //
                .enableRemoteJarScanning()
                // .verbose() //
                .scan()) {
            final String fileName = "D:\\project\\newProject\\new_code_gen\\classgraph\\target/graph5.dot";
            try (PrintWriter writer = new PrintWriter(fileName)) {
                writer.print(scanResult.getAllClasses()
                        // .generateGraphVizDotFileFromClassDependencies()
                        .generateGraphVizDotFile(16, 12, true, true, false, true, false));
            }
            MutableGraph g;
            g = Parser.read(new File(fileName));
            Graphviz.fromGraph(g).width(8000).height(6000).render(Format.PNG).toFile(new File("D:\\project\\newProject\\new_code_gen\\classgraph\\target/graph5.png"));
            System.out.println("Wrote " + fileName);
        }
    }
}
