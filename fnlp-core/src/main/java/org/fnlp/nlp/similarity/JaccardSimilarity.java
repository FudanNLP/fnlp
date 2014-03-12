/**
*  This file is part of FNLP (formerly FudanNLP).
*  
*  FNLP is free software: you can redistribute it and/or modify
*  it under the terms of the GNU Lesser General Public License as published by
*  the Free Software Foundation, either version 3 of the License, or
*  (at your option) any later version.
*  
*  FNLP is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*  GNU Lesser General Public License for more details.
*  
*  You should have received a copy of the GNU General Public License
*  along with FudanNLP.  If not, see <http://www.gnu.org/licenses/>.
*  
*  Copyright 2009-2014 www.fnlp.org. All rights reserved. 
*/

package org.fnlp.nlp.similarity;

import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.set.hash.THashSet;

public class JaccardSimilarity implements ISimilarity<THashSet<Object>> {

    public float calc(THashSet<Object> s1, THashSet<Object> s2) {
        int com = 0;
        if (s1 == null || s2 == null)
            return 0;
        TObjectHashIterator<Object> it = s1.iterator();
        for ( int i = s1.size(); i-- > 0; ) {
            Object v = it.next();
            if(s2.contains(v))
                com++;
        }
        float sim = ((float) com)/(s1.size()+s2.size()-com);
        return sim;
    }
}