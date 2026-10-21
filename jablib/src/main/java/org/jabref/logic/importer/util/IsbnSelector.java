package org.jabref.logic.importer.util;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jabref.model.entry.identifier.ISBN;

import org.jspecify.annotations.NullMarked;

/// Selects the ISBN of a book from the ISBNs found on its bibliographic page.
/// An ISBN-10 and the ISBN-13 of the same book count as one identifier.
/// If no ISBN or more than one different identifier remains, none is selected.
@NullMarked
public class IsbnSelector {

    public Optional<ISBN> select(List<ISBN> candidates) {
        Set<ISBN> isbn13s = candidates.stream()
                                      .map(ISBN::asIsbn13)
                                      .flatMap(Optional::stream)
                                      .collect(Collectors.toSet());
        if (isbn13s.size() == 1) {
            return Optional.of(isbn13s.iterator().next());
        }
        return Optional.empty();
    }
}
