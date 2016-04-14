import edu.cmu.cs.ziy.lucene_frequent_phrase.CValuePhraseScorer;
import edu.cmu.cs.ziy.lucene_frequent_phrase.FrequentPhraseExtractor;
import edu.cmu.cs.ziy.lucene_frequent_phrase.PFIDFPhraseScorer;
import edu.cmu.cs.ziy.lucene_frequent_phrase.Phrase;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:ziy@cs.cmu.edu">Zi Yang</a>
 * created on 4/12/16
 */
public class Example {

  public static void main(String[] args) throws Exception {
    // frequent phrase extraction
    InputStream is = Example.class.getClassLoader().getResourceAsStream("example.txt");
    BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    List<String> texts = br.lines().collect(Collectors.toList());
    FrequentPhraseExtractor fpe = new FrequentPhraseExtractor(6, texts.size() / 2);
    Set<Phrase> phrases = fpe.analyzeTextCollection(texts, new StopAnalyzer());
    phrases.forEach(System.out::println);
    System.out.println();
    // PF-IDF phrase ranking
    FSDirectory refIndex = FSDirectory.open(Paths.get("index/medline16n-lucene"));
    PFIDFPhraseScorer pps = new PFIDFPhraseScorer(refIndex, "abstractText");
    Map<Phrase, Double> phrase2pfidf = pps.scorePhrase(phrases);
    phrase2pfidf.entrySet().stream()
            .sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()))
            .forEach(e -> System.out.println(e.getKey().getTermsString() + " " + e.getValue()));
    System.out.println();
    // C-Value phrase ranking
    CValuePhraseScorer cps = new CValuePhraseScorer(CValuePhraseScorer.Type.AVERAGE);
    Map<Phrase, Double> phrase2cvalue = cps.scorePhrase(phrases);
    phrase2cvalue.entrySet().stream()
            .sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()))
            .forEach(e -> System.out.println(e.getKey().getTermsString() + " " + e.getValue()));
  }
  
}
