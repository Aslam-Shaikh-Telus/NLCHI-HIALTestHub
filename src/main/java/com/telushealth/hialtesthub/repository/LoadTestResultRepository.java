package com.telushealth.hialtesthub.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.telushealth.hialtesthub.entity.LoadTestResult;
import com.telushealth.hialtesthub.entity.TestResultStat;

@Repository
public class LoadTestResultRepository {

	@Autowired
	MongoTemplate mongoTemplate;

	public List<LoadTestResult> getAll() {
		return mongoTemplate.findAll(LoadTestResult.class);
	}

	public void saveResult(LoadTestResult loadTestResult) {
		mongoTemplate.save(loadTestResult);
	}

	public void updateResult(String loadTestStartTime, List<TestResultStat> testResultStats) {
		Query query = Query.query(Criteria.where("startTestTime").is(loadTestStartTime));
		Update update = new Update().set("testResultStats", testResultStats);

		mongoTemplate.updateFirst(query, update, LoadTestResult.class);
	}

	public LoadTestResult getByStartTime(String loadTestStartTime) {
		Query query = Query.query(Criteria.where("startTestTime").is(loadTestStartTime));
		return mongoTemplate.findOne(query, LoadTestResult.class);
	}

}
