package org.enkrip.lungo.core.data;

import org.immutables.value.Value;

@Value.Modifiable
public interface MinioConfigProperties {
    String getEndpoint();

    String getAccessKey();

    String getSecretKey();
}
