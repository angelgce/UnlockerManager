package com.pirate.arena.app.models;


import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserHasSkill {
    private String email;
    Set<Skill> skills;
}
