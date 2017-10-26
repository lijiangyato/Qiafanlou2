package com.ings.gogo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ings.gogo.R;
import com.ings.gogo.activity.ui.BaseActivity;

public class MoreMsgActvity extends BaseActivity implements OnClickListener {
	private final int MORE_RESULT = 105;
	private TextView mAddMsgOk;
	private ImageView mAddBack;
	private EditText mAddInputMsg;
	private TextView mAddWordNum;
	private int num = 200;// 限制的最大字数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_moremsg);
		initViews();
	}

	private void initViews() {
		// TODO Auto-generated method stub
		mAddBack = (ImageView) this.findViewById(R.id.moremsg_back);
		mAddBack.setOnClickListener(this);
		mAddMsgOk = (TextView) this.findViewById(R.id.moremsg_complick);
		mAddMsgOk.setOnClickListener(this);
		mAddInputMsg = (EditText) this.findViewById(R.id.moremsg_addmoremsgedt);
		mAddWordNum = (TextView) this.findViewById(R.id.moremsg_wordNum);
		mAddWordNum.setText("0/200");
		mAddInputMsg.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				temp = s;
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				int number = s.length();
				mAddWordNum.setText(number + "/200");
				selectionStart = mAddInputMsg.getSelectionStart();
				selectionEnd = mAddInputMsg.getSelectionEnd();
				if (temp.length() > num) {
					showToastLong("你输入的字数已经超过了限制！");
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionEnd;
					mAddInputMsg.setText(s);
					mAddInputMsg.setSelection(tempSelection);// 设置光标在最后
				}

			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.moremsg_back:
			finish();

			break;
		case R.id.moremsg_complick:
			if (mAddInputMsg.getText().toString().isEmpty()) {
				finish();
			} else {
				Intent intent = new Intent();
				intent.putExtra("moreMsg", mAddInputMsg.getText().toString());
				setResult(MORE_RESULT, intent);
				finish();
			}

			break;

		default:
			break;
		}
	}

	TextWatcher mTextWatcher = new TextWatcher() {
		private CharSequence temp;
		private int editStart;
		private int editEnd;

		@Override
		public void beforeTextChanged(CharSequence s, int arg1, int arg2,
				int arg3) {
			temp = s;
		}

		@Override
		public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
			mAddWordNum.setText(s);
		}

		@Override
		public void afterTextChanged(Editable s) {
			editStart = mAddInputMsg.getSelectionStart();
			editEnd = mAddInputMsg.getSelectionEnd();
			if (temp.length() > 20) {
				showToastLong("你输入的字数已经超过了限制！");
				s.delete(editStart - 1, editEnd);
				int tempSelection = editStart;
				mAddInputMsg.setText(s);
				mAddInputMsg.setSelection(tempSelection);
			}
		}

	};

}
