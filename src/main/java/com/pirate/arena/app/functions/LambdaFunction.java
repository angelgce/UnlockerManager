package com.pirate.arena.app.functions;

import com.pirate.arena.app.models.Roster;
import com.pirate.arena.app.models.Skill;
import com.pirate.arena.app.request.RequestUnlock;
import com.pirate.arena.app.request.RequestUpdateLocker;
import com.pirate.arena.app.services.ServiceLocker;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
public class LambdaFunction {

    private final ServiceLocker serviceLocker;

    @Bean
    public Function<RequestUpdateLocker, ResponseEntity<String>> updateCharacter() {
        return value -> ResponseEntity.ok().body(serviceLocker.updateLocker(value, Roster.class));
    }

    @Bean
    public Function<RequestUpdateLocker, ResponseEntity<String>> updateSkill() {
        return value -> ResponseEntity.ok().body(serviceLocker.updateLocker(value, Skill.class));
    }


    @Bean
    public Function<RequestUnlock, ResponseEntity<String>> unlockCharacterLocker() {
        return value -> ResponseEntity.ok().body(serviceLocker.unlockLocker(value, Roster.class));
    }

    @Bean
    public Function<RequestUnlock, ResponseEntity<String>> unlockSkillLocker() {
        return value -> ResponseEntity.ok().body(serviceLocker.unlockLocker(value, Skill.class));
    }


}
