package processing;

import entities.Runtime;

public class Settings {
    public static final double WORLDSPEED = 3.2;
    public static final double UNIT_MODIFICATOR = 0.625;
    public static final String ULTIMATE_STRING_UT = "&0&false&true&spear=/sword=/axe=/archer=/spy=/light=/marcher=/heavy=/ram=/catapult=/knight=/snob=/militia=MA==";
    public static final String ULTIMATE_STRING_ZC = "&8&false&true&spear=/sword=/axe=/archer=/spy=/light=/marcher=/heavy=/ram=/catapult=/knight=/snob=/militia=MA==";

    public static final Runtime RUNTIME_STEP_INCREASE = new Runtime(0, 1, 0);

    private Settings() {
    }
}
