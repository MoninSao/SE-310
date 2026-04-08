package AlarmClockRadio;

public class Radio {
    private String currentRadioStation;
    private int currentVolume;
    private boolean isRadioOn;

    public Radio(String currentRadioStation, int currentVolume, boolean isRadioOn) {
        this.currentRadioStation = currentRadioStation;
        this.currentVolume = currentVolume;
        this.isRadioOn = isRadioOn;
    }

    public String getRadioStation() {
        return currentRadioStation;
    }

    public void setRadioStation(String station) {
        currentRadioStation = station;
    }

    public int getVolume() {
        return currentVolume;
    }

    public void setVolume(int volume) {
        currentVolume = volume;
    }

    public boolean getIsRadioOn() {
        return isRadioOn;
    }

    public void turnRadioOff() {
        isRadioOn = false;
    }

    public void turnRadioOn() {
        isRadioOn = true;
    }

}
