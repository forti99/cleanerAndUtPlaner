package util;

import entities.Runtime;

public class Settings {
    public static final double WORLDSPEED = 3.2;
    public static final double UNIT_MODIFICATOR = 0.625;
    public static final String ULTIMATE_STRING_ZC = "&8&false&true&spear=/sword=/axe=/archer=/spy=/light=/marcher=/heavy=/ram=/catapult=/knight=/snob=/militia=MA==";
    public static final String ULTIMATE_STRING_UT = "&0&false&true&spear=/sword=/axe=/archer=/spy=/light=/marcher=/heavy=/ram=/catapult=/knight=/snob=/militia=MA==";

    public static final Runtime MAX_RUNTIME_START = new Runtime(0, 0, 1);
    public static final int RUNTIME_STEP_INCREASE_IN_SECONDS = 60;
    public static final Runtime MAX_RUNTIME_DIFFERENCE = new Runtime(12, 0, 0);
    public static int MAX_CLEANER_TO_SEND_FROM_VILLAGE = 1;
    public static int MAX_CLEANER_TO_SEND_TO_TARGET_VILLAGE = 1;

    private Settings() {
    }
}
