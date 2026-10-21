package orghub.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Template for CSV-backed storage. All file reading/writing lives here once;
 * subclasses only say how to turn a row into an object and back (inheritance).
 */
public abstract class CsvRepository<T> implements Repository<T> {

    private final Path file;
    private final List<String> warnings = new ArrayList<>();

    protected CsvRepository(Path file) {
        this.file = file;
    }

    /** Column names, written as the first line of the file. */
    protected abstract String[] header();

    /** Builds one object from the columns of a row (throw RuntimeException if the row is bad). */
    protected abstract T fromFields(List<String> fields);

    /** The columns for one object, in header order. */
    protected abstract String[] toFields(T item);

    /** Rows skipped during the most recent load, with the reason. */
    public List<String> getWarnings() {
        return warnings;
    }

    @Override
    public List<T> loadAll() throws IOException {
        warnings.clear();
        List<T> items = new ArrayList<>();
        if (!Files.exists(file)) {
            saveAll(items);            // first run: create the file with just a header
            return items;
        }
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) {           // line 0 is the header
            if (lines.get(i).isBlank()) continue;
            try {
                items.add(fromFields(CsvUtil.split(lines.get(i))));
            } catch (RuntimeException e) {
                warnings.add(file.getFileName() + " line " + (i + 1) + " skipped: " + e.getMessage());
            }
        }
        return items;
    }

    @Override
    public void saveAll(List<T> items) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(CsvUtil.join(header()));
        for (T item : items) {
            lines.add(CsvUtil.join(toFields(item)));
        }
        if (file.getParent() != null) Files.createDirectories(file.getParent());
        // Write to a temp file first so a crash mid-write cannot corrupt the real file.
        Path temp = file.resolveSibling(file.getFileName() + ".tmp");
        Files.write(temp, lines, StandardCharsets.UTF_8);
        Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
    }
}
