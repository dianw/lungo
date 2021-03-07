package xyz.mainapi.dashboard.core.data;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.springframework.beans.factory.DisposableBean;

import com.hazelcast.flakeidgen.FlakeIdGenerator;

public class FlakeIdentifierGenerator implements IdentifierGenerator, DisposableBean {
    private static FlakeIdGenerator flakeIdGenerator;

    public FlakeIdentifierGenerator() {
    }

    FlakeIdentifierGenerator(FlakeIdGenerator flakeIdGenerator) {
        FlakeIdentifierGenerator.flakeIdGenerator = flakeIdGenerator;
    }

    @Override
    public void destroy() {
        FlakeIdentifierGenerator.flakeIdGenerator = null;
    }

    @Override
    public Long generate(SharedSessionContractImplementor session, Object object) {
        return flakeIdGenerator.newId();
    }
}
