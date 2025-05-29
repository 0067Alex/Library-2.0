package es.ing.tomillo.library.model.fields;

import java.util.regex.Pattern;

public class PhoneNumber {
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^\\+?[0-9]{7,15}$"
    );

    private final String value;

    public PhoneNumber(String value) {
        if (value == null || !PHONE_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Invalid phone number: " + value);
        }
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PhoneNumber)) return false;
        PhoneNumber other = (PhoneNumber) obj;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
