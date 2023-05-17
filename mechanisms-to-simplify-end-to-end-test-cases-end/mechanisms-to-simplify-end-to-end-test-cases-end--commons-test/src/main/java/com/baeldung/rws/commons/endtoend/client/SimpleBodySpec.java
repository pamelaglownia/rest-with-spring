package com.baeldung.rws.commons.endtoend.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.hamcrest.Matcher;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient.BodySpec;

import com.baeldung.rws.commons.endtoend.spec.DtoSpec;

public class SimpleBodySpec<B, S extends BodySpec<B, S>> implements BodySpec<B, S> {

    private final BodySpec<B, S> wrappedBodySpec;

    public SimpleBodySpec(BodySpec<B, S> wrappedBodySpec) {
        super();
        this.wrappedBodySpec = wrappedBodySpec;
    }

    @Override
    public <T extends S> T isEqualTo(B expected) {
        return wrappedBodySpec.isEqualTo(expected);
    }

    @Override
    public <T extends S> T value(Matcher<? super B> matcher) {
        return wrappedBodySpec.value(matcher);
    }

    @Override
    public <T extends S, R> T value(Function<B, R> bodyMapper, Matcher<? super R> matcher) {
        return wrappedBodySpec.value(bodyMapper, matcher);
    }

    @Override
    public <T extends S> T value(Consumer<B> consumer) {
        return wrappedBodySpec.value(consumer);
    }

    @Override
    public <T extends S> T consumeWith(Consumer<EntityExchangeResult<B>> consumer) {
        return wrappedBodySpec.consumeWith(consumer);
    }

    @Override
    public EntityExchangeResult<B> returnResult() {
        return wrappedBodySpec.returnResult();
    }

    public <T extends S> T valueMatches(DtoSpec<B> spec) {
        return wrappedBodySpec.value(body -> {
            List<String> mismatchingFields = spec.matches(body);
            assertThat(mismatchingFields).as("%s doesn't match %s Spec. Mismatching fields: %s", body.getClass()
                .getSimpleName(),
                spec.getClass()
                    .getSimpleName(),
                mismatchingFields)
                .isEmpty();
        });
    }
}
