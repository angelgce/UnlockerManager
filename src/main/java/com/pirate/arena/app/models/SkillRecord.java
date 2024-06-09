package com.pirate.arena.app.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SkillRecord {

    private int matches = 0;
    private int wins = 0;
    private int losses = 0;
    private int kills = 0;
}
