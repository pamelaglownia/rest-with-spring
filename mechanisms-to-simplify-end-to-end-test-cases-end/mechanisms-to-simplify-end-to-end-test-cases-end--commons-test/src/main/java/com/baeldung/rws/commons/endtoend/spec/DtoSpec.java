package com.baeldung.rws.commons.endtoend.spec;

import java.util.List;

public interface DtoSpec<T> {

    /**
     * 
     * Method to check if the spec matches a particular DTO.
     * 
     * @param expectedDto DTO to evaluate against the Spec.
     * @return list of mismatching fields
     */
    public List<String> matches(T expectedDto);

}
