package uk.gov.register.util;

import org.skife.jdbi.v2.Handle;
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
    public boolean execute(Handle handle, Iterable<RegisterCommand> commands) {
        try {
            // There's only one command here, so just get it
            RegisterCommand command;

            String rootHash = commandHelper.getRootHash(command);
            RegisterProof proof = register.getRegisterProof();

            return proof.getRootHash() != rootHash;
        } catch(NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String getCommandName() {
        return "assert-root-hash";
    }
}
