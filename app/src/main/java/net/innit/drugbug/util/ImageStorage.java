package net.innit.drugbug.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import net.innit.drugbug.PrefScreenActivity;

import java.io.File;

public class ImageStorage {
    private static final String IMAGE_DIR = "images/medications";

    public final File LOCATION_INTERNAL;
    public final File LOCATION_EXTERNAL;

    private final Context context;
    private String displayText;     // Text for display
    private String locationType;    // File storage location type - INTERNAL or EXTERNAL
    private File rootDir;           // Root directory for the storage type
    private File absDir;            // Full directory - rootDir + IMAGE_DIR

    public ImageStorage(Context context) {
        this.context = context;

        LOCATION_EXTERNAL = new File(Environment.getExternalStorageDirectory(), context.getPackageName());
        LOCATION_INTERNAL = context.getFilesDir();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.locationType = sharedPreferences.getString(PrefScreenActivity.KEY_IMAGE_STORAGE, PrefScreenActivity.DEFAULT_IMAGE_STORAGE);

        switch (locationType) {
            case "INTERNAL":
                this.displayText = "Internal";
                this.rootDir = LOCATION_INTERNAL;
                this.absDir = new File(rootDir, IMAGE_DIR);
                break;
            case "EXTERNAL":
                this.displayText = "External";
                this.rootDir = LOCATION_EXTERNAL;
                this.absDir = new File(rootDir, IMAGE_DIR);
                break;
        }
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String type) {
        switch (type) {
            case "INTERNAL":
                File rootDir = LOCATION_INTERNAL;
                File internalDir = new File(rootDir, IMAGE_DIR);
                // if internal directory doesnt exist
                if (!internalDir.exists()) {
                    boolean created = internalDir.mkdirs();
                } else {
                    // empty it
                    for (File file : internalDir.listFiles()) file.delete();
                }

                // if locationType == EXTERNAL
                if (locationType.equals("EXTERNAL")) {
                    // move files from external to internal
                    // if external absDir exists
                    if (absDir.exists()) {
                        // rename external full absDir to internal full absDir
                        boolean moved = absDir.renameTo(internalDir);
                        // if successful
                        if (moved) {
                            Toast.makeText(context, "Files moved from external to internal", Toast.LENGTH_SHORT).show();
                        }
                    } // else do nothing
                } // else it's already internal
                this.rootDir = rootDir;
                this.absDir = internalDir;
                this.locationType = "INTERNAL";
                this.displayText = "Internal";

                return;
            case "EXTERNAL":
                rootDir = LOCATION_EXTERNAL;
                File externalDir = new File(rootDir, IMAGE_DIR);

                // if sd card is read/write
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // if external directory doesnt exist
                    if (!externalDir.exists()) {
                        boolean created = externalDir.mkdirs();
                    } else {
                        // empty it
                        for (File file : externalDir.listFiles()) file.delete();
                    }

                    // if locationType == INTERNAL
                    if (locationType.equals("INTERNAL")) {
                        // move files from internal to external
                        // if internal absDir exists
                        if (absDir.exists()) {
                            // rename internal full absDir to external full absDir
                            boolean moved = absDir.renameTo(externalDir);
                            // if successful
                            if (moved) {
                                Toast.makeText(context, "Files moved from external to internal", Toast.LENGTH_SHORT).show();
                            }
                        } // else do nothing
                    } // else it's already external so we'll change nothing
                    this.rootDir = rootDir;
                    this.absDir = externalDir;
                    this.locationType = "EXTERNAL";
                    this.displayText = "SD Card";
                } else {
                    Toast.makeText(context, "SD card is not available", Toast.LENGTH_SHORT).show();
                    // change sharedPref back to internal
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(PrefScreenActivity.KEY_IMAGE_STORAGE, "INTERNAL");
                    editor.apply();

                }

                return;
            default:
                Toast.makeText(context, "Invalid location type", Toast.LENGTH_SHORT).show();
        }
    }

    public String getDisplayText() {
        return displayText;
    }

    public File getRootDir() {
        return rootDir;
    }

    public File getAbsDir() {
        return absDir;
    }

}