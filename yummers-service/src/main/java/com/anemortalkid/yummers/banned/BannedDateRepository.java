package com.anemortalkid.yummers.banned;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface BannedDateRepository extends MongoRepository<BannedDate, String> {

	BannedDate findByYearAndMonthAndDay(int year, int month, int day);

}
