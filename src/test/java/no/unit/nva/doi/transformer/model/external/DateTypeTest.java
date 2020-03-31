package no.unit.nva.doi.transformer.model.external;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import no.unit.nva.doi.transformer.model.internal.external.DateType;
import org.junit.jupiter.api.Test;

public class DateTypeTest {

    @Test
    public void toStringShouldReturnTheNameOfTheDateType() {
        assertThat(DateType.ACCEPTED.toString(), is(equalTo("ACCEPTED")));
    }
}
