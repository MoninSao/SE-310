package AlarmClockRadio;
public class Radio {
    private String currentRadioStation;

    public Radio(String currentRadioStation) {
        this.currentRadioStation = currentRadioStation;
    }
    public String getRadioStation() {
        return currentRadioStation;
    }

    public void setRadioStation(String station) {
        currentRadioStation = station;
    }


}
