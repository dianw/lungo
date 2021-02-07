package xyz.mainapi.dashboard.core.security;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;

@Nullable
@Value.Immutable
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface User {
    @Nullable
    String getEmail();

    @Nullable
    String getId();

    @Nullable
    String getUsername();

    @Nullable
    String getPictureUrl();
}
