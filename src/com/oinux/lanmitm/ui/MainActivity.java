package com.oinux.lanmitm.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.oinux.lanmitm.ActionBarActivity;
import com.oinux.lanmitm.AppContext;
import com.oinux.lanmitm.R;
import com.oinux.lanmitm.service.ArpService;
import com.oinux.lanmitm.service.HijackService;
import com.oinux.lanmitm.service.SnifferService;
import com.oinux.lanmitm.util.NetworkUtils;
import com.oinux.lanmitm.util.ShellUtils;
import com.oinux.lanmitm.util.ShellUtils.CommandResult;
import com.oinux.lanmitm.widget.RadioDialog;
import com.oinux.lanmitm.widget.YesOrNoDialog;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	private CheckBox protectBox;
	private Button lanScanBtn;
	private String protect_cmds = String.format("arp -s %s %s",
			AppContext.getGateway(), AppContext.getGatewayMac());
	private String close_protect_cmds = String.format("arp -d %s",
			AppContext.getGateway());
	private boolean isProtected;
	private boolean isExit = false;
	private int arp_cheat_way;
	private Button arpCheatWayBtn;
	private Button httpServerBtn;
	private ImageView aboutBtn;
	private RelativeLayout hijackHisBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.main_activity);

		setBarTitle(Html
				.fromHtml("<b>" + getString(R.string.app_name) + "</b>"));

		lanScanBtn = (Button) findViewById(R.id.main_lan_scan_btn);
		lanScanBtn.setOnClickListener(this);

		httpServerBtn = (Button) findViewById(R.id.main_http_server_btn);
		httpServerBtn.setOnClickListener(this);

		aboutBtn = (ImageView) findViewById(R.id.actionbar_about);
		aboutBtn.setOnClickListener(this);

		hijackHisBtn = (RelativeLayout) findViewById(R.id.hijack_history_btn);
		hijackHisBtn.setOnClickListener(this);

		arpCheatWayBtn = (Button) findViewById(R.id.main_arp_cheat_way_btn);
		arpCheatWayBtn.setOnClickListener(this);
		arp_cheat_way = AppContext.getInt("arp_cheat_way", ArpService.TWO_WAY);

		protectBox = (CheckBox) findViewById(R.id.main_open_close_protect);
		isProtected = AppContext.getBoolean("is_protected", false);
		protectBox.setChecked(isProtected);
		if (isProtected) {
			ShellUtils.execCommand(new String[] { close_protect_cmds,
					protect_cmds }, true, true);
		}

		protectBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				AppContext.putBoolean("is_protected", isChecked);
				if (isChecked) {
					ShellUtils.execCommand(new String[] { close_protect_cmds,
							protect_cmds }, true, true);
				} else {
					ShellUtils.execCommand(close_protect_cmds, true, true);
				}
			}
		});

		if (!ShellUtils.checkRootPermission()) {
			YesOrNoDialog.Builder builder = new YesOrNoDialog.Builder(this);
			builder.setTitle("系统提示")
					.setMessage("本应用只能在已root的Android手机上运行，下载root工具？")
					.setNegativeButton("下载",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									if (checkAssistant("root工具")) {
										Intent intent = new Intent(
												Intent.ACTION_VIEW);
										ComponentName cn = new ComponentName(
												"com.qihoo.appstore",
												"com.qihoo.appstore.activities.SearchDistributionActivity");
										intent.setComponent(cn);
										intent.setData(Uri
												.parse("market://details?id=com.qihoo.permmgr"));
										startActivity(intent);
									}
								}
							})
					.setPositiveButton("退出",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									exit();
								}
							}).create().show();
		} else {

			CommandResult cr = ShellUtils.execCommand("which killall", true,
					true, true);
			if (cr.result != 0) {
				YesOrNoDialog.Builder builder = new YesOrNoDialog.Builder(this);
				builder.setTitle("系统提示")
						.setMessage("本程序需要安装busybox才能正确运行，下载busybox？")
						.setNegativeButton("下载",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										if (checkAssistant("busybox")) {
											Intent intent = new Intent(
													Intent.ACTION_VIEW);
											ComponentName cn = new ComponentName(
													"com.qihoo.appstore",
													"com.qihoo.appstore.activities.SearchDistributionActivity");
											intent.setComponent(cn);
											intent.setData(Uri
													.parse("market://details?id=stericson.busybox.donate"));
											startActivity(intent);
										}
									}
								})
						.setPositiveButton("退出",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										exit();
									}
								}).create().show();
			}
		}
	}

	private boolean checkAssistant(String name) {
		try {
			getPackageManager().getApplicationInfo("com.qihoo.appstore",
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			YesOrNoDialog.Builder builder = new YesOrNoDialog.Builder(this);
			builder.setTitle("系统提示")
					.setMessage("本程序运行需要安装" + name + "，是否通过360手机助手安装它？")
					.setNegativeButton("是",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									Uri content_url = Uri
											.parse("http://sj.360.cn");
									intent.setData(content_url);
									startActivity(intent);
								}
							})
					.setPositiveButton("否",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
									exit();
								}
							}).create().show();
			return false;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_http_server_btn:
			startActivity(new Intent(this, HttpActivity.class));
			overridePendingTransition(android.R.anim.fade_in,
					android.R.anim.fade_out);
			break;
		case R.id.main_lan_scan_btn:
			if (!NetworkUtils.isWifiConnected()) {
				YesOrNoDialog.Builder builder = new YesOrNoDialog.Builder(this);
				builder.setTitle("系统提示")
						.setMessage("网络未连接，是否打开设置WiFi？")
						.setNegativeButton("返回",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								})
						.setPositiveButton("设置",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
										Intent intent = new Intent();
										intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
										intent.putExtra(
												"extra_prefs_show_button_bar",
												true);
										intent.putExtra(
												"wifi_enable_next_on_connect",
												true);
										startActivity(intent);
									}
								}).create().show();
			} else {
				startActivity(new Intent(this, HostsActivity.class));
				overridePendingTransition(R.anim.z_slide_in_bottom,
						R.anim.z_slide_out_top);
			}
			break;
		case R.id.main_arp_cheat_way_btn:
			RadioDialog.Builder builder = new RadioDialog.Builder(
					MainActivity.this);
			builder.setTitle("选择方式")
					.setRadio1("主机单向",
							ArpService.ONE_WAY_HOST == arp_cheat_way,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									arp_cheat_way = ArpService.ONE_WAY_HOST;
									AppContext.putInt("arp_cheat_way",
											arp_cheat_way);
									dialog.dismiss();
								}
							})
					.setRadio2("路由单向",
							ArpService.ONE_WAY_ROUTE == arp_cheat_way,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									arp_cheat_way = ArpService.ONE_WAY_ROUTE;
									AppContext.putInt("arp_cheat_way",
											arp_cheat_way);
									dialog.dismiss();
								}
							})
					.setRadio3("双向欺骗", ArpService.TWO_WAY == arp_cheat_way,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									arp_cheat_way = ArpService.TWO_WAY;
									AppContext.putInt("arp_cheat_way",
											arp_cheat_way);
									dialog.dismiss();
								}
							}).create().show();
			break;
		case R.id.actionbar_about:
			startActivity(new Intent(this, AboutActivity.class));
			break;
		case R.id.hijack_history_btn:
			startActivity(new Intent(this, HijackHistory.class));
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			Timer tExit = null;
			if (isExit == false) {
				isExit = true; // 准备退出
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				tExit = new Timer();
				tExit.schedule(new TimerTask() {
					@Override
					public void run() {
						isExit = false; // 取消退出
					}
				}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
			} else {
				exit();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		stopService(new Intent(this, ArpService.class));
		stopService(new Intent(this, HijackService.class));
		stopService(new Intent(this, SnifferService.class));
		NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.cancelAll();
		finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
