package entities;

public class Runtime implements Comparable<Runtime> {
    private final int hours;
    private final int minutes;
    private final int seconds;

    public Runtime(int hours, int minutes, int seconds) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public static Runtime secondsToRuntime(int sec) {
        int remainingSec = sec;
        int hours = sec / 3600;
        remainingSec -= hours * 3600;
        int minutes = remainingSec / 60;
        remainingSec -= minutes * 60;
        int seconds = remainingSec;
        return new Runtime(hours, minutes, seconds);
    }

    public int toSeconds() {
        return hours * 3600 + minutes * 60 + seconds;
    }

    /**
     * Calculates the difference between this and another entities.Runtime
     *
     * @param runtime other entities.Runtime
     * @return difference between both Runtimes
     */
    public Runtime difference(Runtime runtime) {
        return secondsToRuntime(toSeconds() - runtime.toSeconds());
    }

    //Getter + Setter
    public int getHours() {
        return hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    @Override
    public int compareTo(Runtime otherRuntime) {
        if (this.hours < otherRuntime.getHours()) {
            return -1;
        } else {
            if (this.hours > otherRuntime.getHours()) {
                return 1;
            } else {
                if (this.minutes < otherRuntime.getMinutes()) {
                    return -1;
                } else {
                    if (this.minutes > otherRuntime.getMinutes()) {
                        return 1;
                    } else {
                        return Integer.compare(this.seconds, otherRuntime.getSeconds());
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Runtime: " + hours + ":" + minutes + ":" + seconds;
    }
}
