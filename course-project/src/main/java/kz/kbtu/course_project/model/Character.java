package kz.kbtu.course_project.model;

import java.util.UUID;

public class Character {
    private UUID id;
    private String name;
    private String role;

    //Stats
    private int level;
    private int eddies;

    private int maxHealth;
    private int maxHumanity;

    private int currentHealth;
    private int currentHumanity;
    private int currentLuckPoints;

    //Characteristics
    private int intelligence;
    private int reflex;
    private int dexterity;
    private int technology;
    private int cool;
    private int will;
    private int movement;
    private int body;
    private int empathy;
    private int luck;

     public Character(
        UUID id, String name, String role, 
        int eddies, 
        int intelligence, int reflex, int dexterity, int technology, int cool, int will, int movement, int body, int empathy, int luck) {
        this.id = id;
        this.name = name;
        this.role = role;

        this.eddies = eddies;

        this.intelligence = intelligence;
        this.reflex = reflex;
        this.dexterity = dexterity;
        this.technology = technology;
        this.cool = cool;
        this.will = will;
        this.movement = movement;
        this.body = body;
        this.empathy = empathy;
        this.luck = luck;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getRole() { return role; }

    /* 
        Stat Getters
    */
    public int getLevel() { return level; }
    public int getEddies() { return eddies; }

    public int getMaxHitPoints() { return maxHealth; }
    public int getMaxHumanity() { return maxHumanity; }

    public int getCurrentHitPoints() { return currentHealth; }
    public int getCurrentHumanity() { return currentHumanity; }
    public int getCurrentLuckPoints() { return currentLuckPoints; }

    /* 
        Stat Setters
    */
    public void setName(String name) { this.name = name; }
    public void setRole(String role) { this.role = role; }

    public void setLevel(int level) { this.level = level; }
    public void setEddies(int eddies) { this.eddies = eddies; }

    public void setMaxHitPoints(int maxHealth) { this.maxHealth = maxHealth; }
    public void setMaxHumanity(int maxHumanity) { this.maxHumanity = maxHumanity; }

    public void setCurrentHitPoints(int currentHealth) { this.currentHealth = currentHealth; }
    public void setCurrentHumanity(int currentHumanity) { this.currentHumanity = currentHumanity; }
    public void setCurrentLuckPoints(int currentLuckPoints) { this.currentLuckPoints = currentLuckPoints; }

    //Characteristics getters
    public int getIntelligence() { return intelligence; }
    public int getReflex() { return reflex; }
    public int getDexterity() { return dexterity; }
    public int getTechnology() { return technology; }
    public int getCool() { return cool; }
    public int getWill() { return will; }
    public int getMovement() { return movement; }
    public int getBody() { return body; }
    public int getEmpathy() { return empathy; }
    public int getLuck() { return luck; }
}
