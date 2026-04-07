package AlarmClockRadio;

import java.time.LocalTime;

public class main
{
    public static void main(String[] args)
    {
        int i;
        LocalTime alarmTime = LocalTime.of(8,0);
        LocalTime currentTime = LocalTime.of(8,0);
        AlarmClock myAlarmClock = new AlarmClock(alarmTime, true, currentTime);

        for (i = 0; i < 5; i++)
        {
            System.out.println("Time: " + myAlarmClock.getCurrentTime());

            for (int seconds = 0; seconds < 60; seconds++)
            {
                myAlarmClock.getIsAlarmOn();
                myAlarmClock.tick();
            }
        }
        myAlarmClock.snooze();

        for (i = 0; i < 9; i++)
        {
            System.out.println("Time: " + myAlarmClock.getCurrentTime());
            for (int seconds = 0; seconds < 60; seconds++)
            {
                myAlarmClock.getIsAlarmOn();
                myAlarmClock.tick();
            }
        }
        myAlarmClock.turnAlarmOff();
    }
}