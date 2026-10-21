package org.jabref.model.entry.identifier;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ISBNTest {

    @ParameterizedTest
    @ValueSource(strings = {"0-123456-47-9", "0-9752298-0-X"})
    void isValidFormat10Correct(String isbn) {
        assertTrue(new ISBN(isbn).isValidFormat());
    }

    @Test
    void isValidFormat10Incorrect() {
        assertFalse(new ISBN("0-12B456-47-9").isValidFormat());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0-123456-47-9", "0-9752298-0-X", "0-9752298-0-x"})
    void isValidChecksum10Correct(String isbn) {
        assertTrue(new ISBN(isbn).isValidChecksum());
    }

    @Test
    void isValidChecksum10Incorrect() {
        assertFalse(new ISBN("0-123456-47-8").isValidChecksum());
    }

    @Test
    void isValidFormat13Correct() {
        assertTrue(new ISBN("978-1-56619-909-4").isValidFormat());
    }

    @Test
    void isValidFormat13Incorrect() {
        assertFalse(new ISBN("978-1-56619-9O9-4 ").isValidFormat());
    }

    @Test
    void isValidChecksum13Correct() {
        assertTrue(new ISBN("978-1-56619-909-4 ").isValidChecksum());
    }

    @Test
    void isValidChecksum13Incorrect() {
        assertFalse(new ISBN("978-1-56619-909-5").isValidChecksum());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0-123456-47-9", "0-9752298-0-X"})
    void isIsbn10Correct(String isbn) {
        assertTrue(new ISBN(isbn).isIsbn10());
    }

    @Test
    void isIsbn10Incorrect() {
        assertFalse(new ISBN("978-1-56619-909-4").isIsbn10());
    }

    @Test
    void isIsbn13Correct() {
        assertTrue(new ISBN("978-1-56619-909-4").isIsbn13());
    }

    @Test
    void isIsbn13Incorrect() {
        assertFalse(new ISBN("0-123456-47-9").isIsbn13());
    }

    static Stream<Arguments> asIsbn13() {
        return Stream.of(
                Arguments.of("0-596-52068-9", "9780596520687"),
                Arguments.of("0-13-162959-X", "9780131629592"),
                Arguments.of("0-13-162959-x", "9780131629592"),
                Arguments.of("0-9752298-0-X", "9780975229804"),
                // New check digit is 0
                Arguments.of("0-306-40601-2", "9780306406010"),
                Arguments.of("978-1-56619-909-4", "9781566199094"),
                Arguments.of("979-8-6024-0545-3", "9798602405453")
        );
    }

    @ParameterizedTest
    @MethodSource
    void asIsbn13(String input, String expected) {
        assertEquals(Optional.of(new ISBN(expected)), new ISBN(input).asIsbn13());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0-596-52068-0", "978-0-596-52068-0", "0-12B456-47-9"})
    void asIsbn13ReturnsEmptyForInvalidIsbn(String input) {
        assertEquals(Optional.empty(), new ISBN(input).asIsbn13());
    }
}
