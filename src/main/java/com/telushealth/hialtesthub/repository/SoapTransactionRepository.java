package com.telushealth.hialtesthub.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.ConvertOperators;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.telushealth.hialtesthub.entity.ReportStats;
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

	public List<ReportStats> runAllResponseTimeAggregation(String startTestTime, String endTestTime) {
		// Define match operation to filter transactions between startTestTime and
		// endTestTime
		MatchOperation matchOperation = Aggregation
				.match(Criteria.where("soapResponse.timeStamp").gte(startTestTime).lte(endTestTime));

		// Define project operation to calculate duration and other fields
		ProjectionOperation projectOperation = Aggregation.project()
				.and(ConvertOperators.valueOf("$soapResponse.responseTime").convertToDouble()).as("responseTime")
				.andExpression("(responseTime / 1000)").as("durationInSeconds");

		// Define conditional expressions for different duration ranges
		ConditionalOperators.Cond durationRange0To2_5 = ConditionalOperators
				.when(Criteria.where("durationInSeconds").lte(2.5)).then("0-2.5").otherwise("Unknown");
		ConditionalOperators.Cond durationRange2_5To5 = ConditionalOperators
				.when(Criteria.where("durationInSeconds").gt(2.5).lte(5)).then("2.5-5").otherwise("Unknown");
		ConditionalOperators.Cond durationRange5To10 = ConditionalOperators
				.when(Criteria.where("durationInSeconds").gt(5).lte(10)).then("5-10").otherwise("Unknown");
		ConditionalOperators.Cond durationRange10To15 = ConditionalOperators
				.when(Criteria.where("durationInSeconds").gt(10).lte(15)).then("10-15").otherwise("Unknown");
		ConditionalOperators.Cond durationRange15To20 = ConditionalOperators
				.when(Criteria.where("durationInSeconds").gt(15).lte(20)).then("15-20").otherwise("Unknown");
		ConditionalOperators.Cond durationRange20To25 = ConditionalOperators
				.when(Criteria.where("durationInSeconds").gt(20).lte(25)).then("20-25").otherwise("Unknown");
		ConditionalOperators.Cond durationRange25To30 = ConditionalOperators
				.when(Criteria.where("durationInSeconds").gt(25).lte(30)).then("25-30").otherwise("Unknown");
		ConditionalOperators.Cond durationRangeGreaterThan30 = ConditionalOperators
				.when(Criteria.where("durationInSeconds").gt(30)).then(">30").otherwise("Unknown");

		// Group by duration range and calculate statistics
		GroupOperation groupOperation = Aggregation.group("durationRange").count().as("interactions")
				.sum("durationInSeconds").as("totalDuration").avg("durationInSeconds").as("avgDuration");

		// Project operation to format the result
		ProjectionOperation resultProjection = Aggregation.project().and("durationRange").as("durationRange")
				.and("interactions").as("interactions").andExpression("(interactions / totalInteractions) * 100")
				.as("percentageTotal").and("avgDuration").as("avgDuration");

		// Sort operation to sort the results by duration range
		SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Order.asc("durationRange")));

		// Define the aggregation pipeline
		Aggregation aggregation = Aggregation.newAggregation(matchOperation, projectOperation, groupOperation,
				resultProjection, sortOperation);

		// Execute the aggregation and get the result
		AggregationResults<ReportStats> results = mongoTemplate.aggregate(aggregation, SoapTransaction.class,
				ReportStats.class);

		// Calculate total values
		double totalInteractions = results.getMappedResults().stream().mapToLong(ReportStats::getInteractions).sum();
		double totalAvgDuration = results.getMappedResults().stream().mapToDouble(ReportStats::getAvgDuration).average()
				.orElse(0);

		// Create a Total row
		ReportStats totalRow = new ReportStats();
		totalRow.setDurationRange("Total");
		totalRow.setInteractions((long) totalInteractions);
		totalRow.setPercentageTotal(100.0); // Set this value to 100 since it's the total
		totalRow.setAvgDuration(totalAvgDuration);

		// Add the Total row to the result
		List<ReportStats> resultWithTotal = new ArrayList<>(results.getMappedResults());
		resultWithTotal.add(totalRow);

		return resultWithTotal;
	}
}
