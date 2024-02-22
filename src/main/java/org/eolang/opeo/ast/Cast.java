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
package org.eolang.opeo.ast;

import java.util.ArrayList;
import java.util.List;
import org.eolang.jeo.representation.directives.DirectivesData;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Cast node.
 * @since 0.2
 */
public final class Cast implements AstNode, Typed {

    /**
     * Target type.
     */
    private final Type target;

    /**
     * Node to cast.
     */
    private final AstNode origin;

    Cast(final Type target, final AstNode origin) {
        this.origin = origin;
        this.target = target;
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives()
            .add("o").attr("base", "cast")
            .append(this.origin.toXmir())
            .append(new DirectivesData(this.target.getDescriptor()))
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> opcodes = new ArrayList<>(3);
        opcodes.addAll(this.origin.opcodes());
        opcodes.add(this.opcode());
        return opcodes;
    }

    /**
     * Constructor.
     * @return Cast.
     */
    private AstNode opcode() {
        final Type from = new ExpressionType(this.origin).type();
        final Type to = this.target;
        if (from.equals(to)) {
            return new Opcode(Opcodes.NOP);
        } else if (from.equals(Type.INT_TYPE) && to.equals(Type.LONG_TYPE)) {
            return new Opcode(Opcodes.I2L);
        } else if (from.equals(Type.INT_TYPE) && to.equals(Type.FLOAT_TYPE)) {
            return new Opcode(Opcodes.I2F);
        } else if (from.equals(Type.INT_TYPE) && to.equals(Type.DOUBLE_TYPE)) {
            return new Opcode(Opcodes.I2D);
        } else if (from.equals(Type.LONG_TYPE) && to.equals(Type.INT_TYPE)) {
            return new Opcode(Opcodes.L2I);
        } else if (from.equals(Type.LONG_TYPE) && to.equals(Type.FLOAT_TYPE)) {
            return new Opcode(Opcodes.L2F);
        } else if (from.equals(Type.LONG_TYPE) && to.equals(Type.DOUBLE_TYPE)) {
            return new Opcode(Opcodes.L2D);
        } else if (from.equals(Type.FLOAT_TYPE) && to.equals(Type.DOUBLE_TYPE)) {
            return new Opcode(Opcodes.F2D);
        } else if (from.equals(Type.FLOAT_TYPE) && to.equals(Type.INT_TYPE)) {
            return new Opcode(Opcodes.F2I);
        } else if (from.equals(Type.FLOAT_TYPE) && to.equals(Type.LONG_TYPE)) {
            return new Opcode(Opcodes.F2L);
        } else if (from.equals(Type.DOUBLE_TYPE) && to.equals(Type.INT_TYPE)) {
            return new Opcode(Opcodes.D2I);
        } else if (from.equals(Type.DOUBLE_TYPE) && to.equals(Type.LONG_TYPE)) {
            return new Opcode(Opcodes.D2L);
        } else if (from.equals(Type.DOUBLE_TYPE) && to.equals(Type.FLOAT_TYPE)) {
            return new Opcode(Opcodes.D2F);
        } else if (from.equals(Type.INT_TYPE) && to.equals(Type.BYTE_TYPE)) {
            return new Opcode(Opcodes.I2B);
        } else if (from.equals(Type.INT_TYPE) && to.equals(Type.CHAR_TYPE)) {
            return new Opcode(Opcodes.I2C);
        } else if (from.equals(Type.INT_TYPE) && to.equals(Type.SHORT_TYPE)) {
            return new Opcode(Opcodes.I2S);
        } else {
            throw new IllegalStateException(
                String.format(
                    "Can't cast from %s to %s",
                    from.getDescriptor(),
                    to.getDescriptor()
                )
            );
        }
    }

    @Override
    public Type type() {
        return this.target;
    }
}
