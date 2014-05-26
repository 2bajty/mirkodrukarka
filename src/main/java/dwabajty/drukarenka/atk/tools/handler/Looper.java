package dwabajty.drukarenka.atk.tools.handler;


import java.util.concurrent.LinkedBlockingDeque;

public class Looper {
    private static ThreadLocal<Looper> localLooper;

    public static void prepare() {
        localLooper = new ThreadLocal<Looper> () {
            @Override protected Looper initialValue() {
                return new Looper();
            }
        };
    }

    public static Looper myLooper() {
        return localLooper.get();
    }

    public static void loop() {
        myLooper().runLoop();
    }

    private final LinkedBlockingDeque<Message> messages;
    private Handler handler;

    private Looper() {
        messages = new LinkedBlockingDeque<Message>();
        // initialize parts, like the message queue...
    }

    protected void addMessage(Message message) {
        messages.add(message);
    }

    protected void setHandler(Handler h) {
        this.handler = h;
    }

    private void runLoop() {
        while(true) {
            Message message = null;
            try {
                message = messages.take();
                handler.handleMessage(message);
            } catch (InterruptedException e) {}
        }
    }
}

