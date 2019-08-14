package eu.deltasource.internship.hotel.dto;


import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * Transfer object for room
 */
@Getter
public class RoomDTO {

	private int roomId;
	private Set<AbstractCommodityDTO> commodities;

	/**
	 * This is a constructor
	 *
	 * @param roomId      room id
	 * @param commodities set of commodities
	 */
	public RoomDTO(int roomId, Set<AbstractCommodityDTO> commodities) {
		this.roomId = roomId;
		this.commodities = new HashSet<>(commodities);
	}
}
