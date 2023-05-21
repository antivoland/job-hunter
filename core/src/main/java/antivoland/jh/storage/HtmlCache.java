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

public class HtmlCache extends FileStorage<String> {
    private static final String ARCHIVE_EXTENSION = "tar.gz";
    private static final String ENTRY_EXTENSION = "html";

    public HtmlCache(Path directory) {
        super(directory, ARCHIVE_EXTENSION);
    }

    @Override
    public String load(String id) {
        var file = file(id);
        if (!exists(file)) return null;
        try (var fi = Files.newInputStream(file);
             var bi = new BufferedInputStream(fi);
             var gzi = new GzipCompressorInputStream(bi);
             var i = new TarArchiveInputStream(gzi)) {

            i.getNextEntry();
            return new String(IOUtils.toByteArray(i));
        } catch (IOException e) {
            throw new StorageException(format("Failed to decompress file %s", file), e);
        }
    }

    @Override
    public void save(String id, String data) {
        var file = file(id, true);
        try (var fo = Files.newOutputStream(file);
             var gzo = new GzipCompressorOutputStream(fo);
             var o = new TarArchiveOutputStream(gzo)) {

            var entry = new TarArchiveEntry(fileName("entry", ENTRY_EXTENSION));
            entry.setSize(data.getBytes().length);
            o.putArchiveEntry(entry);
            o.write(data.getBytes());
            o.closeArchiveEntry();
            o.finish();
        } catch (IOException e) {
            throw new StorageException(format("Failed to compress file %s", file), e);
        }
    }
}