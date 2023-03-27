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

    public void addRuntime(Runtime runtimeToAdd) {
        addHours(runtimeToAdd.getHours());
        addMinutes(runtimeToAdd.getMinutes());
        addSeconds(runtimeToAdd.getSeconds());
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
        int hours;
        int minutes;
        int seconds;

        if (this.hours < 0) {
            hours = this.hours * (-1);
        } else {
            hours = this.hours;
        }

        if (this.minutes < 0) {
            minutes = this.minutes * (-1);
        } else {
            minutes = this.minutes;
        }

        if (this.seconds < 0) {
            seconds = this.seconds * (-1);
        } else {
            seconds = this.seconds;
        }

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
                        if (this.toSeconds() < otherRuntime.getSeconds()) {
                            return -1;
                        } else {
                            if (this.seconds > otherRuntime.getSeconds()) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
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
