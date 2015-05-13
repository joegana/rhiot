package com.github.camellabs.iot.cloudlet.document.driver.mongodb;

import com.github.camellabs.iot.cloudlet.document.driver.bson.BsonMapper;
import com.github.camellabs.iot.cloudlet.document.driver.spi.DocumentDriver;
import com.github.camellabs.iot.cloudlet.document.driver.spi.SaveOperation;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.github.camellabs.iot.cloudlet.document.driver.bson.BsonMapperProcessor.mapJsonToBson;
import static java.lang.String.format;
import static org.apache.camel.component.mongodb.MongoDbConstants.COLLECTION;
import static org.apache.camel.component.mongodb.MongoDbConstants.OID;

@Component
public class MongodbDocumentDriver implements DocumentDriver {

    private final String documentsDbName;

    private final ProducerTemplate producerTemplate;

    @Autowired
    public MongodbDocumentDriver(@Value("${camel.labs.iot.cloudlet.document.driver.mongodb.db:cloudlet_document}") String documentsDbName,
                                 ProducerTemplate producerTemplate) {
        this.documentsDbName = documentsDbName;
        this.producerTemplate = producerTemplate;
    }

    @Override
    public String save(SaveOperation saveOperation) {
        return producerTemplate.request(baseMongoDbEndpoint() + "save",
                exchange -> {
                    exchange.getIn().setHeader(COLLECTION, saveOperation.collection());
                    exchange.getIn().setBody(saveOperation.pojo());
                    mapJsonToBson().process(exchange);
                }).getIn().getHeader(OID, String.class);
    }

    private String baseMongoDbEndpoint() {
        // TODO:CAMEL Collection should not be required for dynamic endpoints
        return format("mongodb:mongo?database=%s&collection=default&dynamicity=true&operation=", documentsDbName);
    }

}
