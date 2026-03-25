package com.example.web1.services;

import com.example.web1.model.PokeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PokeConsultaService {

    @Autowired
    private PokeService pokeService;

    public List<PokeEntity> findAllPokemon() {
        return pokeService.getAllPokemon();
    }

    public PokeEntity findPokemonById(Long id) {
        return pokeService.getPokemonById(id);
    }

    public List<PokeEntity> findPokemonByLevelRange(int min, int max) {
        return pokeService.getAllPokemon().stream()
                .filter(p -> p.getLevel() >= min && p.getLevel() <= max)
                .collect(Collectors.toList());
    }

    public List<PokeEntity> findPokemonByName(String name) {
        return pokeService.getAllPokemon().stream()
                .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
}