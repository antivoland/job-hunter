package antivoland.jh.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class TextFileStorageTest {
    @Test
    void test(@TempDir Path dir) {
        var storage = new TextFileStorage(dir.resolve("storage"), "txt");

        assertThat(storage.count()).isEqualTo(0);
        assertThat(storage.listIds()).isEmpty();
        assertThat(storage.list()).isEmpty();
        assertThat(storage.exists("see-no-evil")).isFalse();
        assertThat(storage.exists("hear-no-evil")).isFalse();
        assertThat(storage.exists("speak-no-evil")).isFalse();
        assertThat(storage.load("see-no-evil")).isEqualTo(null);
        assertThat(storage.load("hear-no-evil")).isEqualTo(null);
        assertThat(storage.load("speak-no-evil")).isEqualTo(null);

        assertThat(Files.exists(dir.resolve("storage"))).isFalse();
        assertThat(Files.exists(dir.resolve("storage").resolve("see-no-evil.txt"))).isFalse();
        assertThat(Files.exists(dir.resolve("storage").resolve("hear-no-evil.txt"))).isFalse();
        assertThat(Files.exists(dir.resolve("storage").resolve("speak-no-evil.txt"))).isFalse();

        storage.save("see-no-evil", "🙈");
        storage.save("hear-no-evil", "🙉");
        storage.save("speak-no-evil", "🙊");

        assertThat(Files.exists(dir.resolve("storage"))).isTrue();
        assertThat(Files.exists(dir.resolve("storage").resolve("see-no-evil.txt"))).isTrue();
        assertThat(Files.exists(dir.resolve("storage").resolve("hear-no-evil.txt"))).isTrue();
        assertThat(Files.exists(dir.resolve("storage").resolve("speak-no-evil.txt"))).isTrue();

        assertThat(storage.count()).isEqualTo(3);
        assertThat(storage.listIds()).containsOnly("see-no-evil", "hear-no-evil", "speak-no-evil");
        assertThat(storage.list()).containsOnly("🙈", "🙉", "🙊");
        assertThat(storage.exists("see-no-evil")).isTrue();
        assertThat(storage.exists("hear-no-evil")).isTrue();
        assertThat(storage.exists("speak-no-evil")).isTrue();
        assertThat(storage.load("see-no-evil")).isEqualTo("🙈");
        assertThat(storage.load("hear-no-evil")).isEqualTo("🙉");
        assertThat(storage.load("speak-no-evil")).isEqualTo("🙊");
    }
}