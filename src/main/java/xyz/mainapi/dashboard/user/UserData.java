package xyz.mainapi.dashboard.user;

import java.io.Serializable;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;

import xyz.mainapi.dashboard.core.security.User;

@Value.Immutable
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface UserData extends User, Serializable {

}