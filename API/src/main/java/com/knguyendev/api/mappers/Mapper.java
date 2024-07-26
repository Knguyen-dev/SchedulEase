package com.knguyendev.api.mappers;

public interface Mapper<A,B> {

    /**
     * Maps class A to class B. Use this to receive an entity, and return a DTO.
     * @param a An entity object.
     * @return A DTO object
     */
    B mapTo(A a);

    /**
     * Maps class B to class A. Use this to receive a DTO, and return a Entity.
     * @param b A DTO object.
     * @return An entity object
     */
    A mapFrom(B b);

}
