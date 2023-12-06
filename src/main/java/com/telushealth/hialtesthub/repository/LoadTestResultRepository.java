package com.telushealth.hialtesthub.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
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

	public LoadTestResult getLatestResult() {
        // Create an aggregation pipeline
        AggregationOperation sortByStartTimeDesc = Aggregation.sort(Sort.Direction.DESC, "startTestTime");
        AggregationOperation limitToOne = Aggregation.limit(1);

        Aggregation aggregation = Aggregation.newAggregation(sortByStartTimeDesc, limitToOne);

        // Execute the aggregation
        AggregationResults<LoadTestResult> aggregationResults =
                mongoTemplate.aggregate(aggregation, "loadTestResult", LoadTestResult.class);

        // Get the result
        List<LoadTestResult> results = aggregationResults.getMappedResults();

        return results.isEmpty() ? null : results.get(0);
    }

	public void updateEndTestTimeInResult(String loadTestStartTime, String endTestTime) {
		Query query = Query.query(Criteria.where("startTestTime").is(loadTestStartTime));
		Update update = new Update().set("endTestTime", endTestTime);

		mongoTemplate.updateFirst(query, update, LoadTestResult.class);		
	}

}
