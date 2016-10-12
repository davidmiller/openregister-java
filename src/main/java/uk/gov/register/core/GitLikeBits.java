package uk.gov.register.core;

public interface GitLikeBits {
    void moveHeadTo(int entryNumber);
    void cleanRubbishWhichIsPastHead();
}
