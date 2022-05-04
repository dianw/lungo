package org.enkrip.lungo.core.security;

import javax.annotation.Nullable;

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
