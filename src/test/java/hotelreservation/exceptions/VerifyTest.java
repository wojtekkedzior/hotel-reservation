package hotelreservation.exceptions;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

public class VerifyTest {
	
	// https://stackoverflow.com/questions/41265266/how-to-solve-inaccessibleobjectexception-unable-to-make-member-accessible-m
	// add --add-opens java.base/java.lang=ALL-UNNAMED to the VM arguments to make this test pass
	// might consider redoing this

    @Test
    public void verifyMissingOrInvalidArgument() {
        EqualsVerifier.forClass(MissingOrInvalidArgumentException.class).suppress(Warning.STRICT_INHERITANCE).suppress(Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    public void verifyNotDeletedException() {
        EqualsVerifier.forClass(NotDeletedException.class).suppress(Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    public void verifyNotFoundException() {
        EqualsVerifier.forClass(NotFoundException.class).suppress(Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }

    @Test
    public void verifyPaymentNotCreatedException() {
        EqualsVerifier.forClass(PaymentNotCreatedException.class).suppress(Warning.ALL_FIELDS_SHOULD_BE_USED).verify();
    }
}
