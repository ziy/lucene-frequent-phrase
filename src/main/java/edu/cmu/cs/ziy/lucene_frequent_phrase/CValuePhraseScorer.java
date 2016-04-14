package edu.cmu.cs.ziy.lucene_frequent_phrase;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.SetMultimap;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * @author <a href="mailto:ziy@cs.cmu.edu">Zi Yang</a>
 * created on 4/14/16
 */
public class CValuePhraseScorer implements PhraseScorer {

  private Type type;

  public enum Type {AVERAGE, MAXIMUM}

  public CValuePhraseScorer(Type type) {
    this.type = type;
  }

  public double calculateCValue(Phrase phrase, int freq, Set<Integer> subtracts) {
    double subtract = 0;
    switch (type) {
      case AVERAGE:
        subtract = subtracts.stream().mapToDouble(s -> (double) s).average().orElse(0);
        break;
      case MAXIMUM:
        subtract = subtracts.stream().mapToDouble(s -> (double) s).max().orElse(0);
        break;
    }
    return Math.log(phrase.getTerms().size()) * (freq - subtract);
  }

  @Override
  public Map<Phrase, Double> scorePhrase(Collection<Phrase> phrases) throws IOException {
    Map<List<String>, Integer> terms2freq = phrases.stream()
            .collect(toMap(Phrase::getTerms, phrase -> phrase.getDocIdPositionOffsets().size()));
    SetMultimap<List<String>, Integer> terms2substracts = HashMultimap.create();
    for (Phrase phrase : phrases) {
      List<String> terms = phrase.getTerms();
      for (int i = 0; i < terms.size() - 1; i ++) {
        for (int j = i + 1; j < terms.size(); j ++) {
          terms2substracts.put(terms.subList(i, j), terms2freq.get(terms));
        }
      }
    }
    return phrases.stream().collect(toMap(Function.identity(),
            phrase -> calculateCValue(phrase, terms2freq.get(phrase.getTerms()),
                    terms2substracts.get(phrase.getTerms()))));
  }

}
