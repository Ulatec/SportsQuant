package BaseballQuant.Model;

import java.util.Date;

public class IndividualBet {
    boolean success;
    double odds;
    String type;
    Date date;
    double betSize;
    double endingMoney;
    public IndividualBet(boolean success, double odds, String type, Date date, double betSize, double endingMoney) {
        this.success = success;
        this.odds = odds;
        this.type = type;
        this.date = date;
        this.betSize = betSize;
        this.endingMoney = endingMoney;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public double getOdds() {
        return odds;
    }

    public void setOdds(double odds) {
        this.odds = odds;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getBetSize() {
        return betSize;
    }

    public void setBetSize(double betSize) {
        this.betSize = betSize;
    }

    public double getEndingMoney() {
        return endingMoney;
    }

    public void setEndingMoney(double endingMoney) {
        this.endingMoney = endingMoney;
    }
}
