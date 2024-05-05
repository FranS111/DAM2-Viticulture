package manager;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager {
    // Singleton instance for Manager class
    private static Manager manager;
    
    // List to store input documents
    private ArrayList<Document> entradas;
    
    // MongoDB client, database, and collections
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
    
    // Documents for managing data operations
    private Document b; // Used for bodega operations
    private Document c; // Used for campo operations
    
    // Maps for storing relationships between documents
    private Map<Document, List<Document>> bodegaVidsMap; // Maps bodegas to their associated vids
    private List<Document> camposRecolectados; // List of campos that have been collected
    
    // Private constructor for singleton pattern
    private Manager() {
        this.entradas = new ArrayList<>();
        this.bodegaVidsMap = new HashMap<>();
        this.camposRecolectados = new ArrayList<>();
    }
    
    // Singleton instance getter
    public static Manager getInstance() {
        if (manager == null) {
            manager = new Manager();
        }
        return manager;
    }

    // Method to initialize the Manager instance
    public void init() {
        createSession(); // Create MongoDB session
        getEntrada(); // Retrieve input data
        manageActions(); // Manage actions based on input data
        showAllCampos(); // Display all campos
        mongoClient.close(); // Close MongoDB client connection
    }

    // Method to create MongoDB session
    private void createSession() {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("dam2tm06uf2p2");
    }

    // Method to manage actions based on input data
    private void manageActions() {
        for (Document entrada : this.entradas) {
            // Extract instruction from entrada document
            String instruccion = entrada.getString("instruccion").replace("'", "").trim();
            System.out.println(instruccion); // Print the instruction
            
            // Split the instruction into parts
            String[] split = instruccion.toUpperCase().split(" ");

            // Perform action based on the first part of the instruction
            switch (split[0]) {
                case "B":
                    addBodega(split); // Add a new bodega
                    break;
                case "C":
                    addCampo(split); // Add a new campo
                    break;
                case "V":
                    addVid(split); // Add a new vid
                    break;
                case "#":
                    vendimia(); // Perform vendimia action
                    break;
                default:
                    System.out.println("Instruccion incorrecta"); // Invalid instruction
            }
        }
    }

    // Method to add a new vid
    private void addVid(String[] split) {
        // Create a new vid document
        Document v = new Document();
        v.put("tipo", split[1].toUpperCase());
        v.put("cantidad", Integer.parseInt(split[2]));
        v.put("bodega", b.get("_id")); 
        v.put("campo", c.get("_id")); 

        // Insert the vid document into the Vid collection
        collection = database.getCollection("Vid");
        collection.insertOne(v);

        // Update the campo document with the new vid
        List<Document> vids = (List<Document>) c.get("vids");
        if (vids == null) {
            vids = new ArrayList<>();
        }
        vids.add(v);
        c.put("vids", vids);

        // Update the Vid collection with the new vid
        Document updateQuery = new Document();
        updateQuery.append("$set", new Document().append("vids", vids));
        collection.updateOne(new Document("_id", c.get("_id")), updateQuery);

        // Update the bodegaVidsMap with the new vid
        List<Document> bodegaVids = bodegaVidsMap.getOrDefault(b, new ArrayList<>());
        bodegaVids.add(v);
        bodegaVidsMap.put(b, bodegaVids);
    }

    // Method to perform vendimia action
    private void vendimia() {
        // Update each bodega document with its associated vids
        for (Map.Entry<Document, List<Document>> entry : bodegaVidsMap.entrySet()) {
            Document bodega = entry.getKey();
            List<Document> vids = entry.getValue();
            bodega.put("vids", vids);
        }
        
        // Mark campos as recolectado if they contain vids
        for (Document campo : camposRecolectados) {
            if (campo.containsKey("vids")) {
                campo.put("recolectado", true);
                collection = database.getCollection("Campo");
                Document updateQuery = new Document();
                updateQuery.append("$set", new Document().append("recolectado", true));
                collection.updateOne(new Document("_id", campo.get("_id")), updateQuery);
            }
        }
        
        // Clear the bodegaVidsMap after vendimia
        bodegaVidsMap.clear();
    }

    // Method to add a new campo
    private void addCampo(String[] split) {
        c = new Document();
        c.put("bodega", b.get("_id"));
        
        // Insert the campo document into the Campo collection
        collection = database.getCollection("Campo");
        collection.insertOne(c);
        
        // Add the campo to the list of camposRecolectados
        camposRecolectados.add(c);
    }

    // Method to add a new bodega
    private void addBodega(String[] split) {
        b = new Document();
        b.put("nombre", split[1]);
        
        // Insert the bodega document into the Bodega collection
        collection = database.getCollection("Bodega");
        collection.insertOne(b);
    }

    // Method to retrieve input data
    private void getEntrada() {
        collection = database.getCollection("Entrada");
        for (Document doc : collection.find()) {
            this.entradas.add(doc);
        }
    }

    // Method to display all campos
    private void showAllCampos() {
        collection = database.getCollection("Campo");
        for (Document doc : collection.find()) {
            System.out.println(doc.toJson());
        }
    }
}
