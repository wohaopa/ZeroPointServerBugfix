package com.github.zeropoint.bugfix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import net.minecraft.launchwrapper.Launch;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Updater extends Thread {

    private static final String ASSET_DOWNLOAD_URL = "https://github.com/wohaopa/ZeroPointServerBugfix/releases/download/%s/ZeroPointBugfix-%s.jar";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/wohaopa/ZeroPointServerBugfix/releases/latest";
    public static boolean debug = true;

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

                String classPath = Updater.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getFile();
                int index = classPath.indexOf(".jar!");

                oldMod = new File(classPath.substring(6, index + 4));

                if (oldMod.exists()) {
                    SAVE_DIR = oldMod.getParentFile();
                } else {
                    SAVE_DIR = new File(Launch.minecraftHome, "autoDownloadDir");
                }

                Bugfix.LOG.info("New version available: " + latestVersion);
                downloadFile(String.format(ASSET_DOWNLOAD_URL, latestVersion, latestVersion));
                if (verify()) {
                    downloaded = true;
                    if (oldMod != null && oldMod.exists()) { // It cannot be deleted on windows
                        oldMod.deleteOnExit();
                        Runtime.getRuntime()
                            .addShutdownHook(new Thread(() -> {
                                if (oldMod.exists() && !oldMod.delete()) {
                                    File file = new File(SAVE_DIR, "delete.bat");
                                    createDeleteScript(file);
                                    try {
                                        Runtime.getRuntime()
                                            .exec(
                                                "cmd /c start " + file.getAbsolutePath()
                                                    + " \""
                                                    + oldMod.getAbsolutePath()
                                                    + "\"");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }));
                    }
                    Bugfix.LOG.info("Update downloaded.");
                } else {
                    Bugfix.LOG.info("Download file verification failed!");
                }

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

    private static boolean verify() {
        String keystoreFile = "ZeroPointBugfixPublic.jks";
        String keystorePassword = "ZeroPointBugfix";
        String alias = "ZeroPointBugfix";

        try {
            JarFile jarFile = new JarFile(oldMod);

            JarEntry jarEntry1 = jarFile.getJarEntry(keystoreFile);

            if (jarEntry1 != null) {

                InputStream inputStream = jarFile.getInputStream(jarEntry1);
                KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
                keystore.load(inputStream, keystorePassword.toCharArray());

                Certificate cert = keystore.getCertificate(alias);
                if (cert == null) {
                    Bugfix.LOG.warn("Certificate not found!");
                    return false;
                }
                PublicKey publicKey = cert.getPublicKey();

                JarInputStream jarInputStream = new JarInputStream(new FileInputStream(newMod), true);
                JarEntry jarEntry2;

                while ((jarEntry2 = jarInputStream.getNextJarEntry()) != null) {
                    if (jarEntry2.isDirectory()) {
                        continue;
                    }

                    byte[] buffer = new byte[8192];
                    while (jarInputStream.read(buffer, 0, buffer.length) != -1) {

                    }

                    Certificate[] certs = jarEntry2.getCertificates();
                    if (certs != null) {
                        for (Certificate jarCert : certs) {
                            try {
                                jarCert.verify(publicKey);
                            } catch (Exception e) {
                                Bugfix.LOG
                                    .warn("Validation failed: the signature of {} is invalid.", jarEntry2.getName());
                                return false;
                            }
                        }
                    } else {
                        return false;
                    }
                }

                jarInputStream.close();
                inputStream.close();
                jarFile.close();
                Bugfix.LOG.info("Successful!");
                return true;
            }
        } catch (IOException | CertificateException | KeyStoreException | NoSuchAlgorithmException e) {
            return false;
        }

        return false;
    }

    private static final String cmdScript = """
        @echo off
        echo Wait to delete the file
        ping 127.0.0.1 -n 2 > nul
        if exist "%~1" (
            del "%~1"
            echo Deleted file %~1
        ) else (
            echo File %~1 does not exist
        )
        echo Press any key to exit
        pause > nul
        del "%~f0" & exit
        """;

    private static void createDeleteScript(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(cmdScript);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
