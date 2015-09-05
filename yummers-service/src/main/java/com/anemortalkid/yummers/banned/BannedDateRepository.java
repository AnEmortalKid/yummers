package com.anemortalkid.yummers.banned;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Repository for the {@link BannedDate}s
 * 
 * @author JMonterrubio
 *
 */
public interface BannedDateRepository extends MongoRepository<BannedDate, String> {

	/**
	 * Finds a date by the year, month and day
	 * 
	 * @param year
	 *            year of the date
	 * @param month
	 *            month of the date
	 * @param day
	 *            day of the date
	 * @return a {@link BannedDate} if one exists with the given year,month,day
	 *         or <code>null</code>
	 */
	BannedDate findByYearAndMonthAndDay(int year, int month, int day);

}
