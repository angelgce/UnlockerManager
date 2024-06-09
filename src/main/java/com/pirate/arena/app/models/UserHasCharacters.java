package com.pirate.arena.app.models;


import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserHasCharacters {

    private String email;
    Set<Roster> characters;

}
