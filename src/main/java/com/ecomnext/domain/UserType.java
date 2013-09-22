package com.ecomnext.domain;

public enum UserType {
    EMPLOYEE(0),
    EXTERNAL(1);

    private final int value;

    private UserType(int value) {
        this.value = value;
    }

    /**
     * Get the integer value of this enum value.
     */
    public int getValue() {
        return value;
    }

    /**
     * Find a the enum type by its integer value.
     * @return null if the value is not found.
     */
    public static UserType findByValue(int value) {
        switch (value) {
            case 1:
                return EMPLOYEE;
            case 2:
                return EXTERNAL;
            default:
                return null;
        }
    }

    /**
     *
     * @return the Finagle implementation.
     */
    public TUserType toTObject() {
        switch (value) {
            case 0:
                return TUserType$.MODULE$.apply(1);
            case 1:
                return TUserType$.MODULE$.apply(2);
            default:
                return null;
        }
    }
}
