# lucene-frequent-phrase
Frequent Phrase Extractor and Phrase Quality Scorers from Lucene Index or Text Collection

- __Phrase candidate generation__ The phrase candidates are generated from a Lucene index (offset should be indexed), or a text collection (indexed by Lucene's [RAMDirectory](https://lucene.apache.org/core/5_5_0/core/index.html?org/apache/lucene/store/RAMDirectory.html]) internally)

- __Phrase quality scoring__ P(hrase)F-IDF and [CValue](http://personalpages.manchester.ac.uk/staff/sophia.ananiadou/ijodl2000.pdf) based scorers are implemented, i.e. two unsuperivsed baselines of [SegPhrase](http://web.engr.illinois.edu/~shang7/papers/SegPhrase.pdf). A reference Lucene index can be plugged to provide DF statistics.