package eslglobal.com.esl.dialogfragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import eslglobal.com.esl.Bsecure;
import eslglobal.com.esl.R;
import eslglobal.com.esl.common.Item;

public class ReplyDialog extends DialogFragment {

	private Bsecure bsecure = null;

	Intent intent = new Intent(Intent.ACTION_SEND);

	public static ReplyDialog newInstance() {
		return new ReplyDialog();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		bsecure = (Bsecure) activity;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		Bundle mArgs = getArguments();

		View view = inflater.inflate(R.layout.reply_popup, container, false);

		EditText reply = (EditText) view.findViewById(R.id.cmttxt_reply);
		reply.setFilters(new InputFilter[] { EMOJI_FILTER });

		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		view.findViewById(R.id.bn_dg_cancel).setOnClickListener(bsecure);
		view.findViewById(R.id.tvCommentSubmit).setOnClickListener(bsecure);
		view.findViewById(R.id.tvCommentSubmit).setTag((Item) mArgs.getSerializable("item"));

		return view;
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

	}

	public InputFilter EMOJI_FILTER = new InputFilter() {

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			for (int index = start; index < end; index++) {

				int type = Character.getType(source.charAt(index));
				
				if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) {

					return "";
				}
			}
			return null;
		}
	};
}
