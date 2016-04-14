package edu.cmu.cs.ziy.lucene_frequent_phrase;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * @author <a href="mailto:ziy@cs.cmu.edu">Zi Yang</a>
 * created on 4/9/16
 */
public class FrequentPhraseExtractor {

  private int maxPhraseLength = 6;

  private int minFreq = 4;

  public FrequentPhraseExtractor(int maxPhraseLength, int minFreq) {
    this.maxPhraseLength = maxPhraseLength;
    this.minFreq = minFreq;
  }

  public Set<Phrase> analyzeTextCollection(List<String> texts, Analyzer analyzer)
          throws IOException {
    RAMDirectory index = new RAMDirectory();
    IndexWriter writer = new IndexWriter(index, new IndexWriterConfig(analyzer));
    FieldType offsetsType = new FieldType(TextField.TYPE_STORED);
    offsetsType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
    for (String text : texts) {
      Document doc = new Document();
      doc.add(new Field("text", text, offsetsType));
      writer.addDocument(doc);
    }
    writer.close();
    return analyzeLuceneIndex(index, "text");
  }

  public Set<Phrase> analyzeLuceneIndex(Directory index, String field) throws IOException {
    Set<Phrase> phrases = new HashSet<>();
    Map<DocIdPosition, TermOffsets> ip2to = new HashMap<>();
    LeafReader reader = SlowCompositeReaderWrapper.wrap(DirectoryReader.open(index));
    TermsEnum terms = reader.terms(field).iterator();
    PostingsEnum postings = null;
    while (terms.next() != null) {
      if (terms.docFreq() < minFreq) continue;
      String term = terms.term().utf8ToString();
      postings = terms.postings(postings, PostingsEnum.ALL);
      List<DocIdPositionOffsets> ipos = new ArrayList<>();
      while (postings.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
        int docId = postings.docID();
        for (int i = 0; i < postings.freq(); i++) {
          DocIdPosition ip = new DocIdPosition(docId, postings.nextPosition());
          TermOffsets to = new TermOffsets(term, postings.startOffset(), postings.endOffset());
          ip2to.put(ip, to);
          ipos.add(new DocIdPositionOffsets(ip, to));
        }
      }
      phrases.add(new Phrase(ImmutableList.of(term), ipos));
    }
    Sets.newHashSet(phrases).forEach(phrase -> detectFrequentPhrase(phrase, phrases, ip2to));
    return phrases;
  }

  private void detectFrequentPhrase(Phrase phrase, Set<Phrase> phrases,
          Map<DocIdPosition, TermOffsets> ip2to) {
    if (phrase.getTerms().size() >= maxPhraseLength) return;
    // collect all docId/positions for all following terms
    Map<String, List<DocIdPositionOffsets>> next2ipos = phrase.getDocIdPositionOffsets().stream()
            .map(DocIdPositionOffsets::getDocIdPosition).map(DocIdPosition::next)
            .filter(ip2to::containsKey).map(ip -> new DocIdPositionOffsets(ip, ip2to.get(ip)))
            .collect(groupingBy(ipo -> ip2to.get(ipo.getDocIdPosition()).getTerm()));
    // filter the terms and create phrases
    List<Phrase> cphrases = next2ipos.entrySet().stream()
            .filter(e -> e.getValue().size() >= minFreq)
            .map(e -> phrase.append(e.getKey(), e.getValue())).collect(toList());
    // add to the global phrase collection
    phrases.addAll(cphrases);
    // invoke for the appended phrases with length + 1
    cphrases.forEach(cphrase -> detectFrequentPhrase(cphrase, phrases, ip2to));
  }

}
