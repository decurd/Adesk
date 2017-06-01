package kr.co.roonets.adesk.fcm;

public class NotificationData {

    public static final String TEXT = "TEXT";

    private String imageName;
    private int id; // identificador da notificação
    private String title;
    private String textMessage;
    private String sound;
    private String bigBodyMessage;
    private String bigTitle;
    private String bigImage;

    public NotificationData(String imageName, int id, String title, String textMessage, String sound, String bigTitle, String bigBodyMessage, String bigImage) {
        this.imageName = imageName;
        this.id = id;
        this.title = title;
        this.textMessage = textMessage;
        this.sound = sound;
        this.bigBodyMessage = bigBodyMessage;
        this.bigTitle = bigTitle;
        this.bigImage = bigImage;
    }

    public String getBigBodyMessage() {
        return bigBodyMessage;
    }

    public void setBigBodyMessage(String bigBodyMessage) {
        this.bigBodyMessage = bigBodyMessage;
    }

    public String getBigTitle() {
        return bigTitle;
    }

    public void setBigTitle(String bigTitle) {
        this.bigTitle = bigTitle;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getBigImage() {
        return bigImage;
    }

    public void setBigImage(String bigImage) {
        this.bigImage = bigImage;
    }
}