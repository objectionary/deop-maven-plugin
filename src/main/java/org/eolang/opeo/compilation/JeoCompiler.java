/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2023 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.opeo.compilation;

import com.jcabi.xml.XML;
import org.eolang.jeo.representation.xmir.XmlClass;
import org.eolang.jeo.representation.xmir.XmlMethod;
import org.eolang.jeo.representation.xmir.XmlNode;
import org.eolang.jeo.representation.xmir.XmlProgram;

/**
 * Compiler of high-level EO programs to low-level EO suitable for jeo-maven-plugin.
 *
 * @since 0.1
 */
public final class JeoCompiler {

    /**
     * The high-level EO program generated by opeo-maven-plugin.
     */
    private final XML opeo;

    /**
     * Constructor.
     *
     * @param opeo The high-level EO program generated by opeo-maven-plugin.
     */
    public JeoCompiler(final XML opeo) {
        this.opeo = opeo;
    }

    /**
     * Compiles the high-level EO program to low-level EO suitable for jeo-maven-plugin.
     *
     * @return The low-level EO program suitable for jeo-maven-plugin.
     */
    public XML compile() {
        final XmlProgram program = new XmlProgram(this.opeo.node());
        final XmlClass clazz = program.top();
        final XmlMethod[] methods = clazz.methods().stream()
            .map(JeoCompiler::compile)
            .toArray(XmlMethod[]::new);
        return program.replaceTopClass(
            clazz.replaceMethods(methods)
        ).toXml();
    }

    /**
     * Compiles a single method.
     *
     * @param method The method to compile.
     * @return The compiled method.
     */
    private static XmlMethod compile(final XmlMethod method) {
        try {
            return method.withoutMaxs().withInstructions(
                new XmirParser(method.nodes()).toJeoNodes().toArray(XmlNode[]::new)
            );
        } catch (final IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                String.format(
                    "Failed to compile method %s: %s",
                    method.name(),
                    method
                ),
                exception
            );
        } catch (final IllegalStateException exception) {
            throw new IllegalStateException(
                String.format(
                    "Failed to compile method %s: %s",
                    method.name(),
                    method
                ),
                exception
            );
        }
    }
}
