package processing;

import entities.Runtime;

public class Settings {
    public static final double WORLDSPEED = 3.2;
    public static final double UNIT_MODIFICATOR = 0.625;
    public static final String ULTIMATE_STRING_ZC = "&8&false&true&spear=/sword=/axe=/archer=/spy=/light=/marcher=/heavy=/ram=/catapult=/knight=/snob=/militia=MA==";

    public static final Runtime RUNTIME_STEP_INCREASE = new Runtime(0, 1, 0);
    public static final Runtime MAX_RUNTIME_DIFFERENCE = new Runtime(24, 0, 0);
    public static int MAX_CLEANER_TO_SEND_FROM_VILLAGE = 2;

    private Settings() {
    }
}
