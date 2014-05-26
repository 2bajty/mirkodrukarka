package dwabajty.drukarenka;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class LayoutBuilder {

    String layout;
    int cnt = 0;

    public void loadLayout(String filename) throws IOException {
        layout = new String(Files.readAllBytes(new File(filename).toPath()), "UTF-8");
    }

    public String buildHtml(String author, String body, String embedUrl, String avatarUrl) {
        String result = layout.replace("##AVATAR_URL##", avatarUrl);
        result = result.replace("##NICKNAME##", author);
        result = result.replace("##BODY##", body);

        if(embedUrl != null) {
            result = result.replace("##EMBED##", embedUrl);
        } else {
            result = result.replace("##EMBED##", "http://i.imgur.com/L2jwegU.png");
        }

        try {
            Files.write(new File("test-result-" + cnt + ".html").toPath(), result.getBytes(), StandardOpenOption.CREATE);
            cnt++;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}
