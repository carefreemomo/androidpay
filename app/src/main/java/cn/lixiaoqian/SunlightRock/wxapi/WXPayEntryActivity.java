package cn.lixiaoqian.SunlightRock.wxapi;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import static cn.lixiaoqian.PayUtil.PayActivity.WX_appId;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, WX_appId);
        api.handleIntent(getIntent(), this);
    }
    @Override
    public void onReq(BaseReq req) {
    }
    @Override
    public void onResp(BaseResp resp) {
        Log.d("Unity", "onPayFinish, errCode = " + resp.errCode);
        String result = resp.errCode+"";
        if(resp.errCode==0)
        {
            UnityPlayer.UnitySendMessage("PurchaseManager", "PayResultSuccess", result);
        }
        else if(resp.errCode==-1)
        {
            UnityPlayer.UnitySendMessage("PurchaseManager", "PayResultFail", result);
        }
        else
        {
            UnityPlayer.UnitySendMessage("PurchaseManager", "PayResultCancel", result);
        }
        finish();
    }
}