package com.pirate.arena.app.services;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.google.gson.Gson;
import com.pirate.arena.app.dynamoDB.ServiceQueries;
import com.pirate.arena.app.exceptions.BadRequestException;
import com.pirate.arena.app.models.*;
import com.pirate.arena.app.request.RequestUnlock;
import com.pirate.arena.app.request.RequestUpdateLocker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceLocker extends ServiceValidateRequest implements IServiceLocker {

    private final ServiceQueries serviceQueries;

    @Override
    public String updateLocker(RequestUpdateLocker requestUpdateLocker, Class clazz) {
        validateInputs(Optional.ofNullable(requestUpdateLocker));
        validateUserExist(requestUpdateLocker.email());
        validateClass(clazz);
        //Starting variables
        Optional<Item> userData = getUserDataList(requestUpdateLocker.email(), clazz.getSimpleName());
        List<Roster> dbRoster = serviceQueries.getAllRosters();
        List<Skill> dbSkill = serviceQueries.getAllSkills();
        //New user
        String className = clazz.getSimpleName();
        if (userData.isEmpty()) {
            if (className.equals("Roster")) {
                dbRoster.forEach(roster -> roster.buildDefault());
                storeInDB(requestUpdateLocker.email(), "characters", "usersHasCharacters", dbRoster);
                return dbRoster.stream().toList().toString();
            } else if (className.equals("Skill")) {
                dbSkill.forEach(skill -> skill.buildDefault());
                storeInDB(requestUpdateLocker.email(), "skills", "usersHasSkills", dbSkill);
                dbSkill.toString();
                return dbSkill.stream().toList().toString();
            }
            //Not new user
        } else {
            Gson gson = new Gson();
            if (className.equals("Roster")) {
                UserHasCharacters userRoster = gson.fromJson(userData.get().toJSON(), UserHasCharacters.class);
                if (dbRoster.size() > userRoster.getCharacters().size())
                    missingCharacterItems(dbRoster, userRoster);
                return userRoster.toString();

            } else if (className.equals("Skill")) {
                UserHasSkill userHasSkill = gson.fromJson(userData.get().toJSON(), UserHasSkill.class);
                missingSkillsItems(dbSkill, userHasSkill);
                return userHasSkill.toString();
            }
        }
        return "success";
    }

    @Override
    public String unlockLocker(RequestUnlock requestUnlock, Class clazz) {
        validateInputs(Optional.ofNullable(requestUnlock));
        validateUserExist(requestUnlock.email());
        Optional<Item> userData = getUserDataList(requestUnlock.email(), clazz.getSimpleName());
        if (userData.isEmpty()) throw new BadRequestException
                ("[unlockCharacter] The user with email [".concat(requestUnlock.email())
                        .concat("] does has a roster to update."));
        String className = clazz.getSimpleName();
        Gson gson = new Gson();
        if (className.equals("Roster")) {
            UserHasCharacters myRoster = gson.fromJson(userData.get().toJSON(), UserHasCharacters.class);
            Optional<Roster> character = myRoster.getCharacters()
                    .stream()
                    .filter(filter -> filter.getId().equals(requestUnlock.id()))
                    .findFirst();
            validateUnlockID(character, requestUnlock.email(), requestUnlock.id());
            character.get().unlock();
            serviceQueries.updateData("usersHasCharacters", myRoster.getEmail(), "characters", myRoster.getCharacters());
            return myRoster.toString();
        } else if (className.equals("Skill")) {
            UserHasSkill mySkill = gson.fromJson(userData.get().toJSON(), UserHasSkill.class);
            Optional<Skill> skill = mySkill.getSkills()
                    .stream()
                    .filter(filter -> filter.getId().equals(requestUnlock.id()))
                    .findFirst();
            validateUnlockID(skill, requestUnlock.email(), requestUnlock.id());
            skill.get().unlock();
            serviceQueries.updateData("usersHasSkills", mySkill.getEmail(), "skills", mySkill.getSkills());
            return mySkill.toString();
        }
        return "Success";
    }

    private void validateUnlockID(Optional<?> optional, String email, String id) {
        if (optional.isEmpty())
            throw new BadRequestException("[validateUnlockID] The user with email ["
                    .concat(email).concat("] doesn't has the character/skill [".concat(id.concat("]."))));
    }

    private Optional<Item> getUserDataList(String email, String clazz) {
        return clazz.equals("Roster")
                ? serviceQueries.getRosterByEmail(email) : serviceQueries.getSkillByEmail(email);
    }

    private void validateUserExist(String email) {
        if (serviceQueries.getUserByEmail(email).isEmpty())
            throw new BadRequestException
                    ("[Update locker] The user with email [".concat(email).concat("] does not exist."));
    }

    private void validateClass(Class clazz) {
        if (clazz != Roster.class && clazz != Skill.class)
            throw new BadRequestException("This function only works with Roster or Skill class");
    }

    private void storeInDB(String email, String fieldName, String tableName, List<?> list) {
        Gson gson = new Gson();
        Item dynamoItem = new Item();
        dynamoItem.withString("email", email);
        dynamoItem.withJSON(fieldName, gson.toJson(list));
        serviceQueries.putItem(tableName, dynamoItem);
    }

    private UserHasCharacters missingCharacterItems(List<Roster> roster, UserHasCharacters myRoster) {
        List<Roster> missingItem = new ArrayList<>();
        roster.forEach(item -> {
            if (myRoster.getCharacters()
                    .stream()
                    .noneMatch(predicate -> predicate.getId().equals(item.getId()))) {
                item.buildDefault();
                missingItem.add(item);
            }
        });
        missingItem.forEach(item -> myRoster.getCharacters().add(item));
        serviceQueries.updateData("usersHasCharacters", myRoster.getEmail(), "characters", myRoster.getCharacters());
        return myRoster;
    }

    private UserHasSkill missingSkillsItems(List<Skill> skills, UserHasSkill mySkills) {
        List<Skill> missingItem = new ArrayList<>();
        skills.forEach(item -> {
            if (mySkills.getSkills()
                    .stream()
                    .noneMatch(predicate -> predicate.getId().equals(item.getId()))) {
                item.buildDefault();
                missingItem.add(item);
            }
        });
        missingItem.forEach(item -> mySkills.getSkills().add(item));
        serviceQueries.updateData("usersHasSkills", mySkills.getEmail(), "skills", mySkills.getSkills());
        return mySkills;
    }


}
