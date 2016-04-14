package edu.cmu.cs.ziy.lucene_frequent_phrase;

import java.util.Objects;

/**
 * @author <a href="mailto:ziy@cs.cmu.edu">Zi Yang</a>
 * created on 4/12/16
 */
class DocIdPosition {

  int docId;

  int position;

  DocIdPosition(int docId, int position) {
    this.docId = docId;
    this.position = position;
  }

  DocIdPosition next() {
    return new DocIdPosition(docId, position + 1);
  }

  public int getDocId() {
    return docId;
  }

  public int getPosition() {
    return position;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    DocIdPosition that = (DocIdPosition) o;
    return docId == that.docId &&
            position == that.position;
  }

  @Override
  public int hashCode() {
    return Objects.hash(docId, position);
  }

}
