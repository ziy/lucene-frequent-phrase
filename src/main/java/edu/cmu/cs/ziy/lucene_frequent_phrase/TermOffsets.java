package edu.cmu.cs.ziy.lucene_frequent_phrase;

import java.util.Objects;

/**
 * @author <a href="mailto:ziy@cs.cmu.edu">Zi Yang</a>
 * created on 4/12/16
 */
class TermOffsets {

  private String term;

  private int begin;

  private int end;

  TermOffsets(String term, int begin, int end) {
    this.term = term;
    this.begin = begin;
    this.end = end;
  }

  String getTerm() {
    return term;
  }

  int getBegin() {
    return begin;
  }

  int getEnd() {
    return end;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    TermOffsets that = (TermOffsets) o;
    return begin == that.begin &&
            end == that.end &&
            Objects.equals(term, that.term);
  }

  @Override
  public int hashCode() {
    return Objects.hash(term, begin, end);
  }

}
