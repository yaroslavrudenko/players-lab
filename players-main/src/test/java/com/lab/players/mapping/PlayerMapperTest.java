package com.lab.players.mapping;

import com.lab.players.entities.Player;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerMapperTest {

    private final PlayerMapper playerMapper = new PlayerMapper();

    @Test
    public void givenValidCsvLine_whenApply_thenReturnPlayer() {
        String csvLine = "aardsda01,1981,12,27,USA,CO,Denver,,,,,,,David,Aardsma,David Allan,215,75,R,R,2004-04-06,2015-08-23,aardd001,aardsda01";

        Player player = playerMapper.apply(csvLine);

        assertThat(player).isNotNull();
        assertThat(player.getPlayerID()).isEqualTo("aardsda01");
        assertThat(player.getBirthYear()).isEqualTo(1981);
        assertThat(player.getBirthMonth()).isEqualTo(12);
        assertThat(player.getBirthDay()).isEqualTo(27);
        assertThat(player.getBirthCountry()).isEqualTo("USA");
        assertThat(player.getBirthState()).isEqualTo("CO");
        assertThat(player.getBirthCity()).isEqualTo("Denver");
        assertThat(player.getDeathYear()).isNull();
        assertThat(player.getDeathMonth()).isNull();
        assertThat(player.getDeathDay()).isNull();
        assertThat(player.getDeathCountry()).isNull();
        assertThat(player.getDeathState()).isNull();
        assertThat(player.getDeathCity()).isNull();
        assertThat(player.getNameFirst()).isEqualTo("David");
        assertThat(player.getNameLast()).isEqualTo("Aardsma");
        assertThat(player.getNameGiven()).isEqualTo("David Allan");
        assertThat(player.getHeight()).isEqualTo(215);
        assertThat(player.getWeight()).isEqualTo(75);
        assertThat(player.getBats()).isEqualTo("R");
        assertThat(player.getThrowValue()).isEqualTo("R");
        assertThat(player.getDebut()).isEqualTo(LocalDate.of(2004, 4, 6));
        assertThat(player.getFinalGame()).isEqualTo(LocalDate.of(2015, 8, 23));
        assertThat(player.getRetroID()).isEqualTo("aardd001");
        assertThat(player.getBbrefID()).isEqualTo("aardsda01");
    }

    @Test
    public void givenEmptyCsvLine_whenApply_thenReturnNull() {
        String csvLine = "";

        Player player = playerMapper.apply(csvLine);

        assertThat(player).isNull();
    }

    @Test
    public void givenInvalidCsvLine_whenApply_thenReturnNull() {
        String csvLine = "invalid,csv,line";

        Player player = playerMapper.apply(csvLine);

        assertThat(player).isNull();
    }
}