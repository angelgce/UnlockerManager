package com.pirate.arena.app.dynamoDB;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.xspec.S;
import com.pirate.arena.app.models.Roster;
import com.pirate.arena.app.models.Skill;


import java.util.List;
import java.util.Optional;

public interface IServiceQueries {

    Optional<Item> getUserByEmail(String email);
    Optional<Item> getRosterByEmail(String email);
    Optional<Item> getSkillByEmail(String email);

    List<Skill> getAllSkills();

    List<Roster> getAllRosters();
    int updateData(String tableName,String email, String key, Object value);

    void putItem(String table, Item item);
}
