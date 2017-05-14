package info.bunny178.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;

/**
 * DialogFragmentをAlertDialogのように利用するためのクラス.
 *
 * @author Ishimaru Sohei
 */
public class AlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    public static final String KEY_TITLE = "title";

    public static final String KEY_MESSAGE = "message";

    public static final String KEY_ICON = "icon_id";

    private DialogInterface.OnClickListener mListItemListener;

    private DialogInterface.OnClickListener mPositiveButtonListener;

    private DialogInterface.OnClickListener mNegativeButtonListener;

    private DialogInterface.OnClickListener mNeutralButtonListener;

    private int mPositiveButtonTextId = -1;

    private int mNegativeButtonTextId = -1;

    private int mNeutralButtonTextId = -1;

    private int mListItemsId = -1;

    private static AlertDialogFragment mDialogFragment;

    /**
     * 引数で指定されたパラメータでこのインスタンスを初期化する
     * 
     * @param context
     *            コンテキスト.リソースを読み込むために利用する.
     * @param titleId
     *            ダイアログのタイトルとして表示する文字列のリソースID
     * @param messageId
     *            ダイアログの本文として表示する文字列のリソースID
     * @param iconId
     *            ダイアログのアイコンとして表示する画像のリソースID.Android標準のものは,android.R.drawable.ic
     *            .dialog.xxxで参照できる.
     * @return
     */
    public static AlertDialogFragment newInstance(Context context, int titleId, int messageId, int iconId) {
        mDialogFragment = new AlertDialogFragment();
        String title = context.getString(titleId);
        String message = context.getString(messageId);
        return newInstance(title, message, iconId);
    }

    /**
     * 引数で指定されたパラメータでこのインスタンスを初期化する
     * 
     * @param title
     *            ダイアログのタイトルとして表示する文字列
     * @param message
     *            ダイアログの本文として表示する文字列
     * @param iconId
     *            ダイアログのアイコンとして表示する画像のリソースID.Android標準のものは,android.R.drawable.ic
     *            .dialog.xxxで参照できる.
     * @return
     */
    public static AlertDialogFragment newInstance(String title, String message, int iconId) {
        mDialogFragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_MESSAGE, message);
        args.putInt(KEY_ICON, iconId);
        mDialogFragment.setArguments(args);
        return mDialogFragment;
    }

    /**
     * Positive Buttonのイベントリスナ,ボタンの文字列を設定する.指定は任意でOK.通常は,処理を実行するボタンとして用いる.
     * 
     * @param textId
     *            NeutralButtonの文字列
     * @param listener
     *            NeutralButtonのイベントリスナ
     */
    public AlertDialogFragment setPositiveButton(int textId, DialogInterface.OnClickListener listener) {
        mPositiveButtonTextId = textId;
        mPositiveButtonListener = listener;
        return mDialogFragment;
    }

    /**
     * Negative Buttonのイベントリスナ,ボタンの文字列を設定する.指定は任意でOK.通常は,処理を行わないボタンとして用いる.
     * 
     * @param textId
     *            NeutralButtonの文字列
     * @param listener
     *            NeutralButtonのイベントリスナ
     */
    public AlertDialogFragment setNegativeButton(int textId, DialogInterface.OnClickListener listener) {
        mNegativeButtonTextId = textId;
        mNegativeButtonListener = listener;
        return mDialogFragment;
    }

    /**
     * Neutral Buttonのイベントリスナ,ボタンの文字列を設定する.指定は任意でOK.通常は"はい""いいえ"のどちらでもなく,
     * 処理を行わないボタンとして用いる.
     * 
     * @param textId
     *            NeutralButtonの文字列
     * @param listener
     *            NeutralButtonのイベントリスナ
     */
    public AlertDialogFragment setNeutralButton(int textId, DialogInterface.OnClickListener listener) {
        mNeutralButtonTextId = textId;
        mNeutralButtonListener = listener;
        return mDialogFragment;
    }

    /**
     * ダイアログにリストを表示する場合,string-arrayで表示する文字列を指定する
     * 
     * @param itemsId
     *            リストで表示する文字列配列のリソースID
     * @param listener
     *            イベントリスナ
     */
    public AlertDialogFragment setItems(int itemsId, DialogInterface.OnClickListener listener) {
        mListItemListener = listener;
        mListItemsId = itemsId;
        return mDialogFragment;
    }

    /**
     * ダイアログ生成時に実行されるメソッド.Bundleにてタイトル,本文,ダイアログのアイコン指定がある場合はそれに従う. Positive /
     * Negative / Neutralボタンの指定,Listアイテムの指定がある場合は,ダイアログにそれぞれの値を指定する.
     * 
     * @see DialogFragment#onCreateDialog(Bundle)
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle args = getArguments();
        String title = args.getString(KEY_TITLE);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        String message = args.getString(KEY_MESSAGE);
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }
        int iconId = args.getInt(KEY_ICON, -1);
        if (0 < iconId) {
            builder.setIcon(iconId);
        }
        if (0 < mPositiveButtonTextId) {
            builder.setPositiveButton(mPositiveButtonTextId, mPositiveButtonListener);
        }
        if (0 < mNegativeButtonTextId) {
            builder.setNegativeButton(mNegativeButtonTextId, mNegativeButtonListener);
        }
        if (0 < mNeutralButtonTextId) {
            builder.setNeutralButton(mNeutralButtonTextId, mNeutralButtonListener);
        }
        if (0 < mListItemsId) {
            builder.setItems(mListItemsId, mListItemListener);
        }
        return builder.create();
    }

    /**
     * ダイアログのボタンが押された時の処理.それぞれのボタンに対応したOnClickListenerがnullの場合はコールバックしない.
     * 
     * @see DialogInterface.OnClickListener#onClick(DialogInterface,
     *      int)
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
        case DialogInterface.BUTTON_POSITIVE:
            if (mPositiveButtonListener != null) {
                mPositiveButtonListener.onClick(dialog, which);
            }
            break;
        case DialogInterface.BUTTON_NEGATIVE:
            if (mNegativeButtonListener != null) {
                mNegativeButtonListener.onClick(dialog, which);
            }
            break;
        case DialogInterface.BUTTON_NEUTRAL:
            if (mNeutralButtonListener != null) {
                mNeutralButtonListener.onClick(dialog, which);
            }
            break;
        default:
            if (mListItemListener != null) {
                mListItemListener.onClick(dialog, which);
            }
            break;
        }
        dialog.dismiss();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

}
