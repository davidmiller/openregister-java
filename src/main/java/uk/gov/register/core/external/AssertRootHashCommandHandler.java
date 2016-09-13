package uk.gov.register.core.external;

import uk.gov.register.core.Entry;
import uk.gov.register.core.Item;
import uk.gov.register.core.Record;
import uk.gov.register.db.EntryStore;
import uk.gov.register.service.VerifiableLogService;
import uk.gov.register.views.RegisterProof;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class AssertRootHashCommandHandler extends CommandHandler<AssertRootHashCommand> {
    private VerifiableLogService verifiableLogService;

    public AssertRootHashCommandHandler(VerifiableLogService verifiableLogService) {
        super(AssertRootHashCommand.class);
        this.verifiableLogService = verifiableLogService;
    }

    @Override
    void handle(AssertRootHashCommand command) {

        try {
            RegisterProof registerProof = verifiableLogService.getRegisterProof();
            String expectedRootHash = command.getData().get("assert-root-hash");
            if (!expectedRootHash.equals(registerProof.getRootHash())){
                throw new RuntimeException(String.format("Assert root hash - expected: %s, actual: %s", expectedRootHash, registerProof.getRootHash()));
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }



    }
}
