package edu.cmu.cs.ziy.lucene_frequent_phrase;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:ziy@cs.cmu.edu">Zi Yang</a>
 * created on 4/14/16
 */
public interface PhraseScorer {
  Map<Phrase, Double> scorePhrase(Collection<Phrase> phrases) throws IOException;
}
