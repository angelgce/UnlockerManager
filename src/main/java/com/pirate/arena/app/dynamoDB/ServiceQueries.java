package com.pirate.arena.app.dynamoDB;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.google.gson.Gson;
import com.pirate.arena.app.exceptions.BadRequestException;
import com.pirate.arena.app.models.Roster;
import com.pirate.arena.app.models.Skill;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceQueries implements IServiceQueries {

    private final ServiceDynamoDB serviceDynamoDB;

    @Override
    public Optional<Item> getUserByEmail(String email) {
        return Optional.ofNullable(serviceDynamoDB.getItemByKey("users", "email", email));
    }

    @Override
    public Optional<Item> getRosterByEmail(String email) {
        return Optional.ofNullable(serviceDynamoDB.getItemByKey("usersHasCharacters", "email", email));
    }

    @Override
    public Optional<Item> getSkillByEmail(String email) {
        return Optional.ofNullable(serviceDynamoDB.getItemByKey("usersHasSkills", "email", email));
    }

    @Override
    public List<Skill> getAllSkills() {
        DynamoDBMapperConfig mapperConfig = new DynamoDBMapperConfig.Builder()
                .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement("skills")).build();
        DynamoDBMapper mapper = new DynamoDBMapper(serviceDynamoDB.getClient(), mapperConfig);
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        return mapper.scan(Skill.class, scanExpression);
    }

    @Override
    public List<Roster> getAllRosters() {
        DynamoDBMapperConfig mapperConfig = new DynamoDBMapperConfig.Builder()
                .withTableNameOverride(DynamoDBMapperConfig.TableNameOverride.withTableNameReplacement("roster")).build();
        DynamoDBMapper mapper = new DynamoDBMapper(serviceDynamoDB.getClient(), mapperConfig);
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        return mapper.scan(Roster.class, scanExpression);
    }

    @Override
    public int updateData(String tableName, String email, String key, Object value) {
        Gson gson = new Gson();
        Table table = serviceDynamoDB.getDynamoDB().getTable(tableName);
        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("email", email)
                .withUpdateExpression("set " + key + " = :" + key)
        .withValueMap(new ValueMap().withJSON(":" + key,gson.toJson(value)));
        return table.updateItem(updateItemSpec)
                .getUpdateItemResult().getSdkHttpMetadata().getHttpStatusCode();
    }


    //roster
    @Override
    public void putItem(String table, Item item) {
        serviceDynamoDB.putItem(table, item);
    }


}
