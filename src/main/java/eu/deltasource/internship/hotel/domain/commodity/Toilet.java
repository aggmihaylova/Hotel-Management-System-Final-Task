package eu.deltasource.internship.hotel.domain.commodity;

/**
 * Represents a toilet in a hotel room
 */
public class Toilet extends AbstractCommodity {

    public Toilet() {
        super();
    }

    @Override
    public void prepare() {
        System.out.println("The toilet is being cleaned!");
    }
}
