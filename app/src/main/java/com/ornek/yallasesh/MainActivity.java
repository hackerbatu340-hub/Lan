package com.ornek.yallasesh;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final int MIC_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setAudioOnly(true)
                    .setVideoMuted(true)
                    .build();
            JitsiMeet.setDefaultConferenceOptions(options);
        } catch (Exception ignored) { }

        findViewById(R.id.joinBtn).setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, MIC_REQUEST);
                return;
            }
            join();
        });
    }

    private void join() {
        EditText nameBox = findViewById(R.id.nameInput);
        EditText roomBox = findViewById(R.id.roomInput);
        String name = nameBox.getText().toString().trim();
        String room = roomBox.getText().toString().trim();

        if (room.isEmpty()) {
            Toast.makeText(this, "Oda adı gir", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JitsiMeetConferenceOptions.Builder b = new JitsiMeetConferenceOptions.Builder()
                    .setRoom(room);
            if (!name.isEmpty()) b.setUserInfo(new org.jitsi.meet.sdk.JitsiMeetUserInfo(null, name, null));
            JitsiMeetActivity.launch(this, b.build());
        } catch (Exception e) {
            Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int code, @NonNull String[] perms, @NonNull int[] res) {
        super.onRequestPermissionsResult(code, perms, res);
        if (code == MIC_REQUEST) {
            if (res.length > 0 && res[0] == PackageManager.PERMISSION_GRANTED) {
                join();
            } else {
                Toast.makeText(this, "Mikrofon izni gerekli", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                startActivity(i);
            }
        }
    }
}
