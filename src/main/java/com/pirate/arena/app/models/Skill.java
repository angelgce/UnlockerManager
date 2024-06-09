package com.pirate.arena.app.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
public class Skill implements IBuilderDefault {

    private String id;
    private Boolean enable;
    private String unlockDate;
    private SkillRecord record = new SkillRecord();

    @Override
    public void buildDefault() {
        enable = false;
        unlockDate = "";
        record = new SkillRecord();
    }

    @Override
    public void unlock() {
        enable = true;
        unlockDate = LocalDateTime.now().toString();
    }
}
