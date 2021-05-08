package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exeptions.HttpStatusBadRequest;
import com.game.exeptions.HttpStatusNotFound;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> getAllPlayers(String name, String title, Race race, Profession profession,
                                      Long after, Long before, Boolean banned, Integer minExperience,
                                      Integer maxExperience, Integer minLevel, Integer maxLevel) {

        Date afterDate = after == null ? null : new Date(after);
        Date beforeDate = before == null ? null : new Date(before);

        List<Player> playersList = new ArrayList<>();
        playerRepository.findAll().forEach((player) -> {
            if (name != null && !player.getName().contains(name)) return;
            if (title != null && !player.getTitle().contains(title)) return;
            if (race != null && player.getRace() != race) return;
            if (profession != null && player.getProfession() != profession) return;
            if (afterDate != null && player.getBirthday().before(afterDate)) return;
            if (beforeDate != null && player.getBirthday().after(beforeDate)) return;
            if (banned != null && player.getBanned().booleanValue() != banned.booleanValue()) return;
            if (minExperience != null && player.getExperience().compareTo(minExperience) < 0) return;
            if (maxExperience != null && player.getExperience().compareTo(maxExperience) > 0) return;
            if (minLevel != null && player.getLevel().compareTo(minLevel) < 0) return;
            if (maxLevel != null && player.getLevel().compareTo(maxLevel) > 0) return;
            playersList.add(player);
        });

        return playersList;
    }

    public Player createNewPlayer(Player player) {

        if(isValidParams(player) &&
                isValidName(player.getName()) &&
                isValidTitle(player.getTitle()) &&
                isValidDate(player.getBirthday()) &&
                isValidExperience(player.getExperience())) {
            player.setLevel((int) ((Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100));
            player.setUntilNextLevel(50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience());
            playerRepository.save(player);

            return player;
        }
        else throw new HttpStatusBadRequest();

    }

    public Player updatePlayer(Player afterUpdate, Player beforeUpdate) {

        if(beforeUpdate.getName() != null) afterUpdate.setName(beforeUpdate.getName());
        if(beforeUpdate.getTitle() != null) afterUpdate.setTitle(beforeUpdate.getTitle());
        if(beforeUpdate.getRace() != null) afterUpdate.setRace(beforeUpdate.getRace());
        if(beforeUpdate.getProfession() != null) afterUpdate.setProfession(beforeUpdate.getProfession());
        if(beforeUpdate.getBirthday() != null){
            if(isValidDate(beforeUpdate.getBirthday())){
                afterUpdate.setBirthday(beforeUpdate.getBirthday());
            } else throw new HttpStatusBadRequest();
        }
        if(beforeUpdate.getBanned() != null) afterUpdate.setBanned(beforeUpdate.getBanned());
        if (beforeUpdate.getExperience() != null){
            if(isValidExperience(beforeUpdate.getExperience())){
                afterUpdate.setExperience(beforeUpdate.getExperience());
            } else throw new HttpStatusBadRequest();
        }

        afterUpdate.setLevel((int) ((Math.sqrt(2500 + 200 * afterUpdate.getExperience()) - 50) / 100));
        afterUpdate.setUntilNextLevel(50 * (afterUpdate.getLevel() + 1) * (afterUpdate.getLevel() + 2) - afterUpdate.getExperience());
        playerRepository.save(afterUpdate);

        return afterUpdate;
    }

    public void deletePlayer(Player player) {

        if(isValidId(player.getId())) {
            playerRepository.delete(player);
        }
        else throw new HttpStatusBadRequest();
    }

    public Player getPlayerById(String id) {

        Long player;

        try {
            player = Long.parseLong(id);
        } catch (NumberFormatException e){
            throw new HttpStatusBadRequest();
        }

        if(isValidId(player) == false) throw new HttpStatusBadRequest();

        if (playerRepository.existsById(player)) {
            return playerRepository.findById(player).get();
        }
        else {
            throw new HttpStatusNotFound();
        }

    }

    public List<Player> sortPage(List<Player> list, Integer pageNumber, Integer pageSize) {

        if (pageNumber == null) pageNumber = 0;
        if (pageSize == null) pageSize = 3;
        int start = pageNumber * pageSize;
        int end = start + pageSize;
        if (end > list.size()) end = list.size();

        return list.subList(start, end);
    }

    public List<Player> sortPlayers(List<Player> list, PlayerOrder order) {

        if (order != null) {
            switch (order) {
                case ID: {
                    list.sort(Comparator.comparing(Player::getId));
                    break;
                }
                case NAME: {
                    list.sort(Comparator.comparing(Player::getName));
                    break;
                }
                case EXPERIENCE: {
                    list.sort(Comparator.comparing(Player::getExperience));
                    break;
                }
                case BIRTHDAY: {
                    list.sort(Comparator.comparing(Player::getBirthday));
                    break;
                }
            }
        }

        return list;
    }

    private boolean isValidId(Long id){
        return id > 0;
    }

    private boolean isValidTitle(String title) {
        return title.length() <= 30 && !title.isEmpty();
    }

    private boolean isValidName(String name){
        return name.length() <= 12 && !name.isEmpty();
    }

    private boolean isValidDate(Date date) {
        if (date == null) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        Date after = new Date();
        Date before = new Date();
        calendar.set(1999, 11, 31);
        after = calendar.getTime();
        calendar.set(3000, 11, 31);
        before = calendar.getTime();

        return  (date.before(before) && date.after(after));
    }

    private boolean isValidExperience(Integer experience) {
        return experience >= 0 && experience <= 10000000;
    }

    private boolean isValidParams(Player player) {
        
        if (player.getName() == null &&
            player.getTitle() == null &&
            player.getRace() == null &&
            player.getProfession() == null &&
            player.getBirthday() == null &&
            player.getExperience() == null) return false;

        else return true;
    }

}
