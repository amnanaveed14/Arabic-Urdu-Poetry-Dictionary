# Arabic-Urdu Poetry Dictionary 📖

A **Java-based digital dictionary** of Arabic and Urdu poetry, designed to help users search, explore, and learn poetic expressions and meanings.  
Built using **Java 21**, **Maven**, and external libraries like **Al Khalil Morph**, with a structured Eclipse project setup.

---

## Features

- Search poetry entries by **Arabic or Urdu words**  
- Browse by **poets, themes, or genres**  
- Supports advanced **morphological analysis** using Al Khalil Morph library  
- Manage poetry entries (add, edit, delete)  
- Console-based or GUI interface (depending on implementation)  
- Useful for students, researchers, and poetry enthusiasts  

---

## Tech Stack

- **Language:** Java 21  
- **Build Tool:** Maven  
- **IDE:** Eclipse  
- **Libraries / JARs:** Al Khalil Morph (for Arabic morphology)  
- **Database **

---


## Prerequisites

1. **Java JDK 21** installed  
2. **Maven** installed and added to PATH  
3. **Eclipse IDE** (recommended)  
4. External libraries (Al Khalil Morph) added to `lib/` folder or Maven dependencies  

---

## How to Build & Run

### Using Eclipse
1. Open Eclipse → **File → Import → Existing Maven Project**  
2. Select the project folder  
3. Add external JARs (if not using Maven dependency) to the build path  
4. Run `Main.java` (or the main class)  

### Using Maven via Command Line
1. Navigate to project folder in terminal:

```bash
mvn clean install
