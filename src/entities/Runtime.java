package entities;

public class Runtime implements Comparable<Runtime> {
    private int hours;
    private int minutes;
    private int seconds;

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

    public void addSeconds(int seconds) {
        addMinutes((this.seconds + seconds) / 60);
        this.seconds = (this.seconds + seconds) % 60;
    }

    public void addMinutes(int minutes) {
        addHours((this.minutes + minutes) / 60);
        this.minutes = (this.minutes + minutes) % 60;
    }

    public void addHours(int hours) {
        this.hours += hours;
    }

    /**
     * @return returns a all positive runtime of this runtime (all negative values converted to positive)
     */
    public Runtime getPositiveRuntime() {
        return new Runtime(Math.abs(this.hours), Math.abs(this.minutes), Math.abs(this.seconds));
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
        return "Runtime{" +
                "hours=" + hours +
                ", minutes=" + minutes +
                ", seconds=" + seconds +
                '}';
    }
}
