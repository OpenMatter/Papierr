package io.geeteshk.papierr;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FileHandler {

    public static String[] getTitles(Context context) {
        String[] titles = context.fileList();
        for(int i = 0; i < titles.length / 2; i++)
        {
            String temp = titles[i];
            titles[i] = titles[titles.length - i - 1];
            titles[titles.length - i - 1] = temp;
        }

        return titles;
    }

    public static String[] getContents(Context context) {
        List<String> contents = new ArrayList<>();
        String[] titles = getTitles(context);

        for (String title : titles) {
            contents.add(readFromFile(context, title));
        }

        return contents.toArray(new String[contents.size()]);
    }

    private static String readFromFile(Context context, String fileName) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(fileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString).append("\n");
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static void writeToFile(Context context, String fileName, String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setStar(Context context, String name, boolean star) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("io.geeteshk.papier", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(name + "_star", star).apply();
    }

    public static void removeStar(Context context, String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("io.geeteshk.papier", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(name + "_star").apply();
    }

    public static Boolean[] getStars(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("io.geeteshk.papier", Context.MODE_PRIVATE);
        List<Boolean> stars = new ArrayList<>();
        String[] titles = getTitles(context);

        for (String title : titles) {
            stars.add(sharedPreferences.getBoolean(title + "_star", false));
        }

        return stars.toArray(new Boolean[stars.size()]);
    }

    public static String[] getTimes(Context context) {
        String[] titles = getTitles(context);
        File[] files = new File[titles.length];
        String[] times = new String[files.length];

        for (int i = 0; i < files.length; i++) {
            files[i] = new File(context.getFilesDir() + "/" + titles[i]);
            times[i] = getDifference(new Date(files[i].lastModified()));
        }

        return times;
    }

    private static String getDifference(Date creation) {
        String difference;
        Calendar current = Calendar.getInstance();
        Calendar src = Calendar.getInstance();
        src.setTime(creation);

        if (current.get(Calendar.YEAR) == src.get(Calendar.YEAR)
                && current.get(Calendar.MONTH) == src.get(Calendar.MONTH)
                && current.get(Calendar.DAY_OF_MONTH) == src.get(Calendar.DAY_OF_MONTH)
                && current.get(Calendar.HOUR) == src.get(Calendar.HOUR)
                && current.get(Calendar.MINUTE) == src.get(Calendar.MINUTE)
                && current.get(Calendar.SECOND) == src.get(Calendar.SECOND)) {
            difference = "Now";
        } else if (current.get(Calendar.YEAR) == src.get(Calendar.YEAR)
                && current.get(Calendar.MONTH) == src.get(Calendar.MONTH)
                && current.get(Calendar.DAY_OF_MONTH) == src.get(Calendar.DAY_OF_MONTH)
                && current.get(Calendar.HOUR) == src.get(Calendar.HOUR)
                && current.get(Calendar.MINUTE) == src.get(Calendar.MINUTE)) {
            difference = String.valueOf(Math.abs(current.get(Calendar.SECOND) - src.get(Calendar.SECOND))) + "s";
        } else if (current.get(Calendar.YEAR) == src.get(Calendar.YEAR)
                && current.get(Calendar.MONTH) == src.get(Calendar.MONTH)
                && current.get(Calendar.DAY_OF_MONTH) == src.get(Calendar.DAY_OF_MONTH)
                && current.get(Calendar.HOUR) == src.get(Calendar.HOUR)) {
            difference = String.valueOf(Math.abs(current.get(Calendar.MINUTE) - src.get(Calendar.MINUTE))) + "m";
        } else if (current.get(Calendar.YEAR) == src.get(Calendar.YEAR)
                && current.get(Calendar.MONTH) == src.get(Calendar.MONTH)
                && current.get(Calendar.DAY_OF_MONTH) == src.get(Calendar.DAY_OF_MONTH)) {
            difference = String.valueOf(Math.abs(current.get(Calendar.HOUR) - src.get(Calendar.HOUR))) + "h";
        } else if (current.get(Calendar.YEAR) == src.get(Calendar.YEAR)
                && current.get(Calendar.MONTH) == src.get(Calendar.MONTH)) {
            difference = String.valueOf(Math.abs(current.get(Calendar.DAY_OF_MONTH) - src.get(Calendar.DAY_OF_MONTH))) + "d";
        } else if (current.get(Calendar.YEAR) == src.get(Calendar.YEAR)) {
            difference = String.valueOf(Math.abs(current.get(Calendar.MONTH) - src.get(Calendar.MONTH))) + "m";
        } else {
            difference = String.valueOf(Math.abs(current.get(Calendar.YEAR) - src.get(Calendar.YEAR))) + "y";
        }

        return difference;
    }
}
