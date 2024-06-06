package com.github.zeropoint.bugfix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.launchwrapper.Launch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ICrashCallable;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

public class ModsUtil {

    public static void dumpDiff() {
        if (Boolean.getBoolean("ZeroPointBugfix.diff")) {
            Bugfix.LOG.info("Staring dump diff mods list...");
            String string = getDiff();

            File dumpFile = new File(Launch.minecraftHome, "diff.txt");
            // 如果文件不存在，则创建新文件
            try {
                if (!dumpFile.exists()) {
                    dumpFile.createNewFile();
                }
                FileWriter fw = new FileWriter(dumpFile);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(string);
                bw.close();
            } catch (IOException e) {
                Bugfix.LOG.error("Dump diff mods list fatal: {}", e.getMessage());
            }
            Bugfix.LOG.info("Finished dump diff mods list");
        }
    }

    public static void dumpMods(List<ModContainer> mods) {
        if (Boolean.getBoolean("ZeroPointBugfix.dumpMods")) {
            Bugfix.LOG.info("Staring dump mods list...");
            List<ModsPair> modPairs = new ArrayList<>();
            for (ModContainer mod : mods) {
                if (mod.getModId()
                    .equals(Bugfix.MODID)) continue;
                modPairs.add(new ModsPair(mod.getModId(), mod.getVersion()));
            }
            modPairs.sort(Comparator.comparing(o -> o.modid));
            Gson gson = new GsonBuilder().setPrettyPrinting()
                .create();;
            String json = gson.toJson(modPairs);
            File dumpFile = new File(Launch.minecraftHome, "dumpMods.json");
            // 如果文件不存在，则创建新文件
            try {
                if (!dumpFile.exists()) {
                    dumpFile.createNewFile();
                }
                FileWriter fw = new FileWriter(dumpFile);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(json);
                bw.close();
            } catch (IOException e) {
                Bugfix.LOG.error("Dump mods list fatal: {}", e.getMessage());
            }
            Bugfix.LOG.info("Finished dump mods list");
        }
    }

    public static void registry() {

        boolean hasNHCore = false;
        try {
            Class.forName("com.dreammaster.lib.Refstrings");
            hasNHCore = true;
        } catch (ClassNotFoundException e) {

        }
        if (hasNHCore) {
            FMLCommonHandler.instance()
                .registerCrashCallable(new ICrashCallable() {

                    @Override
                    public String getLabel() {
                        return "Mods Changed";
                    }

                    @Override
                    public String call() throws Exception {
                        return getDiff();
                    }
                });
        }
    }

    private static String getDiff() {
        // 加载json，对比Mod。输出
        String json = loadFile();
        if (json.isEmpty())
            return "Unable to load file " + com.dreammaster.lib.Refstrings.MODPACKPACK_VERSION + ".json";
        Gson gson = new GsonBuilder().create();
        JsonArray jsonArray = gson.fromJson(json, JsonArray.class);
        List<ModContainer> mods = Loader.instance()
            .getModList();
        List<ModsPair> missingMods = new LinkedList<>();
        List<ModsPair> addedMods = new LinkedList<>();
        for (JsonElement jsonElement : jsonArray) {
            ModsPair modsPair = gson.fromJson(jsonElement, ModsPair.class);
            missingMods.add(modsPair);
        }

        for (ModContainer mod : mods) {
            addedMods.add(new ModsPair(mod.getModId(), mod.getVersion()));
        }

        List<ModsPair> removedMods = new ArrayList<>(missingMods);
        missingMods.removeAll(addedMods);
        addedMods.removeAll(removedMods);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n\t * Added Mods\n");
        for (ModsPair modsPair : addedMods) {
            stringBuilder.append("\t\t")
                .append(modsPair.modid)
                .append(": ")
                .append(modsPair.version)
                .append("\n");
        }

        stringBuilder.append("\t * Missing Mods\n");
        for (ModsPair modsPair : missingMods) {
            stringBuilder.append("\t\t")
                .append(modsPair.modid)
                .append(": ")
                .append(modsPair.version)
                .append("\n");
        }

        return stringBuilder.toString();
    }

    private static String loadFile() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = LateMixinLoader.class.getClassLoader()
                .getResourceAsStream(com.dreammaster.lib.Refstrings.MODPACKPACK_VERSION + ".json");

            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                reader.close();
                inputStream.close();
            } else {
                Bugfix.LOG.info("File: {}.json not found", com.dreammaster.lib.Refstrings.MODPACKPACK_VERSION);
            }

        } catch (IOException e) {
            Bugfix.LOG.info("File: {} loading failure", e.getMessage());
        }
        return stringBuilder.toString();
    }

    public static class ModsPair {

        public String modid;
        public String version;

        public ModsPair(String modid, String version) {
            this.modid = modid;
            this.version = version;
        }

        @Override
        public int hashCode() {
            return modid.hashCode() | version.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj.hashCode() == this.hashCode();
        }
    }
}
