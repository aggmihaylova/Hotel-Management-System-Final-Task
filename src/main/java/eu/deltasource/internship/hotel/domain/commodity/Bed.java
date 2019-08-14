package eu.deltasource.internship.hotel.domain.commodity;

import lombok.Getter;

/**
 * Represents bed in a hotel room
 */
public class Bed extends AbstractCommodity {

	@Getter
    private final BedType bedType;

    public Bed(BedType bedType) {
        super();
        this.bedType = bedType;

    }

    public int getSize() {
        return bedType.getSize();
    }

    @Override
    public void prepare() {
        System.out.println("The bed sheets are being replaced!");
    }

}
