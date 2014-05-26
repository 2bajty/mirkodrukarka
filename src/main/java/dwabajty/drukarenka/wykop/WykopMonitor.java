package dwabajty.drukarenka.wykop;


import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dwabajty.drukarenka.atk.tools.handler.Handler;
import dwabajty.drukarenka.wykop.Entry;
import dwabajty.drukarenka.wykop.NewEntryListener;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WykopMonitor {

    private final Handler handler = new Handler();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    Executor executor = Executors.newSingleThreadExecutor();
    HttpClient httpclient = HttpClients.createDefault();
    Gson gson = new Gson();

    public static final String BASE_URL = "http://a.wykop.pl/";
    HashMap<Integer, Entry> seenEntries = new HashMap<Integer, Entry>();

    public boolean useAPI = true;
    public NewEntryListener listener;

    public void start(boolean useAPI, NewEntryListener listener) {
        this.useAPI = useAPI;
        this.listener = listener;
        scheduler.scheduleAtFixedRate(loop, 1000, 10000, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        scheduler.shutdown();
    }

    Runnable loop = new Runnable() {
        @Override
        public void run() {
            try {
                System.out.print(".");
                List<Entry> entries;
                if(useAPI) {
                    entries = getMikroEntries();
                } else {
                    entries = scrapeMikroEntries();
                }

                if(seenEntries.size() == 0) {
                    for(int i = 3; i < entries.size(); i++) {
                        seenEntries.put(entries.get(i).id, entries.get(i));
                    }
                }

                final List<Entry> newEntries = new ArrayList<Entry>();

                for(int i = entries.size()-1; i >= 0; i--) {
                    if(!seenEntries.containsKey(entries.get(i).id)) {
                        newEntries.add(entries.get(i));
                        seenEntries.put(entries.get(i).id, entries.get(i));
                    }
                }

                handler.execute(new Runnable() {
                    @Override
                    public void run() {
                        listener.onNewEntry(newEntries);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public List<Entry> getMikroEntries() throws IOException {
        String messagesString = get(BASE_URL + "stream/index/appkey/xrtxog6O5X");
        try {
            Type listType = new TypeToken<ArrayList<Entry>>(){}.getType();
            List<Entry> entries = gson.fromJson(messagesString, listType);
            return entries;
        } catch (JsonSyntaxException e) {
            System.out.println("messagesString = " + messagesString);
            return null;
        }
    }

    public List<Entry> scrapeMikroEntries() throws IOException {
        ArrayList<Entry> ret = new ArrayList<Entry>();
        Document doc = Jsoup.connect("http://www.wykop.pl/mikroblog/").get();
        Elements entryBoxes = doc.select(".pding5");
        for(Element entryElement : entryBoxes) {
            Entry entry = new Entry();

            try {
                entry.author = entryElement.select(".lheight16").first().select(".fbold").get(1).text();
            } catch (Exception e) {
                System.out.println("Error reading author");
            }

            Elements contentElements = entryElement.select("p");
            String content = "";
            for(Element contentElement : contentElements) {
                content += contentElement.text();
            }
            entry.body = content;

            Element avatarElement = entryElement.select(".brc8").get(0);
            entry.author_avatar_med = avatarElement.absUrl("src");

            Element timeElement = entryElement.select("time").first();
            Element timeParent = timeElement.parent().parent();

            entry.date = timeElement.attr("datetime");
            entry.url = timeParent.attr("href");

            // http://www.wykop.pl/wpis/8151166/gimbynieznajo-reklama/
            entry.id = Integer.parseInt(entry.url.split("/")[4]);

            ret.add(entry);
        }



        return ret;
    }

    public String get(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);

        // Create a custom response handler
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            public String handleResponse(final HttpResponse response) throws IOException {
                int status = response.getStatusLine().getStatusCode();
                if (status >= 200 && status < 300) {
                    HttpEntity entity = response.getEntity();
                    return entity != null ? EntityUtils.toString(entity) : null;
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            }
        };

        String responseBody = httpclient.execute(httpGet, responseHandler);
        return responseBody;
    }


}
