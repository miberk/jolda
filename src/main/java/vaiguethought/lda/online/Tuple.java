package vaiguethought.lda.online;
/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
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
