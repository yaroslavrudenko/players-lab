package com.lab.players.mapping;

import com.lab.players.entities.Player;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.function.Function;

import static com.lab.players.mapping.PlayerConstants.*;
import static com.lab.players.utils.PlayersUtils.parseStringToLocalDate;
import static com.lab.players.utils.PlayersUtils.toOptionalInt;

@Slf4j
@Component
public class PlayerMapper implements Function<String, Player> {


    @Override
    public Player apply(String csvLine) {
        if (StringUtils.isEmpty(csvLine)) {
            return null;
        }
        String[] fields = csvLine.split(",");
        try {
            return Player.builder()
                    .playerID(fields[PLAYER_ID])
                    .birthYear(toOptionalInt(fields[BIRTH_YEAR]).orElse(null))
                    .birthMonth(toOptionalInt(fields[BIRTH_MONTH]).orElse(null))
                    .birthDay(toOptionalInt(fields[BIRTH_DAY]).orElse(null))
                    .birthCountry(fields[BIRTH_COUNTRY])
                    .birthState(fields[BIRTH_STATE])
                    .birthCity(fields[BIRTH_CITY])
                    .deathYear(toOptionalInt(fields[DEATH_YEAR]).orElse(null))
                    .deathMonth(toOptionalInt(fields[DEATH_MONTH]).orElse(null))
                    .deathDay(toOptionalInt(fields[DEATH_DAY]).orElse(null))
                    .deathCountry(fields[DEATH_COUNTRY])
                    .deathState(fields[DEATH_STATE])
                    .deathCity(fields[DEATH_CITY])
                    .nameFirst(fields[NAME_FIRST])
                    .nameLast(fields[NAME_LAST])
                    .nameGiven(fields[NAME_GIVEN])
                    .weight(toOptionalInt(fields[WEIGHT]).orElse(null))
                    .height(toOptionalInt(fields[HEIGHT]).orElse(null))
                    .bats(fields[BATS])
                    .throwValue(fields[THROWS])
                    .debut(parseStringToLocalDate(fields[DEBUT], YYYY_MM_DD))
                    .finalGame(parseStringToLocalDate(fields[FINAL_GAME], YYYY_MM_DD))
                    .retroID(fields[RETRO_ID])
                    .bbrefID(fields[BBREF_ID])
                    .build();
        } catch (Exception e) {
            log.error("Error while parsing player ID: {} with line: '{}'", fields[PLAYER_ID], csvLine, e);
        }
        return null;
    }


}
