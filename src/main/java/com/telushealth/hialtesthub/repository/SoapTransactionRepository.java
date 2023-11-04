package com.telushealth.hialtesthub.repository;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.telushealth.hialtesthub.entity.SoapTransaction;

@Repository
public class SoapTransactionRepository {

	@Autowired
	MongoTemplate mongoTemplate;

	public SoapTransaction findByInteractionId(String interactionId) {
		Query query = new Query(Criteria.where("soapRequest.interactionId").is(interactionId));
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
}
