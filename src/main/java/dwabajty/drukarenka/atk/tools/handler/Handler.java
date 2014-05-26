package dwabajty.drukarenka.atk.tools.handler;

public class Handler {
    private final Looper myLooper;

    public Handler() {
        this.myLooper = Looper.myLooper();
        this.myLooper.setHandler(this);
    }

    public void execute(Runnable runnable) {
        sendMessage(new Message(runnable));
    }

    public void sendMessage(Message message) {
        myLooper.addMessage(message);
    }

    public void handleMessage(Message message) {
        if(message.runnable != null) {
            message.runnable.run();
        }
    }
}

