package uk.gov.register.util;

import uk.gov.register.core.Register;
import uk.gov.register.views.RegisterProof;

import javax.inject.Inject;
import java.security.NoSuchAlgorithmException;

public class AssertRootHashCommandHandler implements CommandHandler {
    private final Register register;
    private final CommandHelper commandHelper;

    @Inject
    public AssertRootHashCommandHandler(Register register) {
        this.register = register;
        this.commandHelper = new CommandHelper();
    }

    @Override
    public void execute(RegisterCommand command) {
        try {
            String rootHash = commandHelper.getRootHash(command);
            RegisterProof proof = register.getRegisterProof();

            if (proof.getRootHash() != rootHash) {
                throw new RuntimeException("Could not assert root hash");
            }
        } catch(NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getCommandName() {
        return "assert-root-hash";
    }
}
