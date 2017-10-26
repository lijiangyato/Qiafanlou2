package com.ings.gogo.fragment;

import java.io.IOException;
import com.google.gson.Gson;
import com.ings.gogo.R;
import com.ings.gogo.activity.DetailFastFoodsActivity;
import com.ings.gogo.adapter.FastFoodsAdapter;
import com.ings.gogo.base.BaseData;
import com.ings.gogo.base.BaseFragment;
import com.ings.gogo.entity.FastFoodsEntity;
import com.ings.gogo.utils.LogUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class FastFoodFragment extends BaseFragment {
	public String tag = "FastFoodFragment";
	private static final int GET_DATA_OK = 1;
	private GridView mMyGridView;
	private FastFoodsEntity mFastFoodsEntity;
	private FastFoodsAdapter mFastFoodsAdapter;
	private String mFastFoodsParam;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case GET_DATA_OK:
				mMyGridView.setAdapter(mFastFoodsAdapter);
				mMyGridView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(getActivity(),
								DetailFastFoodsActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("fastFoodsName", mFastFoodsEntity
								.getData().get(position).getProname());
						bundle.putString("fastFoodsPrice", mFastFoodsEntity
								.getData().get(position).getPrice()
								+ "");
						bundle.putString("proid", mFastFoodsEntity.getData()
								.get(position).getProid());
						intent.putExtras(bundle);
						startActivity(intent);

					}
				});
				break;

			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.menulayout, container, false);
		// Bundle bundle = this.getArguments();
		// mFastFoodsParam = bundle.getString("mFastCategory");
		// LogUtils.e("我擦擦擦--->>>", mFastFoodsParam);
		mMyGridView = (GridView) view.findViewById(R.id.mMainPageGridView);

		return view;

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getProductDatas();
			}
		}).start();
	}

	protected void getProductDatas() {
		// TODO Auto-generated method stub
		OkHttpClient mOkHttpClient = new OkHttpClient();
		// 改网址需要的参数 pointid 和 categoryid
		final Request request = new Request.Builder().url(
				BaseData.GET_PRODUCTS_LIST).build();
		// +"&categoryid=8792CBB4-B0CA-4276-AC5A-1F1B08562563&figure=100"
		String trueUrl = BaseData.GET_FAST_PRODUCTS + "ponitId" + "&categoryid="
				+ "categoryid" + "&figure=100";
		Call call = mOkHttpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
			}

			@Override
			public void onResponse(final Response response) throws IOException {
				String getProductBody = response.body().string();
				LogUtils.e("getProductBody--->>", getProductBody);
				Gson gson = new Gson();
				mFastFoodsEntity = gson.fromJson(getProductBody,
						FastFoodsEntity.class);
				mFastFoodsAdapter = new FastFoodsAdapter(getActivity(),
						mFastFoodsEntity.getData());
				Message msg = handler.obtainMessage(GET_DATA_OK);
				msg.sendToTarget();

			}
		});
	}

}
