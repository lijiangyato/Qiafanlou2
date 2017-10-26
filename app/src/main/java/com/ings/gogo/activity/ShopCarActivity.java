package com.ings.gogo.activity;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;
import com.ings.gogo.application.UILApplication;
import com.ings.gogo.db.MyShopSqlHelper;
import com.ings.gogo.entity.MyShopCarEntity;
import com.ings.gogo.entity.SelectedGoodsEntity;
import com.ings.gogo.utils.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShopCarActivity extends BaseActivity implements OnClickListener {

	private ListView mListView;// �б�

	private ListAdapter mListAdapter;// adapter

	private List<MyShopCarEntity> entity = null;// ����
	public static List<SelectedGoodsEntity> mSeclectedEntity = null;
	private boolean isBatchModel;// �Ƿ��ɾ��ģʽ
	private RelativeLayout mBottonLayout;
	public static CheckBox mCheckAll; // ȫѡ ȫ��ѡ
	private TextView mEdit; // �л���ɾ��ģʽ

	private TextView mPriceAll; // ��Ʒ�ܼ�

	// private TextView mSelectNum; // ѡ������

	private TextView mFavorite; // �Ƶ��ղؼ�,����

	private TextView mDelete; // ɾ�� ����

	private float totalPrice = 0; // ��Ʒ�ܼ�
	/** ����ģʽ�£�������¼��ǰѡ��״̬ */
	private SparseArray<Boolean> mSelectState = new SparseArray<Boolean>();
	private ImageView back;
	private boolean flag = true; // ȫѡ��ȫȡ��
	private MyShopSqlHelper mySqlHelper;
	private SQLiteDatabase db;
	private String allPrice;
	private UILApplication myApplication;
	private String telNum;

	// private List<MyShopCarEntity> entity = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_shopcar);
		initView();
		initListener();
		mSeclectedEntity = new ArrayList<SelectedGoodsEntity>();
		mySqlHelper = new MyShopSqlHelper(getApplicationContext(), "GOGO",
				null, 1);
		db = mySqlHelper.getWritableDatabase();
		entity = new ArrayList<MyShopCarEntity>();
		myApplication = (UILApplication) getApplication();
		telNum = myApplication.getTelNum();
		Cursor cursor = db.rawQuery("select * from mycar", null);
		while (cursor.moveToNext()) {
			entity.add(new MyShopCarEntity(cursor.getString(1), cursor
					.getString(2), cursor.getString(3), cursor.getString(4),
					cursor.getString(5), cursor.getString(6)));

		}
		if (entity.size() == 0) {
			LogUtils.e("��ʱû������", "��ʱû������");
		} else {
			LogUtils.e("���ݿ�����ʵ��Ĵ�С---����������", entity.size() + "");
			for (int i = 0; i < entity.size(); i++) {
				LogUtils.e("���ݿ�����ʵ�������---����������", entity.get(i).getGoodsName());
			}
			mListAdapter = new ListAdapter();

			mListView.setAdapter(mListAdapter);
			mListAdapter.notifyDataSetChanged();
		}
		isClearList();
	}

	private void isClearList() {
		// TODO Auto-generated method stub
		if (mSeclectedEntity.size() > 0) {
			mSeclectedEntity.clear();
		} else {
			LogUtils.e("��ǰ����Ϊ0", "");
		}
	}

	public void invisibleOnScreen() {
		LogUtils.e("shopCar--->>���ո�ȥ", "shopCar--->>���ո�ȥ");
		mSeclectedEntity = new ArrayList<SelectedGoodsEntity>();
		mySqlHelper = new MyShopSqlHelper(getApplicationContext(), "GOGO",
				null, 1);
		db = mySqlHelper.getWritableDatabase();
		entity = new ArrayList<MyShopCarEntity>();
		Cursor cursor = db.rawQuery("select * from mycar", null);
		while (cursor.moveToNext()) {
			entity.add(new MyShopCarEntity(cursor.getString(1), cursor
					.getString(2), cursor.getString(3), cursor.getString(4),
					cursor.getString(5), cursor.getString(6)));

		}
		if (entity.size() == 0) {
			LogUtils.e("��ʱû������", "��ʱû������");
			mCheckAll.setChecked(false);
			mPriceAll.setText("��0.00");
		} else {
			mListAdapter = new ListAdapter();
			int size = mListAdapter.getCount();
			LogUtils.e("��ʱû������dd", "��ʱû������" + size);
			mListView.setAdapter(mListAdapter);
			mListAdapter.notifyDataSetChanged();
		}

	}

	public void goneOnScreen() {
		LogUtils.e("", "");
		if (mCheckAll.isChecked() == true) {
			mCheckAll.setChecked(false);
		}

	}

	@SuppressWarnings("deprecation")
	private void initView() {
		back = (ImageView) findViewById(R.id.back);

		mBottonLayout = (RelativeLayout) findViewById(R.id.cart_rl_allprie_total);
		mCheckAll = (CheckBox) findViewById(R.id.check_box_all);
		mEdit = (TextView) findViewById(R.id.subtitle);
		mEdit.setVisibility(View.GONE);
		mPriceAll = (TextView) findViewById(R.id.tv_cart_total);
		// mSelectNum = (TextView) findViewById(R.id.tv_cart_select_num);
		mFavorite = (TextView) findViewById(R.id.tv_cart_move_favorite);
		mDelete = (TextView) findViewById(R.id.tv_cart_buy_or_del);
		mListView = (ListView) findViewById(R.id.listview);
		mListView.setSelector(R.drawable.list_selector);

	}

	private void initListener() {
		// mEdit.setOnClickListener(this);
		mDelete.setOnClickListener(this);
		mCheckAll.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	boolean isSelect = false;

	private class ListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return entity.size();
		}

		@Override
		public Object getItem(int position) {
			return entity.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		ViewHolder holder = null;

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				holder = new ViewHolder();
				view = LayoutInflater.from(ShopCarActivity.this).inflate(
						R.layout.cart_list_item, null);
				holder.checkBox = (CheckBox) view.findViewById(R.id.check_box);
				holder.image = (ImageView) view
						.findViewById(R.id.iv_adapter_list_pic);
				holder.content = (TextView) view.findViewById(R.id.tv_intro);
				holder.carNum = (TextView) view.findViewById(R.id.tv_num);
				holder.price = (TextView) view.findViewById(R.id.tv_price);
				holder.add = (TextView) view.findViewById(R.id.tv_add);
				holder.red = (TextView) view.findViewById(R.id.tv_reduce);
				holder.button = (Button) view.findViewById(R.id.btn_delete);
				holder.frontView = view.findViewById(R.id.item_left);
				holder.mDeleteGoodsIB = (ImageButton) view
						.findViewById(R.id.iv_goods_delete);
				holder.mDeleteGoodsIB.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// LogUtils.e("ɾ����Ʒ��ť �������", "ɾ����Ʒ��ť �������19");
						if (entity.size() != 0) {
							Cursor cursor = db
									.rawQuery(
											"delete from mycar where name = '"
													+ entity.get(position)
															.getGoodsName()
													+ "'", null);
							cursor.moveToNext();
							mListAdapter.notifyDataSetChanged();
							mCheckAll.setChecked(false);
							mPriceAll.setText("��0.00");
							invisibleOnScreen();
						} else {
							LogUtils.e("������ɾ����", "������ɾ����");
							Cursor cursor = db.rawQuery(
									"delete from mycar where name = '"
											+ entity.get(0).getGoodsName()
											+ "'", null);
							cursor.moveToNext();
							mListAdapter.notifyDataSetChanged();
							mCheckAll.setChecked(false);
							mPriceAll.setText("��0.00");
							invisibleOnScreen();
						}

					}
				});

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			final MyShopCarEntity data = entity.get(position);
			bindListItem(holder, data);

			if (data != null) {
				// �ж��Ƿ�ѡ��
				if (data.isChecked()) {
					holder.checkBox.setChecked(true);
				} else {
					holder.checkBox.setChecked(false);
				}

				// ѡ�в���
				holder.checkBox.setOnClickListener(new CheckBoxOnClick(data));
				// ���ٲ���
				holder.red.setOnClickListener(new ReduceOnClick(data,
						holder.carNum));

				// ���Ӳ���
				holder.add.setOnClickListener(new AddOnclick(data,
						holder.carNum));

			}
			return view;
		}

		class CheckBoxOnClick implements OnClickListener {
			MyShopCarEntity shopcartEntity;

			public CheckBoxOnClick(MyShopCarEntity shopcartEntity) {
				this.shopcartEntity = shopcartEntity;
			}

			@Override
			public void onClick(View view) {
				CheckBox cb = (CheckBox) view;
				if (cb.isChecked()) {
					shopcartEntity.setChecked(true);
					mSeclectedEntity.add(new SelectedGoodsEntity(shopcartEntity
							.getGoodsName(), shopcartEntity.getGoodsPrice(),
							shopcartEntity.getGoodsNum(), totalPrice + "",
							shopcartEntity.getGoodsProid(), shopcartEntity
									.getIsToday()));

				} else {
					shopcartEntity.setChecked(false);
					if (mSeclectedEntity.size() > 0) {
						mSeclectedEntity.clear();
					} else {
						LogUtils.e("��ǰ����Ϊ0", "");
					}
				}
				count();
				select();

			}

		}

		private class AddOnclick implements OnClickListener {
			MyShopCarEntity shopcartEntity;
			TextView shopcart_number_btn;

			private AddOnclick(MyShopCarEntity shopcartEntity,
					TextView shopcart_number_btn) {
				this.shopcartEntity = shopcartEntity;
				this.shopcart_number_btn = shopcart_number_btn;
			}

			@Override
			public void onClick(View arg0) {
				// shopcartEntity.setChecked(true);
				String numberStr = shopcart_number_btn.getText().toString();
				if (!TextUtils.isEmpty(numberStr)) {
					int number = Integer.parseInt(numberStr);

					int currentNum = number + 1;
					// �����б�
					shopcartEntity.setGoodsNum(currentNum + "");
					holder.carNum.setText("" + currentNum);
					notifyDataSetChanged();
				}
				count();
			}

		}

		private class ReduceOnClick implements OnClickListener {
			MyShopCarEntity shopcartEntity;
			TextView shopcart_number_btn;

			private ReduceOnClick(MyShopCarEntity shopcartEntity,
					TextView shopcart_number_btn) {
				this.shopcartEntity = shopcartEntity;
				this.shopcart_number_btn = shopcart_number_btn;
			}

			@Override
			public void onClick(View arg0) {
				// shopcartEntity.setChecked(true);
				String numberStr = shopcart_number_btn.getText().toString();
				if (!TextUtils.isEmpty(numberStr)) {
					int number = Integer.parseInt(numberStr);
					if (number == 1) {
						// Toast.makeText(CartListActivity.this, "�������¼�����",
						// Toast.LENGTH_SHORT).show();
					} else {
						int currentNum = number - 1;
						// �����б�
						shopcartEntity.setGoodsNum(currentNum + "");

						holder.carNum.setText("" + currentNum);
						notifyDataSetChanged();

					}

				}
				count();
			}

		}

		private void bindListItem(ViewHolder holder, MyShopCarEntity data) {

			// holder.shopName.setText(data.getShopName());
			ImageLoader.getInstance().displayImage(data.getGoodsImage(),
					holder.image, BaseActivity.options);
			holder.content.setText(data.getGoodsName());
			holder.price.setText("��" + data.getGoodsPrice());
			holder.carNum.setText(data.getGoodsNum());
			int _id = data.getId();

			boolean selected = mSelectState.get(_id, false);
			holder.checkBox.setChecked(selected);

		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		LogUtils.e("���ﳵ�е�pause ִ�е���", "���ﳵ�е�pause ִ�е���");
		mSeclectedEntity.clear();
	}

	public class ViewHolder {
		CheckBox checkBox;
		ImageView image;
		TextView shopName;
		TextView content;
		TextView carNum;
		TextView price;
		TextView add;
		TextView red;
		Button button; // ����ִ��ɾ����button
		RadioButton mDelete;
		View frontView;
		ImageButton mDeleteGoodsIB;
		LinearLayout item_right, item_left;

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.subtitle:
			isBatchModel = !isBatchModel;
			if (isBatchModel) {
				mEdit.setText(getResources().getString(R.string.menu_enter));
				mDelete.setText(getResources().getString(R.string.menu_del));
				mBottonLayout.setVisibility(View.GONE);
				mFavorite.setVisibility(View.VISIBLE);

			} else {
				mEdit.setText(getResources().getString(R.string.menu_edit));

				mFavorite.setVisibility(View.GONE);
				mBottonLayout.setVisibility(View.VISIBLE);
				mDelete.setText(getResources().getString(R.string.Check));
				totalPrice = 0;
				mPriceAll.setText("��" + totalPrice);

			}

			break;

		case R.id.check_box_all:
			totalPrice = 0;
			if (entity.size() != 0) {
				if (mCheckAll.isChecked()) {
					LogUtils.e("���ݿ��� ȫѡ�е���Ʒ����--����", entity.size() + "");
					for (int i = 0; i < entity.size(); i++) {
						entity.get(i).setChecked(true);
						// ���Ϊѡ��
						if (entity.get(i).isChecked()) {
							totalPrice = totalPrice
									+ Integer.parseInt(entity.get(i)
											.getGoodsNum())
									* Float.parseFloat(entity.get(i)
											.getGoodsPrice());
						}
						mSeclectedEntity.add(new SelectedGoodsEntity(entity
								.get(i).getGoodsName(), entity.get(i)
								.getGoodsPrice(), entity.get(i).getGoodsNum(),
								totalPrice + "", entity.get(i).getGoodsProid(),
								entity.get(i).getIsToday()));

					}
					count();
					// ˢ��
					mListAdapter.notifyDataSetChanged();
					// ��ʾ

					// mPriceAll.setText(totalPrice + "Ԫ");
				} else {
					for (int i = 0; i < entity.size(); i++) {
						entity.get(i).setChecked(false);

						// // ˢ��
						mListAdapter.notifyDataSetChanged();
					}
					if (mSeclectedEntity.size() > 0) {
						mSeclectedEntity.clear();
					} else {
						LogUtils.e("��ǰ����Ϊ0", "");
					}
					mPriceAll.setText(totalPrice + "Ԫ");
				}
			} else {
				mCheckAll.setFocusable(false);
				mCheckAll.setClickable(false);
				showToastLong("������Ʒ");
			}

			break;

		case R.id.tv_cart_buy_or_del:

			if (isBatchModel) {
				// deleteDialog();
				for (MyShopCarEntity goodsSelected : entity) {
					System.out.println(goodsSelected.isChecked());
					if (goodsSelected.isChecked()) {
						Cursor cursor = db.rawQuery(
								"delete from mycar where name = '"
										+ goodsSelected.getGoodsName() + "'",
								null);
						cursor.moveToNext();
					}

				}
				// ˢ��������
				invisibleOnScreen();
				isBatchModel = !isBatchModel;

			} else {
				if (totalPrice != 0) {
					//
					if (mSeclectedEntity.size() == 0) {
						Toast.makeText(ShopCarActivity.this, "������ѡ��Ҫ֧������Ʒ",
								Toast.LENGTH_SHORT).show();

					} else {
						if (!numIsOk()) {
							return;
						}
						Intent intent2Order = new Intent(
								getApplicationContext(), MyOrderActivity.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("goodsList",
								(Serializable) mSeclectedEntity);
						bundle.putString("totalMoney", allPrice);
						intent2Order.putExtras(bundle);
						startActivity(intent2Order);

					}

				} else {
					Toast.makeText(ShopCarActivity.this, "��ѡ��Ҫ֧������Ʒ",
							Toast.LENGTH_SHORT).show();
					// mListAdapter.notifyDataSetChanged();
					return;
				}
			}

			break;
		case R.id.back:
			onBackPressed();
			break;

		default:
			break;
		}
	}

	private boolean numIsOk() {

		if (TextUtils.isEmpty(telNum)) {
			showToastLong("���¼");
			return false;
		}

		return true;
	}

	private void deleteDialog() {
		// TODO Auto-generated method stub
		new AlertDialog.Builder(this)
				.setIcon(R.drawable.tishi)
				.setTitle("��ܰ��ʾ")
				.setMessage("ȷ��Ҫɾ������Ʒ��")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// clearData();
						for (MyShopCarEntity goodsSelected : entity) {
							System.out.println(goodsSelected.isChecked());
							if (goodsSelected.isChecked()) {
								Cursor cursor = db.rawQuery(
										"delete from mycar where name = '"
												+ goodsSelected.getGoodsName()
												+ "'", null);
								cursor.moveToNext();
							}

						}
						// ˢ��������
						invisibleOnScreen();
						dialog.dismiss();
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).show();
	}

	// BigDecimal b1 = new BigDecimal(Float.toString(xx));
	// BigDecimal b2 = new BigDecimal(Float.toString(yy));
	// float ss = b1.subtract(b2).floatValue();
	/**
	 * ����۸�
	 */
	public void count() {
		DecimalFormat fnum = new DecimalFormat("##0.00");
		totalPrice = 0;// �����
		if (entity != null && entity.size() > 0) {
			for (int i = 0; i < entity.size(); i++) {
				if (entity.get(i).isChecked()) {

					totalPrice = totalPrice
							+ Integer.parseInt(entity.get(i).getGoodsNum())
							* Float.parseFloat(entity.get(i).getGoodsPrice());

				}
			}
			allPrice = fnum.format(totalPrice);
			mPriceAll.setText("��" + allPrice + "");
		}

	}

	public void select() {
		int count = 0;
		for (int i = 0; i < entity.size(); i++) {
			if (entity.get(i).isChecked()) {
				count++;
			}
		}
		if (count == entity.size()) {
			mCheckAll.setChecked(true);
		} else {
			isSelect = true;
			mCheckAll.setChecked(false);
		}

	}

	// ˫���˳�
	// // ˫���˳� ��һ�ε����ʱ������Ϊ0
	private long firstTime = 0;

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {
				Toast.makeText(this, "�ٰ�һ���˳�", Toast.LENGTH_SHORT).show();
				firstTime = System.currentTimeMillis();
				return true;
			} else {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
				System.exit(0);
				finish();
			}
		}
		return super.onKeyUp(keyCode, event);
	}

}
