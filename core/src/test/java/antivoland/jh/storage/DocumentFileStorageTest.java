package antivoland.jh.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentFileStorageTest {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Monkey {
        String val;
    }

    @Test
    void test(@TempDir Path dir) {
        var storage = new DocumentFileStorage<>(dir.resolve("docs"), Monkey.class);

        assertThat(storage.count()).isEqualTo(0);
        assertThat(storage.listIds()).isEmpty();
        assertThat(storage.list()).isEmpty();
        assertThat(storage.exists("see-no-evil")).isFalse();
        assertThat(storage.exists("hear-no-evil")).isFalse();
        assertThat(storage.exists("speak-no-evil")).isFalse();
        assertThat(storage.load("see-no-evil")).isEqualTo(null);
        assertThat(storage.load("hear-no-evil")).isEqualTo(null);
        assertThat(storage.load("speak-no-evil")).isEqualTo(null);

        assertThat(Files.exists(dir.resolve("docs"))).isFalse();
        assertThat(Files.exists(dir.resolve("docs").resolve("see-no-evil.json"))).isFalse();
        assertThat(Files.exists(dir.resolve("docs").resolve("hear-no-evil.json"))).isFalse();
        assertThat(Files.exists(dir.resolve("docs").resolve("speak-no-evil.json"))).isFalse();

        storage.save("see-no-evil", new Monkey("🙈"));
        storage.save("hear-no-evil", new Monkey("🙉"));
        storage.save("speak-no-evil", new Monkey("🙊"));

        assertThat(Files.exists(dir.resolve("docs"))).isTrue();
        assertThat(Files.exists(dir.resolve("docs").resolve("see-no-evil.json"))).isTrue();
        assertThat(Files.exists(dir.resolve("docs").resolve("hear-no-evil.json"))).isTrue();
        assertThat(Files.exists(dir.resolve("docs").resolve("speak-no-evil.json"))).isTrue();

        assertThat(storage.count()).isEqualTo(3);
        assertThat(storage.listIds()).containsOnly("see-no-evil", "hear-no-evil", "speak-no-evil");
        assertThat(storage.list()).containsOnly(new Monkey("🙈"), new Monkey("🙉"), new Monkey("🙊"));
        assertThat(storage.exists("see-no-evil")).isTrue();
        assertThat(storage.exists("hear-no-evil")).isTrue();
        assertThat(storage.exists("speak-no-evil")).isTrue();
        assertThat(storage.load("see-no-evil")).isEqualTo(new Monkey("🙈"));
        assertThat(storage.load("hear-no-evil")).isEqualTo(new Monkey("🙉"));
        assertThat(storage.load("speak-no-evil")).isEqualTo(new Monkey("🙊"));
    }
}