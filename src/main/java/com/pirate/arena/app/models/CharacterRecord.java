package com.pirate.arena.app.models;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CharacterRecord {
    private int matches = 0;
    private int wins = 0;
    private int losses = 0;
    private int kills = 0;
    private int damage = 0;


}
