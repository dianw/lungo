package xyz.mainapi.dashboard.user;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;

@Value.Immutable
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface UserLocationInfoData extends Serializable {
    @Nullable
    String getCountryCode();

    @Nullable
    String getCountryName();

    @Nullable
    String getCityName();

    @Nullable
    String getTimeZone();

    @Nullable
    String getContinentCode();

    @Nullable
    Double getLatitude();

    @Nullable
    Double getLongitude();

    default String getCityCountryName() {
        return getCityName() + ", " + getCountryName();
    }
}
