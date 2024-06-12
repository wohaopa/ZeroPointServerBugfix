package com.github.zeropoint.bugfix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.minecraft.launchwrapper.Launch;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Updater extends Thread {

    private static final String ASSET_DOWNLOAD_URL = "https://github.com/wohaopa/ZeroPointServerBugfix/releases/download/%s/ZeroPointBugfix-%s.jar";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/wohaopa/ZeroPointServerBugfix/releases/latest";

    public static void checkUpdate() {
        new Updater().start();
    }

    private Updater() {
        setName(Bugfix.MODID + "UpdaterThread");
    }

    @Override
    public void run() {
        try {
            String latestVersion = getLatestVersion();
            if (!Tags.VERSION.equals(latestVersion)) {
                canUpdate = true;
                newVersion = latestVersion;

                String jarFilePath = Updater.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();
                if (jarFilePath == null) {
                    SAVE_DIR = new File(Launch.minecraftHome, "autoDownloadDir");
                } else {
                    oldMod = new File(jarFilePath);
                    SAVE_DIR = oldMod.getParentFile();
                }

                Bugfix.LOG.info("New version available: " + latestVersion);
                downloadFile(String.format(ASSET_DOWNLOAD_URL, latestVersion, latestVersion));
                downloaded = true;
                if (oldMod != null) oldMod.deleteOnExit();
                Bugfix.LOG.info("Update downloaded.");
            } else {
                Bugfix.LOG.info("You are using the latest version.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static File SAVE_DIR;

    public static boolean canUpdate;
    public static boolean downloaded;
    public static String newVersion;
    public static File newMod;
    public static File oldMod;

    private static String getLatestVersion() throws Exception {
        URL url = new URL(GITHUB_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        JsonObject jsonObject = new Gson().fromJson(content.toString(), JsonObject.class);

        return jsonObject.get("tag_name")
            .getAsString();
    }

    private static void downloadFile(String fileURL) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            if (disposition != null) {
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 9);
                }
            } else {
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1);
            }

            InputStream inputStream = httpConn.getInputStream();
            newMod = new File(SAVE_DIR, fileName);
            if (!SAVE_DIR.exists()) SAVE_DIR.mkdirs();

            FileOutputStream outputStream = new FileOutputStream(newMod);

            int bytesRead;
            byte[] buffer = new byte[4096];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
            Bugfix.LOG.info("File downloaded to " + newMod.toString());
        } else {
            Bugfix.LOG.info("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }

}
