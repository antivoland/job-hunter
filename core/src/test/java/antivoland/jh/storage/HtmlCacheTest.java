package antivoland.jh.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class HtmlCacheTest {
    @Test
    void test(@TempDir Path dir) {
        var cache = new HtmlCache(dir.resolve("cache"));

        assertThat(cache.count()).isEqualTo(0);
        assertThat(cache.listIds()).isEmpty();
        assertThat(cache.list()).isEmpty();
        assertThat(cache.exists("see-no-evil")).isFalse();
        assertThat(cache.exists("hear-no-evil")).isFalse();
        assertThat(cache.exists("speak-no-evil")).isFalse();
        assertThat(cache.load("see-no-evil")).isEqualTo(null);
        assertThat(cache.load("hear-no-evil")).isEqualTo(null);
        assertThat(cache.load("speak-no-evil")).isEqualTo(null);

        assertThat(Files.exists(dir.resolve("cache"))).isFalse();
        assertThat(Files.exists(dir.resolve("cache").resolve("see-no-evil.tar.gz"))).isFalse();
        assertThat(Files.exists(dir.resolve("cache").resolve("hear-no-evil.tar.gz"))).isFalse();
        assertThat(Files.exists(dir.resolve("cache").resolve("speak-no-evil.tar.gz"))).isFalse();

        cache.save("see-no-evil", "<🙈>");
        cache.save("hear-no-evil", "<🙉>");
        cache.save("speak-no-evil", "<🙊>");

        assertThat(Files.exists(dir.resolve("cache"))).isTrue();
        assertThat(Files.exists(dir.resolve("cache").resolve("see-no-evil.tar.gz"))).isTrue();
        assertThat(Files.exists(dir.resolve("cache").resolve("hear-no-evil.tar.gz"))).isTrue();
        assertThat(Files.exists(dir.resolve("cache").resolve("speak-no-evil.tar.gz"))).isTrue();

        assertThat(cache.count()).isEqualTo(3);
        assertThat(cache.listIds()).containsOnly("see-no-evil", "hear-no-evil", "speak-no-evil");
        assertThat(cache.list()).containsOnly("<🙈>", "<🙉>", "<🙊>");
        assertThat(cache.exists("see-no-evil")).isTrue();
        assertThat(cache.exists("hear-no-evil")).isTrue();
        assertThat(cache.exists("speak-no-evil")).isTrue();
        assertThat(cache.load("see-no-evil")).isEqualTo("<🙈>");
        assertThat(cache.load("hear-no-evil")).isEqualTo("<🙉>");
        assertThat(cache.load("speak-no-evil")).isEqualTo("<🙊>");
    }
}