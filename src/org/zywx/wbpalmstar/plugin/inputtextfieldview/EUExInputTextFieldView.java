package org.zywx.wbpalmstar.plugin.inputtextfieldview;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;

public class EUExInputTextFieldView extends EUExBase{

    public static final String INPUTTEXTFIELDVIEW_FUN_PARAMS_KEY = "inputtextfieldviewFunParamsKey";
    public static final String INPUTTEXTFIELDVIEW_ACTIVITY_ID = "inputtextfieldviewActivityID";

    public static final int INPUTTEXTFIELDVIEW_MSG_OPEN = 0;
    public static final int INPUTTEXTFIELDVIEW_MSG_CLOSE = 1;
    private static final String BUNDLE_DATA = "data";
    private static final int MSG_SET_INPUT_FOCUSED = 2;
    private static final String TAG = "ACEInputTextFieldViewFragment";
    private ACEInputTextFieldViewFragment inputTextFieldViewFragment;

    public EUExInputTextFieldView(Context context, EBrowserView view) {
        super(context, view);
    }

    private void sendMessageWithType(int msgType, String[] params) {
        if (mHandler == null) {
            return;
        }
        Message msg = new Message();
        msg.what = msgType;
        msg.obj = this;
        Bundle b = new Bundle();
        b.putStringArray(INPUTTEXTFIELDVIEW_FUN_PARAMS_KEY, params);
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    public void setInputFocused(String[] params) {
        Message msg = new Message();
        msg.obj = this;
        msg.what = MSG_SET_INPUT_FOCUSED;
        Bundle bd = new Bundle();
        bd.putStringArray(BUNDLE_DATA, params);
        msg.setData(bd);
        mHandler.sendMessage(msg);
    }

    private void setInputFocusedMsg() {
        if (inputTextFieldViewFragment != null) {
            inputTextFieldViewFragment.setInputFocused();
        }
    }

    @Override
    public void onHandleMessage(Message message) {
        if(message == null){
            return;
        }
        Bundle bundle=message.getData();
        switch (message.what) {

            case INPUTTEXTFIELDVIEW_MSG_OPEN:
                handleOpen(message);
                break;
            case INPUTTEXTFIELDVIEW_MSG_CLOSE:
                handleMessageInputTextFieldView(message);
                break;
            case MSG_SET_INPUT_FOCUSED:
                setInputFocusedMsg();
                break;
            default:
                super.onHandleMessage(message);
        }
    }

    private void handleMessageInputTextFieldView(Message msg) {
        if (inputTextFieldViewFragment != null) {
            switch (msg.what) {
                case INPUTTEXTFIELDVIEW_MSG_CLOSE:
                    handleClose();
                    break;
            }
        }
    }

    private void handleClose() {
        if (inputTextFieldViewFragment != null){
            removeFragmentFromWindow(inputTextFieldViewFragment);
            inputTextFieldViewFragment.onDestroy();
            inputTextFieldViewFragment = null;
        }
    }

    private void handleOpen(Message msg) {
        String[] params = msg.getData().getStringArray(
                INPUTTEXTFIELDVIEW_FUN_PARAMS_KEY);
        if (params == null || params.length < 1) return;
        try {
            JSONObject json = new JSONObject(params[0]);
            String emojicons = json
                    .getString(EInputTextFieldViewUtils.INPUTTEXTFIELDVIEW_PARAMS_JSON_KEY_EMOJICONS);

            String activityId = INPUTTEXTFIELDVIEW_ACTIVITY_ID
                    + EUExInputTextFieldView.this.hashCode();
            if (inputTextFieldViewFragment != null) return;
            inputTextFieldViewFragment = new ACEInputTextFieldViewFragment();
            inputTextFieldViewFragment.setUexBaseObj(this);
            inputTextFieldViewFragment.setEmojiconswgtResXmlPath(emojicons);

            boolean hasPlacehold = json
                    .has(EInputTextFieldViewUtils.INPUTTEXTFIELDVIEW_PARAMS_JSON_KEY_PLACEHOLD);
            if (hasPlacehold) {
                String placehold = json
                        .getString(EInputTextFieldViewUtils.INPUTTEXTFIELDVIEW_PARAMS_JSON_KEY_PLACEHOLD);
                inputTextFieldViewFragment.setHint(placehold);
            }
            if (json.has(EInputTextFieldViewUtils
                    .INPUTTEXTFIELDVIEW_PARAMS_JSON_KEY_BTN_COLOR)) {
                inputTextFieldViewFragment.setBtnColor(json.getString(EInputTextFieldViewUtils.
                        INPUTTEXTFIELDVIEW_PARAMS_JSON_KEY_BTN_COLOR));
            }
            if (json.has(EInputTextFieldViewUtils
                    .INPUTTEXTFIELDVIEW_PARAMS_JSON_KEY_BTN_TEXT_COLOR)) {
                inputTextFieldViewFragment.setBtnTextColor(json.getString(EInputTextFieldViewUtils.
                        INPUTTEXTFIELDVIEW_PARAMS_JSON_KEY_BTN_TEXT_COLOR));
            }
            DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    dm.widthPixels, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            addFragmentToCurrentWindow(inputTextFieldViewFragment, lp, TAG);
            if (inputTextFieldViewFragment.getView() != null){
                mBrwView.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
                            float h = inputTextFieldViewFragment.getView().getHeight();
                            float y = event.getY();
                            if (dm.heightPixels - Math.abs(y) > h) {
                                if (inputTextFieldViewFragment != null) {
                                    inputTextFieldViewFragment.outOfViewTouch();
                                }
                            }
                        }
                        return false;
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void open(String[] params) {
        sendMessageWithType(INPUTTEXTFIELDVIEW_MSG_OPEN, params);
    }

    public void close(String[] params) {
        sendMessageWithType(INPUTTEXTFIELDVIEW_MSG_CLOSE, params);
    }

    @Override
    protected boolean clean() {
        close(null);
        return false;
    }
}
