package AlarmClockRadio;

import java.time.LocalTime;

public class Clock {
    private LocalTime currentTime;

    public Clock(LocalTime currentTime) {
        this.currentTime = currentTime;
    }

    public LocalTime getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(LocalTime time) {
        currentTime = time;
    }
}
