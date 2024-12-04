##  Core Features

###  Initialization and Configuration
- **MongoDB Session Management**:
  - Connects to a MongoDB instance on `localhost:27017`.
  - Uses the database `dam2tm06uf2p2` to store collections.

###  Instruction Management
- Processes instructions stored in the `Entrada` collection:
  - **B**: Adds a new bodega.
  - **C**: Adds a new campo.
  - **V**: Adds a new vid and associates it with a campo and bodega.
  - **#**: Executes a vendimia (harvest) operation.
- Invalid instructions are flagged for correction.

###  Data Operations
#### **Bodegas**
- Stores information about bodegas in the `Bodega` collection.
- Associates bodegas with vids and campos.

#### **Campos**
- Stores information about campos in the `Campo` collection.
- Tracks the association of campos with bodegas and vids.
- Marks campos as "recolectado" (harvested) during the vendimia process.

#### **Vids**
- Stores information about vines (vids) in the `Vid` collection.
- Associates each vid with a specific campo and bodega.

###  Vendimia (Harvest) Management
- Updates bodega documents with associated vids.
- Marks campos containing vids as "recolectado" (harvested).
- Clears intermediate data structures to prepare for the next session.

###  MongoDB Integration
- Retrieves input data from the `Entrada` collection.
- Inserts and updates data dynamically in the following collections:
  - `Bodega`
  - `Campo`
  - `Vid`

###  Utilities and Display
- Displays all campos and their associated data in JSON format for debugging or inspection.
- Provides real-time feedback for each instruction processed.
