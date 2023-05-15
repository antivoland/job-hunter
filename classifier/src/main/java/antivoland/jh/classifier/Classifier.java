package antivoland.jh.classifier;

import com.google.inject.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Classifier {
    private static final Logger LOG = LoggerFactory.getLogger(Classifier.class);

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bind(Classifier.class).in(Scopes.SINGLETON);
            }
        });

        injector.getInstance(Classifier.class).run();
    }

    void run() {
        LOG.info("Classifying...");
    }
}