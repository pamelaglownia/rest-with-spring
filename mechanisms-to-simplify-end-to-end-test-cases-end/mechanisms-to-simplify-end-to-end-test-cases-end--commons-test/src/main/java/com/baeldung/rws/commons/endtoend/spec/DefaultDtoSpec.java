package com.baeldung.rws.commons.endtoend.spec;

import java.util.List;

public abstract class DefaultDtoSpec<T> implements DtoSpec<T> {

    public abstract List<DtoFieldSpec<T, ?>> defineSpecs();

    @Override
    public List<String> matches(T expectedDto) {
        return defineSpecs().stream()
            .filter(fieldSpec -> !fieldSpec.matches(expectedDto))
            .map(fieldSpec -> fieldSpec.getFieldName())
            .toList();
    }
}
