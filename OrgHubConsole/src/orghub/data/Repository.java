package orghub.data;

import java.io.IOException;
import java.util.List;

/**
 * Abstraction of "where data lives". Controllers depend on this interface only, so
 * the CSV files could later be swapped for a database without touching them.
 */
public interface Repository<T> {

    /** Reads every item from storage. */
    List<T> loadAll() throws IOException;

    /** Replaces the stored items with the given list. */
    void saveAll(List<T> items) throws IOException;
}
