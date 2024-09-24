package com.telushealth.hialtesthub.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.telushealth.hialtesthub.entity.SoapTransaction;
import com.telushealth.hialtesthub.entity.TestResultStat;

@Repository
public class SoapTransactionRepository {

	@Autowired
	MongoTemplate mongoTemplate;

	public SoapTransaction findLatestByInteractionId(String interactionId) {
		Query query = new Query(Criteria.where("soapRequest.interactionId").is(interactionId))
				.with(Sort.by(Sort.Order.desc("soapResponse.timeStamp"))) // Assuming you have a timestamp field
				.limit(1);
		return mongoTemplate.findOne(query, SoapTransaction.class);
	}

	public void save(SoapTransaction soapTransaction) {
		mongoTemplate.save(soapTransaction);
	}

	public void saveAll(List<SoapTransaction> soapTransactions) {
		for (SoapTransaction soapTransaction : soapTransactions) {
			mongoTemplate.save(soapTransaction);
		}

	}

	public List<SoapTransaction> findAll() {
		return mongoTemplate.findAll(SoapTransaction.class);
	}

	public SoapTransaction findByMsgId(String msgId) {
		Query query = new Query(Criteria.where("msgId").is(msgId));
		return mongoTemplate.findOne(query, SoapTransaction.class);
	}

	public List<TestResultStat> runLoadTestAggregation(String loadTestStartTime) {

		MatchOperation matchOperation = Aggregation
				.match(Criteria.where("soapResponse.timeStamp").gt(loadTestStartTime));

		GroupOperation groupOperation = Aggregation.group("$soapRequest.interactionId")
				.first("$soapRequest.description").as("description").count().as("testCount")
				.sum(ConditionalOperators
						.when(Criteria.where("soapResponse.responseStatus").is("FAIL")).then(1).otherwise(0))
				.as("failCount")
				.sum(ConditionalOperators.when(Criteria.where("soapResponse.responseStatus").is("PASS")).then(1)
						.otherwise(0))
				.as("successCount").min((ConvertOperators.valueOf("$soapResponse.responseTime").convertToDouble()))
				.as("minResponseTime").max((ConvertOperators.valueOf("$soapResponse.responseTime").convertToDouble()))
				.as("maxResponseTime")
				.avg(ConditionalOperators.when(Criteria.where("soapResponse.responseTime").ne(null).ne(""))
						.then((ConvertOperators.valueOf("$soapResponse.responseTime").convertToDouble())).otherwise(0))
				.as("avgResponseTime");

		ProjectionOperation projectOperation = Aggregation.project().andExpression("interactionId")
				.as("$soapRequest.interactionId").andExpression("description").as("description")
				.andExpression("testCount").as("testCount").andExpression("failCount").as("failCount")
				.andExpression("(successCount / testCount) * 100").as("successRatio")
				.andExpression("(minResponseTime / 1000) * 1000").as("minResponseTime")
				.andExpression("(maxResponseTime / 1000) * 1000").as("maxResponseTime")
				.andExpression("(avgResponseTime / 1000) * 1000").as("avgResponseTime");

		Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation, projectOperation);

		return mongoTemplate.aggregate(aggregation, SoapTransaction.class, TestResultStat.class).getMappedResults();
	}

	public List<String> getAllResponseTimeBetweenTimeGiven(String startTestTime, String endTestTime) {

		Criteria criteria = Criteria.where("soapResponse.timeStamp").gte(startTestTime).lte(endTestTime);
		Query query = new Query(criteria);

		List<SoapTransaction> results = mongoTemplate.find(query, SoapTransaction.class);

		// Extract responseTime from results and convert it to a List<String>
		return results.stream().map(transaction -> transaction.getSoapResponse().getResponseTime())
				.collect(Collectors.toList());

	}

	public List<SoapTransaction> getSoapTransactionsByStartTime(String startTime) {
		Query query = new Query(Criteria.where("soapResponse.timeStamp").gte(startTime));
		
		return mongoTemplate.find(query, SoapTransaction.class);
	}

}
