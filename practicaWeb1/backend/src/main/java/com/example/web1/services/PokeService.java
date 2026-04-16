package com.example.web1.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.web1.model.PokeEntity;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;

import jakarta.annotation.PostConstruct;

@Service
public class PokeService {


    @Autowired
    private FirebaseApp firebaseApp;

    private static final String COLLECTION_NAME = "pokemon";

    private PokeEntity ultimaBatalla = new PokeEntity();

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

   
    @PostConstruct
    public void escucharCambios() {
        // Usamos firebaseApp (inyectado) para que Spring controle el ciclo de vida
        Firestore db = FirestoreClient.getFirestore(firebaseApp);
        DocumentReference docRef = db.collection("pokebattles").document("battle1");

        docRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                error.printStackTrace();
                return;
            }
            if (snapshot != null && snapshot.exists()) {
    
                ultimaBatalla = snapshot.toObject(PokeEntity.class);
                enviarActualizacion();
            }
        });
    }

    public SseEmitter agregarCliente() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        try {
            emitter.send(ultimaBatalla);
        } catch (Exception e) {
            emitters.remove(emitter);
        }
        return emitter;
    }


    private void enviarActualizacion() {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(ultimaBatalla);
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        }
    }


    public PokeEntity createPokemon(String name, int level) {
        try {
            Firestore db = FirestoreClient.getFirestore();
            long newId = System.currentTimeMillis();
            PokeEntity p = new PokeEntity(newId, name, level);
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

    public Long calculateBattle(PokeEntity attacker, PokeEntity defender) {
        int attackScore = attacker.getLevel();
        int defendScore = defender.getLevel();
        Long winnerId = attackScore >= defendScore ? attacker.getId() : defender.getId();
        try {
            PokeEntity winner = winnerId.equals(attacker.getId()) ? attacker : defender;
            winner.setLevel(winner.getLevel() + 1);
            Firestore db = FirestoreClient.getFirestore(firebaseApp);
            db.collection(COLLECTION_NAME)
                    .document(String.valueOf(winnerId))
                    .set(winner)
                    .get();
            db.collection("pokebattles")
                .document("battle1")
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