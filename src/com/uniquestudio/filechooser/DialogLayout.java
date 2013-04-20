package com.uniquestudio.filechooser;

import android.content.Context;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DialogLayout extends LinearLayout {
	private TextView messageTextView;
	private EditText inputEditText;

	public DialogLayout(Context context) {
		super(context);
		// ���ò���Ϊ��ֱ����
		this.setOrientation(LinearLayout.VERTICAL);
		// ���ö��뷽ʽ
		this.setGravity(Gravity.CENTER_HORIZONTAL);

		messageTextView = new TextView(context);
		inputEditText=new EditText(context);
		inputEditText.setSelectAllOnFocus(true);

		// ���TextView��EditText
		this.addView(messageTextView, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		this.addView(inputEditText, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

	}
	//��ȡTextView����
	public TextView getMessageTextView(){
		return messageTextView;
	}
	//��ȡEditText����
	public EditText getInputEditText(){
		return inputEditText;
	}

}
