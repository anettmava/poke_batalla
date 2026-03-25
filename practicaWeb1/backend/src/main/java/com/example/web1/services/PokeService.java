package com.example.web1.services;

import com.example.web1.model.PokeEntity;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PokeService {
    private static final String COLLECTION_NAME = "pokemon";

    // Pokemon creation and management
    public PokeEntity createPokemon(String name, int level) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            
            // Generate new ID based on document count
            long newId = System.currentTimeMillis();
            PokeEntity p = new PokeEntity(newId, name, level);
            
            // Save to Firestore
            db.collection(COLLECTION_NAME)
                    .document(String.valueOf(newId))
                    .set(p)
                    .get();
            
            return p;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Battle calculations with level update
    public Long calculateBattle(PokeEntity attacker, PokeEntity defender) {
        int attackScore = attacker.getLevel();
        int defendScore = defender.getLevel();
        
        Long winnerId = attackScore >= defendScore ? attacker.getId() : defender.getId();
        
        // Winner gains one level after battle.
        try {
            PokeEntity winner = winnerId.equals(attacker.getId()) ? attacker : defender;
            winner.setLevel(winner.getLevel() + 1);
            
            // Update in Firestore
            Firestore db = FirestoreClient.getFirestore();
            db.collection(COLLECTION_NAME)
                    .document(String.valueOf(winnerId))
                    .set(winner)
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        
        return winnerId;
    }

    public int calculateDamage(PokeEntity attacker, String moveName, PokeEntity defender) {
        int base = 10 + attacker.getLevel();
        int diff = Math.max(1, defender.getLevel() - attacker.getLevel());
        return Math.max(1, base - diff);
    }

    public List<PokeEntity> getAllPokemon() {
        try {
            Firestore db = FirestoreClient.getFirestore();
            List<PokeEntity> pokemonList = new ArrayList<>();
            
            db.collection(COLLECTION_NAME)
                    .get()
                    .get()
                    .getDocuments()
                    .forEach(doc -> {
                        PokeEntity p = doc.toObject(PokeEntity.class);
                        pokemonList.add(p);
                    });
            
            return pokemonList;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public PokeEntity getPokemonById(Long id) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            DocumentSnapshot doc = db.collection(COLLECTION_NAME)
                    .document(String.valueOf(id))
                    .get()
                    .get();
            
            return doc.exists() ? doc.toObject(PokeEntity.class) : null;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }
}