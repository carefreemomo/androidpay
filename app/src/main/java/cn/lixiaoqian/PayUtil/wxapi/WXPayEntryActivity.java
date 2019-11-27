package cn.lixiaoqian.PayUtil.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class WXPayEntryActivity extends UnityPlayerActivity implements IWXAPIEventHandler {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onReq(BaseReq req) {
    }
    @Override
    public void onResp(BaseResp resp) {
        Log.d("Unity", "onPayFinish, errCode = " + resp.errCode);
        String result = resp.errCode+"";
        UnityPlayer.UnitySendMessage("PurchaseManager", "WXPayResult", result);
        finish();
    }
}