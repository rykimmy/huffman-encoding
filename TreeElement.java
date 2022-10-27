public class TreeElement {
    private Character data;
    private Integer freq;

    public TreeElement(Character data, Integer freq) {
        this.data = data;
        this.freq = freq;
    }

    public Character getData() {
        return data;
    }

    public Integer getFreq() {
        return freq;
    }

    public void setData(Character data) {
        this.data = data;
    }

    public void setFreq(Integer freq) {
        this.freq = freq;
    }

    public String toString() {
        return (this.data + ":" + this.freq);
    }
}