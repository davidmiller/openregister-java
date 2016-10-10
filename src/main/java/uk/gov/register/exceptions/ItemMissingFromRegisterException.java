package uk.gov.register.exceptions;

public class ItemMissingFromRegisterException extends RuntimeException {
    public final String hash;

    public ItemMissingFromRegisterException(String hash) {
        this.hash = hash;
    }
}