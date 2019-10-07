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
                .whitelistPackagesNonRecursive("io.github.classgraph") //
                .enableMethodInfo() //
                .ignoreMethodVisibility() //
                .enableFieldInfo() //
                .ignoreFieldVisibility() //
                .enableAnnotationInfo() //
                // .enableInterClassDependencies() //
                // .verbose() //
                .scan()) {
            final String fileName = "D:\\project\\newProject\\new_code_gen\\classgraph\\target/graph1.dot";
            try (PrintWriter writer = new PrintWriter(fileName)) {
                writer.print(scanResult.getAllClasses()
                        // .generateGraphVizDotFileFromClassDependencies()
                        .generateGraphVizDotFile(12, 8, false, true, false, true, true));
            }
            MutableGraph g;
            g = Parser.read(new File(fileName));
            Graphviz.fromGraph(g).width(6000).render(Format.PNG).toFile(new File("D:\\project\\newProject\\new_code_gen\\classgraph\\target/graph1.png"));
            System.out.println("Wrote " + fileName);
        }
    }
}
