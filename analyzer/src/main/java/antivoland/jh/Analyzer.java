package antivoland.jh;

import antivoland.jh.model.Offer;
import antivoland.jh.storage.JsonFileStorage;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Scopes;
import lombok.SneakyThrows;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

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
        SentenceIterator iter = new BasicLineIterator(new ByteArrayInputStream("xxxx. \nyyyy".getBytes()));
        TokenizerFactory t = new DefaultTokenizerFactory();

        t.setTokenPreProcessor(new CommonPreprocessor());

        while (iter.hasNext()) {
            var s = iter.nextSentence();
            System.out.println(s);
        }
    }
}