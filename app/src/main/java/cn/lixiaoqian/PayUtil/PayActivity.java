package cn.lixiaoqian.PayUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.pay.demo.R;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class PayActivity extends UnityPlayerActivity {
	private static IWXAPI msgApi;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}

	public static void aliPay(final Activity activity, final String orderInfo){
		Runnable payRun=new Runnable() {
			@Override
			public void run() {
				PayTask task=new PayTask(activity);
				String result= task.pay(orderInfo, true);
				Log.i("Unity", "onALIPayFinish, result = " + result);
				UnityPlayer.UnitySendMessage("PurchaseManager", "ALiPayResult", result);
			}
		};
		Thread payThread = new Thread(payRun);
		payThread.start();
	}

	public void  weichatPay(final Context context,final String appId, String partnerId, String prepayId, String nonceStr, String timeStamp, String sign){
		Log.d("Unity","weichatPayStart");//输出验签是否正确
		msgApi = WXAPIFactory.createWXAPI(context, appId);
		//建议动态监听微信启动广播进行注册到微信
		context.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// 将该app注册到微信
				msgApi.registerApp(appId);
			}
		}, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));

		if (msgApi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
			PayReq request = new PayReq();
			request.appId = appId;
			request.partnerId = partnerId;
			request.prepayId = prepayId;
			request.packageValue = "Sign=WXPay";
			request.nonceStr = nonceStr;
			request.timeStamp = timeStamp;
			request.sign = sign;
			Log.d("Unity", request.checkArgs() + "");//输出验签是否正确
			msgApi.sendReq(request);
		}
	}

	private static void showToast(Context ctx, String msg) {
		Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
	}

//	private static String bundleToString(Bundle bundle) {
//		if (bundle == null) {
//			return "null";
//		}
//		final StringBuilder sb = new StringBuilder();
//		for (String key: bundle.keySet()) {
//			sb.append(key).append("=>").append(bundle.get(key)).append("\n");
//		}
//		return sb.toString();
//	}

}
