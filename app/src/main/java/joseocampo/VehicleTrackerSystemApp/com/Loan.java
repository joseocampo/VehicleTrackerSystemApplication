package joseocampo.VehicleTrackerSystemApp.com;

import java.util.Date;

public class Loan {
    private int consecutive;
    private String date;
    private String justification;
    private String user;
    private String vehicle;
    private String beginHour;
    private String endHour;
    private String details;

    public Loan(int consecutive, String date, String justification, String user, String vehicle, String beginHour, String endHour, String details) {
        this.consecutive = consecutive;
        this.date = date;
        this.justification = justification;
        this.user = user;
        this.vehicle = vehicle;
        this.beginHour = beginHour;
        this.endHour = endHour;
        this.details = details;
    }

    public Loan() {
        this.consecutive = 0;
        this.date = null;
        this.justification = "";
        this.user = "";
        this.vehicle = "";
        this.beginHour = null;
        this.endHour = null;
        this.details = "";
    }

    public int getConsecutive() {
        return consecutive;
    }

    public void setConsecutive(int consecutive) {
        this.consecutive = consecutive;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getBeginHour() {
        return beginHour;
    }

    public void setBeginHour(String beginHour) {
        this.beginHour = beginHour;
    }

    public String getEndHour() {
        return endHour;
    }

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        StringBuilder r = new StringBuilder();

        r.append(getConsecutive()+" - Destino: " + getDetails());
        r.append("\n");
        r.append("Justificación: " + getJustification());
        r.append("\n");
        r.append("Hora Inicio: " + getBeginHour());
        r.append("   ");
        r.append("Hora Final: " + getEndHour());
        r.append("\n");
        r.append("Vehículo: " + getVehicle());
        r.append("\n");
        r.append("Usuario a cargo del vehículo: " + getUser());
        r.append("\n");
        return r.toString();
    }
}
