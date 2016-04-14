package edu.cmu.cs.ziy.lucene_frequent_phrase;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

/**
 * @author <a href="mailto:ziy@cs.cmu.edu">Zi Yang</a>
 * created on 4/14/16
 */
public class PFIDFPhraseScorer implements PhraseScorer {

  private final int docCount;

  private final IndexSearcher searcher;

  private final String refField;

  public PFIDFPhraseScorer(Directory refIndex, String refField) throws IOException {
    DirectoryReader reader = DirectoryReader.open(refIndex);
    this.docCount = reader.getDocCount(refField);
    this.searcher = new IndexSearcher(reader);
    this.refField = refField;
  }

  public int getDocumentFrequency(Phrase phrase) throws IOException {
    PhraseQuery query = new PhraseQuery(refField, phrase.getTerms().toArray(new String[0]));
    TotalHitCountCollector collector = new TotalHitCountCollector();
    searcher.search(query, collector);
    return collector.getTotalHits();
  }

  public double getPFIDF(Phrase phrase) throws IOException {
    int pf = phrase.getPhraseFrequency();
    int idf = getDocumentFrequency(phrase);
    return (1.0 + Math.log(pf)) * Math.log(1.0 + docCount / (idf + 1.0));
  }

  @Override
  public Map<Phrase, Double> scorePhrase(Collection<Phrase> phrases) throws IOException {
    Map<Phrase, Double> phrase2score = new HashMap<>();
    for (Phrase phrase : phrases) {
      phrase2score.put(phrase, getPFIDF(phrase));
    }
    return phrase2score;
  }

}
