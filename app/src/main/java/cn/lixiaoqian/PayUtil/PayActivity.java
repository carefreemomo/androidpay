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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import cn.lixiaoqian.SunlightRock.wxapi.WXPayEntryActivity;

public class PayActivity extends UnityPlayerActivity {
	private static IWXAPI msgApi;
	private static final int SDK_PAY_FLAG = 10;
	public static String WX_appId = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	}

	public void aliPay(final Activity activity, final String orderInfo) {
		Runnable payRun = new Runnable() {

			@Override
			public void run() {
				PayTask task = new PayTask(activity);
				String result = task.pay(orderInfo, true);
				Log.i("Unity", "onALIPayFinish, result = " + result);
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		Thread payThread = new Thread(payRun);
		payThread.start();
	}
		//4.处理支付宝app返回的支付结
		@SuppressLint("HandlerLeak")
		private Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case SDK_PAY_FLAG: {
						@SuppressWarnings("unchecked")
						PayResult payResult = new PayResult((String) msg.obj);
						String resultInfo = payResult.getResult();// 同步返回需要验证的信息
						String resultStatus = payResult.getResultStatus();
						Log.d("Unity","resultStatus:"+resultStatus);
						// 判断resultStatus 为9000则代表支付成功
						if (TextUtils.equals(resultStatus, "9000")) {
							UnityPlayer.UnitySendMessage("PurchaseManager", "PayResultSuccess", resultInfo);
						}
						else if (TextUtils.equals(resultStatus, "6001")) {
							UnityPlayer.UnitySendMessage("PurchaseManager", "PayResultCancel", resultInfo);
						}
						else  {
							UnityPlayer.UnitySendMessage("PurchaseManager", "PayResultFail", resultInfo);
						}
						break;
					}
					default:
						break;
				}
			};
		};


	public void  weichatPay(final Activity activity,final Context context,final String appId, String partnerId, String prepayId,String packageValue, String nonceStr, String timeStamp, String sign){
		Log.d("Unity","weichatPayStart1");//输出验签是否正确
		msgApi = WXAPIFactory.createWXAPI(context, appId);
		msgApi.registerApp(appId);
		//建议动态监听微信启动广播进行注册到微信
		context.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// 将该app注册到微信
				msgApi.registerApp(appId);
			}
		}, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));

		if (msgApi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT) {
			WX_appId=appId;
			PayReq request = new PayReq();
			request.appId = appId;
			request.partnerId = partnerId;
			request.prepayId = prepayId;
			request.packageValue = packageValue;
			request.nonceStr = nonceStr;
			request.timeStamp = timeStamp;
			request.sign = sign;
			Log.d("Unity", appId);
			Log.d("Unity", partnerId);
			Log.d("Unity", prepayId);
			Log.d("Unity", nonceStr);
			Log.d("Unity", timeStamp);
			Log.d("Unity", sign);//输出验签是否正确
			Log.d("Unity", request.checkArgs() + "");//输出验签是否正确
			msgApi.sendReq(request);
		}
		Log.d("Unity"," ");//输出验签是否正确
//        Intent myIntent = new Intent(activity, WXPayEntryActivity.class);
//        activity.startActivity(myIntent);
        Log.d("Unity","weichatPayStart3");//输出验签是否正确
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
