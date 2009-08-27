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

package org.apache.lucene.analysis.cn.smart;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.cn.smart.hhmm.HHMMSegmenter;
import org.apache.lucene.analysis.cn.smart.hhmm.SegToken;
import org.apache.lucene.analysis.cn.smart.hhmm.SegTokenFilter;

/**
 * Segment a sentence of Chinese text into words.
 * <p><font color="#FF0000">
 * WARNING: The status of the analyzers/smartcn <b>analysis.cn</b> package is experimental. 
 * The APIs and file formats introduced here might change in the future and will not be 
 * supported anymore in such a case.</font>
 * </p>
 */
class WordSegmenter {

  private HHMMSegmenter hhmmSegmenter = new HHMMSegmenter();

  private SegTokenFilter tokenFilter = new SegTokenFilter();

  /**
   * Segment a sentence into words with {@link HHMMSegmenter}
   * 
   * @param sentence input sentence
   * @param startOffset start offset of sentence
   * @return {@link List} of {@link SegToken}
   */
  public List segmentSentence(String sentence, int startOffset) {

    List segTokenList = hhmmSegmenter.process(sentence);

    List result = new ArrayList();

    // tokens from sentence, excluding WordType.SENTENCE_BEGIN and WordType.SENTENCE_END
    for (int i = 1; i < segTokenList.size() - 1; i++) {
      result.add(convertSegToken((SegToken) segTokenList.get(i), sentence, startOffset));
    }
    return result;

  }

  /**
   * Process a {@link SegToken} so that it is ready for indexing.
   * 
   * This method calculates offsets and normalizes the token with {@link SegTokenFilter}.
   * 
   * @param st input {@link SegToken}
   * @param sentence associated Sentence
   * @param sentenceStartOffset offset into sentence
   * @return Lucene {@link SegToken}
   */
  public SegToken convertSegToken(SegToken st, String sentence,
      int sentenceStartOffset) {

    switch (st.wordType) {
      case WordType.STRING:
      case WordType.NUMBER:
      case WordType.FULLWIDTH_NUMBER:
      case WordType.FULLWIDTH_STRING:
        st.charArray = sentence.substring(st.startOffset, st.endOffset)
            .toCharArray();
        break;
      default:
        break;
    }

    st = tokenFilter.filter(st);
    st.startOffset += sentenceStartOffset;
    st.endOffset += sentenceStartOffset;
    return st;
  }
}
