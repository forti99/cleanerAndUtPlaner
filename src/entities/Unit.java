package entities;

public enum Unit {
    SPEAR("spear", 1080),
    SWORD("sword", 1320),
    AXE("axe", 1080),
    SPY("spy", 540),
    LIGHT_CAVALRY("light", 600),
    HEAVY_CAVALRY("heavy", 660),
    RAM("ram", 1800),
    CATAPULT("catapult", 1800),
    PALADIN("knight", 600),
    NOBLE("snob", 2100);

    private final String name;
    private final double speed;

    Unit(String name, double speed) {
        this.name = name;
        this.speed = speed;
    }

    public static Unit intToUnit(int unitId) {
        return switch (unitId) {
            case 0 -> SPEAR;
            case 1 -> SWORD;
            case 2 -> AXE;
            case 3 -> SPY;
            case 4 -> LIGHT_CAVALRY;
            case 5 -> HEAVY_CAVALRY;
            case 6 -> RAM;
            case 7 -> CATAPULT;
            case 8 -> PALADIN;
            case 9 -> NOBLE;
            default -> throw new IllegalStateException("Unexpected value: " + unitId);
        };
    }

    public static Unit ultimateStringToUnit(String unitString) {
        return switch (unitString) {
            case "spear" -> SPEAR;
            case "sword" -> SWORD;
            case "axe" -> AXE;
            case "spy" -> SPY;
            case "light" -> LIGHT_CAVALRY;
            case "heavy" -> HEAVY_CAVALRY;
            case "ram" -> RAM;
            case "catapult" -> CATAPULT;
            case "knight" -> PALADIN;
            case "snob" -> NOBLE;
            default -> throw new IllegalStateException("Unexpected value: " + unitString);
        };
    }


    public String getName() {
        return name;
    }

    public double getSpeed() {
        return speed;
    }
}