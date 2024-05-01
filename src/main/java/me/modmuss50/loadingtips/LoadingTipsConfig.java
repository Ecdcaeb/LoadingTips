package me.modmuss50.loadingtips;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoadingTipsConfig {

	public int color = 0x000000;
	public String url = "";
	public List<String> tips = new ArrayList<>();

	public transient List<String> onlineTips = null;
	public transient List<String> allTips;
	private transient boolean refreshList = false;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static LoadingTipsConfig load(File configFile) throws IOException {
		if (configFile.exists()) {
			String json = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
			LoadingTipsConfig config = GSON.fromJson(json, LoadingTipsConfig.class);
			return config;
		} else {
			LoadingTipsConfig config = new LoadingTipsConfig();
			config.tips.add("This is an example tip");
			config.tips.add("Change your tips by editing loadingtips.json");
			String json = GSON.toJson(config);
			FileUtils.writeStringToFile(configFile, json, StandardCharsets.UTF_8);
			return config;
		}
	}

	public void loadOnline(Runnable complete) {
		if (url.isEmpty()) {
			return;
		}
		new Thread(() -> {
			try {
				String onlineJson = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
				JsonArray jsonArray = (JsonArray) new JsonParser().parse(s);
				int size = array.size();
        			onlineTips = new ArrayList<String>(size);
        			for(int i = 0; i < size; i++){
            				onlineTips.set(i, array.get(i).getAsString());
        			}
				refreshList = true;
				complete.run();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	public List<String> getTips() {
		if(refreshList || allTips == null){
			allTips = getAllTips();
			Collections.shuffle(allTips);
		}
		return allTips;
	}

	private List<String> getAllTips() {
		if (onlineTips == null) {
			return tips.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList());
		}
		return Stream.concat(tips.stream(), onlineTips.stream()).filter(s -> !s.isEmpty()).collect(Collectors.toList());
	}

}
