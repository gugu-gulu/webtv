package com.fongmi.android.tv.ui.dialog;

import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import com.fongmi.android.tv.R;
import com.fongmi.android.tv.databinding.DialogUpdateBinding;
import com.fongmi.android.tv.impl.UpdateListener;
import com.fongmi.android.tv.utils.ResUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Locale;

public class UpdateDialog extends BaseAlertDialog {

    private DialogUpdateBinding binding;
    private UpdateListener listener;
    private String title;
    private String desc;
    private String link;

    public static UpdateDialog create() {
        return new UpdateDialog();
    }

    public UpdateDialog title(String title) {
        this.title = title;
        return this;
    }

    public UpdateDialog desc(String desc) {
        this.desc = desc;
        return this;
    }

    public UpdateDialog link(String link) {
        this.link = link;
        return this;
    }

    public UpdateDialog listener(UpdateListener listener) {
        this.listener = listener;
        return this;
    }

    public UpdateDialog show(FragmentActivity activity) {
        show(activity.getSupportFragmentManager(), null);
        return this;
    }

    @Override
    protected ViewBinding getBinding() {
        return binding = DialogUpdateBinding.inflate(getLayoutInflater());
    }

    @Override
    protected MaterialAlertDialogBuilder getBuilder() {
        // Browser first: an in-app download ties the phone up for the whole transfer,
        // while the browser keeps going in the background. Downloading in the app is
        // still offered, as the secondary action.
        return builder().setTitle(title).setView(getBinding().getRoot()).setPositiveButton(R.string.update_browser, null).setNeutralButton(R.string.update_in_app, null).setNegativeButton(R.string.dialog_negative, null).setCancelable(false);
    }

    @Override
    protected void initView() {
        binding.desc.setText(desc);
        boolean hasLink = !TextUtils.isEmpty(link);
        binding.linkHint.setVisibility(hasLink ? View.VISIBLE : View.GONE);
        binding.link.setVisibility(hasLink ? View.VISIBLE : View.GONE);
        binding.link.setText(link);
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog == null) return;
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(view -> listener.onCancel(view));
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(view -> listener.onConfirm(view));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> listener.onBrowser(view));
    }

    public void setProgress(int progress) {
        setDownloadText(String.format(Locale.getDefault(), "%1$d%%", progress));
    }

    public void setProgress(int progress, long speed) {
        String text = progress < 0 ? ResUtil.getString(R.string.update_downloading) : String.format(Locale.getDefault(), "%1$d%% · %2$s", progress, formatSpeed(speed));
        setDownloadText(text);
    }

    /** Progress belongs on the in-app download button, which is the neutral one here. */
    private void setDownloadText(String text) {
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setText(text);
    }

    private String formatSpeed(long speed) {
        if (speed <= 0) return "";
        if (speed >= 1024 * 1024) return String.format(Locale.getDefault(), "%.1fMB/s", speed / 1024f / 1024f);
        return String.format(Locale.getDefault(), "%.0fKB/s", speed / 1024f);
    }
}