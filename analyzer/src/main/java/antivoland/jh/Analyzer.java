package antivoland.jh;

import antivoland.jh.model.Offer;
import antivoland.jh.storage.JsonFileStorage;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Scopes;
import lombok.SneakyThrows;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.Collection;

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

    final JsonFileStorage<Offer, String> storage = new JsonFileStorage<>(Offer.class);

    @SneakyThrows
    void run() {
        storage.list().limit(1).forEach(offer -> {
            var in = new ByteArrayInputStream(offer.getDescription().getBytes());
            SentenceIterator iter = new BasicLineIterator(in);
            TokenizerFactory t = new DefaultTokenizerFactory();

            t.setTokenPreProcessor(new CommonPreprocessor());

//            while (iter.hasNext()) {
//                var s = iter.nextSentence();
//                System.out.println(s);
//            }

            LOG.info("Building model....");
            Word2Vec vec = new Word2Vec.Builder()
                    .minWordFrequency(1)
                    .iterations(1)
                    .layerSize(100)
                    .seed(42)
                    .windowSize(5)
                    .iterate(iter)
                    .tokenizerFactory(t)
                    .build();

            LOG.info("Fitting Word2Vec model....");
            vec.fit();

            LOG.info("Writing word vectors to text file....");

            System.out.println(vec.vocab());


//            LOG.info("Closest Words:");
//            Collection lst = vec.wordsNearestSum("software", 10);
//            LOG.info("10 Words closest to 'day': {}", lst);

        });
    }
}