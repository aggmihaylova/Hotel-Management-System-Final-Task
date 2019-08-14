package eu.deltasource.internship.hotel.domain.commodity;

import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstractCommodity {

	@Getter
	protected final int inventoryId;

	private static int inventoryCount;

	public AbstractCommodity() {
		this.inventoryId = ++inventoryCount;
	}

	public abstract void prepare();

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof AbstractCommodity)) {
			return false;
		}
		return inventoryId == ((AbstractCommodity) obj).inventoryId;
	}

	@Override
	public int hashCode() {
		return inventoryId;
	}
}
