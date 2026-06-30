package com.sys.tool.inputpad; // ✅ 包名与 build.gradle namespace 一致

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class SysInputService extends InputMethodService {
    private static final String TAG = "SysInputService";

    public static final String ACTION_INPUT_TEXT = "SYS_INPUT_PAD_TEXT";
    public static final String ACTION_INPUT_CHARS = "SYS_INPUT_PAD_CHARS";

    private BroadcastReceiver mReceiver;

    @Override
    public View onCreateInputView() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText editText = new EditText(this);
        editText.setId(android.R.id.edit);
        layout.addView(editText);

        return layout;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ACTION_INPUT_TEXT.equals(action)) {
                    String text = intent.getStringExtra("msg");
                    if (text != null) commitText(text);
                } else if (ACTION_INPUT_CHARS.equals(action)) {
                    String chars = intent.getStringExtra("chars");
                    if (chars != null) commitText(chars);
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_INPUT_TEXT);
        filter.addAction(ACTION_INPUT_CHARS);

        // ✅ Android 14 (API 34) 强制要求：外部广播必须显式声明 EXPORTED
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(mReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(mReceiver, filter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }

    private void commitText(String text) {
        try {
            getCurrentInputConnection().commitText(text, 1);
        } catch (Exception e) {
            Log.e(TAG, "Failed to commit text", e);
        }
    }
}
