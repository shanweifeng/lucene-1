package org.apache.lucene.search.regex;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.search.FilteredTermsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;

/**
 * Subclass of FilteredTermsEnum for enumerating all terms that match the
 * specified regular expression term using the specified regular expression
 * implementation.
 * <p>
 * Term enumerations are always ordered by Term.compareTo().  Each term in
 * the enumeration is greater than all that precede it.
 *
 */

public class RegexTermsEnum extends FilteredTermsEnum {
  private String pre = "";
  private RegexCapabilities regexImpl;
  private final BytesRef prefixRef;

  public RegexTermsEnum(IndexReader reader, Term term, RegexCapabilities regexImpl) throws IOException {
    super(reader, term.field());
    String text = term.text();
    this.regexImpl = regexImpl;

    regexImpl.compile(text);

    pre = regexImpl.prefix();
    if (pre == null) pre = "";

    prefixRef = new BytesRef(pre);
    setInitialSeekTerm(prefixRef);
  }

  @Override
  protected final AcceptStatus accept(BytesRef term) {
    if (term.startsWith(prefixRef)) {
      return regexImpl.match(term.utf8ToString()) ? AcceptStatus.YES : AcceptStatus.NO;
    } else {
      return AcceptStatus.END;
    }
  }
}
