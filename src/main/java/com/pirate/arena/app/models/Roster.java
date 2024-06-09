package com.pirate.arena.app.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Roster implements IBuilderDefault {
    private String id;
    private Boolean enable;
    private String unlockDate;
    private CharacterRecord record;

    @Override
    public void buildDefault() {
        enable = false;
        unlockDate = "";
        record = new CharacterRecord();
    }

    @Override
    public void unlock() {
        enable = true;
        unlockDate = LocalDateTime.now().toString();
    }
}
