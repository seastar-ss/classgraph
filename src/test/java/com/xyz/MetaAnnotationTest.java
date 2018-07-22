/*
 * This file is part of FastClasspathScanner.
 *
 * Author: Luke Hutchison
 *
 * Hosted at: https://github.com/lukehutch/fast-classpath-scanner
 *
 * --
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Luke Hutchison
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without
 * limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.xyz;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Test;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.ScanResult;

public class MetaAnnotationTest {
    ScanResult scanResult = new FastClasspathScanner().whitelistPackages("com.xyz.meta").enableAnnotationInfo()
            .scan();

    @Test
    public void oneLevel() {
        assertThat(scanResult.getClassesWithAnnotation("com.xyz.meta.E").directOnly().getClassNames())
                .containsOnly("com.xyz.meta.B");
        assertThat(scanResult.getClassesWithAnnotation("com.xyz.meta.F").directOnly().getClassNames())
                .containsOnly("com.xyz.meta.B", "com.xyz.meta.A");
        assertThat(scanResult.getClassesWithAnnotation("com.xyz.meta.G").directOnly().getClassNames())
                .containsOnly("com.xyz.meta.C");
    }

    @Test
    public void twoLevels() {
        assertThat(scanResult.getClassesWithAnnotation("com.xyz.meta.J").getClassNames())
                .containsOnly("com.xyz.meta.F", "com.xyz.meta.E", "com.xyz.meta.B", "com.xyz.meta.A");
    }

    @Test
    public void threeLevels() {
        assertThat(scanResult.getClassesWithAnnotation("com.xyz.meta.L").getClassNames())
                .containsOnly("com.xyz.meta.I", "com.xyz.meta.E", "com.xyz.meta.B", "com.xyz.meta.H");
    }

    @Test
    public void acrossCycle() {
        assertThat(scanResult.getClassesWithAnnotation("com.xyz.meta.H").directOnly().getClassNames())
                .containsOnly("com.xyz.meta.I");
        assertThat(scanResult.getClassAnnotations("com.xyz.meta.H").directOnly().getClassNames())
                .containsOnly("com.xyz.meta.I", "com.xyz.meta.K");
        assertThat(scanResult.getClassesWithAnnotation("com.xyz.meta.I").directOnly().getClassNames())
                .containsOnly("com.xyz.meta.E", "com.xyz.meta.H");
        assertThat(scanResult.getClassAnnotations("com.xyz.meta.I").directOnly().getClassNames())
                .containsOnly("com.xyz.meta.L", "com.xyz.meta.H");
        assertThat(scanResult.getClassesWithAnnotation("com.xyz.meta.K").directOnly().getClassNames())
                .containsOnly("com.xyz.meta.H");
        assertThat(scanResult.getClassesWithAnnotation("com.xyz.meta.D").directOnly().getClassNames())
                .containsOnly("com.xyz.meta.K");
    }

    @Test
    public void cycleAnnotatesSelf() {
        assertThat(scanResult.getClassesWithAnnotation("com.xyz.meta.I").getClassNames())
                .containsOnly("com.xyz.meta.E", "com.xyz.meta.B", "com.xyz.meta.H", "com.xyz.meta.I");
    }

    @Test
    public void namesOfMetaAnnotations() {
        assertThat(scanResult.getClassAnnotations("com.xyz.meta.A").getClassNames()).containsOnly("com.xyz.meta.J",
                "com.xyz.meta.F");
        assertThat(scanResult.getClassAnnotations("com.xyz.meta.C").getClassNames()).containsOnly("com.xyz.meta.G");
    }

    @Test
    public void union() {
        assertThat(scanResult.getClassesWithAnnotation("com.xyz.meta.J")
                .union(scanResult.getClassesWithAnnotation("com.xyz.meta.G")).directOnly().getClassNames())
                        .containsOnly("com.xyz.meta.E", "com.xyz.meta.F", "com.xyz.meta.C");
        assertThat(scanResult.getClassesWithAnnotation("com.xyz.meta.I")
                .union(scanResult.getClassesWithAnnotation("com.xyz.meta.J")).getClassNames()).containsOnly(
                        "com.xyz.meta.A", "com.xyz.meta.B", "com.xyz.meta.F", "com.xyz.meta.E", "com.xyz.meta.H",
                        "com.xyz.meta.I");
        assertThat(scanResult.getClassesWithAnnotation("com.xyz.meta.I")
                .union(scanResult.getClassesWithAnnotation("com.xyz.meta.J")).directOnly().getClassNames())
                        .containsOnly("com.xyz.meta.F", "com.xyz.meta.E", "com.xyz.meta.H");
    }

    @Test
    public void intersect() {
        assertThat(scanResult.getClassesWithAnnotation("com.xyz.meta.I")
                .intersect(scanResult.getClassesWithAnnotation("com.xyz.meta.J")).getClassNames())
                        .containsOnly("com.xyz.meta.E", "com.xyz.meta.B");
        assertThat(scanResult.getClassesWithAnnotation("com.xyz.meta.I")
                .intersect(scanResult.getClassesWithAnnotation("com.xyz.meta.J")).directOnly().getClassNames())
                        .containsOnly("com.xyz.meta.E");
    }

    public static void main(final String[] args) throws Exception {
        new FastClasspathScanner().whitelistPackages("com.xyz.meta").enableAnnotationInfo().scan()
                .generateClassGraphDotFile(new File("/tmp/x.dot"));
    }
}
