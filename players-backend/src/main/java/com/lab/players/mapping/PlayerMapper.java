package com.lab.players.mapping;

import com.lab.players.entities.Player;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.function.Function;

import static com.lab.players.mapping.PlayerConstants.*;
import static com.lab.players.utils.PlayersUtils.parseStringToLocalDate;
import static com.lab.players.utils.PlayersUtils.toOptionalInt;
import static org.apache.commons.lang3.StringUtils.stripToNull;

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
                    .playerID(stripToNull(fields[PLAYER_ID]))
                    .birthYear(toOptionalInt(fields[BIRTH_YEAR]).orElse(null))
                    .birthMonth(toOptionalInt(fields[BIRTH_MONTH]).orElse(null))
                    .birthDay(toOptionalInt(fields[BIRTH_DAY]).orElse(null))
                    .birthCountry(stripToNull(fields[BIRTH_COUNTRY]))
                    .birthState(stripToNull(fields[BIRTH_STATE]))
                    .birthCity(stripToNull(fields[BIRTH_CITY]))
                    .deathYear(toOptionalInt(fields[DEATH_YEAR]).orElse(null))
                    .deathMonth(toOptionalInt(fields[DEATH_MONTH]).orElse(null))
                    .deathDay(toOptionalInt(fields[DEATH_DAY]).orElse(null))
                    .deathCountry(stripToNull(fields[DEATH_COUNTRY]))
                    .deathState(stripToNull(fields[DEATH_STATE]))
                    .deathCity(stripToNull(fields[DEATH_CITY]))
                    .nameFirst(stripToNull(fields[NAME_FIRST]))
                    .nameLast(stripToNull(fields[NAME_LAST]))
                    .nameGiven(stripToNull(fields[NAME_GIVEN]))
                    .weight(toOptionalInt(fields[WEIGHT]).orElse(null))
                    .height(toOptionalInt(fields[HEIGHT]).orElse(null))
                    .bats(stripToNull(fields[BATS]))
                    .throwValue(stripToNull(fields[THROWS]))
                    .debut(parseStringToLocalDate(fields[DEBUT], YYYY_MM_DD))
                    .finalGame(parseStringToLocalDate(fields[FINAL_GAME], YYYY_MM_DD))
                    .retroID(stripToNull(fields[RETRO_ID]))
                    .bbrefID(stripToNull(fields[BBREF_ID]))
                    .build();
        } catch (Exception e) {
            log.error("Error while parsing player ID: {} with line: '{}'", fields[PLAYER_ID], csvLine, e);
        }
        return null;
    }


}
