package edu.cmu.cs.ziy.lucene_frequent_phrase;

import java.util.Objects;

/**
 * @author <a href="mailto:ziy@cs.cmu.edu">Zi Yang</a>
 * created on 4/12/16
 */
public class DocIdPositionOffsets extends DocIdPosition implements Comparable<DocIdPositionOffsets> {

  private int begin;

  private int end;

  private DocIdPositionOffsets(int docId, int position, int begin, int end) {
    super(docId, position);
    this.begin = begin;
    this.end = end;
  }

  DocIdPositionOffsets(DocIdPosition ip, TermOffsets to) {
    super(ip.docId, ip.position);
    this.begin = to.getBegin();
    this.end = to.getEnd();
  }

  DocIdPosition getDocIdPosition() {
    return new DocIdPosition(docId, position);
  }

  public int getBegin() {
    return begin;
  }

  public int getEnd() {
    return end;
  }

  static DocIdPositionOffsets merge(DocIdPositionOffsets prefix, DocIdPositionOffsets next) {
    return new DocIdPositionOffsets(next.docId, next.position, prefix.begin, next.end);
  }

  @Override
  public String toString() {
    return docId + ":" + position + ":" + begin + "-" + end;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    DocIdPositionOffsets that = (DocIdPositionOffsets) o;
    return docId == that.docId &&
            position == that.position;
  }

  @Override
  public int hashCode() {
    return Objects.hash(docId, position);
  }

  @Override
  public int compareTo(DocIdPositionOffsets that) {
    if (this.docId < that.docId) {
      return -1;
    } else if (this.docId > that.docId) {
      return 1;
    }
    if (this.position < that.position) {
      return -1;
    } else if (this.position > that.position) {
      return 1;
    }
    if (this.begin < that.begin) {
      return -1;
    } else if (this.begin > that.begin) {
      return 1;
    }
    if (this.end < that.end) {
      return -1;
    } else if (this.end > that.end) {
      return 1;
    }
    return 0;
  }
}
