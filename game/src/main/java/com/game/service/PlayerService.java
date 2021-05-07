package com.game.service;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> allPlayer() {
        return playerRepository.findAll();
    }

    public void createNewPlayer(Player player) {
        playerRepository.save(player);
    }

    public void updatePlayer(Player player) {
        playerRepository.saveAndFlush(player);
    }

    public void delete(int id) {
        playerRepository.deleteById(id);
    }

    public Player getPlayerById(int id) {
        return playerRepository.getOne(id);
    }
}
