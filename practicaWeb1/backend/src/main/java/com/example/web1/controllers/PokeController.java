package com.example.web1.controllers;

import com.example.web1.model.PokeEntity;
import com.example.web1.services.PokeConsultaService;
import com.example.web1.services.PokeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pokemon")
public class PokeController {

    @Autowired
    private PokeService pokeService;

    @Autowired
    private PokeConsultaService consultaService;

    @PostMapping("/create")
    public PokeEntity create(@RequestParam String name, @RequestParam int level) {
        return pokeService.createPokemon(name, level);
    }

    @PostMapping("/battle")
    public Long battle(@RequestParam Long pokemon1Id, @RequestParam Long pokemon2Id) {
        PokeEntity pokemon1 = pokeService.getPokemonById(pokemon1Id);
        PokeEntity pokemon2 = pokeService.getPokemonById(pokemon2Id);
        if (pokemon1 == null || pokemon2 == null) {
            throw new IllegalArgumentException("Pokemon not found");
        }
        return pokeService.calculateBattle(pokemon1, pokemon2);
    }

    @GetMapping("/{id}")
    public PokeEntity getById(@PathVariable Long id) {
        return consultaService.findPokemonById(id);
    }

    @GetMapping("/all")
    public List<PokeEntity> getAll() {
        return consultaService.findAllPokemon();
    }
}