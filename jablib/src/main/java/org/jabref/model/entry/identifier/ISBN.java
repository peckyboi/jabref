package org.jabref.model.entry.identifier;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jabref.model.entry.field.Field;
import org.jabref.model.entry.field.StandardField;

import org.jspecify.annotations.NonNull;

public class ISBN implements Identifier {

    private static final Pattern ISBN_PATTERN = Pattern.compile("^(\\d{9}[\\dxX]|\\d{13})$");

    private final String isbnString;

    public ISBN(@NonNull String isbnString) {
        this.isbnString = isbnString.trim().replace("-", "");
    }

    public static Optional<ISBN> parse(String input) {
        ISBN isbn = new ISBN(input);
        if (isbn.isValid()) {
            return Optional.of(isbn);
        } else {
            return Optional.empty();
        }
    }

    public boolean isValidFormat() {
        Matcher isbnMatcher = ISBN_PATTERN.matcher(isbnString);
        return isbnMatcher.matches();
    }

    public boolean isValidChecksum() {
        boolean valid;
        if (isbnString.length() == 10) {
            valid = isbn10check();
        } else {
            // length is either 10 or 13 based on regexp so will be 13 here
            valid = isbn13check();
        }
        return valid;
    }

    public boolean isIsbn10() {
        return isbn10check();
    }

    public boolean isIsbn13() {
        return isbn13check();
    }

    // Check that the control digit is correct, see e.g. https://en.wikipedia.org/wiki/International_Standard_Book_Number#Check_digits
    private boolean isbn10check() {
        if (isbnString.length() != 10) {
            return false;
        }

        int sum = 0;
        for (int pos = 0; pos <= 8; pos++) {
            sum += (isbnString.charAt(pos) - '0') * (10 - pos);
        }
        char control = isbnString.charAt(9);
        if ((control == 'x') || (control == 'X')) {
            control = '9' + 1;
        }
        sum += control - '0';
        return (sum % 11) == 0;
    }

    // Check that the control digit is correct, see e.g. https://en.wikipedia.org/wiki/International_Standard_Book_Number#Check_digits
    private boolean isbn13check() {
        if (isbnString.length() != 13) {
            return false;
        }

        int sum = 0;
        for (int pos = 0; pos <= 12; pos++) {
            sum += (isbnString.charAt(pos) - '0') * ((pos % 2) == 0 ? 1 : 3);
        }
        return (sum % 10) == 0;
    }

    public boolean isValid() {
        return isValidFormat() && isValidChecksum();
    }

    /// Returns the ISBN-13 form of this ISBN, so that an ISBN-10 and its ISBN-13 can be compared.
    /// An ISBN-13 is returned as is. Empty if this ISBN is not valid.
    public Optional<ISBN> asIsbn13() {
        if (!isValid()) {
            return Optional.empty();
        }
        if (isbnString.length() == 13) {
            return Optional.of(this);
        }
        String first12Digits = "978" + isbnString.substring(0, 9);
        return Optional.of(new ISBN(first12Digits + isbn13CheckDigit(first12Digits)));
    }

    /// Computes the check digit of an ISBN-13 from its first 12 digits.
    /// The digits are weighted 1, 3, 1, 3, ... and summed. The check digit brings the sum up to a multiple of 10.
    private static int isbn13CheckDigit(String first12Digits) {
        int sum = 0;
        for (int pos = 0; pos < 12; pos++) {
            sum += (first12Digits.charAt(pos) - '0') * ((pos % 2) == 0 ? 1 : 3);
        }
        return (10 - (sum % 10)) % 10;
    }

    @Override
    public Field getDefaultField() {
        return StandardField.ISBN;
    }

    @Override
    public String asString() {
        return isbnString;
    }

    @Override
    public Optional<URI> getExternalURI() {
        try {
            return Optional.of(new URI("https://www.worldcat.org/isbn/" + isbnString));
        } catch (URISyntaxException _) {
            return Optional.empty();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if ((o == null) || (getClass() != o.getClass())) {
            return false;
        }
        ISBN other = (ISBN) o;
        return isbnString.equalsIgnoreCase(other.isbnString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbnString.toLowerCase(Locale.ENGLISH));
    }
}
