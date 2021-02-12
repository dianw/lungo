/**
 * DANA Indonesia
 * Copyright (c) 2017‐2021 All Rights Reserved.
 */
package xyz.mainapi.dashboard.core.data;

import org.immutables.value.Value;

/**
 * @author Dian Aditya
 * @version $Id: MinioConfigProperties.java, v 0.1 2021‐02‐11 00.08 Dian Aditya Exp $$
 */
@Value.Modifiable
public interface MinioConfigProperties {
    String getEndpoint();

    String getAccessKey();

    String getSecretKey();
}
