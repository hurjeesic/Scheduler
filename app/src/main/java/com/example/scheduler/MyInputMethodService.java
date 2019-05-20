package com.example.scheduler;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Toast;

// Service 객체를 이용한 custom keyboard 제작
// https://github.com/ghrud92/android-custom-keyboard-example
public class MyInputMethodService extends InputMethodService implements KeyboardView.OnKeyboardActionListener{
    private KeyboardView keyboardView;
    private Keyboard keyboard;

    private boolean isCaps = false;   // Caps Lock

    private final int SUBMIT = 130;

    public MyInputMethodService() {

    }

    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        keyboard = new Keyboard(this, R.xml.keyboard_layout);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
        return keyboardView;
    }

    @Override
    public void onKey(int i, int[] ints) {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection == null)
            return;

        playClick(i);
        CharSequence selectedText = inputConnection.getSelectedText(0);
        switch (i) {
            case SUBMIT:
                final int MAX = 1000; // 한 줄의 최대 글자 수
                // 먼저 매크로 인식을 위하여 커서를 맨 뒤로 이동
                inputConnection.setSelection(0, 0);
                inputConnection.commitText("", MAX);

                // 커서가 맨 뒤에 있으므로 전체 텍스트를 커서를 기준으로 탐색
                String disposeText = inputConnection.getTextBeforeCursor(MAX, 0).toString();
                int instructionIndex = disposeText.indexOf(" ");
                if (instructionIndex != -1)
                {
                    ManagedFile manager = new ManagedFile(getFilesDir().getAbsolutePath());

                    String instructionStr = disposeText.substring(0, instructionIndex);
                    if (instructionStr.toUpperCase().equals("ADD") && disposeText.length() >= 14) { // ex) add 20190517 title
                        int dateIndex = disposeText.indexOf(" ", instructionIndex + 1) + 1;
                        if (dateIndex != 0) {
                            String dateStr = disposeText.substring(instructionIndex + 1, dateIndex - 1);
                            String[] data = new String[]{ disposeText.substring(dateIndex), "", "" };

                            manager.writeData(dateStr, data, true);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Invalid instruction", Toast.LENGTH_LONG).show();
                        }
                    }
                    else if (instructionStr.toUpperCase().equals("DEL") && disposeText.length() == 12) { // ex) del 20190517
                        int dateIndex = instructionIndex + 6;
                        String dateStr = disposeText.substring(instructionIndex + 1, dateIndex);

                        manager.deleteFile(dateStr);
                    }
                    else if (instructionStr.toUpperCase().equals("DELETE") && disposeText.length() == 15) { // ex) delete 20190517
                        int dateIndex = instructionIndex + 9;
                        String dateStr = disposeText.substring(instructionIndex + 1, dateIndex);

                        manager.deleteFile(dateStr);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Invalid instruction", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Invalid instruction", Toast.LENGTH_LONG).show();
                }

                inputConnection.deleteSurroundingText(MAX, 0); //매크로 반영 후 텍스트 제거
                break;
            case Keyboard.KEYCODE_DELETE :
                if (TextUtils.isEmpty(selectedText)) {
                    inputConnection.deleteSurroundingText(1, 0);
                } else {
                    inputConnection.commitText("", 1);
                }
                break;
            case Keyboard.KEYCODE_SHIFT:
                isCaps = !isCaps;
                keyboard.setShifted(isCaps);
                keyboardView.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            default:
                char code = (char) i;
                if (Character.isLetter(code) && isCaps) {
                    code = Character.toUpperCase(code);
                }
                inputConnection.commitText(String.valueOf(code), 1);
        }
    }

    private void playClick(int i) {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        switch(i) {
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default: am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }

    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) {

    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeUp() {

    }
}
