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
package org.eolang.opeo.vmachine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eolang.opeo.ast.AstNode;
import org.eolang.opeo.ast.This;
import org.eolang.opeo.ast.Variable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Local variables.
 * @since 0.1
 */
public final class LocalVariables {

    /**
     * Local variables as ast nodes.
     */
    private Map<Integer, AstNode> variables;

    /**
     * Constructor.
     */
    public LocalVariables() {
        this(Map.of(0, new This()));
    }

    /**
     * Constructor.
     * @param modifiers Method access modifiers.
     * @param descriptor Method descriptor.
     */
    public LocalVariables(final int modifiers, final String descriptor) {
        this(LocalVariables.fromMethod(modifiers, descriptor));
    }

    /**
     * Constructor.
     * @param variables Local variables.
     */
    public LocalVariables(final Map<Integer, AstNode> variables) {
        this.variables = variables;
    }

    /**
     * Get variable by index.
     * @param index Index.
     * @return Variable.
     */
    public AstNode variable(final int index) {
        if (this.variables.size() <= index) {
            throw new IllegalArgumentException(
                String.format(
                    "Local variables size is %d, but index is %d, all variables: %s",
                    this.variables.size(),
                    index,
                    Arrays.deepToString(this.variables.entrySet().toArray())
                )
            );
        }
        return this.variables.get(index);
    }

    /**
     * Create variable.
     * @param index Index.
     * @param type Type.
     * @return Variable.
     */
    public AstNode variable(final int index, final Type type) {
        final AstNode element = new Variable(type, index);
        this.variables.put(index, element);
        return element;
    }

    /**
     * Size of local variables.
     * Important for tests.
     * @return Size.
     */
    int size() {
        return this.variables.size();
    }

    /**
     * Create local variables from method description.
     * @param modifiers Method access modifiers.
     * @param descriptor Method descriptor.
     * @return Local variables.
     */
    private static Map<Integer, AstNode> fromMethod(final int modifiers, final String descriptor) {
        final Type[] args = Type.getArgumentTypes(descriptor);
        final int size = args.length;
        final List<AstNode> res = new ArrayList<>(size);
        if ((modifiers & Opcodes.ACC_STATIC) == 0) {
            res.add(new This());
        }
        for (int index = 0; index < size; ++index) {
            res.add(new Variable(args[index], index));
        }
        final Map<Integer, AstNode> result = new HashMap<>(size);
        final int rsize = res.size();
        for (int index = 0; index < rsize; ++index) {
            result.put(index, res.get(index));
        }
        return result;
    }

}
