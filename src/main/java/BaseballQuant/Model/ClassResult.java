package BaseballQuant.Model;

public class ClassResult {

    public int count;
    public int classCorrect;
    public int classIncorrect;
    public double percent;


    public ClassResult() {
        classCorrect = 0;
        classIncorrect = 0;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public int getClassCorrect() {
        return classCorrect;
    }

    public void setClassCorrect(int classCorrect) {
        this.classCorrect = classCorrect;
    }

    public int getClassIncorrect() {
        return classIncorrect;
    }

    public void setClassIncorrect(int classIncorrect) {
        this.classIncorrect = classIncorrect;
    }
}
