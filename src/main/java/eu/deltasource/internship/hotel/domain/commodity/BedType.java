package eu.deltasource.internship.hotel.domain.commodity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Represents different types of beds
 */
public enum BedType {
    SINGLE(1), DOUBLE(2), KING_SIZE(2);

    @Getter
    private final int size;

    BedType(int size) {
        this.size = size;
    }
}
