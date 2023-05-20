package antivoland.jh;

import antivoland.jh.linkedin.LinkedinOffer;
import antivoland.jh.storage.JsonFileStorage;
import antivoland.jh.storage.TextFileStorage;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Scopes;
import lombok.SneakyThrows;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.text.documentiterator.FileLabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.common.primitives.Pair;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static java.util.Comparator.comparing;

public class Analyzer {
    private static final Logger LOG = LoggerFactory.getLogger(Analyzer.class);

    public static void main(String[] args) {
        var injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Analyzer.class).in(Scopes.SINGLETON);
            }
        });

        injector.getInstance(Analyzer.class).run();
    }

    final JsonFileStorage<LinkedinOffer, String> storage = new JsonFileStorage<>(LinkedinOffer.class);

    @SneakyThrows
    void run() {
        makeParagraphVectors();
        checkUnlabeledData();
//        storage.list().limit(1).forEach(offer -> {
//            var in = new ByteArrayInputStream(offer.getDescription().getBytes());
//            SentenceIterator iter = new BasicLineIterator(in);
//            TokenizerFactory t = new DefaultTokenizerFactory();
//
//            t.setTokenPreProcessor(new CommonPreprocessor());
//
////            while (iter.hasNext()) {
////                var s = iter.nextSentence();
////                System.out.println(s);
////            }
//
////            LOG.info("Building model....");
////            Word2Vec vec = new Word2Vec.Builder().minWordFrequency(1).iterations(1).layerSize(100).seed(42).windowSize(5).iterate(iter).tokenizerFactory(t).build();
////
////            LOG.info("Fitting Word2Vec model....");
////            vec.fit();
////
////            LOG.info("Writing word vectors to text file....");
////
////            System.out.println(vec.vocab());
//        });
    }

    ParagraphVectors paragraphVectors;
    LabelAwareIterator iterator;
    TokenizerFactory tokenizerFactory;

    void makeParagraphVectors() throws Exception {
        var resource = Paths.get("nlp", "labeled");

        // build a iterator for our dataset
        iterator = new FileLabelAwareIterator.Builder().addSourceFolder(resource.toFile()).build();

        tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        // ParagraphVectors training configuration
        paragraphVectors = new ParagraphVectors.Builder().learningRate(0.025).minLearningRate(0.001).batchSize(1000).epochs(20).iterate(iterator).trainWordVectors(true).tokenizerFactory(tokenizerFactory).build();

        // Start model training
        paragraphVectors.fit();
    }

    void checkUnlabeledData() throws IOException {
      /*
      At this point we assume that we have model built and we can check
      which categories our unlabeled document falls into.
      So we'll start loading our unlabeled documents and checking them
     */
        var unClassifiedResource = Paths.get("nlp", "unlabeled");
        FileLabelAwareIterator unClassifiedIterator = new FileLabelAwareIterator.Builder().addSourceFolder(unClassifiedResource.toFile()).build();

     /*
      Now we'll iterate over unlabeled data, and check which label it could be assigned to
      Please note: for many domains it's normal to have 1 document fall into few labels at once,
      with different "weight" for each.
     */
        MeansBuilder meansBuilder = new MeansBuilder((InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable(), tokenizerFactory);
        LabelSeeker seeker = new LabelSeeker(iterator.getLabelsSource().getLabels(), (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable());

        while (unClassifiedIterator.hasNextDocument()) {
            try {
                LabelledDocument document = unClassifiedIterator.nextDocument();
                INDArray documentAsCentroid = meansBuilder.documentAsVector(document);
                List<Pair<String, Double>> scores = seeker.getScores(documentAsCentroid);

                 /*
                  please note, document.getLabel() is used just to show which document we're looking at now,
                  as a substitute for printing out the whole document name.
                  So, labels on these two documents are used like titles,
                  just to visualize our classification done properly
                 */
                LOG.info("Document '" + document.getLabels() + "' falls into the following categories: ");
                for (Pair<String, Double> score : scores) {
                    LOG.info("        " + score.getFirst() + ": " + score.getSecond());
                }

                var maxScore = scores.stream().max(comparing(Pair::getSecond)).orElseThrow();
                var jsonStorage = new JsonFileStorage<LinkedinOffer, String>(LinkedinOffer.class);
                var txtStorage = new TextFileStorage<String>("txt", "nlp", "classified", maxScore.getFirst());
                var id = document.getLabels().stream().findFirst().orElseThrow();
                txtStorage.save(id, jsonStorage.load(id).getDescription());
            } catch (Exception e) {
                LOG.warn("", e);
            }
        }

    }
}