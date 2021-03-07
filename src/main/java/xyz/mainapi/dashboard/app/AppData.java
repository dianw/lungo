package xyz.mainapi.dashboard.app;

import java.io.Serializable;
import java.time.Instant;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = ImmutableAppData.Builder.class)
public interface AppData extends Serializable {
    Optional<Long> getId();

    Optional<Instant> createdDate();

    String getAppName();

    String getCompanyName();

    String getBusinessAddress();

    String getCountry();

    String getRepresentativeName();

    Optional<String> getWebsite();

    Optional<String> getCsNumber();

    Optional<String> getCsEmail();
}
