package com.JsonDiffApi.service;

import com.JsonDiffApi.entity.JsonEntity;
import com.JsonDiffApi.repository.JsonRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.zjsonpatch.JsonDiff;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JsonService {
    private final JsonRepository repository;
    private final ObjectMapper objectMapper;

    public JsonService(JsonRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    // JSON verisini kaydeder
    public void saveJson(String id, String jsonData) {
        JsonEntity entity = new JsonEntity();
        entity.setId(id);
        entity.setJsonData(jsonData);
        repository.save(entity);
    }

    // JSON verilerini karşılaştırır ve farkları döndürür
    public List<Map<String, Object>> compareJson(String id, String incomingJson) throws Exception {
        JsonEntity original = repository.findById(id)
                .orElseThrow(() -> new Exception("Kayıt bulunamadı."));

        String originalJson = original.getJsonData();

        // Farklılıkları bul
        JsonNode originalTree = objectMapper.readTree(originalJson);
        JsonNode incomingTree = objectMapper.readTree(incomingJson);
        JsonNode patch = JsonDiff.asJson(originalTree, incomingTree);

        // Farkları işleyerek detaylı bir liste oluştur
        List<Map<String, Object>> differences = new ArrayList<>();

        for (int i = 0; i < patch.size(); i++) {
            JsonNode diff = patch.get(i);
            Map<String, Object> diffMap = new HashMap<>();

            String fieldPath = diff.get("path").asText();
            String operation = diff.get("op").asText();

            // Operation türüne göre eski ve yeni değerleri al
            JsonNode oldValueNode = null;
            JsonNode newValueNode = diff.get("value");

            Object oldValue = null;
            Object newValue = null;

            if (operation.equals("replace")) {
                // 'replace' işleminde eski değeri manuel olarak alıyoruz
                oldValueNode = getNodeAtPath(originalTree, fieldPath); // Eski değer alınacak
                oldValue = (oldValueNode != null) ? oldValueNode.asText() : "field control"; // Eski değeri "absent" olarak işaretle
                newValue = (newValueNode != null) ? newValueNode.asText() : null; // Yeni değeri al
            } else if (operation.equals("add")) {
                // Add işlemi için eski değer yok, sadece yeni değer
                newValue = (newValueNode != null) ? newValueNode.asText() : null;
            } else if (operation.equals("remove")) {
                // Remove işlemi için eski değeri alıyoruz
                oldValue = (oldValueNode != null) ? oldValueNode.asText() : null;
            }

            // Değerleri map'e ekle
            diffMap.put("field", fieldPath);
            diffMap.put("operation", operation);
            diffMap.put("oldValue", oldValue);
            diffMap.put("newValue", newValue);

            differences.add(diffMap);
        }

        return differences;
    }



    // Verilen path'e göre JSON'dan değeri almak için yardımcı metot
    private JsonNode getNodeAtPath(JsonNode rootNode, String path) {
        String[] parts = path.split("/");
        JsonNode currentNode = rootNode;

        for (String part : parts) {
            if (part.isEmpty()) continue; // Boş parçaları geç
            currentNode = currentNode.path(part); // Yeni düğüme geç
            if (currentNode.isMissingNode()) {
                return null; // Path bulunamazsa null döndür
            }
        }

        return currentNode;
    }





    // JSON verisini id ile alır
    public String getOriginalJsonById(String id) throws Exception {
        return repository.findById(id)
                .orElseThrow(() -> new Exception("Kayıt bulunamadı."))
                .getJsonData();
    }

    // JSON string'ini bir JsonNode nesnesine dönüştürür
    public JsonNode parseJson(String jsonData) throws Exception {
        try {
            return objectMapper.readTree(jsonData);
        } catch (Exception e) {
            throw new Exception("Geçersiz JSON formatı: " + e.getMessage());
        }
    }

    // JSON'dan gelen bilgileri daha iyi yönetebilmek için `Map` formatında döndürme
    public Map<String, Object> parseJsonToMap(String jsonData) throws Exception {
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonData);
            return objectMapper.convertValue(jsonNode, Map.class);
        } catch (Exception e) {
            throw new Exception("Geçersiz JSON formatı: " + e.getMessage());
        }
    }
}
