package com.darkweb.genesissearchengine.appManager.bridgeManager;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.darkweb.genesissearchengine.constants.strings;
import com.darkweb.genesissearchengine.helperManager.helperMethod;
import com.darkweb.genesissearchengine.helperManager.sharedUIMethod;
import com.example.myapplication.R;
import java.util.List;

class bridgeViewController
{
    /*Private Variables*/
    private RadioButton mBridgeSettingObfs;
    private RadioButton mBridgeSettingBridgeChina;
    private RadioButton mBridgeSettingCustomPort;
    private Button mBridgeSettingBridgeRequest;
    private EditText mBridgeSettingBridgeCustom;
    private ImageView mBridgeSettingCustomBridgeBlocker;

    /*ViewControllers*/
    private AppCompatActivity mContext;

    /*Initializations*/
    void initialization(EditText pBridgeSettingBridgeCustom, Button pBridgeSettingBridgeRequest, AppCompatActivity pContext,RadioButton pBridgeSettingObfs,RadioButton pBridgeSettingBridgeChina,RadioButton pBridgeSettingCustomPort, ImageView pBridgeSettingCustomBridgeBlocker){
        this.mContext = pContext;
        this.mBridgeSettingObfs = pBridgeSettingObfs;
        this.mBridgeSettingBridgeChina = pBridgeSettingBridgeChina;
        this.mBridgeSettingCustomPort = pBridgeSettingCustomPort;
        this.mBridgeSettingBridgeRequest = pBridgeSettingBridgeRequest;
        this.mBridgeSettingBridgeCustom = pBridgeSettingBridgeCustom;
        this.mBridgeSettingCustomBridgeBlocker = pBridgeSettingCustomBridgeBlocker;

        initPostUI();
    }

    private void initPostUI(){
        sharedUIMethod.updateStatusBar(mContext);
    }

    private void animateColor(TextView p_view, int p_from, int p_to, String p_command, int p_duration){
        ObjectAnimator colorAnim = ObjectAnimator.ofInt(p_view, p_command,p_from, p_to);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.setDuration(p_duration);
        colorAnim.start();
    }

    private void resetRadioButtons(int p_duration){
        animateColor(mBridgeSettingObfs, mBridgeSettingObfs.getCurrentTextColor(), mContext.getResources().getColor(R.color.holo_dark_gray), "textColor", p_duration);
        animateColor(mBridgeSettingCustomPort, mBridgeSettingCustomPort.getCurrentTextColor(), mContext.getResources().getColor(R.color.holo_dark_gray), "textColor", p_duration);
        animateColor(mBridgeSettingBridgeChina, mBridgeSettingBridgeChina.getCurrentTextColor(), mContext.getResources().getColor(R.color.holo_dark_gray), "textColor", p_duration);

        mBridgeSettingObfs.setHighlightColor(mContext.getResources().getColor(R.color.holo_dark_gray));
        mBridgeSettingCustomPort.setHighlightColor(mContext.getResources().getColor(R.color.holo_dark_gray));
        mBridgeSettingBridgeChina.setHighlightColor(mContext.getResources().getColor(R.color.holo_dark_gray));

        mBridgeSettingObfs.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.holo_dark_gray)));
        mBridgeSettingCustomPort.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.holo_dark_gray)));
        mBridgeSettingBridgeChina.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.holo_dark_gray)));

        helperMethod.hideKeyboard(mContext);
        mBridgeSettingBridgeCustom.clearFocus();
        mBridgeSettingBridgeCustom.animate().setDuration(p_duration).alpha(0.35f);
        mBridgeSettingBridgeRequest.animate().setDuration(p_duration).alpha(0.35f);
        mBridgeSettingCustomBridgeBlocker.setVisibility(View.VISIBLE);
    }

    private void onEnableCustomBridge(){
        animateColor(mBridgeSettingCustomPort, mBridgeSettingCustomPort.getCurrentTextColor(), mContext.getResources().getColor(R.color.c_text_v1), "textColor", 200);
        mBridgeSettingCustomPort.setHighlightColor(Color.BLACK);
        mBridgeSettingCustomPort.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
        mBridgeSettingObfs.setChecked(false);
        mBridgeSettingBridgeChina.setChecked(false);
        mBridgeSettingCustomPort.setChecked(true);
        mBridgeSettingBridgeCustom.animate().setDuration(200).alpha(1f);
        mBridgeSettingBridgeRequest.animate().setDuration(200).alpha(1f);
        mBridgeSettingCustomBridgeBlocker.setVisibility(View.GONE);
    }

    private void initViews(String p_bridge, int p_duration){
        resetRadioButtons(p_duration);
        if(p_bridge.equals(strings.BRIDGE_CUSTOM_BRIDGE_OBFS4)){
            animateColor(mBridgeSettingObfs, mBridgeSettingObfs.getCurrentTextColor(), mContext.getResources().getColor(R.color.c_text_v1), "textColor", p_duration);
            mBridgeSettingObfs.setHighlightColor(Color.BLACK);
            mBridgeSettingObfs.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mBridgeSettingObfs.setChecked(true);
            mBridgeSettingBridgeChina.setChecked(false);
            mBridgeSettingCustomPort.setChecked(false);
            mBridgeSettingBridgeCustom.setText(strings.GENERIC_EMPTY_STR);
        }else if(p_bridge.equals(strings.BRIDGE_CUSTOM_BRIDGE_MEEK)){
            animateColor(mBridgeSettingBridgeChina, mBridgeSettingBridgeChina.getCurrentTextColor(), mContext.getResources().getColor(R.color.c_text_v1), "textColor", p_duration);
            mBridgeSettingBridgeChina.setHighlightColor(Color.BLACK);
            mBridgeSettingBridgeChina.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.c_radio_tint)));
            mBridgeSettingObfs.setChecked(false);
            mBridgeSettingBridgeChina.setChecked(true);
            mBridgeSettingCustomPort.setChecked(false);
            mBridgeSettingBridgeCustom.setText(strings.GENERIC_EMPTY_STR);
        }else {
            onEnableCustomBridge();
            mBridgeSettingBridgeCustom.setText(("(Config) ➔ "+p_bridge.replace("\n","")));
        }
    }

    public void onTrigger(bridgeEnums.eBridgeViewCommands pCommands, List<Object> pData){
        if(pCommands == bridgeEnums.eBridgeViewCommands.M_INIT_VIEWS){
            initViews((String) pData.get(0), (int)pData.get(1));
        }
        if(pCommands == bridgeEnums.eBridgeViewCommands.M_ENABLE_CUSTOM_BRIDGE){
            onEnableCustomBridge();
        }
    }

}
