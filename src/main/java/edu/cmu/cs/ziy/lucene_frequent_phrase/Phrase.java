package edu.cmu.cs.ziy.lucene_frequent_phrase;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

/**
 * @author <a href="mailto:ziy@cs.cmu.edu">Zi Yang</a>
 * created on 4/12/16
 */

public class Phrase {

  private ImmutableList<String> terms;

  private List<DocIdPositionOffsets> ipos;

  Phrase(ImmutableList<String> terms, List<DocIdPositionOffsets> ipos) {
    this.terms = terms;
    this.ipos = ipos;
  }

  Phrase append(String nextTerm, List<DocIdPositionOffsets> nextIpos) {
    ImmutableList<String> aTerms = new ImmutableList.Builder<String>().addAll(terms).add(nextTerm)
            .build();
    Collections.sort(ipos);
    List<DocIdPositionOffsets> aIpos = new ArrayList<>();
    for (DocIdPositionOffsets nextIpo : nextIpos) {
      // index cannot be 0 or -1
      int index = Collections.binarySearch(ipos, nextIpo);
      if (index > 0) {
        index = index - 1;
      } else {
        index = - index - 2;
      }
      aIpos.add(DocIdPositionOffsets.merge(ipos.get(index), nextIpo));
    }
    return new Phrase(aTerms, aIpos);
  }

  public List<String> getTerms() {
    return terms;
  }

  public String getTermsString(){
    return String.join(" ", terms);
  }

  public List<DocIdPositionOffsets> getDocIdPositionOffsets() {
    return ipos;
  }

  public int getPhraseFrequency() {
    return ipos.size();
  }

  @Override
  public String toString() {
    return String.join(" ", terms) + " (" + ipos.size() + "): " +
            ipos.stream().map(DocIdPositionOffsets::toString).collect(joining(", "));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Phrase phrase = (Phrase) o;
    return Objects.equals(terms, phrase.terms);
  }

  @Override
  public int hashCode() {
    return Objects.hash(terms);
  }

}

