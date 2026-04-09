package AlarmClockRadio;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class main {
    public static void main(String[] args) {
        int i;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("h:mm a");
        LocalTime alarmTime = LocalTime.of(8, 6);
        LocalTime currentTime = LocalTime.of(8, 0);
        AlarmClockRadio myAlarmClock = new AlarmClockRadio(alarmTime, true, currentTime, "1060 AM", 100, true);

        System.out.println("The radio was turned on and is playing " + myAlarmClock.getRadioStation());

        for (i = 0; i < 6; i++) {
            System.out.println("Time: " + myAlarmClock.getCurrentTime().format(fmt));

            for (int seconds = 0; seconds < 60; seconds++) {
                myAlarmClock.tick(); // tick is called 60 times to increase the time to 1 minute
            }
        }
        if ((myAlarmClock.getCurrentTime().equals(alarmTime)) && (myAlarmClock.getIsAlarmOn() == true)) {
            System.out.println("The radio is playing " + myAlarmClock.getRadioStation());
        }

        myAlarmClock.snooze();
        System.out.println("Snooze was pressed");

        for (i = 0; i < 9; i++) {
            System.out.println("Time: " + myAlarmClock.getCurrentTime().format(fmt));
            for (int seconds = 0; seconds < 60; seconds++) {
                myAlarmClock.tick();
            }
        }
        if ((myAlarmClock.getCurrentTime().equals(myAlarmClock.getAlarm())) && (myAlarmClock.getIsAlarmOn() == true)) {
            System.out.println("The radio is playing " + myAlarmClock.getRadioStation());
        }
        myAlarmClock.turnAlarmOff();
        if (myAlarmClock.getIsAlarmOn() == false) {
            System.out.println("The alarm was shut off");
        }

        System.out.println("The alarm time is: " + myAlarmClock.getAlarm().format(fmt));
    }
}