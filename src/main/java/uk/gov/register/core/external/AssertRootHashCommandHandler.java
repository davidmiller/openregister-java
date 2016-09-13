package uk.gov.register.core.external;

import uk.gov.register.core.Entry;
import uk.gov.register.core.Item;
import uk.gov.register.core.Record;
import uk.gov.register.db.EntryMerkleLeafStore;
import uk.gov.register.db.EntryQueryDAO;
import uk.gov.register.db.EntryStore;
import uk.gov.register.service.VerifiableLogService;
import uk.gov.register.views.RegisterProof;
import uk.gov.verifiablelog.VerifiableLog;
import uk.gov.verifiablelog.store.memoization.InMemoryPowOfTwo;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class AssertRootHashCommandHandler extends CommandHandler<AssertRootHashCommand> {

    private final VerifiableLog verifiableLog;
    private EntryStore entryStore;

    public AssertRootHashCommandHandler(EntryStore entryStore) throws NoSuchAlgorithmException {
        super(AssertRootHashCommand.class);
        this.entryStore = entryStore;
        this.verifiableLog = createVerifiableLog(entryStore.entryDAO);
    }

    @Override
    void handle(AssertRootHashCommand command) {


        String expectedRootHash = command.getData().get("assert-root-hash");
        String actualRootHash = bytesToString(verifiableLog.currentRoot());
        if (!expectedRootHash.equals(actualRootHash)){
            throw new RuntimeException(String.format("Assert root hash - expected: %s, actual: %s", expectedRootHash, actualRootHash));
        }


    }

    private VerifiableLog createVerifiableLog(EntryQueryDAO entryDAO) throws NoSuchAlgorithmException {
        return new VerifiableLog(MessageDigest.getInstance("SHA-256"), new EntryMerkleLeafStore(entryDAO), new InMemoryPowOfTwo());
    }

    private String bytesToString(byte[] bytes) {
        return DatatypeConverter.printHexBinary(bytes).toLowerCase();
    }

}
