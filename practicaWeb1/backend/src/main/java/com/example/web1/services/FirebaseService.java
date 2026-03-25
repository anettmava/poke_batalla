package com.example.web1.services;

import org.springframework.stereotype.Service;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import java.util.Map;
import java.util.HashMap;

@Service
public class FirebaseService {
     public String guardarDato() {
     try {
     // obtener instancia de Firestore
     Firestore db = FirestoreClient.getFirestore();
     // datos a guardar
    Map<String, Object> data = new HashMap<>();
    data.put("mensaje", "Conexion exitosa con Firestore");
     data.put("estado", "ok");
// guardar en colección
db.collection("test")
.document("doc1")
.set(data);
return "Datos guardados correctamente";
} catch (Exception e) {
e.printStackTrace();
return "Error al guardar datos";
}
}
}