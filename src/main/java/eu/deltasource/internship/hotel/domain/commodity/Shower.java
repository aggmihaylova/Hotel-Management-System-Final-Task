package eu.deltasource.internship.hotel.domain.commodity;

/**
 * Represents shower in a hotel room
 */
public class Shower extends AbstractCommodity {

    public Shower() {
        super();
    }

    @Override
    public void prepare() {
        System.out.println("The shower is being cleaned!");
    }
}
