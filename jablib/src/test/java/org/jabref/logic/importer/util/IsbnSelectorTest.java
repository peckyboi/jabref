package org.jabref.logic.importer.util;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.jabref.model.entry.identifier.ISBN;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IsbnSelectorTest {

    private final IsbnSelector selector = new IsbnSelector();

    static Stream<Arguments> select() {
        return Stream.of(
                Arguments.of(List.of(), Optional.empty()),
                Arguments.of(List.of(new ISBN("9780596520687")), Optional.of(new ISBN("9780596520687"))),
                // A single ISBN-10 is returned as ISBN-13
                Arguments.of(List.of(new ISBN("0596520689")), Optional.of(new ISBN("9780596520687"))),
                // ISBN-13 and ISBN-10 of the same book, in both orders
                Arguments.of(List.of(new ISBN("9780596520687"), new ISBN("0596520689")), Optional.of(new ISBN("9780596520687"))),
                Arguments.of(List.of(new ISBN("0596520689"), new ISBN("9780596520687")), Optional.of(new ISBN("9780596520687"))),
                Arguments.of(List.of(new ISBN("9780596520687"), new ISBN("9780596520687")), Optional.of(new ISBN("9780596520687"))),
                Arguments.of(List.of(new ISBN("9798602405453")), Optional.of(new ISBN("9798602405453"))),
                // Different books
                Arguments.of(List.of(new ISBN("9780596520687"), new ISBN("9780134685991")), Optional.empty()),
                Arguments.of(List.of(new ISBN("9780596520687"), new ISBN("9780134685991"), new ISBN("0596520689")), Optional.empty()),
                Arguments.of(List.of(new ISBN("0596520689"), new ISBN("0134685997")), Optional.empty())
        );
    }

    @ParameterizedTest
    @MethodSource
    void select(List<ISBN> candidates, Optional<ISBN> expected) {
        assertEquals(expected, selector.select(candidates));
    }
}
