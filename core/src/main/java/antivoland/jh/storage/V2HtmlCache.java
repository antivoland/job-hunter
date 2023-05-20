package antivoland.jh.storage;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.String.format;

@Deprecated
public class V2HtmlCache extends V2FileStorage {
    private static final String ARCHIVE_EXTENSION = "tar.gz";
    private static final String ENTRY_EXTENSION = "html";

    private final Path directory;

    public V2HtmlCache(Path directory) {
        this.directory = directory;
    }

    public void save(String fileId, String data) {
        var archive = provideDirectory(directory).resolve(fileName(fileId, ARCHIVE_EXTENSION));
        try (var fo = Files.newOutputStream(archive);
             var gzo = new GzipCompressorOutputStream(fo);
             var o = new TarArchiveOutputStream(gzo)) {

            var entry = new TarArchiveEntry(fileName("entry", ENTRY_EXTENSION));
            entry.setSize(data.getBytes().length);
            o.putArchiveEntry(entry);
            o.write(data.getBytes());
            o.closeArchiveEntry();
            o.finish();
        } catch (IOException e) {
            throw new StorageException(format("Failed to compress file %s", fileName(fileId, ENTRY_EXTENSION)), e);
        }
    }

    public String load(String fileId) {
        var archive = provideDirectory(directory).resolve(fileName(fileId, ARCHIVE_EXTENSION));
        if (!Files.exists(archive)) return null;
        try (var fi = Files.newInputStream(archive);
             var bi = new BufferedInputStream(fi);
             var gzi = new GzipCompressorInputStream(bi);
             var i = new TarArchiveInputStream(gzi)) {

            i.getNextEntry();
            return new String(IOUtils.toByteArray(i));
        } catch (IOException e) {
            throw new StorageException(format("Failed to decompress file %s", fileName(fileId, ENTRY_EXTENSION)), e);
        }
    }
}