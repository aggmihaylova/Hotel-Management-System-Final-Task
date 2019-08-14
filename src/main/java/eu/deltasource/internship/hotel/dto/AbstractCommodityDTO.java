package eu.deltasource.internship.hotel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Transfer object for commodities
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
	@JsonSubTypes.Type(value = BedDTO.class, name = "Bed"),

	@JsonSubTypes.Type(value = ToiletDTO.class, name = "Toilet"),
	@JsonSubTypes.Type(value = ShowerDTO.class, name = "Shower")})
public abstract class AbstractCommodityDTO {

	public AbstractCommodityDTO() {
	}
}
