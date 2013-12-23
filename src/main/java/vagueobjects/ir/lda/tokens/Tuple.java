package vagueobjects.ir.lda.tokens;
/*
Copyright (c) 2013 miberk

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Tuple implements Comparable<Tuple> {
    final int position;
    final double value;
    final static NumberFormat  NF = new DecimalFormat ("##.####");
    public Tuple(int position, double value) {
        this.position = position;
        this.value = value;
    }

    @Override
    public int compareTo(Tuple o) {
        if (o.value == value) {
            return new Integer(o.position).compareTo(position);
        }
        return value < o.value ? 1 : -1;
    }
    public void addToString(StringBuilder stringBuilder,Documents documents){
        stringBuilder.append("[").append(documents.getToken(position))
                .append("->").append(NF.format(value)).append("] ");
    }
}
