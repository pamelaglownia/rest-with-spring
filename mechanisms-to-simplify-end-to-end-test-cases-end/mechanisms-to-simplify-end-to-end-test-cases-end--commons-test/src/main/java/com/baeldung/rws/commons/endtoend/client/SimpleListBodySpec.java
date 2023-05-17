package com.baeldung.rws.commons.endtoend.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.hamcrest.Matcher;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec;

import com.baeldung.rws.commons.endtoend.spec.DtoSpec;

@SuppressWarnings("unchecked")
public class SimpleListBodySpec<E> implements ListBodySpec<E> {

    private final ListBodySpec<E> wrappedListBodySpec;

    public SimpleListBodySpec(ListBodySpec<E> wrappedListBodySpec) {
        super();
        this.wrappedListBodySpec = wrappedListBodySpec;
    }

    @Override
    public <T extends ListBodySpec<E>> T isEqualTo(List<E> expected) {
        wrappedListBodySpec.isEqualTo(expected);
        return (T) this;
    }

    @Override
    public <T extends ListBodySpec<E>> T value(Matcher<? super List<E>> matcher) {
        wrappedListBodySpec.value(matcher);
        return (T) this;
    }

    @Override
    public <T extends ListBodySpec<E>, R> T value(Function<List<E>, R> bodyMapper, Matcher<? super R> matcher) {
        wrappedListBodySpec.value(bodyMapper, matcher);
        return (T) this;
    }

    @Override
    public <T extends ListBodySpec<E>> T value(Consumer<List<E>> consumer) {
        wrappedListBodySpec.value(consumer);
        return (T) this;
    }

    @Override
    public <T extends ListBodySpec<E>> T consumeWith(Consumer<EntityExchangeResult<List<E>>> consumer) {
        wrappedListBodySpec.consumeWith(consumer);
        return (T) this;
    }

    @Override
    public EntityExchangeResult<List<E>> returnResult() {
        return wrappedListBodySpec.returnResult();
    }

    @Override
    public ListBodySpec<E> hasSize(int size) {
        return wrappedListBodySpec.hasSize(size);
    }

    @Override
    public ListBodySpec<E> contains(E... elements) {
        return wrappedListBodySpec.contains(elements);
    }

    @Override
    public ListBodySpec<E> doesNotContain(E... elements) {
        return wrappedListBodySpec.doesNotContain(elements);
    }

    @SafeVarargs
    final public ListBodySpec<E> contains(DtoSpec<E>... specs) {
        List<DtoSpec<E>> specsList = List.of(specs);
        return wrappedListBodySpec.value(list -> {
            assertThat(specsList).as("Response list doesn't contain all specified elements")
                .allMatch(spec -> list.stream()
                    .anyMatch(dto -> spec.matches(dto)
                        .isEmpty()));
        });
    }
}
