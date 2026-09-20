# KBTU · Backend Framework. Spring (Fall 2026)

## TSIS Practice 2 & Practice 3

**Student:** Aliaskarov Nursultan  
**Course Module:** INFT3132 · Week 2 & Week 3  

---

### Implementation Details
1. **Project Build & Structure (Week 2):** Fully managed by Maven. Parent versions are handled by the Spring Boot BOM. Custom configuration is isolated using profiles.
2. **Externalized Configuration:** Added structured environment settings via modern Java `record` annotated with `@ConfigurationProperties` and validated using `@Validated`.
3. **Inversion of Control & DI (Week 3):** Refactored the architecture to strict **Constructor Injection**. All service-layer fields are marked as `private final`. No field-level `@Autowired` annotations are used.
4. **Conditional Beans:** Implemented a polymorphic service layer (`NotificationService`) where component creation is driven dynamically by profile-specific configuration via `@ConditionalOnProperty`.

---

### How to Build and Run

#### 1. Build the Artifact
To clean the project and compile the executable "fat" jar, run:
```bash
./mvnw clean package
```

#### 2. Run with Development Profile (Discount Season Enabled)
This profile activates `DiscountNotificationService` and returns special discount rates:
```bash
java -jar target/course-project-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```
* **Test Endpoint:** `http://localhost:8080/order`
* **Expected Output:** `Статус заказа: Внимание! Активирован режим распродаж и скидок!`

#### 3. Run with Testing Profile (Standard Rates Active)
This profile switches off the discount logic and falls back to `RegularNotificationService`:
```bash
java -jar target/course-project-0.0.1-SNAPSHOT.jar --spring.profiles.active=test
```
* **Test Endpoint:** `http://localhost:8080/order`
* **Expected Output:** `Статус заказа: Обычный режим работы. Действуют стандартные тарифы.`
