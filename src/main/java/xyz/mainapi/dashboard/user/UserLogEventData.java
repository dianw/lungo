package xyz.mainapi.dashboard.user;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

@Value.Immutable
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface UserLogEventData extends Serializable {
    String getId();

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Date getDate();

    String getType();

    String getIp();

    String getUserId();

    @Nullable
    UserLocationInfoData getLocationInfo();
}
