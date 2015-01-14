package finki.ukim.agggregator.scholarships;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;


public class UrlFetcher {
	
	public static String fetchPost(String url, String postData) throws IOException {
		URLConnection connection;
		URL u = new URL(url);
		connection = u.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded; charset=UTF-8");
		connection.setRequestProperty("Content-Length", "46");
		OutputStreamWriter wr = new OutputStreamWriter(connection
				.getOutputStream());
		wr.write(postData);
		wr.flush();

		String html = readStringFromStream(connection.getInputStream());
		return html;
	}
	
	public static String fetchGet(String url) throws IOException {
		URL u = new URL(url);
		URLConnection connection;
		connection = u.openConnection();
		connection.setUseCaches(false);
		String html = readStringFromStream(connection.getInputStream());
		return html;
	}

	private static String readStringFromStream(InputStream inputStream) throws IOException {
		StringBuffer html = new StringBuffer();
		BufferedReader bf = new BufferedReader(new InputStreamReader(
				inputStream));
		char[] charBuffer = new char[4096];
		int count = 0;
		do {
			count = bf.read(charBuffer, 0, 4096);
			if (count >= 0)
				html.append(charBuffer, 0, count);
		} while (count > 0);
		bf.close();
		return html.toString();
	}
}
