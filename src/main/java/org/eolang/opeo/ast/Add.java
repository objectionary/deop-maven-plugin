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
import org.objectweb.asm.Opcodes;
import org.xembly.Directive;
import org.xembly.Directives;

/**
 * Add output node.
 * @since 0.1
 */
public final class Add implements AstNode {

    /**
     * Left operand.
     */
    private final AstNode left;

    /**
     * Right operand.
     */
    private final AstNode right;

    /**
     * Attributes.
     */
    private final Attributes attributes;

    /**
     * Constructor.
     * @param left Left operand
     * @param right Right operand
     */
    public Add(final AstNode left, final AstNode right) {
        this(left, right, new Attributes().type("int"));
    }

    /**
     * Constructor.
     * @param left Left operand
     * @param right Right operand
     * @param attributes Attributes
     */
    public Add(final AstNode left, final AstNode right, final Attributes attributes) {
        this.left = left;
        this.right = right;
        this.attributes = attributes;
    }

    @Override
    public String print() {
        return String.format("(%s) + (%s)", this.left.print(), this.right.print());
    }

    @Override
    public Iterable<Directive> toXmir() {
        return new Directives().add("o")
            .attr("base", ".plus")
            .attr("scope", this.attributes)
            .append(this.left.toXmir())
            .append(this.right.toXmir())
            .up();
    }

    @Override
    public List<AstNode> opcodes() {
        final List<AstNode> res = new ArrayList<>(0);
        res.addAll(this.left.opcodes());
        res.addAll(this.right.opcodes());
        res.add(this.opcode());
        return res;
    }

    /**
     * Typed opcode.
     * @return Opcode.
     */
    private AstNode opcode() {
        if (this.attributes.type().equals("long")) {
            return new Opcode(Opcodes.LADD);
        } else {
            return new Opcode(Opcodes.IADD);
        }
    }
}
