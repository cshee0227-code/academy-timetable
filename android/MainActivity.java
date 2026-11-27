package kr.personal.academytimetable;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Insets;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowInsets;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private static final int PICK_FILE = 1;
    private static final int SAVE_FILE = 2;

    private WebView web;
    private ValueCallback<Uri[]> fileCallback;
    private String pendingBackup;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.rgb(0x00, 0x46, 0x3c));
        web = new WebView(this);
        root.addView(web);
        setContentView(root);

        // 상태 표시줄, 하단 바, 키보드에 화면이 가리지 않도록 여백을 준다
        if (Build.VERSION.SDK_INT >= 30) {
            getWindow().setDecorFitsSystemWindows(false);
            root.setOnApplyWindowInsetsListener((view, insets) -> {
                Insets i = insets.getInsets(WindowInsets.Type.systemBars()
                        | WindowInsets.Type.displayCutout() | WindowInsets.Type.ime());
                view.setPadding(i.left, i.top, i.right, i.bottom);
                return WindowInsets.CONSUMED;
            });
        }

        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        web.addJavascriptInterface(new Bridge(), "Android");
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
                fileCallback = callback;
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                try {
                    startActivityForResult(intent, PICK_FILE);
                } catch (ActivityNotFoundException e) {
                    fileCallback = null;
                    return false;
                }
                return true;
            }
        });
        web.loadUrl("file:///android_asset/index.html");
    }

    private class Bridge {
        @JavascriptInterface
        public void saveBackup(String fileName, String content) {
            runOnUiThread(() -> {
                pendingBackup = content;
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/json");
                intent.putExtra(Intent.EXTRA_TITLE, fileName);
                try {
                    startActivityForResult(intent, SAVE_FILE);
                } catch (ActivityNotFoundException e) {
                    toast("파일 저장 앱을 찾을 수 없습니다");
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        Uri uri = result == RESULT_OK && data != null ? data.getData() : null;
        if (request == PICK_FILE && fileCallback != null) {
            fileCallback.onReceiveValue(uri == null ? null : new Uri[]{uri});
            fileCallback = null;
        } else if (request == SAVE_FILE && uri != null) {
            if (pendingBackup == null) {
                toast("저장하지 못했습니다. 다시 시도해 주세요");
                return;
            }
            try (OutputStream out = getContentResolver().openOutputStream(uri, "wt")) {
                out.write(pendingBackup.getBytes(StandardCharsets.UTF_8));
                toast("백업 파일을 저장했습니다");
            } catch (Exception e) {
                toast("저장하지 못했습니다");
            }
            pendingBackup = null;
        }
    }

    // 뒤로 가기: 입력창이 열려 있으면 닫고, 아니면 앱을 내린다
    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        web.evaluateJavascript(
                "(function(){var d=document.querySelector('.sheet');if(d){d.remove();return true}return false})()",
                closed -> {
                    if (!"true".equals(closed)) moveTaskToBack(true);
                });
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
